----------------------------------------
-- Name: Stefon Miller smm248, Adam Buchinsky asb153, Aaron Matthew aam150
----------------------------------------

--CREATE DOMAIN FOR EMAIL ATTRIBUTE
DROP DOMAIN IF EXISTS EMAIL_DOMAIN CASCADE;
CREATE DOMAIN EMAIL_DOMAIN AS varchar(30) CHECK (VALUE ~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');
--CREATE DOMAIN FOR MUTUAL FUND CATEGORY
DROP DOMAIN IF EXISTS CATEGORY_DOMAIN CASCADE;
CREATE DOMAIN CATEGORY_DOMAIN AS varchar(10) CHECK (VALUE IN ('fixed', 'bonds', 'mixed', 'stocks'));
--CREATE DOMAIN FOR ACTION CATEGORY
DROP DOMAIN IF EXISTS ACTION_DOMAIN CASCADE;
CREATE DOMAIN ACTION_DOMAIN AS varchar(10) CHECK (VALUE IN ('deposit', 'buy', 'sell'));

/**********************************************************************************
                        Assumptions:
1) Assume that Customers and Administrators are disjoint.
- That is, no administrator is a customer, vice versa.
2) Assume 'Name' from the Administrator and Customer tables are the Full Name.
3) Assume 'Balance' cannot be negative.
4) Assume that Emails can come from different websites.
- Ex: gol6@pitt.edu, gol6@cmu.edu
5) Assume customers and administrators can come from the same household.
6) Assume 'Password' can be the same for different users
- Ex: abcdefg
7) Assume that p_date in Allocation and Closing_Price tables are unrelated.
8) For the Closing_Price table, assume that price is recorded every day.
9) Assume that when an entry is added to Closing_Price it is the most recent of that symbol
10) ROI per share refers to the unrealized ROI i.e. ((amount from selling shares + value of held shares) - (cost to buy shares))/(cost to buy shares)
11) Shares of the same Mutual Fund are fungible
**********************************************************************************/

--Drop all tables to make sure the Schema is clear!
DROP TABLE IF EXISTS MUTUAL_DATE CASCADE;
DROP TABLE IF EXISTS CUSTOMER CASCADE;
DROP TABLE IF EXISTS ADMINISTRATOR CASCADE;
DROP TABLE IF EXISTS MUTUAL_FUND CASCADE;
DROP TABLE IF EXISTS OWNS CASCADE;
DROP TABLE IF EXISTS TRXLOG CASCADE;
DROP TABLE IF EXISTS ALLOCATION CASCADE;
DROP TABLE IF EXISTS PREFERS CASCADE;
DROP TABLE IF EXISTS CLOSING_PRICE CASCADE;

---CREATING MUTUAL DATE TABLE
-- The c_date is initialized once using INSERT and updated subsequently
CREATE TABLE MUTUAL_DATE
(
    p_date date,
    CONSTRAINT S_DATE_PK PRIMARY KEY (p_date)
);


---CREATING CUSTOMER TABLE
-- Assume emails are unique -> no two users registered on the same site can share an email address
CREATE TABLE CUSTOMER
(
    login    varchar(10),
    name     varchar(20)    NOT NULL,
    email    EMAIL_DOMAIN,
    address  varchar(30)    NOT NULL,
    password varchar(10)    NOT NULL,
    balance  decimal(10, 2) NOT NULL,
    CONSTRAINT LOGIN_PK PRIMARY KEY (login),
    CONSTRAINT EMAIL_UNIQUE UNIQUE (email),
    CONSTRAINT BALANCE_CK CHECK ( balance >= 0 )
);

---CREATING ADMINISTRATOR TABLE
CREATE TABLE ADMINISTRATOR
(
    login    varchar(10),
    name     varchar(20) NOT NULL,
    email    EMAIL_DOMAIN,
    address  varchar(30) NOT NULL,
    password varchar(10) NOT NULL,
    CONSTRAINT ADMIN_LOGIN_PK PRIMARY KEY (login),
    CONSTRAINT ADMIN_EMAIL_UNIQUE UNIQUE (email)
);

---CREATING MUTUAL FUND TABLE
--Assume p_date from MutualDate is not same as c_date in MutualFund table
CREATE TABLE MUTUAL_FUND
(
    symbol      varchar(20),
    name        varchar(30)  NOT NULL,
    description varchar(100) NOT NULL,
    category    CATEGORY_DOMAIN,
    c_date      date         NOT NULL,
    CONSTRAINT MUTUAL_FUND_PK PRIMARY KEY (symbol),
    CONSTRAINT MF_NAME_UQ UNIQUE (name),
    CONSTRAINT MF_DESC_UQ UNIQUE (description)
);

---CREATING OWNS TABLE
CREATE TABLE OWNS
(
    login  varchar(10),
    symbol varchar(20),
    shares integer NOT NULL,
    CONSTRAINT OWNS_PK PRIMARY KEY (login, symbol),
    CONSTRAINT LOGIN_FK FOREIGN KEY (login) REFERENCES CUSTOMER (login),
    CONSTRAINT SYMBOL_FK FOREIGN KEY (symbol) REFERENCES MUTUAL_FUND (symbol),
    CONSTRAINT SHARES_CK CHECK ( shares > 0 )
);

---CREATING TRXLOG TABLE
--Symbol/Shares/Price can be null if the user simply wants to deposit
--No need to make a FK to PK from CLosingPrice or Allocation, in case of a deposit
CREATE TABLE TRXLOG
(
    trx_id     serial,
    login      varchar(10)    NOT NULL,
    symbol     varchar(20),
    t_date     date           NOT NULL,
    action     ACTION_DOMAIN,
    num_shares integer,
    price      decimal(10, 2),
    amount     decimal(10, 2) NOT NULL,
    CONSTRAINT TRXLOG_PK PRIMARY KEY (trx_id),
    CONSTRAINT LOGIN_FK FOREIGN KEY (login) REFERENCES CUSTOMER (login),
    CONSTRAINT SYMBOL_FK FOREIGN KEY (symbol) REFERENCES MUTUAL_FUND (symbol),
    CONSTRAINT AMOUNT_CK CHECK ( amount > 0),
    CONSTRAINT NUM_SHARES_CK CHECK ( num_shares > 0),
    CONSTRAINT PRICE_CK CHECK ( price > 0)
);

---CREATING ALLOCATION TABLE
CREATE TABLE ALLOCATION
(
    allocation_no integer,
    login         varchar(10) NOT NULL,
    p_date        date        NOT NULL, --processing date
    CONSTRAINT ALLOCATION_PK PRIMARY KEY (allocation_no),
    CONSTRAINT ALLOC_LOGIN_FK FOREIGN KEY (login) REFERENCES CUSTOMER (login)
);

---CREATING PREFERS TABLE
CREATE TABLE PREFERS
(
    allocation_no integer     NOT NULL,
    symbol        varchar(20) NOT NULL,
    percentage    decimal(3, 2)       NOT NULL,
    CONSTRAINT PREFERS_PK PRIMARY KEY (allocation_no, symbol),
    CONSTRAINT PREFERS_ALLOCATION_NO_FK FOREIGN KEY (allocation_no) REFERENCES ALLOCATION (allocation_no),
    CONSTRAINT PREFERS_ALLOCATION_SYMBOL_FK FOREIGN KEY (symbol) REFERENCES MUTUAL_FUND (symbol),
    CONSTRAINT PERCENTAGE_CK CHECK ( percentage > 0)
);

---CREATING CLOSING_PRICE TABLE
CREATE TABLE CLOSING_PRICE
(
    symbol varchar(20) NOT NULL,
    price  decimal(10, 2)       NOT NULL,
    p_date date        NOT NULL, --processing date
    CONSTRAINT CLOSING_PRICE_PK PRIMARY KEY (symbol, p_date),
    CONSTRAINT CLOSING_PRICE_SYMBOL_FK FOREIGN KEY (symbol) REFERENCES MUTUAL_FUND (symbol),
    CONSTRAINT CLOSING_PRICE_CK CHECK ( price > 0)
);



--Question 2:
DROP FUNCTION IF EXISTS search_mutual_funds(keyword_1 varchar(30), keyword_2 varchar(30));
CREATE OR REPLACE FUNCTION search_mutual_funds(keyword_1 varchar(30), keyword_2 varchar(30))
    RETURNS text AS
$$
DECLARE
    res text; -- local variable for string of symbols
    i   text; -- current record
BEGIN
    res := '';
    FOR i IN
        SELECT symbol
        FROM mutual_fund
        WHERE description LIKE '%' || keyword_1 || '%'
          AND description LIKE '%' || keyword_2 || '%'
        LOOP
            res := res || ',' || i;
        END LOOP;
    res := res || ']';
    res := ltrim(res, ','); --trim extra ',' at beginning
    res := '[' || res;

    return res;
END;
$$ LANGUAGE plpgsql;


--Question 3:
DROP PROCEDURE IF EXISTS deposit_for_investment(login varchar, deposit decimal);
CREATE OR REPLACE PROCEDURE deposit_for_investment(login varchar(10), deposit decimal(10, 2))
AS
$$
DECLARE
    mutual_date_value date;
    row_count         int;
    alloc_no          int;
    pref              record;
    percentage        decimal(10, 2);
    symbol_price      decimal(10, 2);
    amount_to_buy     decimal(10, 2);
    num_of_shares     int;
    total_amount      decimal(10, 2);
    txc_amount        decimal(10, 2);
    remaining         decimal(10, 2);
    suffient_amount   boolean;
BEGIN
    --  Get the current date
    SELECT p_date
    INTO mutual_date_value
    FROM MUTUAL_DATE
    ORDER BY p_date DESC
    LIMIT 1;

    -- Check if user exists
    SELECT count(*)
    INTO row_count
    FROM CUSTOMER
    WHERE CUSTOMER.login = deposit_for_investment.login; -- The name of the procedure is used as a prefix (i.e., scope)
    IF row_count = 0 THEN
        RAISE EXCEPTION 'User % not found.', login;
    END IF;

    --  Total amount of all transaction
    total_amount = 0;

    --  Find the newest allocation_no for the user
    SELECT ALLOCATION.allocation_no
    INTO alloc_no
    FROM ALLOCATION
    WHERE ALLOCATION.login = deposit_for_investment.login
    ORDER BY ALLOCATION.p_date DESC
    LIMIT 1;

    -- Check if the deposit is enough to buy all symbols in the allocation
    SELECT SUM(P.percentage * deposit) > SUM(CP.price)
    INTO suffient_amount
    FROM PREFERS P
             JOIN CLOSING_PRICE CP ON CP.symbol = P.symbol
             JOIN (SELECT CLOSING_PRICE.symbol, max(p_date) AS max_date
                   FROM CLOSING_PRICE
                   GROUP BY CLOSING_PRICE.symbol) AS MOST_RECENT_CP
                  ON cp.symbol = MOST_RECENT_CP.symbol AND CP.p_date = MOST_RECENT_CP.max_date
    WHERE allocation_no = alloc_no;

    IF not suffient_amount THEN -- FALSE if deposit is not enough for a symbol
        RAISE NOTICE 'Partial allocation purchase is not allowed. The amount of % will be deposited to the account.', deposit;
    ELSE
        -- Buy the shares
        FOR pref in (SELECT * FROM PREFERS WHERE allocation_no = alloc_no)
            LOOP
                percentage = pref.percentage;
                amount_to_buy = deposit * percentage;

                -- Find latest mutual fund price associated with the preference symbol
                SELECT CLOSING_PRICE.price
                INTO symbol_price
                FROM CLOSING_PRICE
                WHERE CLOSING_PRICE.symbol = pref.symbol
                ORDER BY CLOSING_PRICE.p_date DESC
                LIMIT 1;

                -- Number of shares that we have to buy for the user for this symbol
                num_of_shares = FLOOR(amount_to_buy / symbol_price);

                -- transaction total amount
                txc_amount = num_of_shares * symbol_price;
                total_amount = total_amount + txc_amount;

                -- The transaction id will be generated automatically the sequence 'trx_sequence'
                INSERT INTO TRXLOG(login, symbol, action, num_shares, price, amount, t_date)
                VALUES (deposit_for_investment.login, pref.symbol, 'buy', num_of_shares,
                        symbol_price, txc_amount, mutual_date_value);

                -- Check if the user already own some shares of this symbol
                SELECT count(*)
                INTO row_count
                FROM OWNS
                WHERE OWNS.login = deposit_for_investment.login
                  AND OWNS.symbol = pref.symbol;
                IF row_count = 0 THEN
                    -- Create a new row
                    INSERT INTO OWNS(login, symbol, shares)
                    VALUES (deposit_for_investment.login, pref.symbol, num_of_shares);
                ELSE
                    -- Update the existing row
                    UPDATE OWNS
                    SET shares = shares + num_of_shares
                    WHERE OWNS.login = deposit_for_investment.login
                      AND OWNS.symbol = pref.symbol;
                END IF;
            END LOOP;
    END IF;

    -- deposit the remaining amount to user's balance
    remaining = deposit - total_amount;
    UPDATE CUSTOMER
    SET balance = balance + remaining
    WHERE CUSTOMER.login = deposit_for_investment.login;
END;
$$ LANGUAGE PLPGSQL;

--Gets closest date to the supplied one if not in the database
CREATE OR REPLACE FUNCTION get_closest_date(input_date DATE) RETURNS DATE
    AS $$
    DECLARE
        earliest_date DATE;
        latest_date DATE;
    BEGIN
        -- Get latest date in closing_price table
        SELECT p_date INTO latest_date
        FROM closing_price
        ORDER BY p_date DESC
        FETCH FIRST ROW ONLY;

        --Get earliest date in closing_price table
        SELECT p_date INTO earliest_date
        FROM closing_price
        ORDER BY p_date ASC
        FETCH FIRST ROW ONLY;

        --Return the latest date if the supplied date is in the future.
        --Return the earliest date if the supplied date is before any
        --records. Return the supplied date if it is in the date range
        --of our records.
        IF input_date > latest_date THEN
            RETURN latest_date;
        ELSIF input_date < earliest_date THEN
            RETURN earliest_date;
        ELSE
            RETURN input_date;
        END IF;


    END;
$$  LANGUAGE PLPGSQL;

--Deletes all data from db without violating fk constraints
CREATE OR REPLACE PROCEDURE delete_from_db()
    AS $$
    BEGIN
        DELETE FROM trxlog WHERE true;
        DELETE FROM prefers WHERE true;
        DELETE FROM owns WHERE true;
        DELETE FROM closing_price WHERE true;
        DELETE FROM mutual_fund WHERE true;
        DELETE FROM mutual_date WHERE true;
        DELETE FROM allocation WHERE true;
        DELETE FROM customer WHERE true;
        DELETE FROM administrator WHERE true;

    END;
$$ LANGUAGE PLPGSQL;

--Question 4:
DROP FUNCTION IF EXISTS buy_shares(login varchar, symbol varchar, number_of_shares int);
CREATE OR REPLACE FUNCTION buy_shares(login varchar(10), symbol varchar(20), number_of_shares int)
    RETURNS BOOLEAN AS
$$
DECLARE
    mutual_date_value date;
    row_count         int;
    symbol_price      decimal(10, 2);
    customer_balance  decimal(10, 2);
BEGIN

    --  Get the current date
    SELECT p_date
    INTO mutual_date_value
    FROM MUTUAL_DATE
    ORDER BY p_date DESC
    LIMIT 1;

    -- Check if customer exists
    SELECT count(*)
    INTO row_count
    FROM CUSTOMER
    WHERE CUSTOMER.login = buy_shares.login; -- The name of the procedure is used as a prefix (i.e., scope)
    IF row_count = 0 THEN
        RAISE EXCEPTION 'User % not found.', login;
    END IF;

    -- Check if the symbol exists
    SELECT count(*)
    INTO row_count
    FROM MUTUAL_FUND
    WHERE MUTUAL_FUND.symbol = buy_shares.symbol; -- The name of the procedure is used as a prefix (i.e., scope)
    IF row_count = 0 THEN
        RAISE EXCEPTION 'Symbol % not found.', symbol;
    END IF;

    -- get the customer's balance
    SELECT balance
    INTO customer_balance
    FROM CUSTOMER
    WHERE CUSTOMER.login = buy_shares.login;

    -- Get the latest price for the desired symbol
    SELECT CLOSING_PRICE.price
    INTO symbol_price
    FROM CLOSING_PRICE
    WHERE CLOSING_PRICE.symbol = buy_shares.symbol
    ORDER BY CLOSING_PRICE.p_date DESC
    LIMIT 1;

    -- no sufficient funds to buy the shares
    IF (symbol_price * number_of_shares) > customer_balance THEN
        RETURN FALSE;
    END IF;

    -- buy shares section ==

    -- The transaction id will be generated automatically the sequence 'trx_sequence'
    INSERT INTO TRXLOG(login, symbol, action, num_shares, price, amount, t_date)
    VALUES (login, symbol, 'buy', number_of_shares, symbol_price, (symbol_price * number_of_shares),
            mutual_date_value);

    -- Check if the user already own some shares of this symbol
    SELECT count(*)
    INTO row_count
    FROM OWNS
    WHERE OWNS.login = buy_shares.login
      AND OWNS.symbol = buy_shares.symbol;

    -- if no shares are owned of the same symbol
    IF row_count = 0 THEN
        -- add an ownership of shares
        INSERT INTO OWNS(login, symbol, shares)
        VALUES (buy_shares.login, buy_shares.symbol, buy_shares.number_of_shares);
    ELSE
        -- Update the existing row
        UPDATE OWNS
        SET shares = shares + buy_shares.number_of_shares
        WHERE OWNS.login = buy_shares.login
          AND OWNS.symbol = buy_shares.symbol;
    END IF;
    -- end buy shares section ==

    -- update the customer's balance section ==
    UPDATE CUSTOMER
    SET balance = balance - (symbol_price * number_of_shares)
    WHERE CUSTOMER.login = buy_shares.login;
    -- end update customer's balance section ==

    -- shares are bought
    RETURN TRUE;

END;
$$ LANGUAGE PLPGSQL;


-- Question 5:
DROP FUNCTION IF EXISTS buy_on_date_helper();
CREATE OR REPLACE FUNCTION buy_on_date_helper()
    RETURNS trigger AS
$$
DECLARE
    num_of_shares   int;
    customer_symbol varchar(20);
    symbol_price    decimal(10, 2);
    c_customer      record;
BEGIN

    FOR c_customer in (SELECT * FROM CUSTOMER)
        LOOP
            -- Get the symbol with the minimum shares
            SELECT symbol
            into customer_symbol
            FROM OWNS
            WHERE login = c_customer.login
            ORDER BY shares
            LIMIT 1;

            IF customer_symbol IS NOT NULL THEN
                -- Get the latest price for the desired symbol
                SELECT CLOSING_PRICE.price
                INTO symbol_price
                FROM CLOSING_PRICE
                WHERE CLOSING_PRICE.symbol = customer_symbol
                ORDER BY CLOSING_PRICE.p_date DESC
                LIMIT 1;

                num_of_shares = FLOOR(c_customer.balance / symbol_price);

                --Return if there are no shares to buy
                IF num_of_shares = 0 THEN
                    RETURN NULL;
                end if;

                RAISE NOTICE 'Did the customer % buy % shares of % symbol? (%).',c_customer.login,  num_of_shares,customer_symbol,
                    buy_shares(c_customer.login, customer_symbol, num_of_shares);
            END IF;

        END LOOP;
    RETURN NULL; -- result is ignored since this is an AFTER trigger
END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER buy_on_date
    AFTER UPDATE OR INSERT
    ON mutual_date
    FOR EACH ROW
EXECUTE FUNCTION buy_on_date_helper();

-- QUESTION 6:
DROP FUNCTION IF EXISTS buy_on_price_helper();
CREATE OR REPLACE FUNCTION buy_on_price_helper()
    RETURNS trigger AS
$$
DECLARE
    num_of_shares   int;
    customer_symbol varchar(20);
    symbol_price    decimal(10, 2);
    c_customer      record;
BEGIN

    FOR c_customer in (SELECT * FROM CUSTOMER)
        LOOP
            -- Get the symbol with the minimum shares
            SELECT symbol
            into customer_symbol
            FROM OWNS
            WHERE login = c_customer.login AND symbol = NEW.symbol -- This can be optimize
            ORDER BY shares
            LIMIT 1;

            IF customer_symbol IS NOT NULL THEN
                -- Get the latest price for the desired symbol
                SELECT CLOSING_PRICE.price
                INTO symbol_price
                FROM CLOSING_PRICE
                WHERE CLOSING_PRICE.symbol = customer_symbol
                ORDER BY CLOSING_PRICE.p_date DESC
                LIMIT 1;

                num_of_shares = FLOOR(c_customer.balance / symbol_price);

                RAISE NOTICE 'Did the customer % buy % shares of % symbol? (%).',c_customer.login,  num_of_shares,customer_symbol,
                    buy_shares(c_customer.login, customer_symbol, num_of_shares);
            END IF;

        END LOOP;
    RETURN NULL; -- result is ignored since this is an AFTER trigger
END;
$$ LANGUAGE PLPGSQL;

CREATE TRIGGER buy_on_price
    AFTER UPDATE OF PRICE
    ON closing_price
    FOR EACH ROW
EXECUTE FUNCTION buy_on_price_helper();

--Sell shares
CREATE OR REPLACE FUNCTION sell_shares(user_login VARCHAR(10), fund_symbol varchar(20), num_shares INT) RETURNS BOOLEAN
AS  $$
    DECLARE
        sell_price DECIMAL(10,2);
        user_balance DECIMAL(10,2);
        curr_shares INT;
        curr_date DATE;
    BEGIN
        --Get price for most recent date in the closing_price table
        SELECT price INTO sell_price
        FROM closing_price
        WHERE symbol = fund_symbol
        ORDER BY p_date DESC
        FETCH FIRST ROW ONLY;

        --Get a balance for the given login
        SELECT balance INTO user_balance
        FROM customer
        WHERE login = user_login;

        --If no rows were found for either balance or buy price, raise an exception
        IF sell_price IS NULL THEN RAISE 'Invalid fund' USING ERRCODE='FDERR';
        ELSEIF user_balance IS NULL THEN RAISE 'Invalid login' USING ERRCODE='USERR';
        ELSEIF num_shares < 1 THEN RAISE 'Invalid number of shares'  USING ERRCODE='SHERR';
        END IF;

        --find number of shares owned by user
        SELECT shares INTO curr_shares
        FROM owns
        WHERE login = user_login AND symbol = fund_symbol;

        --Check if user has enough shares
        IF curr_shares IS NULL OR curr_shares < num_shares THEN RAISE 'Not enough shares' USING ERRCODE='SBERR'; END IF;

        --Subtract shares
        --If user is selling all their shares delete entry

        --Made obsolete by sell_rebalance trigger
        /*
        IF curr_shares = num_shares THEN
            DELETE
            FROM owns
            WHERE login = user_login AND symbol = fund_symbol;
        --IF they still have shares update
        ELSE
            UPDATE owns
            SET shares = curr_shares - num_shares
            WHERE login = user_login AND symbol = fund_symbol;
        end if;

        --add total price to customer balance
        UPDATE customer
        SET balance = balance + (sell_price * num_shares)
        WHERE login = user_login;

         */

        --Get date to insert
        SELECT p_date INTO curr_date
        FROM mutual_date;

        --Create new transaction in the log
        INSERT INTO trxlog(login, symbol, t_date, action, num_shares, price, amount)
        VALUES
        (user_login, fund_symbol, curr_date, 'sell', num_shares, sell_price, (sell_price * num_shares));

        RETURN TRUE;
    EXCEPTION
        --For all exceptions, print an error and return false
        WHEN SQLSTATE 'FDERR' THEN
            RAISE NOTICE 'No data exists for fund %', fund_symbol;
            RETURN FALSE;
        WHEN SQLSTATE 'USERR' THEN
            RAISE NOTICE 'No user exists with login %', user_login;
            RETURN FALSE;
        WHEN SQLSTATE 'SHERR' THEN
            RAISE NOTICE 'Invalid number of shares to be bought: %', num_shares;
        WHEN SQLState 'SBERR' THEN
            RAISE NOTICE 'User % does not have enough shares of % to sell %', user_login, fund_symbol, num_shares;
            RETURN FALSE;
    END $$ LANGUAGE plpgsql;


--Task 5

--Helper View
CREATE OR REPLACE VIEW LOWEST_NUM_SHARES AS
    SELECT o.login AS login, symbol
    FROM owns o JOIN
        (
            SELECT login, MIN(shares) AS min_shares
            FROM owns
            GROUP BY login
        ) m
        ON o.shares = m.min_shares AND o.login = m.login;

--Function for 5
CREATE OR REPLACE FUNCTION buy_on_date_func()
    RETURNS TRIGGER AS
$$
    DECLARE
        to_buy RECORD;
        num_to_buy int;
        user_balance decimal(10,2);
        buy_price DECIMAL(10,2);

        to_buy_cur CURSOR FOR SELECT login, symbol
            FROM LOWEST_NUM_SHARES;
    BEGIN
        OPEN to_buy_cur;
        LOOP
            --Cycle through all users who will attempt to buy
            FETCH to_buy_cur INTO to_buy;
            IF NOT FOUND THEN
                EXIT;
            END IF;

            --Get user balance
            SELECT balance INTO user_balance
                FROM customer
                WHERE login = to_buy.login;

            --Get mutual price
            SELECT price INTO buy_price
            FROM closing_price
            WHERE symbol = to_buy.symbol
            ORDER BY p_date DESC
            FETCH FIRST ROW ONLY;

            --Calculate how many shares to buy
            num_to_buy := FLOOR(user_balance / buy_price);

            --Buy the Shares
            PERFORM buy_shares(to_buy.login, to_buy.symbol, num_to_buy);

        END LOOP;
        CLOSE to_buy_cur;
        RETURN NULL;
    END $$ LANGUAGE plpgsql;

--Trigger for 5
DROP TRIGGER IF EXISTS buy_on_date ON mutual_date;
CREATE TRIGGER buy_on_date
    BEFORE UPDATE
    ON mutual_date
    FOR EACH ROW
        WHEN ( NEW.p_date = TO_DATE('2021-03-26', 'YYYY-MM-DD') )
    EXECUTE FUNCTION buy_on_date_func();

--Task 6

--Function for 6
CREATE OR REPLACE FUNCTION buy_on_price_func()
    RETURNS TRIGGER AS
$$
    DECLARE
        to_buy varchar(10);
        num_to_buy int;
        user_balance decimal(10,2);
        to_buy_cur CURSOR FOR SELECT login
            FROM LOWEST_NUM_SHARES
            WHERE symbol = NEW.symbol;
    BEGIN
        OPEN to_buy_cur;
        LOOP
            --Cycle through all users who will attempt to buy
            FETCH to_buy_cur INTO to_buy;
            IF NOT FOUND THEN
                EXIT;
            END IF;

            --Get user balance
            SELECT balance INTO user_balance
                FROM customer
                WHERE login = to_buy;

            --Calculate how many shares to buy
            num_to_buy := FLOOR(user_balance / NEW.price);

            IF num_to_buy > 0 THEN
                --Buy the Shares
                PERFORM buy_shares(to_buy, NEW.symbol, num_to_buy);
            END IF;

        END LOOP;
        CLOSE to_buy_cur;
        RETURN NULL;
    END $$ LANGUAGE plpgsql;

--Trigger for 6
DROP TRIGGER IF EXISTS buy_on_price ON closing_price;
CREATE TRIGGER buy_on_price
    AFTER INSERT
    ON closing_price
    FOR EACH ROW
    EXECUTE FUNCTION buy_on_price_func();



--Merge conflict avoidance
--Aaron
CREATE OR REPLACE FUNCTION sell_rebalance_func()
    RETURNS TRIGGER AS $$
    DECLARE
    added_balance decimal(10,2);
    sold_shares int;
    curr_shares int;
    BEGIN

        --Update customer balance
        SELECT new.amount into added_balance
        FROM TRXLOG;
        UPDATE customer
        SET balance = balance + (added_balance)
        WHERE login = new.login;

        --Update shares in trxlog
        SELECT new.num_shares into sold_shares
        FROM TRXLOG;

        --Get the current number of shares the user owns
        SELECT shares INTO curr_shares
        FROM owns
        WHERE login = new.login AND symbol = new.symbol;

        --Update shares if the user still has some after selling. Delete the tuple otherwise
        IF curr_shares - sold_shares = 0 THEN
            DELETE FROM OWNS
            WHERE login = new.login AND symbol = new.symbol;
        ELSE
            UPDATE OWNS
            SET shares = shares - sold_shares
            WHERE login = new.login AND symbol = new.symbol;
        end if;


        return new;
    END $$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS sell_rebalance ON TRXLOG;
CREATE TRIGGER sell_rebalance
    AFTER INSERT
    ON TRXLOG
    FOR EACH ROW
    WHEN(NEW.action = 'sell')
    EXECUTE FUNCTION sell_rebalance_func();
--Testing Aaron's Trigger
--insert into trxlog( trx_id,login, symbol,t_date,action,num_shares ,price ,amount) VALUES(1,'mike','RE','2020-03-29','sell',50,15.00,750);
--DELETE FROM trxlog WHERE login = 'mike'
--Adam
CREATE OR REPLACE FUNCTION price_jump_func()
    RETURNS TRIGGER AS
$$
    DECLARE
        old_price DECIMAL(10,2);
        to_sell RECORD;
        to_sell_cur CURSOR FOR
            SELECT login, shares
            FROM owns
            WHERE symbol = new.symbol;
    BEGIN
        --Get price for most second most recent date in the closing_price table
        SELECT price INTO old_price
        FROM closing_price
        WHERE symbol = new.symbol
        ORDER BY p_date DESC
        LIMIT 1 OFFSET 1;

        --I don't feel that not having an old price should throw an exception
        IF old_price IS NOT NULL THEN
            --Check if price diffence is great enough
            IF abs(old_price - new.price) >= 10 THEN
                --Attempts to sell all of stock for users who have it
                OPEN to_sell_cur;
                LOOP
                    FETCH to_sell_cur INTO to_sell;
                    IF NOT FOUND THEN
                        EXIT;
                    END IF;

                    --Sell all shares
                    PERFORM sell_shares(to_sell.login, new.symbol, to_sell.shares);

                END LOOP;
                CLOSE to_sell_cur;
            END IF;
        END IF;
        RETURN NULL;
    END;
$$ LANGUAGE plpgsql;



DROP TRIGGER IF EXISTS price_jump ON closing_price;
CREATE TRIGGER price_jump
    AFTER INSERT
    ON closing_price
    FOR EACH ROW
    EXECUTE FUNCTION price_jump_func();

--Stefon
CREATE OR REPLACE FUNCTION price_initialization_func()
    RETURNS TRIGGER AS $$
    DECLARE
        symbol VARCHAR(20);
        lowest_price DECIMAL(10, 2);
        curr_date DATE;
    BEGIN
        --Get the current date
        SELECT p_date INTO curr_date
        FROM mutual_date;

        --Get lowest price for the most recent date
        SELECT MIN(price) INTO lowest_price
        FROM closing_price
        WHERE p_date = (curr_date - 1);

        --If there are no mutual funds to initialize a price off of, then use an error
        IF lowest_price IS NULL THEN RAISE 'Error purchasing' USING ERRCODE='MFERR';
        END If;

        --Insert new symbol into closing price
        INSERT INTO closing_price(symbol,price,p_date)
        VALUES (NEW.symbol,lowest_price,(curr_date - 1));
        RETURN new;

        EXCEPTION
        WHEN SQLSTATE 'MFERR' THEN
            RAISE NOTICE 'No mutual fund prices found in the database';
            RETURN new;

    END $$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS price_initialization ON MUTUAL_FUND;
CREATE TRIGGER price_initialization
    AFTER INSERT
    ON MUTUAL_FUND
    FOR EACH ROW
    EXECUTE FUNCTION price_initialization_func();

--Trigger test
--insert into mutualfund (symbol,name,description,category,c_date) values('AAA','money-market','money market,conservative','fixed','2021-04-19');
--SELECT * FROM closing_price;
SELECT max(allocation_no) as alloc_id FROM prefers natural join(SELECT allocation_no FROM allocation WHERE p_date =(SELECT max(p_date) FROM allocation WHERE login='timothy' GROUP BY login)) AS alloc;