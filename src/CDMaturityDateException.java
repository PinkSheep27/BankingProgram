public class CDMaturityDateException extends Exception{
    public CDMaturityDateException(int acctNum,String date){
        super("Error: The Maturity Date for Account #" + acctNum + "Hasn't Come Yet. Please Try Again On" + date + ".");
    }
    public CDMaturityDateException(String str){
        super(str);
    }
}