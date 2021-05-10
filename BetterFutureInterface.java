//package src;

import java.io.File;
import java.io.FileNotFoundException;
/*CS1555 Team 7
Stefon Miller SMM248, Aaron Mathew aam150, Adam Buchinsky asb153
*/
import java.sql.*;
import java.util.*;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;

public class BetterFutureInterface {
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

        Scanner in = new Scanner(System.in);

        // Have user login
        System.out.println("Welcome to Better Future!");
        System.out.println("Please enter your username:");
        String loginUserName = in.nextLine();
        System.out.println("Please enter your password");
        String loginPassword = in.nextLine();
        int panel = userLogin(conn, loginUserName, loginPassword);
        while (true) {
            switch (panel) {
            case 1:
                // Admin Options
                generateAdminMenu();
                int adminSelect = 0;
                // Ensure the user enters a valid number
                try {
                    adminSelect = in.nextInt();
                } catch (InputMismatchException ime) {
                    // Do nothing if we catch the error as our switch statement will take care of
                    // it.
                }
                // Get rid of \n not consumed by nextInt()
                in.nextLine();
                switch (adminSelect) {
                // Delete all tuples from all tables
                case 1:
                    // Confirm data erasure
                    System.out.println("Are you sure you want to erase all data?");
                    String confirmation = in.nextLine();
                    // Delete tables if user confirms
                    if (confirmation.equalsIgnoreCase("y") || confirmation.equalsIgnoreCase("yes")) {
                        admin1(conn);

                    }
                    break;
                // Insert new customer into database
                case 2:
                    System.out.println("Please enter the following");
                    System.out.println("Login:");
                    String login = in.nextLine();
                    System.out.println("Name:");
                    String customerName = in.nextLine();
                    System.out.println("Email:");
                    String email = in.nextLine();
                    System.out.println("Password:");
                    String password = in.nextLine();
                    System.out.println("Address:");
                    String address = in.nextLine();
                    // Read balance in as a string and test if it is empty. If so, leave the initial
                    // balance as 0
                    System.out.println("Initial Balance(Optional):");
                    String strBalance = in.nextLine();
                    admin2(conn, login, customerName, email, password, address, strBalance);

                    break;
                case 3:
                    System.out.println("Please enter the following");
                    System.out.println("Symbol:");
                    String symbol = in.nextLine();
                    System.out.println("Fund name:");
                    String fundName = in.nextLine();
                    System.out.println("Description:");
                    String description = in.nextLine();
                    System.out.println("Category:");
                    String category = in.nextLine();
                    admin3(conn, symbol, fundName, description, category);
                    break;
                case 4:
                    // Get file name containing prices
                    System.out.println("Please enter a file name containing fund prices:");
                    String fundFileName = in.nextLine();
                    File fundFile = new File(fundFileName);
                    admin4(conn, fundFileName);
                    break;
                case 5:
                    System.out.println("Please enter a k value:");
                    String kValue = in.nextLine();
                    admin5(conn, kValue);
                    break;
                case 6:
                    admin6(conn);
                    break;
                case 7:
                    System.out.println("Please enter a date(YYYY-MM-DD):");
                    String inputDate = in.nextLine();
                    admin7(conn, inputDate);
                    break;
                case 8:
                    System.out.println("Thank you for using BetterFuture!");
                    System.exit(0);
                default:
                    System.out.println("Invalid command. Please try again");
                    break;
                }
                break;

            case 2:
                // Customer Options
                generateCustomerMenu();
                int customerSelect = 0;
                // Ensure the user enters a valid number
                try {
                    customerSelect = in.nextInt();
                } catch (InputMismatchException ime) {
                    // Do nothing if we catch the error as our switch statement will take care of
                    // it.
                }
                // Consume \n leftover from nextInt()
                in.nextLine();
                switch (customerSelect) {
                case 1:
                    customer1(conn, loginUserName);
                    break;
                case 2:
                    customer2(conn);
                    break;
                case 3:
                    System.out.println("Please enter a date(YYYY-MM-DD):");
                    String iDate = in.nextLine();
                    customer3(conn, iDate, loginUserName);
                    break;
                case 4:
                    // Get up to 2 keywords based on user input. Split it into 2 prompts in case
                    // user wants to search for words w/ commas, etc.
                    System.out.println("Please enter a first keyword to search for:");
                    String keyword1 = in.nextLine();
                    System.out.println("Please enter a second keyword to search for(optional):");
                    String keyword2 = in.nextLine();
                    if (keyword1.isEmpty() && keyword2.isEmpty()) {
                        System.out.println("No keywords entered.");
                    } else {
                        customer4(conn, keyword1, keyword2);
                    }
                    break;
                case 5:
                    System.out.println("Please enter the amount you wish to deposit:");
                    try {
                        float deposit = in.nextFloat();
                        in.nextLine();
                        if (deposit <= 0) {
                            System.out.println("Deposit must be positive and nonzero. Please try again");
                        } else {
                            System.out.println("1. Yes, proceed");
                            System.out.println("2. No, keep my current balance");
                            int option = in.nextInt();
                            customer5(conn, loginUserName, deposit, option);
                        }
                    } catch (InputMismatchException ime) {
                        System.out.println("Please enter an integer");
                    }
                    break;
                case 6:
                    try {
                        // Buy shares
                        System.out.println("Would you like to buy shares by: 1)number of shares or 2)amount?");
                        int choice = in.nextInt();
                        in.nextLine(); // clear newline
                        if (choice == 1) {
                            // number of shares
                            System.out.println("What share would you like to buy?");
                            String symbol = in.nextLine();
                            System.out.println("How many would you like to buy?");
                            int numShares = in.nextInt();
                            in.nextLine(); // clear newline

                            customer6a(conn, loginUserName, symbol, numShares);
                        } else {
                            // amount
                            System.out.println("What share would you like to buy?");
                            String symbol = in.nextLine();
                            System.out.println("How much would you like to spend?");
                            Double amount = in.nextDouble();
                            in.nextLine(); // clear newline

                            customer6b(conn, loginUserName, symbol, amount);
                        }

                    } catch (InputMismatchException ime) {
                        System.out.println("Invalid data entered, please try again");
                        break;
                    }

                    break;
                case 7:
                    // 7 Sell Shares
                    try {
                        System.out.println("What share would you like to sell?");
                        String symbol = in.nextLine();
                        System.out.println("How many would you like to sell?");
                        int numShares = in.nextInt();
                        in.nextLine(); // clear newline
                        customer7(conn, loginUserName, symbol, numShares);
                    } catch (InputMismatchException ime) {
                        System.out.println("Invalid data entered, please try again");
                        break;
                    }

                    break;
                case 8:
                    customer8(conn, loginUserName);
                    break;
                case 9:
                    customer9(conn, loginUserName);
                    break;
                case 10:
                    String[] empty = new String[0];
                    customer10(conn, loginUserName, false, empty);
                    break;
                case 11:
                    customer11(conn, loginUserName);
                    break;
                case 12:
                    customer12(conn, loginUserName);
                    break;
                case 13:
                    System.out.println("Thank you for using BetterFuture!");
                    System.exit(0);
                default:
                    System.out.println("Invalid command");
                    break;
                }
                break;
            case 3:
                System.out.println("Invalid credentials entered. Please try again");
                System.out.println("Please enter your username:");
                loginUserName = in.nextLine();
                System.out.println("Please enter your password");
                loginPassword = in.nextLine();
                panel = userLogin(conn, loginUserName, loginPassword);
                break;
            }

        }
    }

    /**
     * Verifies user's login credentials
     * 
     * @param c
     */
    private static int userLogin(Connection c, String un, String pw) {
        try {
            // Determine if the user is an admin
            PreparedStatement stmt = c
                    .prepareStatement("SELECT login FROM administrator WHERE (login = ? AND password = ?);");
            stmt.setString(1, un);
            stmt.setString(2, pw);
            ResultSet res = stmt.executeQuery();
            // If an admin exists with the given credentials, return 1
            if (res.isBeforeFirst()) {
                return 1;
            }

            // Determine if the user is a customer
            stmt = c.prepareStatement("SELECT login FROM customer WHERE (login = ? AND password = ?);");
            stmt.setString(1, un);
            stmt.setString(2, pw);
            res = stmt.executeQuery();
            // If a customer exists with the given credentials, return 2
            if (res.isBeforeFirst()) {
                return 2;
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
        // Return 3 if invlaid credentials
        return 3;
    }

    /**
     * Deletes all tuples in the database
     * 
     * @param c Connection to the database
     */
    public static void admin1(Connection c) {
        try {
            // Call procedure to delete all tuples in database
            CallableStatement stmt = c.prepareCall("CALL delete_from_db();");
            stmt.execute();

            System.out.println("Data erased.");

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
     * Adds a user to the database.
     * 
     * @param c   Connection to the database
     * @param l   New user login
     * @param n   New user name
     * @param e   New user email
     * @param p   New user password
     * @param a   New user address
     * @param bal New user balance
     */
    public static void admin2(Connection c, String l, String n, String e, String a, String p, String bal) {
        // Ensure user entered a number for the balance. If the balance string is empty
        // we
        Double b = 0.0;
        if (!bal.isEmpty()) {
            try {
                b = Double.parseDouble(bal);
            } catch (NumberFormatException nfe) {
                // If the user didn't enter a number, throw an error message
                System.out.println("Please enter a valid number for the balance");
                return;
            }
        }

        try {
            PreparedStatement stmt = c.prepareStatement(
                    "INSERT INTO customer(login,name,email,address,password,balance)" + "VALUES(?, ?, ?, ?, ?, ?);");
            // Set values of prepared statement
            stmt.setString(1, l);
            stmt.setString(2, n);
            stmt.setString(3, e);
            stmt.setString(4, a);
            stmt.setString(5, p);
            stmt.setDouble(6, b);
            // Insert tuples transactionally
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            stmt.executeUpdate();
            c.commit();

            System.out.println("User successfully added.");
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
     * Creates a new mutual fund
     * 
     * @param c   Connection to the database
     * @param s   Mutual fund symbol
     * @param n   Mutual fund name
     * @param d   Mutual fund description
     * @param cat Mutual fund category
     */
    public static void admin3(Connection c, String s, String n, String d, String cat) {
        try {
            // Get current date from mutual_date table
            PreparedStatement stmt = c.prepareStatement("SELECT p_date FROM mutual_date;");
            ResultSet res = stmt.executeQuery();
            Date cDate = null;
            while (res.next()) {
                cDate = res.getDate(1);
            }

            stmt = c.prepareStatement(
                    "INSERT INTO MUTUAL_FUND(symbol, name, description, category, c_date)" + "VALUES(?, ?, ?, ?, ?);");
            // Set values of prepared statement
            stmt.setString(1, s);
            stmt.setString(2, n);
            stmt.setString(3, d);
            stmt.setString(4, cat);
            stmt.setDate(5, cDate);
            // Insert tuple in a transaction
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            stmt.executeUpdate();
            c.commit();
            System.out.println("Mutual fund successfully added");
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
            System.out.println("SQL Error creating a new fund");
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
     * Updates mutual fun prices
     * 
     * @param c  Connection to the database
     * @param fn File name to get fund prices from
     */
    public static void admin4(Connection c, String fn) {
        // Ensure filename is valid
        // Check if file exists before calling admin4
        File f = new File(fn);
        if (!f.exists()) {
            System.out.println("File " + fn + " not found. Please try again.");
            return;
        }
        try {
            // Get current date from mutual_date table
            PreparedStatement stmt = c.prepareStatement("SELECT p_date FROM mutual_date;");
            ResultSet res = stmt.executeQuery();
            Date cDate = null;
            while (res.next()) {
                cDate = res.getDate(1);
            }
            // Create scanner to read in file
            Scanner fr = null;
            try {
                fr = new Scanner(f);
            } catch (FileNotFoundException fne) {
                System.out.println("Fund file not found.");
                return;
            }
            // Dictionary for funds and their prices
            Hashtable<String, Double> funds = new Hashtable<String, Double>();
            while (fr.hasNextLine()) {
                String line = fr.nextLine();
                // Set the fund to all text before the first comma. This allows for funds with
                // prices that contain commas(1,000+)
                String fund = line.substring(0, line.indexOf(","));
                // Parse number potentially containing commas to a double
                NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
                Number n = null;
                try {

                    n = format.parse(line.substring(line.indexOf(",") + 1, line.length()));
                } catch (ParseException pe) {
                    System.out.println("Your file has an invalid price for stock " + fund);
                    return;
                }
                Double price = n.doubleValue();
                // Store fund and price in a dictionary
                funds.put(fund, price);
            }
            fr.close();
            // Insert tuples if they do not already exist for the given day. If they do,
            // then simply update the price.
            stmt = c.prepareStatement(
                    "INSERT INTO closing_price(symbol, price, p_date) VALUES(?, ?, ?) ON CONFLICT ON CONSTRAINT CLOSING_PRICE_PK DO UPDATE SET price = ?;");

            // Insert tuples in a transaction
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Set<String> fundNames = funds.keySet();
            for (String name : fundNames) {
                // Set values for each fund and do a batch insert after looping through all
                // values in the dictionary
                stmt.setString(1, name);
                stmt.setDouble(2, funds.get(name));
                stmt.setDate(3, cDate);
                stmt.setDouble(4, funds.get(name));
                stmt.addBatch();
            }
            stmt.executeBatch();
            c.commit();
            System.out.println("Mutual fund prices successfully updated.");
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
            System.out.println("SQL Error updating fund prices");
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
     * Displays top k categories based on volume
     * 
     * @param c  Connection to the database
     * @param kv Number of tuples to fetch
     */
    public static void admin5(Connection c, String kv) {
        // Ensure user entered a valid k value
        int k = 0;
        try {
            k = Integer.parseInt(kv);
            if (k <= 0) {
                System.out.println("k value must be positive and nonzero. Please try again");
                return;
            }
        } catch (NumberFormatException nfre) {
            System.out.println("Please enter an integer for the k value");
            return;
        }
        try {
            // Prepare statement and assign the k value
            PreparedStatement stmt = c.prepareStatement(
                    "SELECT category, SUM(shares) AS total_owned FROM owns JOIN mutual_fund ON owns.symbol = mutual_fund.symbol GROUP BY category ORDER BY SUM(shares) DESC FETCH FIRST ? ROWS ONLY;");
            stmt.setInt(1, k);
            System.out.println("Displaying top " + k + " categories based on volume:");
            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            // Check if tuples were returned
            if (res.isBeforeFirst()) {
                // If there were tuples returned, display them
                while (res.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (i > 1)
                            System.out.print(",  ");
                        String columnValue = res.getString(i);
                        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                    }
                    System.out.println();
                }
            } else {
                // If no tuples were found, then there are no stocks/owners
                System.out.println("No investors with stocks found in the database");
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
     * Ranks investors based on portfolio value
     * 
     * @param c Connection to the database
     */
    public static void admin6(Connection c) {
        try {
            // Get current most recent date from the transaction table
            PreparedStatement stmt = c
                    .prepareStatement("SELECT p_date FROM closing_price ORDER BY p_date DESC FETCH FIRST ROW ONLY;");
            ResultSet res = stmt.executeQuery();
            Date cDate = null;
            while (res.next()) {
                cDate = res.getDate(1);
            }

            stmt = c.prepareStatement(
                    "SELECT login, SUM(price * shares) AS portfolio_value FROM owns JOIN closing_price ON owns.symbol = closing_price.symbol WHERE closing_price.p_date = ? GROUP BY login ORDER BY SUM(price * shares) DESC;");

            System.out.println("Displaying ranked list of all portfolios as of " + cDate.toString() + ":");

            // Set value of prepared statement
            stmt.setDate(1, cDate);
            res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            // Check if tuples were returned
            if (res.isBeforeFirst()) {
                // If there were tuples returned, display them
                while (res.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (i > 1)
                            System.out.print(",  ");
                        String columnValue = res.getString(i);
                        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                    }
                    System.out.println();
                }
            } else {
                // If no tuples were found, then the db doesn't have any investors
                // or they own no stocks
                System.out.println("No investors with stocks found in the database");
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
     * Creates a new mutual fund
     * 
     * @param c      Connection to the database
     * @param chDate Date to update to
     */
    public static void admin7(Connection c, String chDate) {
        // Ensure input date is valid
        Date cDate = null;
        try {
            cDate = Date.valueOf(chDate);
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }
        try {
            // Prepare statement to update the date in mutual_date
            PreparedStatement stmt = c.prepareStatement("UPDATE mutual_date SET p_date = ?;");
            stmt.setDate(1, cDate);
            c.setAutoCommit(false);
            // Set isolation level of transaction to avoid inconsistent states
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            stmt.executeUpdate();
            c.commit();

            System.out.println("Date changed to " + cDate.toString());
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
     * Displays the customer's name, shares, and balance
     * 
     * @param c  Connection to the database
     * @param un Customer's username
     */
    public static void customer1(Connection c, String un) {
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT name, balance, SUM(shares) AS shares "
                    + "FROM customer JOIN owns ON CUSTOMER.login = OWNS.login "
                    + "WHERE CUSTOMER.login = ? GROUP BY name, balance;");
            stmt.setString(1, un);

            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            // Check if the user has shares
            if (res.isBeforeFirst()) {
                // If the user owns shares, display the data
                while (res.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (i > 1)
                            System.out.print(",  ");
                        String columnValue = res.getString(i);
                        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                    }
                    System.out.println();
                }
            } else {
                // If no tuples were found, then the user doesn't have shares since their login
                // info was previously validated
                System.out.println("No shares found for " + un);
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
     * Displays the customer's name, shares, and balance
     * 
     * @param c  Connection to the database
     * @param un Customer's username
     */
    public static void customer2(Connection c) {
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM mutual_fund GROUP BY symbol ORDER BY name ASC;");

            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            // Check if there are mutual funds in the database
            if (res.isBeforeFirst()) {
                // If the user owns shares, display the data
                while (res.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (i > 1)
                            System.out.print(",  ");
                        String columnValue = res.getString(i);
                        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                    }
                    System.out.println();
                }
            } else {
                // If no tuples were found, then the database doesn't have any funds
                System.out.println("No funds found in the database");
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
     * Displays mutual fund price information on a given date
     * 
     * @param c   Connection to the database
     * @param dte Date to look up
     */
    public static void customer3(Connection c, String dte, String un) {
        Date d = null;
        try {
            d = Date.valueOf(dte);
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid date format. Please try again.");
            return;
        }
        try {
            // Check if the user entered a valid date. If their date is before our records
            // start of in the future, supply the earliest and latest
            // dates from the closing_price table respectively.
            PreparedStatement stmt = c.prepareStatement("SELECT get_closest_date(?);");
            stmt.setDate(1, d);
            ResultSet res = stmt.executeQuery();
            // Update our date to the closest matching one
            while (res.next()) {
                d = res.getDate(1);
            }

            // Get the result set of all funds on the given day
            stmt = c.prepareStatement(
                    "SELECT * FROM MUTUAL_FUND JOIN closing_price ON mutual_fund.symbol = closing_price.symbol WHERE p_date = ? ORDER BY price DESC;");

            stmt.setDate(1, d);
            res = stmt.executeQuery();

            // Get the result set of all funds owned by the user
            stmt = c.prepareStatement("SELECT symbol FROM owns WHERE login = ?;");
            stmt.setString(1, un);
            ResultSet res2 = stmt.executeQuery();
            // Add all user stocks to an arraylist
            ArrayList<String> userStocks = new ArrayList<String>();
            while (res2.next()) {
                userStocks.add(res2.getString(1));
            }
            System.out.println("\nDisplaying all stocks on " + d.toString() + ". Stocks owned by user " + un
                    + " are denoted by **");
            ResultSetMetaData rsmd = res.getMetaData();
            // Check if there are mutual funds for the given date
            if (res.isBeforeFirst()) {
                // If there are funds for the given date, display them
                while (res.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        if (i > 1)
                            System.out.print(",  ");
                        // Mark stock if the user owns it
                        String columnValue = res.getString(i);
                        if (i == 1 && userStocks.contains(columnValue)) {
                            System.out.print("**" + rsmd.getColumnName(i) + ": " + columnValue);
                        } else {
                            System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                        }
                    }
                    System.out.println();
                }
            } else {
                // If no tuples were found, then the database doesn't have any funds for the
                // given date
                System.out.println("No funds found for the given date");
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
     * Searches for a stock that contains all keywords
     * 
     * @param c  Connection to the database
     * @param k1 First keyword
     * @param k2 Second keyword
     */
    public static void customer4(Connection c, String k1, String k2) {
        try {
            PreparedStatement stmt = null;
            // Prepare statement based on number of keywords entered by user
            if (!k1.isEmpty() && !k2.isEmpty()) {
                stmt = c.prepareStatement(
                        "SELECT symbol FROM mutual_fund WHERE description LIKE ? AND description LIKE ?;");
                stmt.setString(1, "%" + k1 + "%");
                stmt.setString(2, "%" + k2 + "%");
                System.out.println("\nDisplaying all funds matching keywords '" + k1 + "' and '" + k2 + "':");
            } else {
                stmt = c.prepareStatement("SELECT symbol FROM mutual_fund WHERE description LIKE ?;");
                // Set the string in the statement to whatever value isn't empty
                if (k1.isEmpty()) {
                    stmt.setString(1, "%" + k2 + "%");
                    System.out.println("\nDisplaying all funds matching keyword '" + k2 + "':");
                } else {
                    stmt.setString(1, "%" + k1 + "%");
                    System.out.println("\nDisplaying all funds matching keyword '" + k1 + "':");
                }
            }

            ResultSet res = stmt.executeQuery();
            ResultSetMetaData rsmd = res.getMetaData();
            // Check if there are results to the keyword search
            if (res.isBeforeFirst()) {
                // If there are matches, display them
                ArrayList<String> matches = new ArrayList<String>();
                while (res.next()) {
                    matches.add(res.getString(1));
                }
                System.out.println(matches.toString());
            } else {
                // If no tuples were found, then no funds were found based on the keywords
                System.out.println("No funds found in the database matching your keywords");
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
     * Deposits into account or uses deposit to
     * 
     * @param c      Connection to the database
     * @param u      username
     * @param d      deposit
     * @param option choosing to invest or keep balance
     */
    public static void customer5(Connection c, String u, float d, int option) {
        PreparedStatement stmt;
        CallableStatement func;
        ResultSet res;
        ResultSet r;
        float balance = 0;
        Scanner in = new Scanner(System.in);

        try {

            ArrayList<Float> allocper = new ArrayList<Float>();
            ArrayList<String> allocsym = new ArrayList<String>();
            stmt = c.prepareStatement(
                    "SELECT symbol, percentage from prefers natural join(SELECT allocation_no FROM allocation WHERE p_date =(SELECT max(p_date) FROM allocation WHERE login=? GROUP BY login)) AS alloc");
            stmt.setString(1, u);
            r = stmt.executeQuery();
            while (r.next()) {
                allocsym.add(r.getString("symbol"));
                allocper.add(r.getFloat("percentage"));

            }

            stmt = c.prepareStatement("SELECT balance FROM CUSTOMER WHERE login=?;");
            stmt.setString(1, u);
            res = stmt.executeQuery();
            while (res.next()) {
                balance = res.getFloat("balance");
            }
            stmt.close();
            boolean invalid = true;
            float newBalance = (float) balance + d;
            while (invalid) {
                System.out.println("\nYour current balance is now $" + newBalance);
                System.out.println("Allocation preferences: ");
                if (allocsym.isEmpty()) {
                    System.out.println("You currently have no allocation preferences.");
                } else {
                    for (int i = 0; i < allocsym.size(); i++) {
                        System.out.print("Symbol: " + allocsym.get(i));
                        System.out.println(" Percentage: " + allocper.get(i) * 100 + "%");
                    }
                }

                func = c.prepareCall("CALL deposit_for_investment(?,?)");
                int dep = (int) d;
                func.setString(1, u);
                func.setInt(2, dep);
                func.execute();
                System.out.println("Investment Made!");
                invalid = false;
            }
            stmt = c.prepareStatement(
                    "INSERT into TRXLOG VALUES(DEFAULT,?,NULL,(SELECT p_date FROM MUTUAL_DATE),'deposit',NULL,NULL,?);");
            stmt.setString(1, u);
            stmt.setFloat(2, d);

            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            stmt.executeUpdate();
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

        } finally {
            // Re-enable auto-commit and reset isolation level after transaction regardless
            // of failure/success
            try {
                c.setAutoCommit(true);
                c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } catch (SQLException e4) {
                // Exception handling
                System.out.println("SQL Error resetting auto commit and isolation level");
                while (e4 != null) {
                    System.out.println("Message = " + e4.getMessage());
                    System.out.println("SQLState = " + e4.getSQLState());
                    System.out.println("SQL Code = " + e4.getErrorCode());
                    e4 = e4.getNextException();
                }
            }
        }

    }

    /**
     * Buys shares based on the number of shares
     *
     * @param c         Connection to the database
     * @param un        Username
     * @param sym       The stock symbol
     * @param numShares the number of shares to buy
     */
    public static void customer6a(Connection c, String un, String sym, int numShares) {
        try {
            // Get share price
            PreparedStatement stmt = c.prepareStatement("SELECT price " + "FROM closing_price " + "WHERE symbol = ? "
                    + "ORDER BY p_date DESC " + "FETCH FIRST ROW ONLY;");
            stmt.setString(1, sym);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.printf("%s is not a valid symbol\n", sym);
                return;
            }
            Double price = rs.getDouble("price");

            // Get Customer balance
            stmt = c.prepareStatement("SELECT balance " + "FROM customer " + "WHERE login = ?;");
            stmt.setString(1, un);
            rs = stmt.executeQuery();
            rs.next();
            Double bal = rs.getDouble("balance");

            // Check if sufficient funds
            if (bal >= (price * numShares)) {
                // Sufficient funds
                CallableStatement func = c.prepareCall("{call buy_shares(?, ?, ?)}");
                func.setString(1, un);
                func.setString(2, sym);
                func.setInt(3, numShares);

                try {
                    c.setAutoCommit(false);
                    c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    func.execute();
                    c.commit();

                    System.out.printf("Successfully bought %d shares of %s for $%.2f", numShares, sym,
                            price * numShares);
                    System.out.println();
                } catch (SQLException e2) {
                    // Attempt to roll back changes if an error occurs
                    try {
                        c.rollback();
                    } catch (SQLException e3) {
                        // Exception handling
                        System.out.println("SQL Error rolling back transaction");
                        while (e3 != null) {
                            System.out.println("Message = " + e3.getMessage());
                            System.out.println("SQLState = " + e3.getSQLState());
                            System.out.println("SQL Code = " + e3.getErrorCode());
                            e3 = e3.getNextException();
                        }
                    }
                    // Exception handling
                    System.out.println("SQL Error buying shares");
                    while (e2 != null) {
                        System.out.println("Message = " + e2.getMessage());
                        System.out.println("SQLState = " + e2.getSQLState());
                        System.out.println("SQL Code = " + e2.getErrorCode());
                        e2 = e2.getNextException();
                    }
                } finally {
                    // Re-enable auto-commit and reset isolation level after transaction regardless
                    // of failure/success
                    try {
                        c.setAutoCommit(true);
                        c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    } catch (SQLException e4) {
                        // Exception handling
                        System.out.println("SQL Error resetting auto commit and isolation level");
                        while (e4 != null) {
                            System.out.println("Message = " + e4.getMessage());
                            System.out.println("SQLState = " + e4.getSQLState());
                            System.out.println("SQL Code = " + e4.getErrorCode());
                            e4 = e4.getNextException();
                        }
                    }
                }

            } else {
                // Insufficient funds
                System.out.println("you do not have enough funds to perform this transaction");
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error buying shares");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Buys shares based on the amount to spend
     *
     * @param c      Connection to the database
     * @param sym    The stock symbol
     * @param amount the amount to spend on the shares
     */
    public static void customer6b(Connection c, String un, String sym, Double amount) {
        try {
            // Get share price
            PreparedStatement stmt = c.prepareStatement("SELECT price " + "FROM closing_price " + "WHERE symbol = ? "
                    + "ORDER BY p_date DESC " + "FETCH FIRST ROW ONLY;");
            stmt.setString(1, sym);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.printf("%s is not a valid symbol\n", sym);
                return;
            }
            Double price = rs.getDouble("price");

            // Get Customer balance
            stmt = c.prepareStatement("SELECT balance " + "FROM customer " + "WHERE login = ?;");
            stmt.setString(1, un);
            rs = stmt.executeQuery();
            rs.next();
            Double bal = rs.getDouble("balance");

            // Check if sufficient funds
            if (bal >= amount) {
                // Sufficient funds
                // Determine how many shares could be bought with amount
                int numShares = (int) Math.floor(amount / price);

                CallableStatement func = c.prepareCall("{call buy_shares(?, ?, ?)}");
                func.setString(1, un);
                func.setString(2, sym);
                func.setInt(3, numShares);

                try {
                    c.setAutoCommit(false);
                    c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    func.execute();
                    c.commit();

                    System.out.printf("Successfully bought %d shares of %s for $%.2f", numShares, sym,
                            price * numShares);
                    System.out.println();
                } catch (SQLException e2) {
                    // Attempt to roll back changes if an error occurs
                    try {
                        c.rollback();
                    } catch (SQLException e3) {
                        // Exception handling
                        System.out.println("SQL Error rolling back transaction");
                        while (e3 != null) {
                            System.out.println("Message = " + e3.getMessage());
                            System.out.println("SQLState = " + e3.getSQLState());
                            System.out.println("SQL Code = " + e3.getErrorCode());
                            e3 = e3.getNextException();
                        }
                    }
                    // Exception handling
                    System.out.println("SQL Error buying shares");
                    while (e2 != null) {
                        System.out.println("Message = " + e2.getMessage());
                        System.out.println("SQLState = " + e2.getSQLState());
                        System.out.println("SQL Code = " + e2.getErrorCode());
                        e2 = e2.getNextException();
                    }
                } finally {
                    // Re-enable auto-commit and reset isolation level after transaction regardless
                    // of failure/success
                    try {
                        c.setAutoCommit(true);
                        c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    } catch (SQLException e4) {
                        // Exception handling
                        System.out.println("SQL Error resetting auto commit and isolation level");
                        while (e4 != null) {
                            System.out.println("Message = " + e4.getMessage());
                            System.out.println("SQLState = " + e4.getSQLState());
                            System.out.println("SQL Code = " + e4.getErrorCode());
                            e4 = e4.getNextException();
                        }
                    }
                }

            } else {
                // Insufficient funds
                System.out.println("you do not have enough funds to perform this transaction");
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error buying shares");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    /**
     * Sells shares based on the number of shares
     *
     * @param c         Connection to the database
     * @param un        Username
     * @param sym       The stock symbol
     * @param numShares the number of shares to sell
     */
    public static void customer7(Connection c, String un, String sym, int numShares) {
        // Get number of shares owned by user
        try {
            // Get number of shares
            PreparedStatement stmt = c
                    .prepareStatement("SELECT shares " + "FROM owns " + "WHERE login = ? AND symbol = ?;");
            stmt.setString(1, un);
            stmt.setString(2, sym);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.printf("You do not own any %s\n", sym);
                return;
            }
            int numOwned = rs.getInt("shares");

            if (numOwned >= numShares) {
                // Sufficient shares
                CallableStatement func = c.prepareCall("{call sell_shares(?, ?, ?)}");
                func.setString(1, un);
                func.setString(2, sym);
                func.setInt(3, numShares);

                try {
                    c.setAutoCommit(false);
                    c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    func.execute();
                    c.commit();

                    System.out.printf("Successfully sold %d shares of %s", numShares, sym);
                    System.out.println();
                } catch (SQLException e2) {
                    // Attempt to roll back changes if an error occurs
                    try {
                        c.rollback();
                    } catch (SQLException e3) {
                        // Exception handling
                        System.out.println("SQL Error rolling back transaction");
                        while (e3 != null) {
                            System.out.println("Message = " + e3.getMessage());
                            System.out.println("SQLState = " + e3.getSQLState());
                            System.out.println("SQL Code = " + e3.getErrorCode());
                            e3 = e3.getNextException();
                        }
                    }
                    // Exception handling
                    System.out.println("SQL Error selling shares");
                    while (e2 != null) {
                        System.out.println("Message = " + e2.getMessage());
                        System.out.println("SQLState = " + e2.getSQLState());
                        System.out.println("SQL Code = " + e2.getErrorCode());
                        e2 = e2.getNextException();
                    }
                } finally {
                    // Re-enable auto-commit and reset isolation level after transaction regardless
                    // of failure/success
                    try {
                        c.setAutoCommit(true);
                        c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    } catch (SQLException e4) {
                        // Exception handling
                        System.out.println("SQL Error resetting auto commit and isolation level");
                        while (e4 != null) {
                            System.out.println("Message = " + e4.getMessage());
                            System.out.println("SQLState = " + e4.getSQLState());
                            System.out.println("SQL Code = " + e4.getErrorCode());
                            e4 = e4.getNextException();
                        }
                    }
                }
            } else {
                // Insufficient shares
                System.out.printf("You can not sell %d shares of %s, you only have %d\n", numShares, sym, numOwned);
            }
        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error selling shares");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }

    }

    private static Double getROI(Connection c, String un, String sym) throws SQLException {
        // Get share price
        PreparedStatement stmt = c.prepareStatement("SELECT price " + "FROM closing_price " + "WHERE symbol = ? "
                + "ORDER BY p_date DESC " + "FETCH FIRST ROW ONLY;");
        stmt.setString(1, sym);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            throw new IllegalArgumentException(sym + " is not a valid symbol\n");
        }
        Double price = rs.getDouble("price");

        // Get number of shares
        stmt = c.prepareStatement("SELECT shares " + "FROM owns " + "WHERE login = ? AND symbol = ?;");
        stmt.setString(1, un);
        stmt.setString(2, sym);
        rs = stmt.executeQuery();
        int numOwned;
        if (!rs.next()) {
            numOwned = 0;
        } else {
            numOwned = rs.getInt("shares");
        }

        // Get total revenue from selling shares
        stmt = c.prepareStatement("SELECT SUM(amount) AS revenue " + "FROM TRXLOG "
                + "WHERE action = \'sell\' AND login = ? AND symbol = ?;");
        stmt.setString(1, un);
        stmt.setString(2, sym);
        rs = stmt.executeQuery();
        Double revenue;
        if (!rs.next()) {
            revenue = 0.0;
        } else {
            revenue = rs.getDouble("revenue");
        }

        // Get total cost of buying shares
        stmt = c.prepareStatement("SELECT SUM(amount) AS expense " + "FROM TRXLOG "
                + "WHERE action = \'buy\' AND login = ? AND symbol = ?;");
        stmt.setString(1, un);
        stmt.setString(2, sym);
        rs = stmt.executeQuery();
        Double expense;
        if (!rs.next()) {
            expense = 0.0;
        } else {
            expense = rs.getDouble("expense");
        }

        /*
         * System.out.println("Sym: " + sym); System.out.println("revenue: " + revenue);
         * System.out.println("price: " + price); System.out.println("numOwned: " +
         * numOwned); System.out.println("expense: " + expense);
         */

        // Calculate and return ROI
        return (revenue + (price * numOwned) - (expense)) / (expense);
    }

    /**
     * Display the symbol, the mutual fund name and the return of investment (ROI)
     * of the customer
     *
     * @param c  Connection to the database
     * @param un Username
     */
    public static void customer8(Connection c, String un) {
        try {
            // get all mutual funds
            PreparedStatement stmt = c.prepareStatement("SELECT symbol, name " + "FROM MUTUAL_FUND;");
            ResultSet rs = stmt.executeQuery();

            System.out.println("Sym\tName\tROI");
            // For each mutual fund
            while (rs.next()) {
                String symbol = rs.getString("symbol");
                String name = rs.getString("name");
                Double roi = getROI(c, un, symbol);

                // if roi is infinite no money was ever spent on it so we don't care
                if (Double.isFinite(roi)) {
                    System.out.printf("%s\t%s\t%.3f\n", symbol, name, roi);
                }
            }

        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error determing ROI");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    public static void customer9(Connection c, String un) {
        PreparedStatement stmt;

        try {
            stmt = c.prepareStatement("SELECT trx_id,action,symbol,num_shares,price,amount FROM TRXLOG WHERE login=?");
            stmt.setString(1, un);
            ResultSet res = stmt.executeQuery();
            boolean check = false;

            while (res.next()) {
                check = true;
                String trx_id = res.getString("trx_id");
                String action = res.getString("action");
                String symbol = res.getString("symbol");
                int num_shares = res.getInt("num_shares");
                float boughtPrice = res.getFloat("price");
                float amount = res.getFloat("amount");

                stmt = c.prepareStatement("SELECT price FROM CLOSING_PRICE WHERE symbol = ? ORDER BY p_date limit 1;");
                stmt.setString(1, symbol);
                ResultSet res2 = stmt.executeQuery();
                while (res2.next()) {

                    float latestPrice = res2.getFloat("price");

                    float difference = latestPrice - boughtPrice;

                    if (action.equals("buy")) {
                        if (difference > 0) {
                            System.out.print("Profit!");
                            System.out.print(" Transaction ID: " + trx_id + " Symbol: " + symbol + " Bought: "
                                    + num_shares + " at $" + boughtPrice + " for a total of $" + amount);
                            System.out.println(" You can gain $" + Math.abs(difference));
                        } else if (difference < 0) {
                            System.out.print("Loss!");
                            System.out.print(" Transaction ID: " + trx_id + " Symbol: " + symbol + " Bought: "
                                    + num_shares + " at $" + boughtPrice + " for a total of $" + amount);
                            System.out.println(" You will lose $" + Math.abs(difference));
                        } else {
                            System.out.println("Hold, net earning would be zero");
                            System.out.print(" Transaction ID: " + trx_id + " Symbol: " + symbol + " Bought: "
                                    + num_shares + " at $" + boughtPrice + " for a total of $" + amount);
                            System.out.println(" You can gain $" + Math.abs(difference));
                        }
                    } else if (action.equals("sell")) {
                        if (difference > 0) {
                            System.out.print("Loss!");
                            System.out.print(" Transaction ID: " + trx_id + " Symbol: " + symbol + " Sold: "
                                    + num_shares + " at $" + boughtPrice + " for a total of $" + amount);
                            System.out.println(" You will lose $" + Math.abs(difference));
                        } else if (difference < 0) {
                            System.out.print("Profit!");
                            System.out.print(" Transaction ID: " + trx_id + " Symbol: " + symbol + " Bought: "
                                    + num_shares + " at $" + boughtPrice + " for a total of $" + amount);
                            System.out.println(" You can gain $" + Math.abs(difference));

                        } else {
                            System.out.println("Hold, net earning would be zero");
                            System.out.print(" Transaction ID: " + trx_id + " Symbol: " + symbol + " Bought: "
                                    + num_shares + " at $" + boughtPrice + " for a total of $" + amount);
                            System.out.println(" You can gain $" + Math.abs(difference));
                        }
                    }
                }

            }
            if (check == false)
                System.out.println("User has no buy/sell transactions");

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

    public static void customer10(Connection c, String un, boolean driver, String[] responses) {
        PreparedStatement stmt;
        boolean invalid = true;
        Scanner in = new Scanner(System.in);

        ArrayList<String> allocsym = new ArrayList<String>();
        ArrayList<String> allocname = new ArrayList<String>();
        try {
            stmt = c.prepareStatement("SELECT name,symbol FROM MUTUAL_FUND");
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                allocname.add(res.getString("name"));
                allocsym.add(res.getString("symbol"));
            }
            stmt.close();
            String str = "";
            if (driver == false) {
                System.out.println(
                        "Please provide the comma separated symbols of all Mutual Funds you wish to change. Ex. MM,RE,STB,GS");
                for (int x = 0; x < allocsym.size(); x++) {
                    System.out.println("Name: " + allocname.get(x) + " Symbol " + allocsym.get(x));
                }
                allocsym.clear();
                str = in.nextLine();
            } else {
                str = responses[0];
            }
            String[] changingSymbols = (str.split(","));
            ArrayList<Integer> allocpercent = new ArrayList<Integer>();
            int percentTracker = 0;
            boolean loop = true;
            int r = 1;
            int input = 0;

            allocsym.clear();
            while (loop) {
                for (int y = 0; y < changingSymbols.length; y++) {
                    if (driver == false) {
                        System.out.println("Enter a allocation percentage for " + changingSymbols[y]
                                + " 0-100 Remember that the percentages must sum to 100 ");
                        input = in.nextInt();
                    } else {
                        input = Integer.parseInt(responses[r]);

                        r++;

                        if (r == responses.length + 1) {
                            break;
                        }
                    }
                    percentTracker += input;
                    allocpercent.add(input);
                    allocsym.add(changingSymbols[y]);
                    if (percentTracker > 100) {
                        break;

                    }

                }
                if (percentTracker == 100) {
                    loop = false;
                } else {
                    System.out.println("Try Again, sum did not equate to 100");
                    percentTracker = 0;
                    allocpercent.clear();
                    allocsym.clear();
                }

            }
            stmt = c.prepareStatement(
                    "SELECT max(allocation_no) as alloc_id FROM prefers natural join(SELECT allocation_no FROM allocation WHERE p_date =(SELECT max(p_date) FROM allocation WHERE login=? GROUP BY login)) AS alloc;");
            stmt.setString(1, un);
            ResultSet res2 = stmt.executeQuery();
            int allocation_no = 0;
            res2.next();
            allocation_no = res2.getInt("alloc_id");
            allocation_no++;

            stmt = c.prepareStatement("SELECT max(p_date) as today FROM mutual_date");
            ResultSet res3 = stmt.executeQuery();

            res3.next();
            java.sql.Date todayDate = res3.getDate("today");

            try {
                // Insert in Allocations
                stmt = c.prepareStatement("INSERT INTO Allocation(allocation_no,login,p_date) VALUES(?,?,?);");
                stmt.setInt(1, allocation_no);
                stmt.setString(2, un);
                stmt.setDate(3, todayDate);

                c.setAutoCommit(false);
                c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                stmt.executeUpdate();
                c.commit();
                System.out.println("Successfully made changes to your allocations");
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

            for (int i = 0; i < allocsym.size(); i++) {

                try {
                    // Insert in Prefers
                    Float percent = (float) (allocpercent.get(i));
                    percent = percent / 100;
                    stmt = c.prepareStatement("INSERT INTO PREFERS(allocation_no,symbol,percentage) VALUES(?,?,?)");
                    stmt.setInt(1, allocation_no);
                    stmt.setString(2, allocsym.get(i));
                    stmt.setFloat(3, percent);

                    c.setAutoCommit(false);
                    c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    stmt.executeUpdate();
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

        } catch (SQLException e1) {
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
     * Rank the customer’s allocations
     *
     * @param c  Connection to the database
     * @param un Username
     */
    public static void customer11(Connection c, String un) {
        class RankableAllocation implements Comparable {
            private Connection c;
            public int allocation_no;
            public Date p_date;

            RankableAllocation(Connection c, int allocation_no, Date p_date) {
                this.c = c;
                this.allocation_no = allocation_no;
                this.p_date = p_date;
            }

            public double getROI() throws SQLException {
                double roi = 0;

                // get table of prefrences
                PreparedStatement stmt = c
                        .prepareStatement("SELECT symbol, percentage " + "FROM PREFERS " + "WHERE allocation_no = ?;");
                stmt.setInt(1, allocation_no);
                ResultSet prefers = stmt.executeQuery();
                // for each prefers
                while (prefers.next()) {
                    String sym = prefers.getString("symbol");
                    Double percent = prefers.getDouble("percentage");

                    // get price when allocation was made
                    stmt = c.prepareStatement("SELECT price " + "FROM closing_price "
                            + "WHERE symbol = ? AND p_date < ?" + "ORDER BY p_date DESC " + "FETCH FIRST ROW ONLY;");
                    stmt.setString(1, sym);
                    stmt.setDate(2, p_date);
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    Double oldPrice = rs.getDouble("price");

                    // get price now
                    stmt = c.prepareStatement("SELECT price " + "FROM closing_price " + "WHERE symbol = ? "
                            + "ORDER BY p_date DESC " + "FETCH FIRST ROW ONLY;");
                    stmt.setString(1, sym);
                    rs = stmt.executeQuery();
                    rs.next();
                    Double currPrice = rs.getDouble("price");
                    // calculate prefer's roi
                    Double prefROI = (currPrice - oldPrice) / oldPrice;
                    // Add it to the overall roi weighted by the percentage
                    roi += prefROI * percent;
                }

                return roi;
            }

            @Override
            public int compareTo(Object o) {
                try {
                    Double myROI = this.getROI();
                    Double otherROI = ((RankableAllocation) o).getROI();

                    return otherROI.compareTo(myROI);
                } catch (SQLException e1) {
                    // Exception handling
                    System.out.println("SQL Error ranking allocations");
                    while (e1 != null) {
                        System.out.println("Message = " + e1.getMessage());
                        System.out.println("SQLState = " + e1.getSQLState());
                        System.out.println("SQL Code = " + e1.getErrorCode());
                        e1 = e1.getNextException();
                    }

                    return 0;
                }

            }

        }

        try {
            // Get table of allocations
            ArrayList<RankableAllocation> rankableAllocations = new ArrayList<RankableAllocation>();
            PreparedStatement stmt = c
                    .prepareStatement("SELECT allocation_no, p_date " + "FROM ALLOCATION " + "WHERE login = ?;");
            stmt.setString(1, un);
            ResultSet allocations = stmt.executeQuery();

            // Make into rankable allocations
            while (allocations.next()) {
                rankableAllocations.add(
                        new RankableAllocation(c, allocations.getInt("allocation_no"), allocations.getDate("p_date")));
            }

            // sort list by roi
            Collections.sort(rankableAllocations);

            // print out ranking
            System.out.println("Rank\talloc_no\tDate Created\tROI");
            for (int i = 0; i < rankableAllocations.size(); i++) {
                int rank = i + 1;
                int alloc_no = rankableAllocations.get(i).allocation_no;
                Date p_date = rankableAllocations.get(i).p_date;
                Double roi = rankableAllocations.get(i).getROI();

                System.out.printf("%d\t%d\t\t%s\t%.3f\n", rank, alloc_no, p_date.toString(), roi);
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
     * Shows the customer's portfolio
     *
     * @param c  Connection to the database
     * @param un Username
     */
    public static void customer12(Connection c, String un) {
        try {
            PreparedStatement stmt = c.prepareStatement("Select symbol, shares " + "FROM owns " + "WHERE login = ?;");
            stmt.setString(1, un);
            ResultSet owns = stmt.executeQuery();

            System.out.println("Symbol\tShares\tCurrent Value\tCost\tAdjusted Cost\tYield");
            Double totalValue = 0.0;
            // For each shares
            while (owns.next()) {
                String sym = owns.getString("symbol");
                int numShares = owns.getInt("shares");

                // Get current price of fund
                stmt = c.prepareStatement("SELECT price " + "FROM closing_price " + "WHERE symbol = ? "
                        + "ORDER BY p_date DESC " + "FETCH FIRST ROW ONLY;");
                stmt.setString(1, sym);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    System.out.printf("%s is not a valid symbol\n", sym);
                    return;
                }
                Double price = rs.getDouble("price");

                // Get total revenue from selling shares
                stmt = c.prepareStatement("SELECT SUM(amount) AS revenue " + "FROM TRXLOG "
                        + "WHERE action = \'sell\' AND login = ? AND symbol = ?;");
                stmt.setString(1, un);
                stmt.setString(2, sym);
                rs = stmt.executeQuery();
                Double revenue;
                if (!rs.next()) {
                    revenue = 0.0;
                } else {
                    revenue = rs.getDouble("revenue");
                }

                // Get total cost of buying shares
                stmt = c.prepareStatement("SELECT SUM(amount) AS expense " + "FROM TRXLOG "
                        + "WHERE action = \'buy\' AND login = ? AND symbol = ?;");
                stmt.setString(1, un);
                stmt.setString(2, sym);
                rs = stmt.executeQuery();
                Double expense;
                if (!rs.next()) {
                    expense = 0.0;
                } else {
                    expense = rs.getDouble("expense");
                }

                Double currentValue = price * numShares;
                Double cost = expense;
                Double adjustedCost = cost - revenue;
                Double yield = currentValue - adjustedCost;
                totalValue += currentValue;

                System.out.printf("%s\t%d\t%.2f\t\t%.2f\t%.2f\t\t%.2f\n", sym, numShares, currentValue, cost,
                        adjustedCost, yield);
            }
            System.out.printf("\nTotal value: %.2f\n", totalValue);
        } catch (SQLException e1) {
            // Exception handling
            System.out.println("SQL Error generating portfolio");
            while (e1 != null) {
                System.out.println("Message = " + e1.getMessage());
                System.out.println("SQLState = " + e1.getSQLState());
                System.out.println("SQL Code = " + e1.getErrorCode());
                e1 = e1.getNextException();
            }
        }
    }

    private static void generateAdminMenu() {
        System.out.println("\nAdministrator Interface");
        System.out.println("1: Erase the database");
        System.out.println("2: Add a customer");
        System.out.println("3: Add new mutual fund");
        System.out.println("4: Update share quotes for a day");
        System.out.println("5: Show top-k highest volume categories");
        System.out.println("6: Rank all the investors");
        System.out.println("7: Update the current date");
        System.out.println("8: Exit");
    }

    private static void generateCustomerMenu() {
        System.out.println("\nCustomer Interface");
        System.out.println("1: Show the customer’s balance and total number of shares");
        System.out.println("2: Show mutual funds sorted by name");
        System.out.println("3: Show mutual funds sorted by prices on a date");
        System.out.println("4: Search for a mutual fund");
        System.out.println("5: Deposit an amount for investment");
        System.out.println("6: Buy shares");
        System.out.println("7: Sell shares");
        System.out.println("8: Show ROI (return of investment)");
        System.out.println("9: Predict the gain or loss of the customer’s transactions");
        System.out.println("10: Change allocation preference");
        System.out.println("11: Rank the customer’s allocations");
        System.out.println("12: Show portfolio");
        System.out.println("13: Exit");
    }

}
