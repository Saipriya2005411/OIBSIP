import java.util.ArrayList;
import java.util.List;

public class Account {
    private String userId;
    private String pin;
    private String holderName;
    private double balance;
    private List<String> transactionHistory;

    /** Normal constructor – records "Account opened" in history. */
    public Account(String userId, String pin, String holderName, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add("Account opened with balance: ₹" + String.format("%.2f", balance));
    }

    // ── Core operations ───────────────────────────────────────────────────────

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        transactionHistory.add(String.format("DEPOSIT    + ₹%.2f   |  Balance: ₹%.2f", amount, balance));
        persist();
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        transactionHistory.add(String.format("WITHDRAWAL - ₹%.2f   |  Balance: ₹%.2f", amount, balance));
        persist();
        return true;
    }

    public boolean transfer(Account target, double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        target.balance += amount;
        transactionHistory.add(String.format(
            "TRANSFER   - ₹%.2f → %s  |  Balance: ₹%.2f", amount, target.holderName, balance));
        target.transactionHistory.add(String.format(
            "RECEIVED   + ₹%.2f ← %s  |  Balance: ₹%.2f", amount, this.holderName, target.balance));
        persist();
        target.persist();
        return true;
    }

    public void changePin(String newPin) {
        this.pin = newPin;
        transactionHistory.add("PIN changed successfully.");
        persist();
    }

    /** Save this account's current state to the database. */
    public void persist() {
        Database.save(this);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getUserId()      { return userId; }
    public String getHolderName()  { return holderName; }
    public double getBalance()     { return balance; }
    public String getPinRaw()      { return pin; }          // used only by Database
    public List<String> getTransactionHistory() { return transactionHistory; }
}
