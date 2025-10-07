import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.Calendar;

public class Bank {
    private static final int LAST_NAME_LEN = 15;
    private static final int FIRST_NAME_LEN = 15;
    private static final int SSN_LEN = 9;
    private static final int ACCOUNT_TYPE_LEN = 10;
    private static final int STATUS_LEN = 8;
    public static final int DATA_SIZE = (LAST_NAME_LEN * 2) + (FIRST_NAME_LEN * 2)
                                        + (SSN_LEN * 2) + 4
                                        + (ACCOUNT_TYPE_LEN * 2) + (STATUS_LEN * 2)
                                        + 8 + 8;

    private static final String FILE_NAME = "BankAccounts.dat";
    private final RandomAccessFile bankAccts;
    private int numAccts;

    private static double totalSavings = 0;
    private static double totalChecking = 0;
    private static double totalCD = 0;
    private static double totalAll = 0;

    public Bank() throws IOException {
        File file = new File(FILE_NAME);
        bankAccts = new RandomAccessFile(file,"rw");
        numAccts = (int) (bankAccts.length() / DATA_SIZE);
    }

    public RandomAccessFile getBankAccts(){
        return bankAccts;
    }

    public int getNumAccounts() {
        return numAccts;
    }

    public void appendNewAccount(Account acct){
        try{
            bankAccts.seek(bankAccts.length());
            writeAccount(acct);
            numAccts++;
        }catch(IOException e){
            System.out.println("Error Writing Into File:" + e.getMessage());
        }
    }

    public void deletingAcct(int index){
        try{
            bankAccts.seek(DATA_SIZE * index);
            bankAccts.writeChars(getFile(getNumAccounts()-1));

            --numAccts;
            bankAccts.setLength(DATA_SIZE * getNumAccounts());
        }catch(IOException e){
            System.out.println("Error: Binary File DNE.");
        }
    }

    public String getFile(int n) throws IOException
    {
        Account account = getAcct(n);

        String lastName = account.getDepositor().getName().getLast();
        String firstName = account.getDepositor().getName().getFirst();
        String SSN = account.getDepositor().getSSN();
        int acctNum = account.getAcctNum();
        String acctType = account.getAcctType();
        String status = account.getStatus();
        double balance = account.getBalance();
        String date = account.getDate();

        return String.format("%10s %10s %15s %8s %16s %15s         %8.2f%22s",
                lastName,firstName,SSN,acctNum,acctType,status,balance,date);
    }

    public Account getAcct(int n)throws IOException {
        bankAccts.seek(n * DATA_SIZE);
        return readAccount();
    }

    private void writeAccount(Account acct) throws IOException {
        writeString(acct.getDepositor().getName().getLast(), LAST_NAME_LEN);
        writeString(acct.getDepositor().getName().getFirst(), FIRST_NAME_LEN);
        writeString(acct.getDepositor().getSSN(), SSN_LEN);
        bankAccts.writeInt(acct.getAcctNum());

        writeString(acct.getAcctType(), ACCOUNT_TYPE_LEN);
        writeString(acct.getStatus(), STATUS_LEN);
        bankAccts.writeDouble(acct.getBalance());
        
        if (acct instanceof CDAccount)
            bankAccts.writeLong(((CDAccount) acct).getMaturityDate().getTimeInMillis());
        else 
            bankAccts.writeLong(0);
    }

