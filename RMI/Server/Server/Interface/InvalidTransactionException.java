package Server.Interface;

public class InvalidTransactionException extends Exception {
    private int xid;

    public InvalidTransactionException(int xid) {
        super("Transaction" + xid + " is invalid");
        this.xid = xid;
    }

    public int getxid() {
        return this.xid;
    }

}
