//package src;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class BetterFutureDriver {
    public static void main(String args[]) {
        Connection conn = null;
        try {
            // Attempt to load the jdbc driver
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost/postgres";
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "postgres");
            // Attempt to connect to the database
            conn = DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException ce) {
            // Error handling for no jdbc driver found
            System.out.println("No postgres jdbc driver found. Please ensure a valid jdbc driver is present");
            System.exit(1);
        } catch (SQLException sqle) {
            // Error handling for bad connection to database
            System.out.println(
                    "Error establising a connection to the database. Please ensure valid credentials were entered");
            System.exit(1);
        }
        // Delete all DB data initially for testing
        BetterFutureInterface.admin1(conn);
        // Insert sample data into the db
        insertSampleData(conn);

        testAdmin2(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testAdmin3(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testAdmin4(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testAdmin5(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testAdmin6(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testAdmin7(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer1(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer2(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer3(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer4(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer5(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer6a(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer6b(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer7(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer8(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer9(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer10(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer11(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testCustomer12(conn);
        System.out.println(
                "------------------------------------------------------------------------------------------------------");
        testAdmin1(conn);
    }

    /**
     * Inserts sample data into the db for testing
     * 
     * @param c Connection to the db
     */
    private static void insertSampleData(Connection c) {
        try {
            // Insert tuples into customer table
            PreparedStatement stmt = c.prepareStatement(
                    "INSERT INTO customer(login,name,email,address,password,balance)" + "VALUES(?, ?, ?, ?, ?, ?);");
            // Sample data for customers
            String customerLogins[] = new String[] { "John", "mike", "mary", "timothy" };
            String customerNames[] = new String[] { "John Doe", "Mike Costa", "Mary Chrysanthis", "Timothy Jones" };
            String customerEmails[] = new String[] { "jDoe@betterfuture.com", "mike@betterfuture.com",
                    "mary@betterfuture.com", "timothy@betterfuture.com" };
            String customerAddresses[] = new String[] { "123 Oak Street", "1st Street", "2nd Street",
                    "3234 Forbes Avenue" };
            String customerPasswords[] = new String[] { "password", "pwd", "pwd", "tim1" };
            Double customerBalances[] = new Double[] { 100.0, 750.0, 0.0, 10000.0 };
            // Insert all mutual funds in a batch
            for (int i = 0; i < customerLogins.length; i++) {
                stmt.setString(1, customerLogins[i]);
                stmt.setString(2, customerNames[i]);
                stmt.setString(3, customerEmails[i]);
                stmt.setString(4, customerAddresses[i]);
                stmt.setString(5, customerPasswords[i]);
                stmt.setDouble(6, customerBalances[i]);
                stmt.addBatch();
            }
            // Insert tuples transactionally
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            stmt.executeBatch();
            c.commit();

            // Insert tuples into admin table
            stmt = c.prepareStatement(
                    "INSERT INTO administrator(login,name,email,address,password)" + "VALUES(?, ?, ?, ?, ?);");
            // Set values of prepared statement
            stmt.setString(1, "admin");
            stmt.setString(2, "admin_name");
            stmt.setString(3, "admin@betterfuture.com");
            stmt.setString(4, "123 tech lane");
            stmt.setString(5, "root");
            // Insert tuples transactionally
            stmt.executeUpdate();
            c.commit();

            // Insert tuples into date table
            Date testDate = Date.valueOf("2021-04-19");
            stmt = c.prepareStatement("insert into mutual_date(p_date) values (?);");
            // Set values of prepared statement
            stmt.setDate(1, testDate);
            // Insert tuples transactionally
            stmt.executeUpdate();
            c.commit();

            // Insert tuples into mutual fund table
            stmt = c.prepareStatement(
                    "INSERT INTO mutual_fund(symbol, name, description, category, c_date)" + "VALUES(?, ?, ?, ?, ?);");
            // Sample data for mutual funds
            String fund_symbols[] = new String[] { "MM", "RE", "STB", "LTB", "BBS", "SRBS", "GS", "AS", "IMS", "AMZ" };
            String fund_names[] = new String[] { "money-market", "real-estate", "short-term-bonds", "long-term-bonds",
                    "balance-bonds-stocks", "social-response-bonds-stocks", "general-stocks", "aggressive-stocks",
                    "international-markets-stock", "Amazon" };
            String fund_descriptions[] = new String[] { "money-market, conservative", "real estate", "short term bonds",
                    "long term bonds", "balance bonds and stocks", "social responsibility and stocks", "stocks",
                    "agressive stocks", "international markets stock, risky", "Amazon Stock" };
            String fund_categories[] = new String[] { "fixed", "fixed", "bonds", "bonds", "mixed", "mixed", "stocks",
                    "stocks", "stocks", "stocks" };
            Date fund_dates[] = new Date[] { Date.valueOf("2021-02-19"), Date.valueOf("2021-04-10"),
                    Date.valueOf("2021-02-19"), Date.valueOf("2021-02-19"), Date.valueOf("2021-04-19"),
                    Date.valueOf("2021-04-19"), Date.valueOf("2021-02-19"), Date.valueOf("2021-02-19"),
                    Date.valueOf("2021-02-19"), Date.valueOf("2021-04-19") };
            // Insert all mutual funds in a batch
            for (int i = 0; i < fund_symbols.length; i++) {
                stmt.setString(1, fund_symbols[i]);
                stmt.setString(2, fund_names[i]);
                stmt.setString(3, fund_descriptions[i]);
                stmt.setString(4, fund_categories[i]);
                stmt.setDate(5, fund_dates[i]);
                stmt.addBatch();
            }
            // Insert tuples transactionally
            stmt.executeBatch();
            c.commit();

            // Insert tuples into closing price table
            stmt = c.prepareStatement("insert into closing_price(symbol,price,p_date) values (?, ?, ?);");
            Double fund_prices[] = new Double[] { 10.0, 15.0, 20.0, 200.0, 104.0, 32.0, 634.0, 32.0, 19.0, 92.0 };
            // Set of closing prices for 4/18
            for (int i = 0; i < fund_symbols.length; i++) {
                stmt.setString(1, fund_symbols[i]);
                stmt.setDouble(2, fund_prices[i]);
                stmt.setDate(3, Date.valueOf("2021-04-18"));
                stmt.addBatch();
            }
            // Insert another set of tuples for 4/17
            List<Double> dList = Arrays.asList(fund_prices);
            Collections.shuffle(dList);
            dList.toArray(fund_prices);
            for (int i = 0; i < fund_symbols.length; i++) {
                stmt.setString(1, fund_symbols[i]);
                stmt.setDouble(2, fund_prices[i]);
                stmt.setDate(3, Date.valueOf("2021-04-17"));
                stmt.addBatch();
            }
            // Insert another set of tuples for 4/16
            dList = Arrays.asList(fund_prices);
            Collections.shuffle(dList);
            dList.toArray(fund_prices);
            for (int i = 0; i < fund_symbols.length; i++) {
                stmt.setString(1, fund_symbols[i]);
                stmt.setDouble(2, fund_prices[i]);
                stmt.setDate(3, Date.valueOf("2021-04-16"));
                stmt.addBatch();
            }
            // Insert another set of tuples for 4/15
            dList = Arrays.asList(fund_prices);
            Collections.shuffle(dList);
            dList.toArray(fund_prices);
            for (int i = 0; i < fund_symbols.length; i++) {
                stmt.setString(1, fund_symbols[i]);
                stmt.setDouble(2, fund_prices[i]);
                stmt.setDate(3, Date.valueOf("2021-04-15"));
                stmt.addBatch();
            }
            // Insert tuples transactionally
            stmt.executeBatch();
            c.commit();

            // Insert tuples into TRXLOG table
            stmt = c.prepareStatement("INSERT INTO trxlog(trx_id,login,symbol,t_date,action,num_shares,price,amount) "
                    + "VALUES(DEFAULT, ?, ?,?,?,?,?,?);");
            String trxLogins[] = new String[] { "John", "John", "mary" };
            String trxSymbols[] = new String[] { "AMZ", "IMS", "AS" };
            String trxt_date[] = new String[] { "2020-04-04", "2020-05-04", "2020-06-04" };
            String trxAction[] = new String[] { "deposit", "sell", "buy" };
            int trxNumShares[] = new int[] { 1, 6, 7 };
            float trxPrice[] = new float[] { 15, 11, 12 };
            float trxAmount[] = new float[] { 10, 66, 84 };

            // Set values of prepared statement
            for (int i = 0; i < trxt_date.length; i++) {
                stmt.setString(1, trxLogins[i]);
                stmt.setString(2, trxSymbols[i]);
                stmt.setDate(3, Date.valueOf(trxt_date[i]));
                stmt.setString(4, trxAction[i]);
                stmt.setInt(5, trxNumShares[i]);
                stmt.setFloat(6, trxPrice[i]);
                stmt.setFloat(7, trxAmount[i]);
                stmt.addBatch();
            }
            // Insert tuples transactionally
            stmt.executeBatch();
            c.commit();

            // Insert tuples into owns table
            stmt = c.prepareStatement("INSERT INTO owns(login,symbol, shares)" + "VALUES(?, ?, ?);");
            String ownsLogins[] = new String[] { "John", "John", "mary", "timothy", "John", "mike", "timothy", "John" };
            String ownsSymbols[] = new String[] { "AMZ", "IMS", "AS", "LTB", "STB", "GS", "IMS", "BBS" };
            int ownsQuantities[] = new int[] { 10, 14, 2, 3, 63, 2, 5, 6 };
            // Set values of prepared statement
            for (int i = 0; i < ownsLogins.length; i++) {
                stmt.setString(1, ownsLogins[i]);
                stmt.setString(2, ownsSymbols[i]);
                stmt.setInt(3, ownsQuantities[i]);
                stmt.addBatch();
            }
            // Insert tuples transactionally
            stmt.executeBatch();
            c.commit();

            // Insert tuples into ALLOCATION table
            stmt = c.prepareStatement("INSERT INTO allocation(allocation_no, login, p_date)" + "VALUES(?, ?, ?);");

            stmt.setInt(1, 1);
            stmt.setString(2, "timothy");
            stmt.setDate(3, Date.valueOf("2021-04-16"));
            stmt.addBatch();

            stmt.setInt(1, 2);
            stmt.setString(2, "timothy");
            stmt.setDate(3, Date.valueOf("2021-04-17"));
            stmt.addBatch();

            // Insert tuples transactionally
            stmt.executeBatch();
            c.commit();

            // Insert tuples into prefers table
            stmt = c.prepareStatement("INSERT INTO PREFERS(allocation_no, symbol, percentage)" + "VALUES(?, ?, ?);");

            stmt.setInt(1, 1);
            stmt.setString(2, "AMZ");
            stmt.setDouble(3, 0.5);
            stmt.addBatch();

            stmt.setInt(1, 1);
            stmt.setString(2, "AS");
            stmt.setDouble(3, 0.5);
            stmt.addBatch();

            stmt.setInt(1, 2);
            stmt.setString(2, "AMZ");
            stmt.setDouble(3, 0.75);
            stmt.addBatch();

            stmt.setInt(1, 2);
            stmt.setString(2, "AS");
            stmt.setDouble(3, 0.25);
            stmt.addBatch();

            // Insert tuples transactionally
            stmt.executeBatch();
            c.commit();

        } catch (SQLException e1) {
            // Attempt to roll back changes if an error occurs
            try {
                c.rollback();
            } catch (SQLException e2) {
                // Exception handling
                System.out.println("SQL Error rolling back transaction");
                while (e2 != null) {
                    System.out.println("Message = " + e2.getMessage());
                    System.out.println("SQLState = " + e2.getSQLState());
                    System.out.println("SQL Code = " + e2.getErrorCode());
                    e2 = e2.getNextException();
                }
            }
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        } finally {
            // Re-enable auto-commit and reset isolation level after transaction regardless
            // of failure/success
            try {
                c.setAutoCommit(true);
                c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } catch (SQLException e3) {
                // Exception handling
                System.out.println("SQL Error resetting auto commit and isolation level");
                while (e3 != null) {
                    System.out.println("Message = " + e3.getMessage());
                    System.out.println("SQLState = " + e3.getSQLState());
                    System.out.println("SQL Code = " + e3.getErrorCode());
                    e3 = e3.getNextException();
                }
            }
        }
    }

    /**
     * Tests admin panel funciton 1
     * 
     * @param c SQL connection
     */
    private static void testAdmin1(Connection c) {
        System.out.println("Testing admin panel function 1...\n");
        System.out.println("Current tuples in customer table:");
        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM customer");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            // Drop all tables with admin panel funciton
            System.out.println("\nDropping tables...");
            BetterFutureInterface.admin1(c);
            System.out.println("\nTuples in customer table after function:");

            // Print state of the customer table after calling the admin panel function
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    /**
     * Tests admin panel funciton 2
     * 
     * @param c SQL connection
     */
    private static void testAdmin2(Connection c) {
        System.out.println("Testing admin panel function 2...\n");
        System.out.println("Current tuples in customer table:");
        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM customer");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            // Example of error handling
            System.out.println(
                    "\nAttempting to insert tuple with bad balance(t_login, t_name, test@test.com, t_addr, t_pq, not_a_number!)");
            BetterFutureInterface.admin2(c, "t_login", "t_name", "test@test.com", "t_addr", "t_pw", "not_a_number!");

            // Use BetterFuture to insert tuples
            System.out.println("\nInserting new tuple with values(t_login, t_name, test@test.com, t_addr, t_pw, 1000)");

            BetterFutureInterface.admin2(c, "t_login", "t_name", "test@test.com", "t_addr", "t_pw", "1000.0");

            // Print state of customer table after insert
            System.out.println("Tuples in customer after admin2:");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests admin panel funciton 3
     * 
     * @param c SQL connection
     */
    private static void testAdmin3(Connection c) {
        System.out.println("Testing admin panel function 3...\n");
        System.out.println("Current tuples in mutual fund table:");
        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM mutual_fund");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            // Example of error handling
            System.out.println("\nInserting bad tuple with values(TST, test, test fund, bad_category)");

            BetterFutureInterface.admin3(c, "TST", "test", "test fund", "bad_cat");

            // Use BetterFuture to insert tuples
            System.out.println("\nInserting new tuple with values(TST, test, test fund, fixed)");

            BetterFutureInterface.admin3(c, "TST", "test", "test fund", "fixed");
            System.out.println();
            // Print state of mutualfund table after insert
            System.out.println("Tuples in mutualfund after admin3:");
            stmt = c.prepareStatement("SELECT * FROM mutual_fund");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests admin panel funciton 4
     * 
     * @param c SQL connection
     */
    private static void testAdmin4(Connection c) {
        System.out.println("Testing admin panel function 4...\n");
        System.out.println("Current closing prices:");
        try {
            // Print intial state of closing price table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM closing_price ORDER BY p_date DESC");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            // Test exception handling
            System.out.println("\nAttempting to insert data from non-existant file not_a_file.txt");
            BetterFutureInterface.admin4(c, "not_a_file.txt");

            // Use BetterFuture to update prices
            System.out.println(
                    "\nUpdating prices from file fund_prices.txt. This will update funds MM, RE, STB, LTB, BBS, and SRBS");
            System.out.println();
            BetterFutureInterface.admin4(c, "fund_prices.txt");

            // Print state of closing price table after update
            System.out.println("\nTuples in closing_price after admin function 4:");
            stmt = c.prepareStatement("SELECT * FROM closing_price ORDER BY p_date DESC");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests admin panel funciton 5
     * 
     * @param c SQL connection
     */
    private static void testAdmin5(Connection c) {
        System.out.println("Testing admin panel function 5...\n");
        System.out.println("Current volume of based on stock categories:");
        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement(
                    "SELECT login, owns.symbol, shares, category FROM owns JOIN mutual_fund ON owns.symbol = mutual_fund.symbol");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            // Test exception handling
            System.out.println("\nAttempting to display top not_a_number categories:");
            BetterFutureInterface.admin5(c, "not_a_number");
            System.out.println();
            // Test actual function
            BetterFutureInterface.admin5(c, "2");

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests admin panel funciton 6
     * 
     * @param c SQL connection
     */
    private static void testAdmin6(Connection c) {
        System.out.println("Testing admin panel function 6...\n");
        System.out.println("Current price information for 2021-04-19:");
        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement(
                    "SELECT login, owns.symbol, shares, price, p_date FROM owns JOIN closing_price ON owns.symbol = closing_price.symbol WHERE p_date = '2021-04-19'");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            BetterFutureInterface.admin6(c);

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests admin panel funciton 7
     * 
     * @param c SQL connection
     */
    private static void testAdmin7(Connection c) {
        System.out.println("Testing admin panel function 7...\n");
        System.out.println("Current date in auxilary table:");
        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM mutual_date");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            System.out.println("\nAttempting to insert bad date 2021-02-40");
            BetterFutureInterface.admin7(c, "2021-02-40");
            System.out.println("\nChanging date to 2021-04-20");
            BetterFutureInterface.admin7(c, "2021-04-20");
            System.out.println();
            // Print state of mutualfund table after insert
            System.out.println("New date in auxilary table:");
            stmt = c.prepareStatement("SELECT * FROM mutual_date");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel funciton 1
     * 
     * @param c Connection to the database
     */
    private static void testCustomer1(Connection c) {
        System.out.println("Testing customer panel function 1...\n");
        System.out.println("Current data in customer table:");

        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM customer");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            // Use BetterFuture to insert tuples
            System.out.println("\nCurrent data in owns table:");

            // Print intial state of owns table
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            System.out.println("\nResult of calling customer function 1 for user John:");
            BetterFutureInterface.customer1(c, "John");

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 2
     * 
     * @param c Connection to the database
     */
    private static void testCustomer2(Connection c) {
        System.out.println("Testing customer panel function 2...\n");
        System.out.println("Current data in mutual fund table(unordered):");

        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM mutual_fund");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            System.out.println("\nOrdered mutual funds when calling customer 2 function:");
            BetterFutureInterface.customer2(c);

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 3
     * 
     * @param c Connection to the database
     */
    private static void testCustomer3(Connection c) {
        System.out.println("Testing customer panel function 3...\n");
        System.out.println("Stocks owned by user John:");

        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM owns WHERE login = 'John'");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("All closing prices on 2021-04-19:");
            // Print all closing prices on the given date
            stmt = c.prepareStatement("SELECT * FROM closing_price WHERE p_date = ?");
            Date cust3Date = Date.valueOf("2021-04-19");
            stmt.setDate(1, cust3Date);
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

            System.out.println("\nAttempting to get funds for date in the future 2029-04-23.");
            BetterFutureInterface.customer3(c, "2029-04-23", "John");

            System.out.println("\nAttempting to get funds for date in the past 2010-04-23");
            BetterFutureInterface.customer3(c, "2010-04-23", "John");

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 4
     * 
     * @param c Connection to the database
     */
    private static void testCustomer4(Connection c) {
        System.out.println("Testing customer panel function 4...\n");
        System.out.println("Mutual funds in the database:");

        try {
            // Print intial state of customer table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM mutual_fund");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            BetterFutureInterface.customer4(c, "term", "bond");

            System.out.println("\nAttempting to search for terms not_a_term1 and not_a_term2");
            BetterFutureInterface.customer4(c, "not_a_term1", "not_a_term2");

            System.out.println();
            BetterFutureInterface.customer4(c, "money", "");

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 5
     * 
     * @param c Connection to the database
     */
    private static void testCustomer5(Connection c) {
        System.out.println("Testing customer panel function 5...\n");
        try {
            // Print initial state of trxlog table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM trxlog");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            BetterFutureInterface.customer5(c, "mike", 50, 1);
            System.out.println();
            // Print after state of trxlog
            stmt = c.prepareStatement("SELECT * FROM trxlog");
            ResultSet r2 = stmt.executeQuery();
            ResultSetMetaData rsmd2 = r2.getMetaData();
            while (r2.next()) {
                for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r2.getString(i);
                    System.out.print(rsmd2.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 6a
     * 
     * @param c Connection to the database
     */
    private static void testCustomer6a(Connection c) {
        System.out.println("Testing customer panel function 6a...\n");
        try {
            // Print intial state of customer table
            System.out.println("Customer");
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM customer");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print intial state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Timothy buying 20 shares AMZ");
            BetterFutureInterface.customer6a(c, "timothy", "AMZ", 20);
            System.out.println();
            // Print after state of customer table
            System.out.println("Customer");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print after state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Timothy buying 2000 shares AMZ - insufficient funds");
            BetterFutureInterface.customer6a(c, "timothy", "AMZ", 2000);
            System.out.println();
            // Print after state of customer table
            System.out.println("Customer");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print after state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    /**
     * Tests customer panel function 6b
     * 
     * @param c Connection to the database
     */
    private static void testCustomer6b(Connection c) {
        System.out.println("Testing customer panel function 6b...\n");
        try {
            // Print intial state of customer table
            System.out.println("Customer");
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM customer");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print intial state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Timothy buying ~$2000 worth of shares in AMZ");
            BetterFutureInterface.customer6b(c, "timothy", "AMZ", 2000.0);
            System.out.println();
            // Print after state of customer table
            System.out.println("Customer");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print after state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Timothy buying ~$200000 worth of shares in AMZ - insufficent funds");
            BetterFutureInterface.customer6b(c, "timothy", "AMZ", 200000.0);
            System.out.println();
            // Print after state of customer table
            System.out.println("Customer");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print after state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    /**
     * Tests customer panel function 7
     * 
     * @param c Connection to the database
     */
    private static void testCustomer7(Connection c) {
        System.out.println("Testing customer panel function 7...\n");
        try {
            // Print intial state of customer table
            System.out.println("Customer");
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM customer");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print intial state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Timothy selling 20 shares AMZ");
            BetterFutureInterface.customer7(c, "timothy", "AMZ", 20);
            System.out.println();
            // Print after state of customer table
            System.out.println("Customer");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print after state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Timothy selling 2000 shares AMZ - not enough shares");
            BetterFutureInterface.customer7(c, "timothy", "AMZ", 2000);
            System.out.println();
            // Print after state of customer table
            System.out.println("Customer");
            stmt = c.prepareStatement("SELECT * FROM customer");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            // Print after state of owns table
            System.out.println("Owns");
            stmt = c.prepareStatement("SELECT * FROM owns");
            r = stmt.executeQuery();
            rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 8
     * 
     * @param c Connection to the database
     */
    private static void testCustomer8(Connection c) {
        System.out.println("Testing customer panel function 8...\n");
        try {
            System.out.println("Timothy's shares");
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM owns WHERE login = \'timothy\'");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Buying AS");
            BetterFutureInterface.customer6a(c, "timothy", "AS", 20);
            System.out.println();
            System.out.println("Raising price of AS enough to cause sale by trigger (32 -> 64)");
            stmt = c.prepareStatement("insert into closing_price(symbol,price,p_date) values (?, ?, ?);");
            stmt.setString(1, "AS");
            stmt.setDouble(2, 64.0);
            stmt.setDate(3, Date.valueOf("2021-04-19"));
            stmt.execute();
            System.out.println();
            System.out.println("Expect AS to have ROI of 1.0");
            BetterFutureInterface.customer8(c, "timothy");
        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    /*
     * Tests customer panel function 9
     * 
     * @param c Connection to the database
     */
    private static void testCustomer9(Connection c) {
        System.out.println("Testing customer panel function 9...\n");
        BetterFutureInterface.customer9(c, "timothy");
        BetterFutureInterface.customer9(c, "mike");

    }

    /*
     * Tests customer panel function 10
     * 
     * @param c Connection to the database
     */
    private static void testCustomer10(Connection c) {
        System.out.println("Testing customer panel 10");

        System.out.println("Prefers");
        try {
            // Print before state of Prefers Table
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM PREFERS");
            ResultSet r = stmt.executeQuery();
            ResultSetMetaData rsmd = r.getMetaData();
            while (r.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
            String[] array = new String[3];
            array[0] = "MM,RE";
            array[1] = "60";
            array[2] = "40";
            System.out.println("\nChanging allocation preferences to .6 for MM and .4 for RE");
            BetterFutureInterface.customer10(c, "timothy", true, array);
            System.out.println();
            // Print after state of trxlog
            stmt = c.prepareStatement("SELECT * FROM PREFERS");
            ResultSet r2 = stmt.executeQuery();
            ResultSetMetaData rsmd2 = r2.getMetaData();
            while (r2.next()) {
                for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnValue = r2.getString(i);
                    System.out.print(rsmd2.getColumnName(i) + ": " + columnValue);
                }
                System.out.println();
            }
        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Tests customer panel function 11
     * 
     * @param c Connection to the database
     */
    private static void testCustomer11(Connection c) {
        System.out.println("Testing customer panel function 11...\n");
        System.out.println("NOTE: This is different each time ran due randomized closing prices for each day\n");
        BetterFutureInterface.customer11(c, "timothy");
    }

    /**
     * Tests customer panel function 12
     * 
     * @param c Connection to the database
     */
    private static void testCustomer12(Connection c) {
        System.out.println("Testing customer panel function 12...\n");
        System.out.println("NOTE: Due to how this test is written some shares are owned having never been bought\n");
        BetterFutureInterface.customer12(c, "timothy");
    }
}