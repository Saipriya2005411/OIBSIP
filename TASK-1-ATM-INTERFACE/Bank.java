public class Bank {

    public Bank() {
        Database.init();   
    }
    public Account findAccount(String userId) {
        return Database.findById(userId);
    }

    public Account findAccountByName(String name) {
        return Database.findByName(name);
    }
    public Account register(String userId, String pin, String holderName, double openingBalance) {
        if (userId == null || userId.isBlank())        return null;
        if (Database.exists(userId))                   return null;   // duplicate
        Account a = new Account(userId, pin, holderName, openingBalance);
        Database.save(a);
        return a;
    }
}
