package xin.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author frankcl
 * @date 2022-08-12 11:48:25
 */
public class JSONMessageConverterTest {

    private JSONMessageConverter converter;

    @Before
    public void setUp() {
        converter = new JSONMessageConverter(new HashMap<>());
        Assert.assertTrue(converter.init());
    }

    @After
    public void tearDown() {
        converter.destroy();
    }

    @Test
    public void testConvertNormal() throws Exception {
        JSONObject body = new JSONObject();
        body.put("key", "k");
        body.put("v1", 1000);
        Message message = new Message();
        message.setMsgID("message_id");
        message.setKey("message_key");
        message.setTopic("topic");
        message.setTag("A");
        message.setBody(body.toJSONString().getBytes(StandardCharsets.UTF_8));
        Context context = new Context();
        KVRecords kvRecords = converter.convert(context, message);
        Assert.assertEquals(1, kvRecords.getRecordCount());
        KVRecord kvRecord = kvRecords.getRecord(0);
        Assert.assertTrue(kvRecord.has("key"));
        Assert.assertTrue(kvRecord.has("v1"));
        Assert.assertTrue(kvRecord.has(StreamConstants.STREAM_MESSAGE_ID));
        Assert.assertTrue(kvRecord.has(StreamConstants.STREAM_MESSAGE_KEY));
        Assert.assertTrue(kvRecord.has(StreamConstants.STREAM_MESSAGE_TIMESTAMP));
        Assert.assertTrue(kvRecord.has(StreamConstants.STREAM_MESSAGE_TOPIC));
        Assert.assertTrue(kvRecord.has(StreamConstants.STREAM_MESSAGE_TAG));
        Assert.assertEquals("k", kvRecord.get("key"));
        Assert.assertEquals(1000, kvRecord.get("v1"));
        Assert.assertEquals("message_id", kvRecord.get(StreamConstants.STREAM_MESSAGE_ID));
        Assert.assertEquals("message_key", kvRecord.get(StreamConstants.STREAM_MESSAGE_KEY));
        Assert.assertEquals("topic", kvRecord.get(StreamConstants.STREAM_MESSAGE_TOPIC));
        Assert.assertEquals("A", kvRecord.get(StreamConstants.STREAM_MESSAGE_TAG));
        Assert.assertEquals(0L, (long) kvRecord.get(StreamConstants.STREAM_MESSAGE_TIMESTAMP));
        Assert.assertTrue(context.contains(StreamConstants.STREAM_MESSAGE_ID));
        Assert.assertTrue(context.contains(StreamConstants.STREAM_MESSAGE_KEY));
        Assert.assertTrue(context.contains(StreamConstants.STREAM_MESSAGE_TIMESTAMP));
        Assert.assertTrue(context.contains(StreamConstants.STREAM_MESSAGE_TOPIC));
        Assert.assertTrue(context.contains(StreamConstants.STREAM_MESSAGE_TAG));
        Assert.assertEquals("message_id", context.get(StreamConstants.STREAM_MESSAGE_ID));
        Assert.assertEquals("message_key", context.get(StreamConstants.STREAM_MESSAGE_KEY));
        Assert.assertEquals("topic", context.get(StreamConstants.STREAM_MESSAGE_TOPIC));
        Assert.assertEquals("A", context.get(StreamConstants.STREAM_MESSAGE_TAG));
        Assert.assertEquals(0L, (long) context.get(StreamConstants.STREAM_MESSAGE_TIMESTAMP));
    }

    @Test
    public void testConvertFail() throws Exception {
        Context context = new Context();
        KVRecords kvRecords = converter.convert(context, "unknown");
        Assert.assertNull(kvRecords);
    }
}
