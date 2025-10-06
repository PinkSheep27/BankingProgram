import java.io.IOException;

public class SavingsAccount extends Account{
    private double balance;

    public SavingsAccount()throws IOException {
        super();
    }

    public SavingsAccount(Depositor d, int AN, String AT, String s, double b, String date)throws IOException{
        super(d,AN,AT,s,b,date);
    }

    public SavingsAccount(SavingsAccount SA)throws IOException{
        super(SA.getDepositor(),SA.getAcctNum(),SA.getAcctType(), SA.getStatus(),SA.getBalance(),SA.getDate());
    }
    public TransactionReceipt makeWithdrawal2(Bank bank, TransactionTicket ticket) throws IOException {
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
        }}catch(InsufficientFundsException | InvalidAmountException | AccountClosedException e){
            history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Failed", super.getBalance(), e.getMessage());
            super.writeTransacHistData(history,super.getAcctNum());

            receipt = new TransactionReceipt(ticket, false, e.getMessage(), "", "", 0.00, 0.00);
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
            }}catch(AccountClosedException | InvalidAmountException e){
                history = new TransactionReceipt(ticket,"Depositing", ticket.getTransactionAmount(), "Failed", super.getBalance(), e.getMessage());
                super.writeTransacHistData(history,super.getAcctNum());

                receipt = new TransactionReceipt(ticket, false, e.getMessage(), "", "", 0.00, 0.00);
    }
        return receipt;
    }
    public String toString(){
        if(super.getAcctType().equals("CD"))
            return super.toString();
        else
            return String.format("%s", super.toString());
    }
}