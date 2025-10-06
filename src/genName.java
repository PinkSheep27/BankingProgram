public abstract class genName {
    private String last;
    private String first;

public genName(){
    first = "";
    last = "";
}
public genName(String last, String first){
    this.last = last;
    this.first = first;
}
public genName(genName copy){
    last = copy.last;
    first = copy.first;
}
public String toString(){
    return String.format("%-10s %-10s",first,last);
}
public boolean equals(Name myName){
    if(last.equals(myName.getLast()) && first.equals(myName.getFirst()))
        return true;
    else
        return false;
}
protected abstract void setLast(String lastName);
protected abstract void setFirst(String firstName);
public abstract String getLast();
public abstract String getFirst();
}