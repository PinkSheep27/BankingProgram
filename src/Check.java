import java.util.Calendar;

public class Check {
    private int accountNumber;
    private double checkAmount;
    private Calendar dateOfCheck;

    public Check (int AN,double CA,Calendar DOC){
       setAccountNumber(AN);
       setCheckAmount(CA);
       setDateOfCheck(DOC);
    }
    public Check(Check check){
        accountNumber = check.accountNumber;
        checkAmount = check.checkAmount;
        dateOfCheck = check.dateOfCheck;
    }
    public String toString(){
        String stringDate = String.format("%02d/%02d/%4d", dateOfCheck.get(Calendar.MONTH) + 1, dateOfCheck.get(Calendar.DAY_OF_MONTH), dateOfCheck.get(Calendar.YEAR));
        return String.format("%s $%.2f %s",accountNumber,checkAmount,stringDate);
    }

    private void setAccountNumber(int AN){
        accountNumber = AN;
    }
    private void setCheckAmount(double CA){
        checkAmount = CA;
    }
    private void setDateOfCheck(Calendar DOC){
        dateOfCheck = DOC;
    }

    public int getAccountNumber(){
        return accountNumber;
    }
    public double getCheckAmount() {
        return checkAmount;
    }
    public Calendar getDateOfCheck(){
        return dateOfCheck;
    }

    public String getDate(){
        return String.format("%02d/%02d/%4d", dateOfCheck.get(Calendar.MONTH) + 1, dateOfCheck.get(Calendar.DAY_OF_MONTH), dateOfCheck.get(Calendar.YEAR));
    }
}