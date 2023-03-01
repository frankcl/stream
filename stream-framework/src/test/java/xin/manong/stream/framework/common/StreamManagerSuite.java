package xin.manong.stream.framework.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;

import java.util.Set;

/**
 * @author frankcl
 * @date 2023-03-01 15:34:07
 */
public class StreamManagerSuite {

    @Test
    public void testRemoveStreamHistory() {
        KVRecord kvRecord = new KVRecord();
        kvRecord.put(StreamConstants.STREAM_RECEIVER, "receiver");
        kvRecord.put(StreamConstants.STREAM_TRACE_ID, "xxx");
        kvRecord.put(StreamConstants.STREAM_HISTORY, "[{}]");
        kvRecord.put("k1", "v1");
        StreamManager.removeStreamHistory(kvRecord);
        Assert.assertEquals(2, kvRecord.getFieldCount());
        Assert.assertTrue(kvRecord.has("k1"));
        Assert.assertTrue(kvRecord.has(StreamConstants.STREAM_HISTORY));
        Assert.assertEquals("v1", kvRecord.get("k1"));
        Assert.assertEquals("[{}]", kvRecord.get(StreamConstants.STREAM_HISTORY));
    }

    @Test
    public void testKeepWatchRecord() {
        String streamHistory = "[{\"__STREAM_RECEIVER__\":\"receiver\", \"__STREAM_BIRTH_PROCESSOR__\":\"processor\", \"__STREAM_TRACE_ID__\":\"xxx\"}]";
        Context context = new Context();
        context.put(StreamConstants.STREAM_RECEIVER, "fake_receiver");
        context.put(StreamConstants.STREAM_TRACE_ID, "xxx");
        context.put(StreamConstants.STREAM_PROCESSOR, "test_processor");
        KVRecord kvRecord = new KVRecord();
        kvRecord.put("k1", "v1");
        kvRecord.put(StreamConstants.STREAM_HISTORY, JSON.parseArray(streamHistory));
        StreamManager.keepWatchRecord(kvRecord, context);
        Assert.assertTrue(context.contains(StreamConstants.STREAM_KEEP_WATCH));
        Set<KVRecord> kvRecords = (Set<KVRecord>) context.get(StreamConstants.STREAM_KEEP_WATCH);
        Assert.assertTrue(kvRecords.contains(kvRecord));
        Assert.assertEquals(5, kvRecord.getFieldCount());
        Assert.assertEquals("v1", kvRecord.get("k1"));
        Assert.assertEquals("fake_receiver", kvRecord.get(StreamConstants.STREAM_RECEIVER));
        Assert.assertEquals("xxx", kvRecord.get(StreamConstants.STREAM_TRACE_ID));
        Assert.assertEquals("test_processor", kvRecord.get(StreamConstants.STREAM_BIRTH_PROCESSOR));
        JSONArray history = (JSONArray) kvRecord.get(StreamConstants.STREAM_HISTORY);
        Assert.assertEquals(2, history.size());
        Assert.assertEquals(3, history.getJSONObject(0).size());
        Assert.assertEquals("receiver", history.getJSONObject(0).getString(StreamConstants.STREAM_RECEIVER));
        Assert.assertEquals("processor", history.getJSONObject(0).getString(StreamConstants.STREAM_BIRTH_PROCESSOR));
        Assert.assertEquals("xxx", history.getJSONObject(0).getString(StreamConstants.STREAM_TRACE_ID));
        Assert.assertEquals("fake_receiver", history.getJSONObject(1).getString(StreamConstants.STREAM_RECEIVER));
        Assert.assertEquals("test_processor", history.getJSONObject(1).getString(StreamConstants.STREAM_BIRTH_PROCESSOR));
        Assert.assertEquals("xxx", history.getJSONObject(1).getString(StreamConstants.STREAM_TRACE_ID));
    }
}
