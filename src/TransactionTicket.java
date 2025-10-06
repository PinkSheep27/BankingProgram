import java.util.Calendar;

public class TransactionTicket extends genTransactionTicket{
        private int accountNumber;
        private Calendar transactionDate;
        private String transactionType;
        private double transactionAmount;
        private int termOfCD;
        private String accountSSN;
        private int Index;
        private String typeAccount;

        public TransactionTicket(){
            accountNumber = 0;
            transactionDate = null;
            transactionType = "";
            transactionAmount = 0;
            termOfCD = 0;
            accountSSN = "";
            Index = 0;
            typeAccount = "";
        }
        public TransactionTicket(int AN, String TD, String TT,String AT, double TA, int TOCD) {
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
    public TransactionTicket(int AN, Calendar TD, String TT, double TA, int TOCD) {
        setAccountNumber(AN);
        setTransactionType(TT);
        setTransactionAmount(TA);
        setTermOfCD(TOCD);
        setTransactionDate(TD);
    }
        public TransactionTicket(int index){
            setIndex(index);
        }

        public TransactionTicket(TransactionTicket ticket){
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

        protected void setTransactionAmount(double TA) {
            transactionAmount = TA;
        }
        protected void setTransactionType(String TT) {
            transactionType = TT;
        }
        protected void setAccountNumber(int AN) {
            accountNumber = AN;
        }
        protected void setTransactionDate(Calendar TD) {
            transactionDate = TD;
        }
        protected void setTermOfCD(int TOCD) {
            termOfCD = TOCD;
        }
        protected void setAccountSSN(String ASSN){
            accountSSN = ASSN;
        }
        protected void setIndex(int index){
            Index = index;
        }
        protected void setTypeAccount(String TA){typeAccount = TA;}

        public int getAccountNumber() {
            return accountNumber;
        }
        public Calendar getTransactionDate() {
            return transactionDate;
        }
        public String getTransactionType() {
            return transactionType;
        }
        public double getTransactionAmount() {
            return transactionAmount;
        }
        public int getTermOfCD() {
            return termOfCD;
        }
        public String getAccountSSN(){
            return accountSSN;
        }
        public int getIndex(){return Index;}
        public String getTypeAccount(){return typeAccount;}
        public String getDate(){
            return String.format("%02d/%02d/%4d", transactionDate.get(Calendar.MONTH) + 1, transactionDate.get(Calendar.DAY_OF_MONTH), transactionDate.get(Calendar.YEAR));
        }
}