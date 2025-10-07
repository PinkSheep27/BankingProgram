import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class Account extends genAccount{
    private Depositor depositor;
    private int acctNum;
    private String acctType;
    private String status;
    private double balance;
    private String date;
    private RandomAccessFile history;
    final static private int TYPE_LEN = 12;
    final static private int STATUS_LEN = 8;
    final static private int REASON_LEN = 80;
    final private long RECEIPT_SIZE = (TYPE_LEN * 2) + 8 + (STATUS_LEN * 2) + 12 + (REASON_LEN * 2);

    public Account () throws IOException {
        depositor = new Depositor();
        acctNum = 0;
        acctType = "";
        status = "";
        balance = 0;
        date = "";
    }
    public Account (Depositor d, int AN, String AT, String s,double balance,String date) throws IOException {
        setDepositor(d);
        setAcctNum(AN);
        setAcctType(AT);
        setStatus(s);
        setDate(date);
        setBal(balance);
    }
    public Account(Account account)throws IOException{
        depositor = new Depositor(account.depositor);
        acctNum = account.acctNum;
        acctType = account.acctType;
        status = account.status;
        balance = account.balance;
        history = account.history;
        date = account.date;
        history = new RandomAccessFile("Receipt" + account.acctNum + ".dat","rw");
    }

    public String toString() {
        return String.format("%s %s %8s %16s %15s         %8.2f", depositor.getName().toString(), depositor.toString(), acctNum, acctType, status, balance);
    }

    protected void setDepositor(Depositor d){
        depositor = d;
    }
    protected void setAcctNum(int AN){
        acctNum = AN;
    }
    protected void setAcctType(String AT){
        acctType = AT;
    }
    protected void setStatus(String s){
        status = s;
    }
    protected void setBal(double b){
        balance = b;
    }
    protected void setDate(String s){ date = s;}

    public Depositor getDepositor(){
        return depositor;
    }
    public int getAcctNum(){
        return acctNum;
    }
    public String getAcctType(){
        return acctType;
    }
    public String getStatus(){
        return status;
    }
    public double getBalance(){
        return balance;
    }
    public String getDate(){ return date;}

    public Calendar getMaturityDate(){return null;}
    public void openHistoryFile() throws IOException{
        history = new RandomAccessFile("Receipt" + acctNum + ".dat", "rw");
    }


    public TransactionReceipt getHistory(int n) throws IOException {
        history.seek(n * RECEIPT_SIZE);

        String transacType = readString(TYPE_LEN);
        double transacAmount = history.readDouble();
        String status = readString(STATUS_LEN);
        double acctBal = history.readDouble();
        String errorStr = readString(REASON_LEN);

        TransactionTicket ticket = new TransactionTicket(getAcctNum(), Calendar.getInstance(), transacType, transacAmount, 0);
        return new TransactionReceipt(ticket, transacType, transacAmount, status, acctBal, errorStr);
    }

    private void writeString(String str, int size) throws IOException {
        for (int i = 0; i < size; i++){
            if (str != null && i < str.length())
                history.writeChar(str.charAt(i));
            else
                history.writeChar(0);
        }
    }

    private String readString(int size) throws IOException {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = history.readChar();
        }
        return new String(chars).replace('\0', ' ').trim();
    }

    public void writeTransacHistData(TransactionReceipt history,int acctNum)throws IOException{
        this.history.seek(this.history.length());

        writeString(history.getTypeTransaction(), TYPE_LEN);
        this.history.writeDouble(history.getTransactionAmount());
        writeString(history.getTransactionStatus(), STATUS_LEN);
        this.history.writeDouble(history.getBalance());
        writeString(history.getReasonForFailure(), REASON_LEN);
    }

    public String getHistoryData(TransactionReceipt history,int acctNum){
        Calendar tdy = Calendar.getInstance();
        String strDate = String.format("%02d/%02d/%4d", tdy.get(Calendar.MONTH) + 1, tdy.get(Calendar.DAY_OF_MONTH), tdy.get(Calendar.YEAR));
        try (RandomAccessFile receipt = new RandomAccessFile("Receipt" + acctNum + ".dat", "rw")){
            String str = String.format("%-10s %-10s %-10.2f %-10s %-10.2f %-60s", strDate, history.getTypeTransaction(), history.getTransactionAmount(), history.getTransactionStatus(), history.getBalance(), history.getReasonForFailure());

            receipt.close();
            return str;
        }catch(IOException e){
            System.out.println("Error: File DNE.");
            return "";
        }
    }

    public long getHistorySize() throws IOException {
        return history.length() / RECEIPT_SIZE;
    }
    public TransactionReceipt makeWithdrawal2(Bank bank, TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt = null;

        return receipt;
    }
    public TransactionReceipt makeDeposit2(Bank bank,TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt = null;

        return receipt;
    }
    public TransactionReceipt clearCheck2(Bank bank, Check check) throws IOException {
        TransactionReceipt receipt = null;

        return receipt;
    }
    public TransactionReceipt reopenAccount2(TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt;
        TransactionReceipt history;
        Account SA;
        Account CDA;
        Account CA;
        String success;

        System.out.println("yooo");
        if (getStatus().equals("closed")) {
            status = "open";

            history = new TransactionReceipt(ticket,"ReOpen A.", ticket.getTransactionAmount(), "Done", getBalance(), "");
            writeTransacHistData(history,getAcctNum());

            if(acctType.equals("Checking")){
                CA = new CheckingAccount(depositor,acctNum,acctType,status,balance,"");
                receipt = new TransactionReceipt(ticket, true, "", acctType, CA.getBalance(), CA.getStatus());
            }else if(acctType.equals("Savings")){
                SA = new SavingsAccount(depositor,acctNum,acctType,status,balance,"");
                receipt = new TransactionReceipt(ticket, true, "", acctType, SA.getBalance(), SA.getStatus());
            }else{
                CDA = new CDAccount(depositor,acctNum,acctType,status,balance,date);
                receipt = new TransactionReceipt(ticket, true, "", acctType, CDA.getBalance(), CDA.getStatus());
            }
        }
        else {
            success = String.format("Can't Reopen Account #" + acctNum + " it's Already Open.");
            history = new TransactionReceipt(ticket,"ReOpen A.", ticket.getTransactionAmount(), "Failed", balance, success);
            writeTransacHistData(history,getAcctNum());

            receipt = new TransactionReceipt(ticket, false, success, ticket.getTransactionType(), 0.00, "");
        }
        return receipt;
    }
    public TransactionReceipt closeAccount2(TransactionTicket ticket) throws IOException {
        TransactionReceipt receipt;
        TransactionReceipt history;
        String success;

        Account SA;
        Account CDA;
        Account CA;

        if (getStatus().equals("open")) {
            status = "closed";
            history = new TransactionReceipt(ticket,"Close A.", ticket.getTransactionAmount(), "Done", balance, "");
            writeTransacHistData(history,getAcctNum());

            if(acctType.equals("Checking")){
                CA = new CheckingAccount(depositor,acctNum,acctType,status,balance,"");
                receipt = new TransactionReceipt(ticket, true, "", acctType, CA.getBalance(), CA.getStatus());
            }else if(acctType.equals("Savings")){
                SA = new SavingsAccount(depositor,acctNum,acctType,status,balance,"");
                receipt = new TransactionReceipt(ticket, true, "", acctType, SA.getBalance(), SA.getStatus());
            }else{
                CDA = new CDAccount(depositor,acctNum,acctType,status,balance,date);
                receipt = new TransactionReceipt(ticket, true, "", acctType, CDA.getBalance(), CDA.getStatus());
                }
        }
        else {
            success = String.format("Can't Close Account #" + acctNum + " it's Already Closed.");
            history = new TransactionReceipt(ticket, "Close A.", ticket.getTransactionAmount(), "Failed", balance, success);
            writeTransacHistData(history,getAcctNum());

            receipt = new TransactionReceipt(ticket, false, success, ticket.getTransactionType(), 0.00, "");
        }
        return receipt;
    }

    public TransactionReceipt getBalance2(TransactionTicket ticket) throws IOException {
        TransactionReceipt history;
        TransactionReceipt receipt;

        Account SA;
        Account CDA;
        Account CA;

        history = new TransactionReceipt(ticket,"Bal.", ticket.getTransactionAmount(), "Done", balance, "");
        writeTransacHistData(history,getAcctNum());

        if (acctType.equals("CD")) {
            CDA = new CDAccount(depositor,acctNum,acctType,status,balance,date);
            receipt = new TransactionReceipt(ticket, true, "", acctType, CDA.getStatus(), CDA.getBalance(), CDA.getBalance(),CDA.getDate());
        } else if(acctType.equals("Checking")){
            CA = new CheckingAccount(depositor,acctNum,acctType,status,balance,"");
            receipt = new TransactionReceipt(ticket, true, "", acctType, CA.getStatus(), CA.getBalance(), CA.getBalance());
        }else{
            SA = new SavingsAccount(depositor,acctNum,acctType,status,balance,"");
            receipt = new TransactionReceipt(ticket, true, "", acctType, SA.getStatus(), SA.getBalance(), SA.getBalance());
        }
        return receipt;
    }
    public boolean equals(Account myAccount){
        if(acctNum == myAccount.getAcctNum() && acctType.equals(myAccount.getAcctType()) && status.equals(myAccount.getStatus()) && balance == myAccount.getBalance() && depositor.equals(myAccount.getDepositor()))
            return true;
        else
            return false;
    }
}