package com.my.mq.storage.rocksdb;

import com.my.mq.storage.rockdsdb.autobatcher.Batcher;
import com.my.mq.storage.rockdsdb.db.CFManager;
import com.my.mq.storage.rockdsdb.db.RDB;
import com.my.mq.storage.rockdsdb.enums.CFHandlerNames;
import org.junit.Test;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatch;

import java.util.concurrent.TimeUnit;


public class RDBTest {

    @Test
    public void testGetRange() throws Exception {
        RDB.init("F:\\storage\\rocksdb");
        Batcher BATCHER = Batcher.getInstance();
//
//        for (int i = 0; i < 100; i++) {
//            long time = System.currentTimeMillis();
//            BATCHER.put(CFManager.getColumnFamilyHandle(CFHandlerNames.META),("1324356527-"+time+"_"+i).getBytes(),"this is test!!".getBytes());
//        }
//        BATCHER.flush();

        try (RocksIterator it = RDB.newIterator(CFManager.getColumnFamilyHandle(CFHandlerNames.META))) {
            for (it.seek("1324356527-".getBytes()); it.isValid(); it.next()) {
                String id = new String(it.key());
                String value = new String(it.value());
                System.out.println(id + "::" + value);
            }
        }
//        RDB.deleteFilesInRange(CFManager.getColumnFamilyHandle(), "handx_test_topic_0".getBytes(), "handx_test_topic_50".getBytes());
    }


    @Test
    public void testDeleteRange() throws RocksDBException {
        WriteBatch wb = new WriteBatch();
        ColumnFamilyHandle cfHandle = CFManager.getColumnFamilyHandle(CFHandlerNames.DEFAULT);

        long st = System.currentTimeMillis();
        for (int i = 100000; i < 200000; i++) {
            wb.put(cfHandle, ("1324356527-" + i + "-5-5-345-356-234-232").getBytes(), "tasdfasdgasdfestfordb".getBytes());

            if (i % 30 == 0) {
                RDB.writeAsync(wb);
                wb.clear();
            }
        }
        for (int i = 100000; i < 200000; i++) {
            wb.put(cfHandle, ("1324356525-" + i + "-5-5-345-356-234-232").getBytes(), "tasdfasdgasdfestfordb".getBytes());

            if (i % 30 == 0) {
                RDB.writeAsync(wb);
                wb.clear();
            }
        }
        for (int i = 100000; i < 200000; i++) {
            wb.put(cfHandle, ("1324356529-" + i + "-5-5-345-356-234-232").getBytes(), "tasdfasdgasdfestfordb".getBytes());

            if (i % 30 == 0) {
                RDB.writeAsync(wb);
                wb.clear();
            }
        }
        RDB.writeAsync(wb);

        long ed = System.currentTimeMillis();
        System.out.println("write cost :" + (ed - st));

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();
        RocksIterator it = RDB.newIterator(cfHandle);
        byte[] now = "1324356527".getBytes();
        long count = 0;
        for (it.seek(now); it.isValid(); it.next()) {
//            System.out.println(new String(it.key()) + " " + new String(it.value()));
            count++;
            if (count == 100000)
                break;
        }
        it.close();
        long end = System.currentTimeMillis();
        System.out.println("cost : " + (end - start) + " count:" + count);
        RDB.deleteFilesInRange(CFManager.getColumnFamilyHandle(CFHandlerNames.DEFAULT), "132435653".getBytes(), "1324356529".getBytes());

        count = 0;
        it = RDB.newIterator(cfHandle);
        now = "1324356525".getBytes();
        for (it.seek(now); it.isValid(); it.next()) {
//            System.out.println(new String(it.key()) + " " + new String(it.value()));
            count++;
            if (count == 100000)
                break;
        }
        it.close();
        end = System.currentTimeMillis();
        System.out.println("cost : " + (end - start) + " count:" + count);

        destructor();
    }

    public static void destructor() {
        RDB.close();
    }
}
