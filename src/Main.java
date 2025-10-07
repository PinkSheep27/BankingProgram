//Author: Ivan Rudik :)
//This is still a work in progress
//Reminder: Learn JOptionPane and implement
//Reminder: Fix RandomAccessFile output

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        Bank bank = new Bank();

        try {
            char choice;
            boolean notDone = true;

            File testCases = new File("testCases.txt");
            Scanner UI = new Scanner(testCases);
            PrintWriter pw = new PrintWriter("output.txt");

            readAccts(bank);

            Bank.totalAmountInCheckingAccts(bank);
            Bank.totalAmountInSavingsAccts(bank);
            Bank.totalAmountInCDAccts(bank);
            Bank.totalAmountInAllAccts(bank);

            printAccts(bank, pw);

            do {
                try {
                    menu();
                    choice = UI.next().charAt(0);
                    if (choice == 'w' || choice == 'W') {
                        withdrawal(bank, pw, UI);
                    } else if (choice == 'd' || choice == 'D') {
                        deposit(bank, pw, UI);
                    } else if (choice == 'n' || choice == 'N') {
                        newAcct(bank, pw, UI);
                    } else if (choice == 'r' || choice == 'R') {
                        reopenAcct(bank, pw, UI);
                    } else if (choice == 'b' || choice == 'B') {
                        balance(bank, pw, UI);
                    } else if (choice == 'x' || choice == 'X') {
                        deleteAcct(bank, pw, UI);
                    } else if (choice == 's' || choice == 'S') {
                        closeAcct(bank, pw, UI);
                    } else if (choice == 'i' || choice == 'I') {
                        accountInfo(bank, pw, UI);
                    } else if (choice == 'h' || choice == 'H') {
                        accountInfoHistory(bank, pw, UI);
                    } else if (choice == 'c' || choice == 'C') {
                        clearCheck(bank, pw, UI);
                    } else if (choice == 'q' || choice == 'Q') {
                        printAccts(bank, pw);
                        notDone = false;
                    } else {
                        throw new InvalidMenuSelectionException(choice);
                    }
                } catch (InvalidMenuSelectionException | InvalidAccountException | AccountClosedException |
                         InsufficientFundsException | CDMaturityDateException | InvalidAmountException e) {
                    pw.println(e.getMessage());
                    pw.println();
                    pw.flush();
                }

                pause(UI);//allows for the user to look at their result before continuing

            } while (notDone);
            pw.close();  //closes the output file
            UI.close(); //closes the Scanner

            System.out.println();
            System.out.println("The Program is Closing");

        } catch (IOException e) {
            System.out.println("Error: File Not Found.");
        }
    }

    /*
  Input:
  Bank Class, PrintWriter, Scanner
  Process:
  Asks user for an SSN and then searches for all accounts with that SSN
  Output:
  Errors are printed if there are any
  If successful the program outputs the full account histories for all accounts with
  the input SSN
   */
    public static void accountInfoHistory(Bank bank, PrintWriter pw, Scanner UI) throws IOException {
        int index;
        int count = 0;
        TransactionReceipt history;

        Account myAccount;

        pw.println();
        pw.println("Requested Account Info with Transaction History");
        System.out.println("Please Enter your SSN:");
        String requestedSSN = UI.next();

        if (requestedSSN.length() != 9) {
            pw.println("Error:" + requestedSSN + " isn't a valid SSN Due to it Not Being 9 Digits Long.");
            pw.println();
            pw.println();
        } else {
            index = bank.findSSN(requestedSSN);
            if (index == -1) {
                pw.println("Error: No Account Exists with the following SSN:" + requestedSSN + ".");
                pw.println();
                pw.println();
            } else {
                pw.println("SSN: " + requestedSSN);
                pw.println("-----------------------------------------------------------------------------------------------------------------------------");
                for (int i = 0; i < bank.getNumAccounts(); i++) {
                    if (bank.getAcct(i).getDepositor().getSSN().equals(requestedSSN)) {
                        pw.println();

                        pw.printf("%s %11s %7s %15s %16s %15s %19s %22s", "Last N.", "First N.", "SSN", "Acct Num", "Acct Type", "Status", "Balance", "Maturity Date");
                        pw.println();

                        myAccount = bank.getAcct(i);
                        Account copyAcct = new Account(myAccount);
                        pw.print(copyAcct);

                        pw.println();
                        pw.println("                         *****Account Transactions*****");
                        pw.printf("%-12s%s%9s%11s%9s%23s", "Date", "Transaction", "Amount", "Status", "Balance", "Reason For Failure");
                        pw.println();
                        for (int x = 0; x < myAccount.getHistorySize(); x++) {
                            history = myAccount.getHistory(x);
                            pw.print(history.toString().trim());
                            pw.println();
                        }

                        count++;
                    }
                }
                pw.println("-----------------------------------------------------------------------------------------------------------------------------");
                pw.println();
                pw.println();
                pw.println(count + " accounts were found.");
                pw.println();
            }
        }
    }

    /*
        Input:
        Bank Class, PrintWriter, Scanner
        Process:
        The user inputs an account number which is then sent to the bank class and if the account is found error
        If the account isn't found a new account is added after the user inputs first and last name, SSN,
        acctType, and initial deposit amount.
        Output:
        Errors are printed if there are any
        If successful the program outputs the account number, type, new balance
        and if the account is CD there is a set maturity Date( the day today )
 */
    public static void newAcct(Bank bank, PrintWriter pw, Scanner UI) throws InvalidAccountException, IOException {
        String First;
        String Last;
        String SSN;
        int acctNum;
        String typeOfAcct;
        double balance;

        TransactionReceipt newAcct;

        Name myName;
        Depositor myDepositor;
        CheckingAccount CA;
        CDAccount CDA;
        SavingsAccount SA;

        Calendar today = Calendar.getInstance();
        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        pw.println("Requested To Create a New Account");
        System.out.println("Please Enter a Account Number:");
        acctNum = UI.nextInt();
        System.out.println("Please Enter Your Last Name:");
        First = UI.next();
        System.out.println("Please Enter Your First Name:");
        Last = UI.next();
        System.out.println("Please Enter Your Social Security Number:");
        SSN = UI.next();
        System.out.println("Please Enter the Account Type you Want to Open (CD, Savings, Checking) :");
        typeOfAcct = UI.next();
        System.out.println("Please Enter an Initial Deposit Amount:");
        balance = UI.nextDouble();

        TransactionTicket ticket = new TransactionTicket(acctNum, todayS, "New Account", typeOfAcct, balance, 0);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);

        myName = new Name(Last, First);

        myDepositor = new Depositor(myName, SSN);

        if (typeOfAcct.equals("Checking")) {
            CA = new CheckingAccount(myDepositor, acctNum, typeOfAcct, "open", balance, "");
            newAcct = bank.openNewAcct(copyOfTicket, CA);
        } else if (typeOfAcct.equals("Savings")) {
            SA = new SavingsAccount(myDepositor, acctNum, typeOfAcct, "open", balance, "");
            newAcct = bank.openNewAcct(copyOfTicket, SA);
        } else {
            CDA = new CDAccount(myDepositor, acctNum, typeOfAcct, "open", balance, todayS);
            newAcct = bank.openNewAcct(copyOfTicket, CDA);
        }
        pw.println(newAcct);
        pw.println();
    }

    /*
    Input:
    Bank Class, PrintWriter, Scanner
    Process:
    Prompts the user for the account that will be deleted and passes it into Ticket
    That ticket is then sent into the bank class and if the account exists it is searched for
    the balance and if the balance is above 0, the account won't be deleted.
    If the account is 0 then the account will be dragged to the bottom of the accounts array and deleted.
    Output:
    Errors are printed if there are any
    If the account is 0 then the account will be dragged to the bottom of the accounts array and deleted.
     */
    public static void deleteAcct(Bank bank, PrintWriter pw, Scanner UI) throws InvalidAccountException, IOException {
        int requestedAccount;

        Calendar today = Calendar.getInstance();
        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        pw.println("Requested An Account Deletion.");
        System.out.println("Please Enter the Account To Delete: ");
        requestedAccount = UI.nextInt();

        TransactionTicket ticket = new TransactionTicket(requestedAccount, todayS, "Deleting", "", 0.00, 0);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);

        TransactionReceipt deleteAcct = bank.deleteAcct(copyOfTicket);
        TransactionReceipt copyOfReceipt = new TransactionReceipt(deleteAcct);

        pw.println(copyOfReceipt);
        pw.println();
    }

    /*
Input:
Bank Class, PrintWriter, Scanner
Process:
Asks for the account that needs to be reopened and searches for that account
Output:
Errors are printed if there are any
If successful the program sets the accounts status to open
*/
    public static void reopenAcct(Bank bank, PrintWriter pw, Scanner UI) throws InvalidAccountException, IOException {
        int requestedAcct;

        Calendar today = Calendar.getInstance();
        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        pw.println("Requested to Reopen an Account.");
        System.out.println("Please Enter the Account you Want to Reopen:");
        requestedAcct = UI.nextInt();

        TransactionTicket ticket = new TransactionTicket(requestedAcct, todayS, "Reopening", "", 0.00, 0);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);

        TransactionReceipt reopenAcct = bank.reopenAccount(copyOfTicket);
        TransactionReceipt copyOfReceipt = new TransactionReceipt(reopenAcct);

        pw.println(copyOfReceipt);
        pw.println();
    }

    /*
Input:
Bank Class, PrintWriter, Scanner
Process:
Asks for the account that needs to be closed and searches for that account
Output:
Errors are printed if there are any
If successful the program sets the accounts status to closed
*/
    public static void closeAcct(Bank bank, PrintWriter pw, Scanner UI) throws InvalidAccountException, IOException {
        int requestedAcct;

        Calendar today = Calendar.getInstance();
        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        pw.println("Requested to Close an Account.");
        System.out.println("Please Enter the Account you Want to Close:");
        requestedAcct = UI.nextInt();

        TransactionTicket ticket = new TransactionTicket(requestedAcct, todayS, "Closing", "", 0.00, 0);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);

        TransactionReceipt closeAcct = bank.closeAccount(copyOfTicket);
        TransactionReceipt copyOfReceipt = new TransactionReceipt(closeAcct);

        pw.println(copyOfReceipt);
        pw.println();
    }

    /*
    Input:
    Bank Class, PrintWriter, Scanner
    Process:
    The method prompts for an account number, checkAmount, and a dateOfCheck
    Then sends them to the Check Class which stores the prompted items
    After which is sent into the Bank class to check the index of the account number and if it's valid
    Later the account pertaining to the index is sent to the Account class which checks balance and account type
    Sends back error or Success
    Output:
    The output is either an error of not finding the account,Account found isn't checking,the balance is less than the
    checkAmount, and the dateOfCheck is past 6 months
    or the output is a success and prints out the Account #, pre balance, after balance and withdraw amount
     */
    public static void clearCheck(Bank bank, PrintWriter pw, Scanner UI) throws InvalidAccountException, IOException, InsufficientFundsException, AccountClosedException {
        int acctNum;
        double checkAmount;
        String dateOfCheck;

        pw.println("Requested to Clear a Check.");
        System.out.println("Please Enter Your Account Number: ");
        acctNum = UI.nextInt();
        System.out.println("Please Enter Amount of Check: ");
        checkAmount = UI.nextDouble();
        System.out.println("Please Enter the Date on Your Check (in XX/XX/XXXX form): ");
        dateOfCheck = UI.next();

        Calendar DOC = Calendar.getInstance();
        String[] mdArray = dateOfCheck.split("/");
        DOC.set(Integer.parseInt(mdArray[2]), Integer.parseInt(mdArray[0]) - 1, Integer.parseInt(mdArray[1]));

        Check check = new Check(acctNum, checkAmount, DOC);
        Check copyOfCheck = new Check(check);

        TransactionReceipt clearCheck = bank.clearCheck(bank, copyOfCheck);
        TransactionReceipt copyOfReceipt = new TransactionReceipt(clearCheck);

        pw.println(copyOfReceipt);
        pw.println();
    }

    /*
    Input:
Bank Class, PrintWriter, Scanner
    Process:
The user inputs an SSN that they want to find and if the SSN isn't 9 digits and error is printed.
If the SSN is 9 digits long then the program continues and the findSSN is invoked in the bank class which returns an index
If that index isn't found, error is printed. Else, the printSSN method is called and prints the full stats of the accounts
with the requested SSN.
    Output:
Errors are printed if there are any
The accounts with the requested SSN are printed in a neatly formatted table
     */
    public static void accountInfo(Bank bank, PrintWriter pw, Scanner UI) throws IOException {
        int index;

        pw.println("Requested Account Info");
        System.out.println("Please Enter your SSN:");
        String requestedSSN = UI.next();

        if (requestedSSN.length() != 9) {
            pw.println("Error:" + requestedSSN + " isn't a valid SSN Due to it Not Being 9 Digits Long.");
            pw.println();
            pw.println();
        } else {
            index = bank.findSSN(requestedSSN);
            if (index == -1) {
                pw.println("Error: No Account Exists with the following SSN:" + requestedSSN + ".");
                pw.println();
                pw.println();
            } else {
                printSSN(bank, requestedSSN, pw);
                pw.println();
            }
        }
    }

    /*
    Input:
Bank Class, PrintWriter, Scanner
    Process:
Prompts the user for an account number, withdrawal amount, and a new CD date for CD accounts
Those are sent into a ticket which is sent into the makeWithdrawal method in the bank class
The method checks for account(if it exists) then if account is CD the termOfCD is passed on
Then it gets passed onto another account class method which checks for more errors and returns an updated balance
after the withdrawal
    Output:
Errors if there are any
Prints out the account number, acct type, pre balance, post balance, new MD, and withdrawal amount
     */
    public static void withdrawal(Bank bank, PrintWriter pw, Scanner UI) throws InvalidAccountException, AccountClosedException, IOException, CDMaturityDateException, InsufficientFundsException, InvalidAmountException {
        int requestedAccount;
        double requestedAmount;
        int requestedTerm;

        Calendar today = Calendar.getInstance();
        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        System.out.println();
        System.out.println("Please Enter Your Account Number:");
        requestedAccount = UI.nextInt();

        pw.println("Requested Withdrawal From Account #" + requestedAccount + ".");

        System.out.println("Please Enter The Amount of Money to Withdraw:");
        requestedAmount = UI.nextDouble();

        System.out.println("Please Enter A new CD date(6,12,18,or 24): ");
        requestedTerm = UI.nextInt();

        TransactionTicket ticket = new TransactionTicket(requestedAccount, todayS, "Withdrawal", "", requestedAmount, requestedTerm);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);

        TransactionReceipt postBalance = bank.makeWithdrawal(bank, copyOfTicket);
        TransactionReceipt copyOfReceipt = new TransactionReceipt(postBalance);

        pw.println(copyOfReceipt);
        pw.println();
    }

    /*
    Input:
Bank Class, PrintWriter, Scanner
    Process:
Prompts the user for an account number, deposit amount, and a new CD date for CD accounts
Those are sent into a ticket which is sent into the makeDeposit method in the bank class
The method checks for account(if it exists) then if account is CD the termOfCD is passed on
Then it gets passed onto another account class method which checks for more errors and returns an updated balance
after the deposit
    Output:
Errors if there are any
Prints out the account number, acct type, pre balance, post balance, new MD, and deposit amount
     */
    public static void deposit(Bank bank, PrintWriter pw, Scanner UI) throws IOException, InvalidAccountException, CDMaturityDateException, InvalidAmountException, AccountClosedException {
        int requestedAccount;
        double requestedAmount;
        int requestedTerm;

        Calendar today = Calendar.getInstance();
        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        System.out.println();
        System.out.println("Please Enter Your Account Number:");
        requestedAccount = UI.nextInt();

        pw.println("Requested Deposit From Account #" + requestedAccount + ".");

        System.out.println("Please Enter The Amount of Money to Deposit:");
        requestedAmount = UI.nextDouble();

        System.out.println("Please Enter A new CD date: ");
        requestedTerm = UI.nextInt();

        TransactionTicket ticket = new TransactionTicket(requestedAccount, todayS, "Deposit", "", requestedAmount, requestedTerm);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);

        TransactionReceipt postBalance = bank.makeDeposit(bank, copyOfTicket);
        TransactionReceipt copyOfReceipt = new TransactionReceipt(postBalance);

        pw.println(copyOfReceipt);
        pw.println();
    }

    /*
    Input:
Bank Class, PrintWriter, Scanner
    Process:
Prompts the user for an account number that is sent into a ticket which is sent into the getBalance method in the bank class
The method checks for account(if it exists) then if account is CD a maturityDate is returned
Then it gets passed onto another account class method which checks for more errors and returns and returns the balance.
    Output:
Errors if there are any
returns the acct number, acct type, balance, and maturity date if the acct type is CD
     */
    public static void balance(Bank bank, PrintWriter pw, Scanner UI) throws IOException {

        int requestedAccount;
        Calendar today = Calendar.getInstance();

        System.out.println();
        System.out.println("Please Enter Your Account Number:");
        requestedAccount = UI.nextInt();
        pw.println("Requested Balance of Account #" + requestedAccount + ".");

        String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

        TransactionTicket Ticket = new TransactionTicket(requestedAccount, todayS, "Balance", "", 0, 0);

        TransactionReceipt balance = bank.getBalance(Ticket);

        pw.println(balance);
        pw.println();

        pw.flush();
    }

    /*
    Input:
Bank Class, RequestedSSN ,PrintWriter
    Process:
First name, Last name, SSN, Acct Num, Acct Type, Balance, and, if the acct type is CD,
the maturityDate are printed in a neatly formatted table of the requested SSN
    Output:
Prints out the accounts that have the same SSN
     */
    public static void printSSN(Bank bank, String requestedSSN, PrintWriter pw) throws IOException {
        int count = 0;

        Account myAccount;

        pw.println();
        pw.println("SSN:" + requestedSSN);
        pw.println("-----------------------------------------------------------------------------------------------------------------------------");
        pw.printf("%-21s%-14s%-15s%-20s%-20s%-17s%-11s\n", "Name", "SSN", "Account #", "Account Type", "Status", "Balance", "Maturity Date");
        pw.println("-----------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < bank.getNumAccounts(); i++) {
            if (bank.getAcct(i).getDepositor().getSSN().equals(requestedSSN)) {

                myAccount = bank.getAcct(i);

                Account copyAcct = new Account(myAccount);

                pw.print(copyAcct);

                pw.println();
                count++;
            }
        }
        pw.println("-----------------------------------------------------------------------------------------------------------------------------");
        pw.println();
        pw.println(count + " Accounts Were Found.");
        pw.println();
    }

    /*
    Input:
Bank Class, PrintWriter
    Process:
First name, Last name, SSN, Acct Num, Acct Type, Balance, and, if the acct type is CD,
the maturityDate are printed in a neatly formatted table
    Output:
Prints out the array of accounts in the Bank class
     */
    public static void printAccts(Bank bank, PrintWriter pw) throws IOException {
        Account myAccount;

        pw.printf("%50s", "Accounts in the Database\n");
        pw.println("-----------------------------------------------------------------------------------------------------------------------------");
        pw.printf("%-25s%-14s%-15s%-20s%-16s%-17s%-11s\n", "Name", "SSN", "Account #", "Account Type", "Status", "Balance", "Maturity Date");
        pw.println("-----------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < bank.getNumAccounts(); i++) {

            if (bank.getAcct(i).getAcctType().equals("CD")) {
                myAccount = new CDAccount(bank.getAcct(i).getDepositor(), bank.getAcct(i).getAcctNum(), bank.getAcct(i).getAcctType(), bank.getAcct(i).getStatus(), bank.getAcct(i).getBalance(), bank.getAcct(i).getDate());
                pw.print(myAccount);
            } else if (bank.getAcct(i).getAcctType().equals("Checking")) {
                myAccount = new CheckingAccount(bank.getAcct(i).getDepositor(), bank.getAcct(i).getAcctNum(), bank.getAcct(i).getAcctType(), bank.getAcct(i).getStatus(), bank.getAcct(i).getBalance(), "");
                pw.print(myAccount);
            } else {
                myAccount = new SavingsAccount(bank.getAcct(i).getDepositor(), bank.getAcct(i).getAcctNum(), bank.getAcct(i).getAcctType(), bank.getAcct(i).getStatus(), bank.getAcct(i).getBalance(), "");
                pw.print(myAccount);
            }

            pw.println();
        }
        pw.println("-----------------------------------------------------------------------------------------------------------------------------");

        pw.printf("Total Amount in Savings Accounts: $%.2f\n", bank.getTotalSavings());
        pw.printf("Total Amount in Checking Accounts: $%.2f\n", bank.getTotalChecking());
        pw.printf("Total Amount in CD Accounts: $%.2f\n", bank.getTotalCD());
        pw.printf("Total Amount in All Accounts: $%.2f\n", bank.getTotal());
        pw.println();

        pw.flush();
    }

    /*
    Input:
Bank
    Process:
First name, Last name, SSN, Acct Num, Acct Type, Balance, and, if the acct type is CD,
the maturityDate are read into their respective spots
    Output:
Reads all the accounts into the accounts array
     */
    public static void readAccts(Bank bank) throws IOException {
        String line;

        Name myName;
        Depositor myDepositor;
        Account AC;

        try {
            File file = new File("input.txt");
            //try-with-resources which closes the scanner once try block is exited
            try (Scanner scnr = new Scanner(file)) {
                while (scnr.hasNext()) {

                    line = scnr.nextLine();
                    String[] tokens = line.split(" ");

                    myName = new Name(tokens[0], tokens[1]);
                    myDepositor = new Depositor(myName, tokens[2]);

                    if (tokens[4].equals("CD"))
                        AC = new CDAccount(myDepositor, Integer.parseInt(tokens[3]), tokens[4], tokens[5], Double.parseDouble(tokens[6]), tokens[7]);
                    else if (tokens[4].equals("Savings"))
                        AC = new SavingsAccount(myDepositor, Integer.parseInt(tokens[3]), tokens[4], tokens[5], Double.parseDouble(tokens[6]), "");
                    else
                        AC = new CheckingAccount(myDepositor, Integer.parseInt(tokens[3]), tokens[4], tokens[5], Double.parseDouble(tokens[6]), "");

                    AC.openHistoryFile();
                    bank.appendNewAccount(AC);
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Data is Invalid.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Input File not Found.");
        }
    }

    /*
    Input:
Nothing
    Process:
Prints out everything
    Output:
prints out the menu
     */
    public static void menu() {
        System.out.println();
        System.out.println("Welcome to Bank Palace");
        System.out.println("----------------------");
        System.out.println("I - Account Info");
        System.out.println("H - Account Info w/ Trans. Hist.");
        System.out.println("C - Clear Check");
        System.out.println("B - Balance");
        System.out.println("D - Deposit");
        System.out.println("W - Withdrawal");
        System.out.println("N - New Account");
        System.out.println("R - Reopen Account");
        System.out.println("X - Delete Account");
        System.out.println("S - Close Account");
        System.out.println("Q - Quit");
        System.out.println("----------------------");
        System.out.println("Please enter an Action: ");
    }

    public static void pause(Scanner output) {
        String tempstr;
        System.out.println();
        System.out.print("press ENTER to continue");
        tempstr = output.nextLine();
        tempstr = output.nextLine();
    }
}