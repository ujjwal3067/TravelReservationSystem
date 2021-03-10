package Server.Middleware;

import java.util.*;

public class TransactionManager implements Runnable {

    // TRANSACTION MANAGER
    public HashMap<Integer, Transaction> ACTIVE_Transaction = new HashMap<>(); // active list of transactions
    public HashMap<Integer, String> INACTIVE_Transaction = new HashMap<>(); // maintains a list of inactive transaction
    private Integer first_xid = 1; // starting from 1
    private Middleware middleware; // middleware object
    // private int TTL; // time to live

    /**
     * method to check is the transaction is active or not
     * 
     * @param xid
     * @return true if transaction is active
     */
    public boolean isActive(int xid) {
        assert xid != 0;
        synchronized (ACTIVE_Transaction) {
            return ACTIVE_Transaction.keySet().contains(xid);
        }
    }

    /**
     * methods to add transaction to list of active transaction
     * 
     * @param xid
     * @param transaction
     */
    public void addTransaction(int xid, Transaction transaction) {
        assert xid != 0 && transaction != null;
        synchronized (ACTIVE_Transaction) {
            System.out.println("TM: " + xid);
            ACTIVE_Transaction.put(xid, transaction);
        }
    }

    /**
     * methods returns a transaction
     * 
     * @param xid
     * @return
     */
    public Transaction getTransaction(int xid) {
        assert xid != 0;
        synchronized (ACTIVE_Transaction) {
            return ACTIVE_Transaction.get(xid);
        }

    }

    // Constructor for middleware
    public TransactionManager(Middleware middleware) {
        this.middleware = middleware;
        Thread thread = new Thread(this); // transaction manager in different thread
        thread.start();
    }

    // Constructor for Servers. No need to watch timeout on individual server.
    public TransactionManager() {
    }

    /**
     * create a new transaction as per the client request
     * 
     * @return xid of transaction created
     */
    public synchronized int start() {
        // The case when TransactionManager is not bound to middleware but servers
        // instead. Returns negative xid as invalid.
        if (middleware == null) {
            return -1;
        }

        int xid;
        this.first_xid++; // increment the xid counter
        xid = this.first_xid;
        Transaction transaction = new Transaction(xid); // create new transaction
        this.addTransaction(xid, transaction);
        return xid; // return xid to client
    }

    // TODO : shift this loop to separate parrarel thread
    public void run() {
        // keep checking for timeouts transaction and abort them
        while (true) {
            try {
                synchronized (ACTIVE_Transaction) {
                    for (Integer key : ACTIVE_Transaction.keySet()) {
                        Transaction transaction = ACTIVE_Transaction.get(key);
                        if (transaction != null && transaction.timeOut()) { // if timeout for the transaction :: abort
                            System.out.println("TimeOut for Transaction <" + transaction.getTransactionID() + ">");
                            this.middleware.abort(transaction.getTransactionID()); // abort
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR : TransactionManager.run() method");
            }
        }

    }

}
