import java.io.IOException;
import java.util.*;

public class CDAccount extends Account{
        private Calendar maturityDate;

        public CDAccount()throws IOException{
                super();
                maturityDate = null;
        }

        public CDAccount(Depositor d, int AN, String AT, String s, double b,String MD)throws IOException{
                super(d,AN,AT,s,b,MD);
                setMaturityDate(MD);
        }

        public CDAccount(CDAccount CDA)throws IOException{
                super(CDA.getDepositor(),CDA.getAcctNum(),CDA.getAcctType(),CDA.getStatus(),CDA.getBalance(),CDA.getDate());
                maturityDate = CDA.getMaturityDate();
        }

        private void setMaturityDate(String MD){
                Calendar mD = Calendar.getInstance();
                mD.clear();
                String[] mdArray = MD.split("/");
                mD.set(Integer.parseInt(mdArray[2]), Integer.parseInt(mdArray[0]) - 1, Integer.parseInt(mdArray[1]));
                maturityDate = mD;
        }

        public TransactionReceipt makeDeposit2(Bank bank,TransactionTicket ticket) throws IOException {
                double preBalance;
                double postBalance;
                String success;
                TransactionReceipt history;
                TransactionReceipt receipt;

                Calendar today = Calendar.getInstance();
try{
                if (super.getStatus().equals("open")) {
                        if (!(ticket.getTermOfCD() == 6 || ticket.getTermOfCD() == 12 || ticket.getTermOfCD() == 18 || ticket.getTermOfCD() == 24)) {
                                success = String.format("Account Number: %s\nError:%s Month(s) Isn't A Valid New Term.", super.getAcctNum(), ticket.getTermOfCD());
                                history = new TransactionReceipt(ticket,"Depositing", ticket.getTransactionAmount(), "Failed", super.getBalance(), success);
                                super.writeTransacHistData(history,super.getAcctNum());

                                receipt = new TransactionReceipt(ticket, false, success, super.getAcctType(), super.getStatus(), super.getBalance(), 0.00);

                                return receipt;
                        }if (ticket.getTransactionAmount() <= 0.00) {
                                success = String.format("Account Number: %s\n$%.2f Is An Invalid Deposit Amount", super.getAcctNum(), ticket.getTransactionAmount());
                                throw new InvalidAmountException(success);
                        }if(maturityDate.before(today) || today.equals(maturityDate)){
                                today.add(Calendar.MONTH, ticket.getTermOfCD());
                                String newMD = String.format("%02d/%02d/%4d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

                                preBalance = super.getBalance();
                                postBalance = ticket.getTransactionAmount() + preBalance;
                                super.setBal(postBalance);
                                Bank.addAmount(bank, ticket);

                                history = new TransactionReceipt(ticket,"Depositing", ticket.getTransactionAmount(), "Done", super.getBalance(),"");
                                super.writeTransacHistData(history,super.getAcctNum());
                                super.setDate(newMD);

                                receipt = new TransactionReceipt(ticket, true, "", super.getAcctType(), super.getStatus(), preBalance, postBalance, newMD);
                        }else {
                                success = String.format("Error: The Maturity Date for Account #%s Hasn't Come Yet.\nPlease Try Again On %s.", super.getAcctNum(), getDate());
                                throw new CDMaturityDateException(success);
                        }}else {
                                success = String.format("Error:Account #%s is closed.\n",super.getAcctNum());
                                throw new AccountClosedException(success);
                }}catch(CDMaturityDateException e){
                        history = new TransactionReceipt(ticket, "Depositing", ticket.getTransactionAmount(), "Failed", super.getBalance(), "The Maturity Date Hasn't Come Yet.");
                        super.writeTransacHistData(history,super.getAcctNum());

                        receipt = new TransactionReceipt(ticket, false, e.getMessage(), "", "", 0.00, 0.00);
                } catch(AccountClosedException | InvalidAmountException e){
                        history = new TransactionReceipt(ticket,"Depositing", ticket.getTransactionAmount(), "Failed", super.getBalance(), e.getMessage());
                        super.writeTransacHistData(history,super.getAcctNum());

                        receipt = new TransactionReceipt(ticket, false, e.getMessage(), "", "", 0.00, 0.00);
}
        return receipt;
}
        public TransactionReceipt makeWithdrawal2(Bank bank, TransactionTicket ticket) throws IOException {
                String success;
                TransactionReceipt history;
                TransactionReceipt receipt;

                Calendar today = Calendar.getInstance();
try{
                if (getStatus().equals("open")) {
                        if (!(ticket.getTermOfCD() == 6 || ticket.getTermOfCD() == 12 || ticket.getTermOfCD() == 18 || ticket.getTermOfCD() == 24)) {
                                success = String.format("Account Number: %s\nError:%s Month(s) Isn't A Valid New Term.", super.getAcctNum(), ticket.getTermOfCD());
                                history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Failed", super.getBalance(), "Error:" + ticket.getTermOfCD() +" Month(s) Isn't A Valid New Term.");
                                super.writeTransacHistData(history,super.getAcctNum());

                                receipt =  new TransactionReceipt(ticket, false, success, super.getAcctType(), super.getStatus(), super.getBalance(), 0.00, getDate());
                                return receipt;
                        } else if (ticket.getTransactionAmount() <= 0.00) {
                                success = String.format("Account Number: %s\n$%.2f Is An Invalid Withdrawal Amount",super.getAcctNum(), ticket.getTransactionAmount());
                                throw new InvalidAmountException(success);
                        } else if (ticket.getTransactionAmount() > super.getBalance()) {
                                success = String.format("$%.2f Is Over The Balance And Can't be Withdrawn.", ticket.getTransactionAmount());
                                throw new InsufficientFundsException(success);
                        }if (maturityDate.before(today) || today.equals(maturityDate)) {
                                today.add(Calendar.MONTH, ticket.getTermOfCD());
                                String newMD = String.format("%02d/%02d/%4d", today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR));

                                double balance = super.getBalance();
                                double balance2 = balance - ticket.getTransactionAmount();

                                TransactionTicket newTicket = new TransactionTicket(ticket.getAccountNumber(),ticket.getDate(),ticket.getTransactionType(),ticket.getTypeAccount(),balance2,ticket.getTermOfCD());
                                super.setBal(balance2);
                                super.setDate(newMD);
                                Bank.subtractAmount(bank, newTicket);

                                history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Done", balance2, "");
                                super.writeTransacHistData(history,super.getAcctNum());

                                receipt = new TransactionReceipt(ticket, true, "", super.getAcctType(), super.getStatus(), balance, balance2, newMD);
                        }else{
                                success = String.format("Error: The Maturity Date for Account #%s Hasn't Come Yet.\nPlease Try Again On %s.", super.getAcctNum(), getDate());
                                throw new CDMaturityDateException(success);
                        }} else {
                                success = String.format("Can't Withdraw $%.2f Due to Account #" + super.getAcctNum() + " Being Closed.", ticket.getTransactionAmount());
                                throw new AccountClosedException(success);
                }}catch(CDMaturityDateException e){
                        history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Failed", super.getBalance(), "The Maturity Date Hasn't Come Yet.");
                        super.writeTransacHistData(history,super.getAcctNum());

                        receipt = new TransactionReceipt(ticket, false, e.getMessage(), "", "", 0.00, 0.00);
                } catch(InsufficientFundsException | InvalidAmountException | AccountClosedException e){
                        history = new TransactionReceipt(ticket,"Withdraw", ticket.getTransactionAmount(), "Failed", super.getBalance(), e.getMessage());
                        super.writeTransacHistData(history,super.getAcctNum());

                        receipt = new TransactionReceipt(ticket, false, e.getMessage(), "", "", 0.00, 0.00);
}
        return receipt;
}
        public String toString(){
                String date = String.format("%02d/%02d/%4d", maturityDate.get(Calendar.MONTH) + 1, maturityDate.get(Calendar.DAY_OF_MONTH), maturityDate.get(Calendar.YEAR));
                return String.format("%s%21s", super.toString(), date);
        }

        public Calendar getMaturityDate() {
                return maturityDate;
        }
}