public abstract class genDepositor {
    private String SSN;
    private Name name;

    public genDepositor(){
        SSN = "";
        name = new Name();
    }
    public genDepositor(Name userName, String S){
        setSSN(S);
        setName(userName);
    }
    public genDepositor(genDepositor depositor){
        name = new Name(depositor.name);
        SSN = depositor.SSN;
    }
    public String toString(){
        return String.format("%-15s",SSN);
    }
    public boolean equals(Depositor myDepositor){
        if(SSN.equals(myDepositor.getSSN()) && name.equals(myDepositor.getName()))
            return true;
        else
            return false;
    }
    protected abstract void setSSN(String S);
    protected abstract void setName(Name userName);

    public abstract String getSSN();
    public abstract Name getName();
}