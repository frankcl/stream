package xin.manong.stream.sdk.common;

import org.junit.Assert;
import org.junit.Test;
import xin.manong.weapon.base.record.KVRecord;

/**
 * @author frankcl
 * @date 2022-08-12 14:12:21
 */
public class ProcessResultSuite {

    @Test
    public void testCommonOperations() {
        ProcessResult processResult = new ProcessResult();
        KVRecord kvRecord1 = new KVRecord();
        KVRecord kvRecord2 = new KVRecord();
        KVRecord kvRecord3 = new KVRecord();
        processResult.addRecord("success", kvRecord1);
        processResult.addRecord("success", kvRecord2);
        processResult.addRecord("fail", kvRecord3);
        Assert.assertEquals(2, processResult.getForkCount());
        Assert.assertEquals(2, processResult.getForks().size());
        Assert.assertTrue(processResult.getForks().contains("success"));
        Assert.assertTrue(processResult.getForks().contains("fail"));
        Assert.assertEquals(2, processResult.getRecords("success").getRecordCount());
        Assert.assertTrue(kvRecord1 == processResult.getRecords("success").getRecord(0));
        Assert.assertTrue(kvRecord2 == processResult.getRecords("success").getRecord(1));
        Assert.assertEquals(1, processResult.getRecords("fail").getRecordCount());
        Assert.assertTrue(kvRecord3 == processResult.getRecords("fail").getRecord(0));
    }

    @Test
    public void testAddProcessResult() {
        ProcessResult processResult1 = new ProcessResult();
        KVRecord kvRecord1 = new KVRecord();
        KVRecord kvRecord2 = new KVRecord();
        processResult1.addRecord("fork1", kvRecord1);
        processResult1.addRecord("fork2", kvRecord2);

        ProcessResult processResult2 = new ProcessResult();
        KVRecord kvRecord3 = new KVRecord();
        KVRecord kvRecord4 = new KVRecord();
        processResult2.addRecord("fork2", kvRecord3);
        processResult2.addRecord("fork3", kvRecord4);

        processResult1.addResult(processResult2);
        Assert.assertEquals(3, processResult1.getForkCount());
        Assert.assertEquals(3, processResult1.getForks().size());
        Assert.assertTrue(processResult1.getForks().contains("fork1"));
        Assert.assertTrue(processResult1.getForks().contains("fork2"));
        Assert.assertTrue(processResult1.getForks().contains("fork3"));
        Assert.assertEquals(1, processResult1.getRecords("fork1").getRecordCount());
        Assert.assertEquals(2, processResult1.getRecords("fork2").getRecordCount());
        Assert.assertEquals(1, processResult1.getRecords("fork3").getRecordCount());
        Assert.assertTrue(kvRecord1 == processResult1.getRecords("fork1").getRecord(0));
        Assert.assertTrue(kvRecord2 == processResult1.getRecords("fork2").getRecord(0));
        Assert.assertTrue(kvRecord3 == processResult1.getRecords("fork2").getRecord(1));
        Assert.assertTrue(kvRecord4 == processResult1.getRecords("fork3").getRecord(0));

        Assert.assertEquals(2, processResult2.getForkCount());
        Assert.assertEquals(2, processResult2.getForks().size());
        Assert.assertTrue(processResult2.getForks().contains("fork2"));
        Assert.assertTrue(processResult2.getForks().contains("fork3"));
        Assert.assertEquals(1, processResult2.getRecords("fork2").getRecordCount());
        Assert.assertEquals(1, processResult2.getRecords("fork3").getRecordCount());
        Assert.assertTrue(kvRecord3 == processResult2.getRecords("fork2").getRecord(0));
        Assert.assertTrue(kvRecord4 == processResult2.getRecords("fork3").getRecord(0));
    }
}
