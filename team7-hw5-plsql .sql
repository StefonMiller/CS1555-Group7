--CS1555 Team 7
--Stefon Miller SMM248, Aaron Mathew aam150, Adam Buchinsky asb153
--
--Task 2
CREATE OR REPLACE FUNCTION search_mutual_funds(keyword1 varchar(30), keyword2 varchar(30))
RETURNS TABLE(output text)AS
$$

BEGIN

    RETURN QUERY SELECT string_agg(symbol::text, ', ')   FROM mutualfund
    WHERE description LIKE '%' || keyword1 || '%' ||  keyword2 || '%' OR description LIKE '%' || keyword2 || '%' ||  keyword1 || '%';

END
$$ LANGUAGE plpgsql;


SELECT  search_mutual_funds('bonds','term') as output;

--Task 4(task 3 is below)
CREATE OR REPLACE FUNCTION buy_shares(user_login VARCHAR(10), fund_symbol varchar(20), num_shares INT) RETURNS BOOLEAN
AS  $$
    DECLARE
        buy_price DECIMAL(10,2);
        user_balance DECIMAL(10,2);
        curr_shares INT;
        curr_date DATE;
    BEGIN
        --Get price for most recent date in the closing_price table
        SELECT price INTO buy_price
        FROM closing_price
        WHERE symbol = fund_symbol
        ORDER BY p_date DESC
        FETCH FIRST ROW ONLY;

        --Get a balance for the given login
        SELECT balance INTO user_balance
        FROM customer
        WHERE login = user_login;

        --If no rows were found for either balance or buy price, raise an exception
        IF buy_price IS NULL THEN RAISE 'Invalid fund' USING ERRCODE='FDERR';
        ELSEIF user_balance IS NULL THEN RAISE 'Invalid login' USING ERRCODE='USERR';
        ELSEIF num_shares < 1 THEN RAISE 'Invalid number of shares'  USING ERRCODE='SHERR';
        END IF;

        --After ensuring there is valid data, check if the user has enough money
        IF (buy_price * num_shares) > user_balance THEN RAISE 'Not enough money' USING ERRCODE='BLERR';
        END IF;

        --Subtract total price from customer balance
        UPDATE customer
        SET balance = balance - (buy_price * num_shares)
        WHERE login = user_login;

        --Check if user already has shares for this fund
        SELECT shares INTO curr_shares
        FROM owns
        WHERE login = user_login AND symbol = fund_symbol;


        --If they don't have shares, insert a new tuple
        IF curr_shares IS NULL THEN
            INSERT INTO owns(login, symbol, shares)
            VALUES
            (user_login, fund_symbol, num_shares);
        --If they have shares, update the amount
        ELSE
            UPDATE owns
            SET shares = curr_shares + num_shares
            WHERE login = user_login AND symbol = fund_symbol;
        end if;

        --Get date to insert
        SELECT p_date INTO curr_date
        FROM mutual_date;

        --Create new transaction in the log
        INSERT INTO trxlog(login, symbol, t_date, action, num_shares, price, amount)
        VALUES
        (user_login, fund_symbol, curr_date, 'buy', num_shares, buy_price, (buy_price * num_shares));

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
        WHEN SQLState 'BLERR' THEN
            RAISE NOTICE 'User % does not have enough money for % shares of %', user_login, num_shares, fund_symbol;
            RETURN FALSE;
    END $$ LANGUAGE plpgsql;

SELECT buy_shares('mike', 'MM', 10);

