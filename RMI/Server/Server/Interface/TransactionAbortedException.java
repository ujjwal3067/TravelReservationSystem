package Server.Interface;

public class TransactionAbortedException extends Exception {
    private int xid;

    public TransactionAbortedException(int xid) {
        super("Transaction " + xid);
        this.xid = xid;
    }

    public int getxid() {
        return this.xid;
    }

}
