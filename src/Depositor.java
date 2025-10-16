public class Depositor extends genDepositor{
    private String SSN;
    private Name name;

    public Depositor(){
        SSN = "";
        name = new Name();
    }

    public Depositor(Name userName, String S){
        setSSN(S);
        setName(userName);
    }

    public Depositor(Depositor depositor) {
        name = new Name(depositor.name);
        SSN = depositor.SSN;
    }

    public String toString(){
        return String.format("%-13s",SSN);
    }
    public boolean equals(Depositor myDepositor){
        if(SSN.equals(myDepositor.getSSN()) && name.equals(myDepositor.getName()))
            return true;
        else
            return false;
    }

    protected void setSSN(String S){
        SSN = S;
    }
    protected void setName(Name userName){
        name = userName;
    }

    public String getSSN(){
        return SSN;
    }
    public Name getName(){
        return name;
    }
}