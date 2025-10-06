import java.util.Calendar;

public class TransactionReceipt extends genTransactionReceipt{
    private TransactionTicket transactionTicket;
    private Check check;
    private boolean successIndicatorFlag;
    private String reasonForFailure;
    private String accountType;
    private double preTransactionBalance;
    private double postTransactionBalance;
    private String status;
    private Calendar postTransactionMaturityDate;

    private String typeTransaction;
    private double transactionAmount;
    private String transactionStatus;
    private double balance;
    private int accountNumber;
    private int termOfCD;

    public TransactionReceipt(TransactionTicket ticket,String TT, double TA, String TS, double b, String RFF) {
        setTransactionTicket(ticket);
        setTypeTransaction(TT);
        setTransactionAmount(TA);
        setTransactionStatus(TS);
        setReasonForFailure(RFF);
        setBalance(b);
    }
    public TransactionReceipt (TransactionTicket ticket, boolean SIF, String RFF, String AT, double BTB, double ATB){
        setTransactionTicket(ticket);
        setSuccessIndicatorFlag(SIF);
        setReasonForFailure(RFF);
        setAccountType(AT);
        setPreTransactionBalance(BTB);
        setPostTransactionBalance(ATB);
        setTermOfCD(transactionTicket.getTermOfCD());
        setAccountNumber(transactionTicket.getAccountNumber());
        setTypeTransaction(transactionTicket.getTransactionType());
        setTransactionAmount(transactionTicket.getTransactionAmount());
    }
    public TransactionReceipt (TransactionTicket TT, boolean SIF, String RFF, String AT, double BTB, String s){
        setTransactionTicket(TT);
        setSuccessIndicatorFlag(SIF);
        setReasonForFailure(RFF);
        setAccountType(AT);
        setPreTransactionBalance(BTB);
        setStatus(s);
        setTermOfCD(transactionTicket.getTermOfCD());
        setAccountNumber(transactionTicket.getAccountNumber());
        setTypeTransaction(transactionTicket.getTransactionType());
        setTransactionAmount(transactionTicket.getTransactionAmount());
    }
    public TransactionReceipt (TransactionTicket TT, boolean SIF,String RFF, String AT,String s,double BTB, double ATB){
        setTransactionTicket(TT);
        setSuccessIndicatorFlag(SIF);
        setReasonForFailure(RFF);
        setAccountType(AT);
        setPreTransactionBalance(BTB);
        setPostTransactionBalance(ATB);
        setStatus(s);
        setTermOfCD(transactionTicket.getTermOfCD());
        setAccountNumber(transactionTicket.getAccountNumber());
        setTypeTransaction(transactionTicket.getTransactionType());
        setTransactionAmount(transactionTicket.getTransactionAmount());
    }
    public TransactionReceipt (TransactionTicket TT, boolean SIF,String RFF, String AT, String s,double BTB, double ATB,String PTMD){
        setTransactionTicket(TT);
        setSuccessIndicatorFlag(SIF);
        setReasonForFailure(RFF);
        setAccountType(AT);
        setPreTransactionBalance(BTB);
        setPostTransactionBalance(ATB);
        setStatus(s);
        postTransactionMaturityDate = Calendar.getInstance();
        postTransactionMaturityDate.clear();
        String[] mdArray = PTMD.split("/");
        postTransactionMaturityDate.set(Integer.parseInt(mdArray[2]), Integer.parseInt(mdArray[0]) - 1, Integer.parseInt(mdArray[1]));
        setPostTransactionMaturityDate(postTransactionMaturityDate);
        setTermOfCD(transactionTicket.getTermOfCD());
        setAccountNumber(transactionTicket.getAccountNumber());
        setTypeTransaction(transactionTicket.getTransactionType());
        setTransactionAmount(transactionTicket.getTransactionAmount());
    }
    public TransactionReceipt(TransactionReceipt receipt){
        typeTransaction = receipt.typeTransaction;
        successIndicatorFlag = receipt.successIndicatorFlag;
        if(typeTransaction.equals("New Account"))
            accountType = receipt.transactionTicket.getTypeAccount();
        else
            accountType = receipt.accountType;
        preTransactionBalance = receipt.preTransactionBalance;
        postTransactionBalance = receipt.postTransactionBalance;
        postTransactionMaturityDate = receipt.postTransactionMaturityDate;
        transactionTicket = new TransactionTicket(receipt.transactionTicket);
        reasonForFailure = receipt.reasonForFailure;
        status = receipt.status;
        balance = receipt.balance;
        transactionAmount = receipt.transactionAmount;
        transactionStatus = receipt.transactionStatus;
        accountNumber = receipt.accountNumber;
        termOfCD = receipt.termOfCD;
    }

