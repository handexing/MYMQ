package com.my.mq.storage.rockdsdb.autobatcher;

import com.my.mq.storage.rockdsdb.db.RDB;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class Batcher {

    private static Logger log = LoggerFactory.getLogger(Batcher.class);


    private static final int PULL_BATCH_ITEM_NUM = 50;

    private WriteBatch wb = new WriteBatch();
    private volatile int itemNum = 0;
    private static volatile Batcher instance = null;

    public static volatile ReentrantLock lock = new ReentrantLock();

    public static Batcher getInstance() {
        if (instance == null) {
            synchronized (Batcher.class) {
                instance = new Batcher();
            }
        }
        return instance;
    }

    private void checkFrequency() {
        if (itemNum >= PULL_BATCH_ITEM_NUM) {
            flush();
        }
    }

    public void flush() {
        lock.lock();
        try {
            if (itemNum > 0) {
                // make sure write succ
                while (!RDB.writeSync(wb)) {
                    log.error("error while flush to db!");
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
                wb.clear();
                itemNum = 0;
            }
        } finally {
            lock.unlock();
        }
    }

    public void put(final ColumnFamilyHandle cfh, final byte[] key, final byte[] value) {
        lock.lock();
        try {
            wb.put(cfh, key, value);
            itemNum++;
            checkFrequency();
        } catch (RocksDBException e) {

        } finally {
            lock.unlock();
        }
    }

    public void close() {
        wb.close();
    }
}
