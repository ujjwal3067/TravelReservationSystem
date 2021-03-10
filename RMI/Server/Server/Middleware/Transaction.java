package Server.Middleware;

import Server.Common.*;
import java.util.*;
import java.time.*;

/**
 * Class represents a Transaction object :: one transaction = one state of this
 * object
 */
public class Transaction {
    public int xid;
    private Set<String> resourceManagers = new HashSet<String>(); // # of RM this Transaction is accessing
    private int TTL = 100; // Time to live for the Transaction :: seconds
    long timeStamp;
    public RMHashMap m_data = new RMHashMap(); // copy of object

    // INIT
    public Transaction(int xid) {
        assert xid != 0;
        this.xid = xid;
        this.timeStamp = Instant.now().getEpochSecond();
    }

    public void WRITE(int xid, String key, RMItem value) {
        assert xid != 0 && key != null && value != null;
        synchronized (m_data) {
            // writing to local copy of the object
            m_data.put(key, value);
        }
    }

    public RMItem READ(int xid, String key) {
        assert xid != 0 && key != null;
        synchronized (m_data) {
            RMItem object = m_data.get(key);
            if (object != null) {
                // return the copy of the object
                return (RMItem) object.clone();
            }
            return null;
        }

    }

    public int getTransactionID() {
        return this.xid;
    }

    // Add new Object to the transaction RM object list
    public void addRM(String rm) {
        assert rm != null;
        resourceManagers.add(rm);
    }

    public Set<String> getRM() {
        return this.resourceManagers; // returns the list of resourceManager Transaction is accessing
    }

    public RMHashMap getDataCOPY() {
        return this.m_data;

    }

    public boolean hasKey(String key) {
        synchronized (this.m_data) {
            return this.m_data.keySet().contains(key); // returns true if key is present
        }
    }

    public boolean timeOut() {
        long currentTimeStamp = Instant.now().getEpochSecond();
        // if currentTimeStamp is greater than last read/write operation time + timeout
        // (TTL) time => abort = true
        if (currentTimeStamp > timeStamp + TTL) {
            return true;
        } else {
            return false; // timeout = false
        }
    }

    public void updateTimeStamp() {
        this.timeStamp = Instant.now().getEpochSecond();
    }

}