    public String toString(){
        if(typeTransaction.equals("Deleting")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }
            return String.format("Account Type: " + accountType + "\nAccount #" + accountNumber + " has Been Deleted.");
        }
        if(typeTransaction.equals("Reopening")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }
            return String.format("Account #" + transactionTicket.getAccountNumber() + " Successfully Reopened.");
        }
        if(typeTransaction.equals("Closing")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }
            return String.format("Account #" + transactionTicket.getAccountNumber() + " Successfully Closed.");
        }
        if(typeTransaction.equals("Check")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }
            return String.format("Check Cleared\nAccount #" + accountNumber + " has Withdrawn $%.2f.\nOld Account Balance is: $%.2f \nNew Account Balance is: $%.2f", transactionAmount, preTransactionBalance, postTransactionBalance);
        }
        if(typeTransaction.equals("New Account")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }if (transactionTicket.getTypeAccount().equals("CD")) {
                String stringDate = String.format("%02d/%02d/%4d", transactionTicket.getTransactionDate().get(Calendar.MONTH) + 1, transactionTicket.getTransactionDate().get(Calendar.DAY_OF_MONTH), transactionTicket.getTransactionDate().get(Calendar.YEAR));
                return String.format("Account Number:%s\nAccount Type:%s\nMaturity Date:%s\nBalance: $%.2f", accountNumber, transactionTicket.getTypeAccount(),stringDate, transactionAmount);
            } else
                return String.format("Account Type:%s\nAccount Number:%s\nBalance: $%.2f", transactionTicket.getTypeAccount(), accountNumber, transactionTicket.getTransactionAmount());
        }
        if(typeTransaction.equals("Balance")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }else if (accountType.equals("CD")) {
                String stringDate = String.format("%02d/%02d/%4d", postTransactionMaturityDate.get(Calendar.MONTH) + 1, postTransactionMaturityDate.get(Calendar.DAY_OF_MONTH), postTransactionMaturityDate.get(Calendar.YEAR));
                return String.format("Account Number:%s\nAccount Type:%s\nMaturity Date:%s\nBalance: $%.2f", accountNumber, accountType, stringDate, postTransactionBalance);
            } else
                return String.format("Account Type:%s\nAccount Number:%s\nBalance: $%.2f", accountType, accountNumber, postTransactionBalance);
        }
        if(typeTransaction.equals("Deposit")) {
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }else if(accountType.equals("CD")) {
                String stringDate = String.format("%02d/%02d/%4d", postTransactionMaturityDate.get(Calendar.MONTH) + 1, postTransactionMaturityDate.get(Calendar.DAY_OF_MONTH), postTransactionMaturityDate.get(Calendar.YEAR));
                return String.format("Account Number:%s\nAccount Type:%s\nTerm Of CD:%s\nMaturity Date:%s\nPre Balance: $%.2f\nDeposit Amount: $%.2f\nPost Balance: $%.2f", accountNumber, accountType, stringDate, termOfCD,preTransactionBalance,transactionAmount,postTransactionBalance);
            }else{
                return String.format("Account Type:%s\nAccount Number:%s\nPre Balance: $%.2f\nDeposit Amount:$%.2f\nPost Balance:$%.2f",accountType,accountNumber,preTransactionBalance,transactionAmount,postTransactionBalance);
        }}
        if(typeTransaction.equals("Withdrawal")){
            if (successIndicatorFlag == false) {
                return String.format(reasonForFailure);
            }else if(accountType.equals("CD")) {
                String stringDate = String.format("%02d/%02d/%4d", postTransactionMaturityDate.get(Calendar.MONTH) + 1, postTransactionMaturityDate.get(Calendar.DAY_OF_MONTH), postTransactionMaturityDate.get(Calendar.YEAR));
                return String.format("Account Number:%s\nAccount Type:%s\nTerm Of CD:%s\nMaturity Date:%s\nPre Balance: $%.2f\nWithdrawal Amount: $%.2f\nPost Balance: $%.2f", accountNumber, accountType, stringDate, termOfCD,preTransactionBalance,transactionAmount,postTransactionBalance);
            }else{
                return String.format("Account Type:%s\nAccount Number:%s\nPre Balance: $%.2f\nWithdrawal Amount: $%.2f\nPost Balance: $%.2f",accountType,accountNumber,preTransactionBalance,transactionAmount,postTransactionBalance);
        }}
        else{
            Calendar today = Calendar.getInstance();
            String todayS = String.format("%02d/%02d/%04d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

            return String.format("%10s %12s %8.2f %10s %7.2f      %10s", todayS, typeTransaction, transactionAmount, transactionStatus, balance, reasonForFailure);
        }
    }

    protected void setReasonForFailure(String RFF){
        reasonForFailure = RFF;
    }
    protected void setTransactionTicket(TransactionTicket TT){
        transactionTicket = TT;
    }
    protected void setSuccessIndicatorFlag(boolean SIF){
        successIndicatorFlag = SIF;
    }
    protected void setAccountType(String AT){
        accountType = AT;
    }
    protected void setPreTransactionBalance(double BTB){
        preTransactionBalance = BTB;
    }
    protected void setPostTransactionBalance(double ATB){
        postTransactionBalance = ATB;
    }
    protected void setPostTransactionMaturityDate(Calendar PTMD) {
        postTransactionMaturityDate = PTMD;
    }
    protected void setCheck(Check C){
        check = C;
    }
    protected void setStatus(String s){
        status = s;
    }
    protected void setTypeTransaction(String TT){
        typeTransaction = TT;
    }
    protected void setTransactionAmount(double TA){
        transactionAmount = TA;
    }
    protected void setTransactionStatus(String TS){
        transactionStatus = TS;
    }
    protected void setBalance(double b){
        balance = b;
    }
    protected void setAccountNumber(int i){accountNumber = i;}
    protected void setTermOfCD(int tocd){termOfCD = tocd;}

    public TransactionTicket getTransactionTicket(){
        return transactionTicket;
    }
    public String getReasonForFailure(){
        return reasonForFailure;
    }
    public String getAccountType(){
        return accountType;
    }
    public boolean getSuccessIndicatorFlag(){
        return successIndicatorFlag;
    }
    public double getPreTransactionBalance() {
        return preTransactionBalance;
    }
    public double getPostTransactionBalance() {
        return postTransactionBalance;
    }
    public Calendar getPostTransactionMaturityDate() {
        return postTransactionMaturityDate;
    }
    public Check getCheck(){
        return check;
    }
    public String getStatus(){return status;}
    public String getTypeTransaction(){return typeTransaction;}
    public String getTransactionStatus(){return transactionStatus;}
    public double getTransactionAmount(){return transactionAmount;}
    public double getBalance(){return balance;}
    public String getDate() {
        String str = "";
        if (accountType.equals("CD"))
            str = String.format("%02d/%02d/%4d", postTransactionMaturityDate.get(Calendar.MONTH) + 1, postTransactionMaturityDate.get(Calendar.DAY_OF_MONTH), postTransactionMaturityDate.get(Calendar.YEAR));
        return str;
    }
}