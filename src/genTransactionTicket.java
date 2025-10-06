import java.util.Calendar;

public abstract class genTransactionTicket {
    private int accountNumber;
    private Calendar transactionDate;
    private String transactionType;
    private double transactionAmount;
    private int termOfCD;
    private String accountSSN;
    private int Index;
    private String typeAccount;

    public genTransactionTicket(){
        accountNumber = 0;
        transactionDate = null;
        transactionType = "";
        transactionAmount = 0;
        termOfCD = 0;
        accountSSN = "";
        Index = 0;
        typeAccount = "";
    }
    public genTransactionTicket(int AN, String TD, String TT,String AT, double TA, int TOCD) {
        setTypeAccount(AT);
        setAccountNumber(AN);
        setTransactionType(TT);
        setTransactionAmount(TA);
        setTermOfCD(TOCD);
        transactionDate = Calendar.getInstance();
        transactionDate.clear();
        String[] mdArray = TD.split("/");
        transactionDate.set(Integer.parseInt(mdArray[2]), Integer.parseInt(mdArray[0]) - 1, Integer.parseInt(mdArray[1]));
        setTransactionDate(transactionDate);
    }
    public genTransactionTicket(int AN, Calendar TD, String TT, double TA, int TOCD) {
        setAccountNumber(AN);
        setTransactionType(TT);
        setTransactionAmount(TA);
        setTermOfCD(TOCD);
        setTransactionDate(TD);
    }
    public genTransactionTicket(int index){
        setIndex(index);
    }
    public genTransactionTicket(genTransactionTicket ticket){
        Index = ticket.Index;
        accountSSN = ticket.accountSSN;
        accountNumber = ticket.accountNumber;
        transactionDate = ticket.transactionDate;
        transactionType = ticket.transactionType;
        transactionAmount = ticket.transactionAmount;
        termOfCD = ticket.termOfCD;
        typeAccount = ticket.typeAccount;
    }
    public String toString(){
        if(transactionType.equals("CD")) {
            String stringDate = String.format("%02d/%02d/%4d", transactionDate.get(Calendar.MONTH) + 1, transactionDate.get(Calendar.DAY_OF_MONTH), transactionDate.get(Calendar.YEAR));
            return String.format("Account Number:%s\nAccount Type:%s\nMaturity Date:%s\nBalance: $%.2f", accountNumber, transactionType, stringDate,transactionAmount);
        }else
            return String.format("Account Type:%s\nAccount Number:%s\nBalance: $%.2f",transactionType,accountNumber,transactionAmount);
    }
    protected abstract void setTransactionAmount(double TA);
    protected abstract void setTransactionType(String TT);
    protected abstract void setAccountNumber(int AN);
    protected abstract void setTransactionDate(Calendar TD);
    protected abstract void setTermOfCD(int TOCD);
    protected abstract void setAccountSSN(String ASSN);
    protected abstract void setIndex(int index);
    protected abstract void setTypeAccount(String TA);

    public abstract int getAccountNumber();
    public abstract Calendar getTransactionDate();
    public abstract String getTransactionType();
    public abstract double getTransactionAmount();
    public abstract int getTermOfCD();
    public abstract String getAccountSSN();
    public abstract int getIndex();
    public abstract String getTypeAccount();
    public String getDate(){
        return String.format("%02d/%02d/%4d", transactionDate.get(Calendar.MONTH) + 1, transactionDate.get(Calendar.DAY_OF_MONTH), transactionDate.get(Calendar.YEAR));
    }
}