# CS1555 - Database Management Systems Final Project
University of Pittsburgh Spring 2021

This project required myself and my group memebers to design and implement an electronic investing system using a PostgresSQL backend along with a Java interface utilizing JDBC connectivity. The system utilizes a login system to differentiate administrators/customers and display the corresponding available functions. The following functionality is provided:


## Administrator functionality
* Task #1: Erase the database
  * Ask the user to verify deletion of all the data. Simply delete all the tuples of all the tables in the database.
* Task #2: Add a customer
  * Ask the user to supply the customer’s information (login, name, email, password) and the initial balance. If the balance is not given, the balance should be initialized with 0.    Then insert the information into the appropriate tables.
* Task #3: Add new mutual fund
  * Ask the user to supply the needed information (column name, symbol, name, description, category, c date) to create a new mutual fund.
* Task #4: Update share quotes for a day
  * Ask the user to supply the filename where all the prices for all the mutual funds, and then load them into the appropriate table(s).
* Task #5: Show top-k highest volume categories
  * Ask the user to supply the k value, and display the corresponding categories. The categories in the result are the top k categories based on the number of shares owned by         customers.
* Task #6: Rank all the investors
  * The system should rank the customers based on the total value of shares that they owned according to the most recent closing price.
* Task #7: Update the current date (i.e., the “pseudo” date)
  * Ask the user to supply a date to be set as the current date (p date) in MUTUAL DATE table

## Customer functionality
* Task #1: Show the customer’s balance and total number of shares
  * Display the name, balance and total number of shares owned by the customer.
* Task #2: Show mutual funds sorted by name
  * Display to the user all the mutual funds and their information sorted alphabetically.
* Task #3: Show mutual funds sorted by prices on a date
  * Ask the user to supply a date, and then display to the user all the mutual funds and their
information sorted based on the prices high to low, marking those owned by the customer.
* Task #4: Search for a mutual fund
  * Ask the user to supply up to two keywords, and then return a text with the products that
contain ALL these keywords in their mutual fund description in the format: “[symbol 1,
symbol 2, ...]”. Where symbol 1 and symbol 2 are symbols for the products that contain
the search keywords in their description.
* Task #5: Deposit an amount for investment
  * Ask the user to supply an amount for investment (a deposit), then use that to buy shares
based on their allocation preference.
Note that a ‘deposit’ transaction should make a set of ‘buy’ transactions.
Investing should either result in buying shares of all mutual funds as specified in the allocation or none. Buying mutual funds partially is not an option. Thus, when an amount
is deposited, and the new balance in the account is not sufficient to buy all the shares as
specified in the allocation, that amount is just deposited in the account. In addition, any
remaining amount after an investment becomes the new balance in the account.
* Task #6: Buy shares
  * Ask the user to provide: (i) the symbol and number of shares of the mutual fund they want
to buy; or (ii) the symbol and the amount to be used in the trade. In both cases, the required
amount should not exceed the balance in the user’s account.
(i) When a user buys shares by specifying the number of shares to be bought, if the balance
is not sufficient to buy all the shares, no share is bought.
(ii) When a user buys shares by specifying an amount which is equal or less of the balance in
their account, the user buys the maximum number of shares based on the specified amount
and the remaining amount remains in the balance.
Also, the price of a share on a given day is the closing price of the most recent trading day.
Note that the balance should be updated automatically by the respect trigger.
* Task #7: Sell shares
  * Ask the user to provide the symbol and number of shares of the mutual fund they want to
sell. The resulting amount from the sell is added in the customer’s balance which can be
used for buying shares of another mutual fund in future.
Note that the balance should be updated automatically by the respect trigger.
* Task #8: Show ROI (return of investment)
  * Display the symbol, the mutual fund name and the return of investment (ROI) of the customer.
* Task #9: Predict the gain or loss of the customer’s transactions
  * Display the difference for each transaction amount with the “predicted” transaction amount,
which is based on the most recent closing price. You should also display next to the difference, the status of how successful was the decision of buying or selling. The statuses are 3:
(i)“loss”, if the predicted buy transaction has a higher price or the predicted sell transaction
has a lower price than the already processed transaction; (ii)“profit”, if the predicted buy
transaction has a lower price or the predicted sell transaction has a higher price than the
already processed transaction; (iii)“hold”, if the predicted buy transaction or the predicted
sell transaction has the same price with the already processed transaction.
* Task #10: Change allocation preference
  * Ask the user to provide the symbol of mutual fund and the percentage for all the desired
funds. The total of all percentages should be 100% to be updated successfully.
Note that the allocation can change only once per day
* Task #11: Rank the customer’s allocations
  * Display the rank of the customer’s allocations based on the ROI. Considering the ROI calculation, you should use the most recent closing price for all the allocation periods.
Note that the allocation can change only once per day
* Task #12: Show portfolio
  * This task generates a performance report of a user’s portfolio based on their owned shares.
That means, find out what mutual funds the customer owns, and find out their current values
(the most recent prices of currently owned mutual funds multiplied by the number of owned
shares), their cost (the total purchase price, for all currently owned mutual funds), their adjusted cost (the cost value minus the sum of all the sales values of a given stock), as well
as their yield (the current value minus the adjusted cost). The report will contain the following: 1) mutual fund symbols, 2) number of shares owned of each mutual fund, 3) current value of each mutual fund, 4) cost value for each mutual fund, 5) the adjusted cost for
each mutual fund, 6) the yield for each mutual fund, and 7) the total value of the portfolio
on the current date.
