import java.io.IOException;
import java.util.Calendar;

public class CheckingAccount extends Account{

    private double balance;

    public CheckingAccount()throws IOException{
        super();
    }
    public CheckingAccount(Depositor d, int AN,String AT,String s,double b,String DOC)throws IOException{
        super(d,AN,AT,s,b,DOC);
    }
    public CheckingAccount(CheckingAccount CA)throws IOException{
        super(CA.getDepositor(),CA.getAcctNum(),CA.getAcctType(),CA.getStatus(),CA.getBalance(),CA.getDate());
        balance = CA.getBalance();
    }
    public TransactionReceipt makeWithdrawal2(Bank bank, TransactionTicket ticket)throws IOException {
        String success;
        TransactionReceipt history;
        TransactionReceipt receipt;
        try{
            if (super.getStatus().equals("open")) {
                if (ticket.getTransactionAmount() <= 0.00) {
                    success = String.format("Account Number: %s\n$%.2f Is An Invalid Withdrawal Amount", super.getAcctNum(), ticket.getTransactionAmount());
                    throw new InvalidAmountException(success);
                } else if (ticket.getTransactionAmount() > super.getBalance()) {
                    success = String.format("$%.2f Is Over The Balance And Can't be Withdrawn.", ticket.getTransactionAmount());
                    throw new InsufficientFundsException(success);
                }else{
                    double balance = super.getBalance();
                    double balance2 = balance - ticket.getTransactionAmount();

                    TransactionTicket newTicket = new TransactionTicket(ticket.getAccountNumber(),ticket.getDate(),ticket.getTransactionType(),ticket.getTypeAccount(),balance2,ticket.getTermOfCD());
                    super.setBal(balance2);
                    Bank.subtractAmount(bank, newTicket);

                    history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Done", balance2, "");
                    super.writeTransacHistData(history,super.getAcctNum());

                    receipt = new TransactionReceipt(ticket, true, "", super.getAcctType(), super.getStatus(), balance, balance2);
                }}else {
                success = String.format("Can't Withdraw $%.2f Due to Account #" + super.getAcctNum() + " Being Closed.", ticket.getTransactionAmount());
                throw new AccountClosedException(success);
            }
        }catch(InsufficientFundsException | InvalidAmountException | AccountClosedException e){
            history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Failed", super.getBalance(), e.getMessage());
            super.writeTransacHistData(history,super.getAcctNum());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), super.getAcctType(), super.getStatus(), super.getBalance(), 0);
        }
        return receipt;
    }
    public TransactionReceipt makeDeposit2(Bank bank,TransactionTicket ticket) throws IOException {
        String success;
        TransactionReceipt history;
        TransactionReceipt receipt;
        try{
            if (super.getStatus().equals("open")) {
                if (ticket.getTransactionAmount() <= 0.00) {
                    success = String.format("Account Number: %s\n$%.2f Is An Invalid Deposit Amount", super.getAcctNum(), ticket.getTransactionAmount());
                    throw new InvalidAmountException(success);
                }else {
                    double preBalance = super.getBalance();
                    double postBalance = ticket.getTransactionAmount() + preBalance;
                    super.setBal(postBalance);
                    Bank.addAmount(bank, ticket);

                    history = new TransactionReceipt(ticket, "Depositing", ticket.getTransactionAmount(), "Done", postBalance, "");
                    super.writeTransacHistData(history,super.getAcctNum());

                    receipt = new TransactionReceipt(ticket, true, "", super.getAcctType(), super.getStatus(), preBalance, postBalance);
                }}else {
                success = String.format("Error:Account #%s is closed.\n",super.getAcctNum());
                throw new AccountClosedException(success);
            }
        }catch(InvalidAmountException | AccountClosedException e){
            history = new TransactionReceipt(ticket,"Depositing", ticket.getTransactionAmount(), "Failed", super.getBalance(), e.getMessage());
            super.writeTransacHistData(history,super.getAcctNum());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), super.getAcctType(), super.getStatus(), super.getBalance(), 0.00);
        }
        return receipt;
    }
    public TransactionReceipt clearCheck2(Bank bank, Check check) throws IOException {
        String success;
        double normBal;
        double postBal;

        TransactionReceipt history;
        TransactionReceipt receipt;

        Calendar today = Calendar.getInstance();
        Calendar today1 = Calendar.getInstance();
        today.add(Calendar.MONTH, -6);

        TransactionTicket ticket = new TransactionTicket(check.getAccountNumber(),check.getDateOfCheck(),"Check",check.getCheckAmount(),0);
        TransactionTicket copyOfTicket = new TransactionTicket(ticket);
try{
        if (getStatus().equals("open")) {
            if(!(check.getDateOfCheck().after(today))){
                throw new CheckTooOldException();
            }else if(!(check.getDateOfCheck().before(today1))){
                throw new PostDatedCheckException();
            }
            if (super.getBalance() < check.getCheckAmount()) {
                normBal = super.getBalance();
                postBal = normBal - 2.5;
                super.setBal(postBal);
                Bank.Bounce();

                success = String.format("Account #" + super.getAcctNum() + "Doesn't Contain Enough Money for Withdrawal.\n Deducting Your Account $2.50 for Bouncing the Check.\n Old Account Balance is: $%.2f \nNew Account Balance is: $%.2f", normBal, postBal);
                throw new InsufficientFundsException(success);
            } else {
                normBal = super.getBalance();
                postBal = normBal - check.getCheckAmount();
                super.setBal(postBal);

                Bank.subtractAmountCheck(bank, check);

                success = String.format("Check Cleared\nAccount #" + super.getAcctNum() + " has Withdrawn $%.2f.\nOld Account Balance is: $%.2f \nNew Account Balance is: $%.2f", check.getCheckAmount(), normBal, postBal);

                history = new TransactionReceipt(copyOfTicket,"Check.", check.getCheckAmount(), "Done", super.getBalance(), "");
                super.writeTransacHistData(history,super.getAcctNum());

                receipt = new TransactionReceipt(copyOfTicket, true, success, super.getAcctType(), normBal, super.getBalance());
            }
        }else {
            success = String.format("Can't Clear Check of $%.2f Due to Account #" + super.getAcctNum() + " Being Closed.", check.getCheckAmount());
            history = new TransactionReceipt(copyOfTicket, "Check.", check.getCheckAmount(), "Failed", super.getBalance(), success);
            super.writeTransacHistData(history,super.getAcctNum());

            throw new AccountClosedException(success);
        }}catch(InsufficientFundsException e){
            history = new TransactionReceipt(copyOfTicket,"Check.",check.getCheckAmount(), "Failed", super.getBalance(), "Deducting Your Account $2.50 for Bouncing the Check.");
            super.writeTransacHistData(history,super.getAcctNum());

            receipt = new TransactionReceipt(copyOfTicket, false, e.getMessage(),"Clear Check",0.00,0.00);
        } catch(CheckTooOldException | PostDatedCheckException | AccountClosedException e){
            history = new TransactionReceipt(ticket,"Check.", check.getCheckAmount(), "Failed", super.getBalance(), e.getMessage());
            super.writeTransacHistData(history,super.getAcctNum());

            receipt = new TransactionReceipt(copyOfTicket, false, e.getMessage(),"Clear Check",0.00,0.00);
    }
    return receipt;
}
    public String toString(){
        return String.format("%s", super.toString());
    }

}