    private Account readAccount() throws IOException {
        String lastName = readString(LAST_NAME_LEN);
        String firstName = readString(FIRST_NAME_LEN);
        String ssn = readString(SSN_LEN);
        int acctNum = bankAccts.readInt();
        String acctType = readString(ACCOUNT_TYPE_LEN).trim();
        String status = readString(STATUS_LEN);
        double balance = bankAccts.readDouble();
        long maturityDateMillis = bankAccts.readLong();

        Name name = new Name(lastName, firstName);
        Depositor depositor = new Depositor(name, ssn);
        Account account;

        if (acctType.equalsIgnoreCase("CD")) {
            Calendar maturityDate = Calendar.getInstance();
            maturityDate.setTimeInMillis(maturityDateMillis);
            String mdString = String.format("%02d/%02d/%04d", maturityDate.get(Calendar.MONTH) + 1, maturityDate.get(Calendar.DAY_OF_MONTH), maturityDate.get(Calendar.YEAR));
            account = new CDAccount(depositor, acctNum, acctType, status, balance, mdString);
        } else if (acctType.equalsIgnoreCase("Checking"))
            account = new CheckingAccount(depositor, acctNum, acctType, status, balance, "");
        else
            account = new SavingsAccount(depositor, acctNum, acctType, status, balance, "");
        
        account.openHistoryFile();
        return account;
    }
    private void writeString(String str, int size) throws IOException {
        for (int i = 0; i < size; i++) {
            if (i < str.length())
                bankAccts.writeChar(str.charAt(i));
            else
                bankAccts.writeChar(0);
        }
    }
    private String readString(int size) throws IOException {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++)
            chars[i] = bankAccts.readChar();
        return new String(chars).replace('\0', ' ').trim();
    }

    private int findAcct(int requestedAccount) throws InvalidAccountException, IOException {
        for (int index = 0; index < getNumAccounts(); index++)
            if (getAcct(index).getAcctNum() == requestedAccount)
                return index;
        throw new InvalidAccountException(requestedAccount);
    }

    public int findSSN(String requestAccount) throws IOException {
        for (int index = 0; index < getNumAccounts(); index++)
            if (getAcct(index).getDepositor().getSSN().equals(requestAccount))
                return index;
        return -1;
    }

    public TransactionReceipt reopenAccount(TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt;

        try {
            int index = findAcct(ticket.getAccountNumber());

            receipt = getAcct(index).reopenAccount2(ticket);
        }catch(InvalidAccountException e){
            e = new InvalidAccountException(ticket.getAccountNumber());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), ticket.getTransactionType(), 0.00, "");
        }
        return receipt;
    }

    public TransactionReceipt closeAccount(TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt;
        try {
            int index = findAcct(ticket.getAccountNumber());

            receipt = getAcct(index).closeAccount2(ticket);
        }catch(InvalidAccountException e) {
            e = new InvalidAccountException(ticket.getAccountNumber());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), ticket.getTransactionType(), 0.00, "");
        }
        return receipt;
    }

    public TransactionReceipt clearCheck(Bank bank,Check check) throws IOException{
        int index;
        TransactionReceipt receipt;
        TransactionTicket ticket = new TransactionTicket(check.getAccountNumber(),check.getDateOfCheck(),"Check",check.getCheckAmount(),0);

        try{
            index = findAcct(check.getAccountNumber());

            receipt = getAcct(index).clearCheck2(bank,check);
        }catch(InvalidAccountException e){
            e = new InvalidAccountException(check.getAccountNumber());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), "Clear Check", 0.00, 0.00);
        }
        return receipt;
    }

    public TransactionReceipt makeWithdrawal(Bank bank,TransactionTicket ticket) throws IOException{
        TransactionReceipt receipt;
        try {
            int index = findAcct(ticket.getAccountNumber());

            receipt = getAcct(index).makeWithdrawal2(bank, ticket);
        }catch(InvalidAccountException e) {
            e = new InvalidAccountException(ticket.getAccountNumber());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), ticket.getTransactionType(), "", 0.00, 0.00);
        }
        return receipt;
    }

    public TransactionReceipt openNewAcct(TransactionTicket ticket, Account account) throws IOException{
        TransactionReceipt receipt;
        TransactionReceipt history;

        Account SA;
        Account CDA;
        Account CA;
        try
        {
            int index = findAcct(ticket.getAccountNumber());

            if (index != -1) {
                return new TransactionReceipt(ticket, false, "Error: Account #" + ticket.getAccountNumber() + " Already Exists.", "", "", 0.00, 0.00);
            }
        }
        catch(InvalidAccountException e)
        {
            if (account.getDepositor().getSSN().length() != 9) {
                return new TransactionReceipt(ticket, false, "Error:" + account.getDepositor().getSSN() + " isn't a valid SSN Due to it is Not Being 9 Digits Long.", "", "", 0.00, 0.00);
            } else if (!(ticket.getTypeAccount().equals("CD") || ticket.getTypeAccount().equals("Savings") || ticket.getTypeAccount().equals("savings") || ticket.getTypeAccount().equals("Checking") || ticket.getTypeAccount().equals("checking"))) {
                return new TransactionReceipt(ticket, false, "Error: " + ticket.getTypeAccount() + " Is An Invalid Choice", "", "", 0.00, 0.00);
            } else if (ticket.getTransactionAmount() < 0) {
                return new TransactionReceipt(ticket, false, String.format("Error: %.2f Is An Invalid Amount.", ticket.getTransactionAmount()), "", "", 0.00, 0.00);
            } else if (String.valueOf(ticket.getAccountNumber()).length() != 6)
                return new TransactionReceipt(ticket, false, "Error: Account #" + ticket.getAccountNumber() + " is Invalid Because It Isn't 6 digits long.", "", "", 0.00, 0.00);
        }
        String acctType = account.getAcctType();
        if (acctType.equals("Checking")) {
            CA = new CheckingAccount(account.getDepositor(), account.getAcctNum(), account.getAcctType(), account.getStatus(), account.getBalance(), "");

            CA.openHistoryFile();
            appendNewAccount(CA);

            history = new TransactionReceipt(ticket, "New Acc.", ticket.getTransactionAmount(), "Done", CA.getBalance(), "");
            CA.writeTransacHistData(history, account.getAcctNum());

            addAmount(this, ticket);

            receipt = new TransactionReceipt(ticket, true, "Account #" + CA.getAcctNum() + " has Been Created.", "", "", 0.00, 0.00);
        } else if (acctType.equals("CD")) {
            CDA = new CDAccount(account.getDepositor(), account.getAcctNum(), account.getAcctType(), account.getStatus(), account.getBalance(), account.getDate());

            CDA.openHistoryFile();
            appendNewAccount(CDA);

            history = new TransactionReceipt(ticket, "New Acc.", ticket.getTransactionAmount(), "Done", CDA.getBalance(), "");
            CDA.writeTransacHistData(history, account.getAcctNum());

            Bank bank = new Bank();
            addAmount(bank, ticket);

            receipt = new TransactionReceipt(ticket, true, "Account #" + CDA.getAcctNum() + " has Been Created.", "", "", 0.00, 0.00);
        } else {
            SA = new SavingsAccount(account.getDepositor(), account.getAcctNum(), account.getAcctType(), account.getStatus(), account.getBalance(), "");

            SA.openHistoryFile();
            appendNewAccount(SA);

            history = new TransactionReceipt(ticket, "New Acc.", ticket.getTransactionAmount(), "Done", SA.getBalance(), "");
            SA.writeTransacHistData(history, account.getAcctNum());

            Bank bank = new Bank();
            addAmount(bank, ticket);

            receipt = new TransactionReceipt(ticket, true, "Account #" + SA.getAcctNum() + " has Been Created.", "", "", 0.00, 0.00);
        }
        return receipt;
    }

    public TransactionReceipt deleteAcct(TransactionTicket ticket) throws IOException {
        try
        {
            int index = findAcct(ticket.getAccountNumber());
            try
            {
                if (getAcct(index).getBalance() != 0) {
                    throw new InsufficientFundsException(
                            String.format(
                                    "Error: Account #" + ticket.getAccountNumber() + " has $%.2f Therefore we Can't Delete the Account.",
                            getAcct(index).getBalance())
                    );
                }
            }
            catch(InsufficientFundsException e)
            {
                TransactionReceipt history = new TransactionReceipt(ticket,"Delete Acct.", 0, "Failed", getAcct(index).getBalance(), e.getMessage());
                getAcct(index).writeTransacHistData(history,ticket.getAccountNumber());

                return new TransactionReceipt(ticket, false, e.getMessage(), "", 0.00, "");
            }

            deletingAcct(index);
            return new TransactionReceipt(ticket, true, "", getAcct(index).getAcctType(), "", 0.00, 0.00);
        }
        catch(InvalidAccountException e)
        {
            return new TransactionReceipt(ticket, false, e.getMessage(), "", 0.00, "");
        }
    }

    public TransactionReceipt makeDeposit(Bank bank,TransactionTicket ticket) throws IOException{
        TransactionReceipt receipt;

        try{
            int index = findAcct(ticket.getAccountNumber());

            receipt = getAcct(index).makeDeposit2(bank, ticket);
        } catch(InvalidAccountException e){
            receipt = new TransactionReceipt(ticket, false, e.getMessage(), ticket.getTransactionType(), "", 0.00,0.00);
        }
        return receipt;
    }

    public TransactionReceipt getBalance(TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt;

        try{
            int index = findAcct(ticket.getAccountNumber());

            receipt =  getAcct(index).getBalance2(ticket);
        }catch(InvalidAccountException e) {
            receipt = new TransactionReceipt(ticket, false, e.getMessage(), ticket.getTransactionType(), "", 0.00, 0.00);
        }
        return receipt;
    }

    public static void totalAmountInSavingsAccts(Bank bank)throws IOException{
        for(int i=0;i< bank.getNumAccounts();i++)
        {
            Account acct = bank.getAcct(i);
            if (acct.getAcctType().equals("Savings"))
                totalSavings += acct.getBalance();
        }
    }

    public static void totalAmountInCheckingAccts(Bank bank)throws IOException{
        for(int i=0;i<bank.getNumAccounts();i++)
        {
            Account acct = bank.getAcct(i);
            if (acct.getAcctType().equals("Checking"))
                totalChecking += acct.getBalance();
        }
    }

    public static void totalAmountInCDAccts(Bank bank)throws IOException{
        for(int i=0;i<bank.getNumAccounts();i++)
        {
            Account acct = bank.getAcct(i);
            if(acct.getAcctType().equals("CD"))
                totalCD += acct.getBalance();
        }
    }

    public static void totalAmountInAllAccts(Bank bank){
        totalAll = bank.getTotalCD() + bank.getTotalChecking() + bank.getTotalSavings();
    }

    public double getTotalSavings(){
        return totalSavings;
    }

    public double getTotalCD(){
        return totalCD;
    }

    public double getTotal(){
        return totalAll;
    }

    public double getTotalChecking(){
        return totalChecking;
    }

    public static void addAmount(Bank bank, TransactionTicket ticket)throws IOException{
        for(int i=0;i<bank.getNumAccounts();i++)
        {
            Account acct = bank.getAcct(i);
            if (acct.getAcctType().equals("Savings") && acct.getAcctNum() == ticket.getAccountNumber())
                totalSavings += ticket.getTransactionAmount();
            if (acct.getAcctType().equals("Checking") && acct.getAcctNum() == ticket.getAccountNumber())
                totalChecking += ticket.getTransactionAmount();
            if (acct.getAcctType().equals("CD") && acct.getAcctNum() == ticket.getAccountNumber())
                totalCD += ticket.getTransactionAmount();
        }
        totalAll += ticket.getTransactionAmount();
    }

    public static void subtractAmount(Bank bank, TransactionTicket ticket)throws IOException{
        for(int i=0;i<bank.getNumAccounts();i++)
        {
            Account acct = bank.getAcct(i);
            if (acct.getAcctType().equals("Savings") && acct.getAcctNum() == ticket.getAccountNumber())
                totalSavings -= ticket.getTransactionAmount();
            if (acct.getAcctType().equals("Checking") && acct.getAcctNum() == ticket.getAccountNumber())
                totalChecking -= ticket.getTransactionAmount();
            if (acct.getAcctType().equals("CD") && acct.getAcctNum() == ticket.getAccountNumber())
                totalCD -= ticket.getTransactionAmount();
        }
        totalAll -= ticket.getTransactionAmount();
    }

    public static void subtractAmountCheck(Bank bank, Check check)throws IOException{
        for(int i=0;i<bank.getNumAccounts();i++)
        {
            Account acct = bank.getAcct(i);
            if (acct.getAcctType().equals("Checking") && acct.getAcctNum() == check.getAccountNumber())
                totalChecking -= check.getCheckAmount();
        }
        totalAll -= check.getCheckAmount();
    }

    public static void Bounce(){
        totalChecking -= 2.50;
        totalAll -= 2.50;
    }
}