--Task 3
CREATE OR REPLACE PROCEDURE deposit_for_investment(user_login VARCHAR(10), amount DECIMAL(10,2))
AS  $$
    DECLARE
        most_recent_allocation_date DATE;
        allocation_record RECORD;
        curr_allocation_record_symbol VARCHAR(20);
        curr_allocation_record_percentage DECIMAL(3,2);
        curr_date DATE;
        curr_num_shares INT;
        user_balance DECIMAL(10, 2);
        most_recent_preference_percentage DECIMAL(3, 2);
        curr_symbol_price DECIMAL(10,2);
        purchase_result BOOLEAN;
    BEGIN
        --Get the most recent allocation date for the user
        SELECT p_date INTO most_recent_allocation_date
        FROM allocation a JOIN prefers p ON a.allocation_no = p.allocation_no
        WHERE login = user_login
        ORDER BY p_date DESC
        FETCH FIRST ROW ONLY;

        --Ensure the user is depositing a valid amount and there are existing allocations for them
        IF most_recent_allocation_date IS NULL THEN RAISE 'No allocation found' USING ERRCODE='ALERR';
        ELSEIF amount < 0 THEN RAISE 'Negative deposit' USING ERRCODE='NEGDP';
        END IF;

        --Insert the deposit into their balance before buying anything
        UPDATE customer
        SET balance = balance + amount
        WHERE login = user_login;

        --Store the new balance for later
        SELECT balance INTO user_balance
        FROM customer
        WHERE login = user_login;

        --Get date to insert
        SELECT p_date INTO curr_date
        FROM mutual_date;

        --Create new deposit transaction in the log
        INSERT INTO trxlog(login, t_date, action, amount)
        VALUES
        (user_login, curr_date, 'deposit', amount);

        --Once we ensure there is allocation data, ensure the percentages add up to 100
        SELECT SUM(percentage) INTO most_recent_preference_percentage
        FROM allocation a JOIN prefers p ON a.allocation_no = p.allocation_no
        WHERE login = user_login AND p_date = most_recent_allocation_date;

        IF most_recent_preference_percentage != 1.00 THEN RAISE 'Invalid preferences' USING ERRCODE='PRERR';
        END IF;

        --Loop through each allocation preference for the most recent date
        FOR allocation_record IN SELECT *
                                 FROM allocation a JOIN prefers p
                                 ON a.allocation_no = p.allocation_no
                                 WHERE login = user_login AND p_date = most_recent_allocation_date
        LOOP
            --Store the symbol and percentage for the current allocation record
            curr_allocation_record_symbol := allocation_record.symbol;
            curr_allocation_record_percentage := allocation_record.percentage;

            --Get price for most recent date in the closing_price table
            SELECT price INTO curr_symbol_price
            FROM closing_price
            WHERE symbol = curr_allocation_record_symbol
            ORDER BY p_date DESC
            FETCH FIRST ROW ONLY;

            --Multiply their deposit amount by the percentage to get the amount they would like to invest for this symbol
            --Then, get the floor of that amount divided by the current symbol price for the amount of shares to buy
            curr_num_shares = floor((amount * curr_allocation_record_percentage) / curr_symbol_price);

            --Attempt to buy the amount of shares if it is more than 0
            IF curr_num_shares > 0 THEN
                SELECT buy_shares(user_login, curr_allocation_record_symbol, curr_num_shares) INTO purchase_result;
                IF purchase_result = FALSE THEN RAISE 'Error purchasing' USING ERRCODE='PUERR';
                END If;
            END IF;
         END LOOP;

        RAISE NOTICE 'Most recent allocation: %', most_recent_allocation_date;
    EXCEPTION
        WHEN SQLSTATE 'ALERR' THEN
            RAISE NOTICE 'No allocations found for user %', user_login;
        WHEN SQLSTATE 'NEGDP' THEN
            RAISE NOTICE 'Cannot deposit a negative amount of money';
        WHEN SQLSTATE 'PRERR' THEN
            RAISE NOTICE 'Preferences for user % do not total 100', user_login;
        WHEN SQLSTATE 'PUERR' THEN
            RAISE NOTICE 'Error when purchasing stock';


    END $$ LANGUAGE plpgsql;
--This fails if run in order for the first time, run task 4 and come back. Also may need to be run multiple times before it works ~4
--This issue is caused by Datagrip's sequence for the trxlog table being out of sync with the current increment in the
--table.
CALL deposit_for_investment('mike', 750);

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
CREATE TRIGGER buy_on_date
    BEFORE UPDATE
    ON mutual_date
    FOR EACH ROW
        WHEN ( NEW.p_date = TO_DATE('2021-03-26', 'YYYY-MM-DD') )
    EXECUTE FUNCTION buy_on_date_func();

--Call trigger
UPDATE mutual_date
SET p_date = TO_DATE('2021-03-26', 'YYYY-MM-DD')
WHERE p_date IN (SELECT * FROM mutual_date);

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
            --Buy the Shares
            PERFORM buy_shares(to_buy, NEW.symbol, num_to_buy);

        END LOOP;
        CLOSE to_buy_cur;
        RETURN NULL;
    END $$ LANGUAGE plpgsql;

--Trigger for 6
CREATE TRIGGER buy_on_price
    AFTER INSERT
    ON closing_price
    FOR EACH ROW
    EXECUTE FUNCTION buy_on_price_func();

--Fire Trigger
INSERT INTO closing_price
(symbol, price, p_date)
VALUES('RE', 10, (SELECT p_date FROM mutual_date));