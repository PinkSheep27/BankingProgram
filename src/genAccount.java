import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class genAccount {
    private Depositor depositor;
    private int acctNum;
    private String acctType;
    private String status;
    private double balance;
    private String date;
    private RandomAccessFile history;

    public genAccount(){
        depositor = new Depositor();
        acctNum = 0;
        acctType = "";
        status = "";
        balance = 0;
        date = "";
    }

    public genAccount(Depositor depositor, int AcctNum, String AcctType, String status,double balance,String date)throws IOException{
        setDepositor(depositor);
        setAcctNum(AcctNum);
        setAcctType(AcctType);
        setStatus(status);
        setBal(balance);
        setDate(date);
        history = new RandomAccessFile("Receipt" + AcctNum + ".dat","rw");
    }

    public genAccount(genAccount account)throws IOException{
        depositor = new Depositor(account.depositor);
        acctNum = account.acctNum;
        acctType = account.acctType;
        status = account.status;
        balance = account.balance;
        date = account.date;
        history = new RandomAccessFile("Receipt" + account.getAcctNum() + ".dat","rw");
    }

    public String toString() {
        return String.format("%s %s %8s %16s %15s         $%8.2f", depositor.getName().toString(), depositor.toString(), acctNum, acctType, status, balance);
    }
    public abstract TransactionReceipt reopenAccount2(TransactionTicket ticket) throws AccountClosedException, IOException;
    public abstract TransactionReceipt closeAccount2(TransactionTicket ticket) throws AccountClosedException, IOException;
    public abstract TransactionReceipt makeWithdrawal2(Bank bank, TransactionTicket ticket) throws AccountClosedException, InsufficientFundsException, InvalidAmountException, CDMaturityDateException, IOException;
    public abstract TransactionReceipt makeDeposit2(Bank bank, TransactionTicket ticket) throws InvalidAmountException, AccountClosedException, CDMaturityDateException, IOException;
    public abstract TransactionReceipt getBalance2(TransactionTicket ticket) throws IOException;
    public abstract TransactionReceipt clearCheck2(Bank bank, Check check) throws InsufficientFundsException, AccountClosedException, IOException;
    protected abstract void setDepositor(Depositor d);
    protected abstract void setAcctNum(int AN);
    protected abstract void setAcctType(String AT);
    protected abstract void setStatus(String s);
    protected abstract void setBal(double b);
    protected abstract void setDate(String s);

    public abstract Depositor getDepositor();
    public abstract int getAcctNum();
    public abstract String getAcctType();
    public abstract String getStatus();
    public abstract double getBalance();
    public abstract String getDate();
    public abstract long getHistorySize() throws IOException;
    public abstract void writeTransacHistData(TransactionReceipt receipt,int acctNum) throws IOException;
    public abstract TransactionReceipt getHistory(int n)throws IOException;
    public boolean equals(genAccount myAccount){
        if(acctNum == myAccount.acctNum && acctType.equals(myAccount.acctType) && status.equals(myAccount.status) && balance == myAccount.balance && depositor.equals(myAccount.depositor))
            return true;
        else
            return false;
    }
}