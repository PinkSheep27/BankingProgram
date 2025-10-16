public class Name extends genName{
    private String last;
    private String first;

    public Name(){
        first = "";
        last = "";
    }

    public Name(String lastName, String firstName){
        setLast(lastName);
        setFirst(firstName);
    }
    public Name(Name nameCopy){
        last = nameCopy.last;
        first = nameCopy.first;
    }
    public String toString(){
        return String.format("%-10s %-11s",first,last);
    }
    public boolean equals(Name myName){
        if(last.equals(myName.last) && first.equals(myName.first))
            return true;
        else
            return false;
    }

    protected void setLast(String lastName) {
        last = lastName;
    }

    protected void setFirst(String firstName) {
        first = firstName;
    }

    public String getLast(){
        return last;
    }

    public String getFirst(){
        return first;
    }
}