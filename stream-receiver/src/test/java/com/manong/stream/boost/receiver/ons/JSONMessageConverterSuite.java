package com.manong.stream.boost.receiver.ons;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.manong.weapon.base.common.Context;
import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @author frankcl
 * @date 2022-08-12 11:48:25
 */
public class JSONMessageConverterSuite {

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
        message.setBody(body.toJSONString().getBytes(Charset.forName("UTF-8")));
        Context context = new Context();
        KVRecords kvRecords = converter.convert(context, message);
        Assert.assertEquals(1, kvRecords.getRecordCount());
        KVRecord kvRecord = kvRecords.getRecord(0);
        Assert.assertTrue(kvRecord.has("key"));
        Assert.assertTrue(kvRecord.has("v1"));
        Assert.assertTrue(kvRecord.has("message_id"));
        Assert.assertTrue(kvRecord.has("message_key"));
        Assert.assertEquals("k", kvRecord.get("key"));
        Assert.assertEquals(1000, kvRecord.get("v1"));
        Assert.assertEquals("message_id", kvRecord.get("message_id"));
        Assert.assertEquals("message_key", kvRecord.get("message_key"));
        Assert.assertTrue(context.contains("message_id"));
        Assert.assertTrue(context.contains("message_key"));
        Assert.assertEquals("message_id", context.get("message_id"));
        Assert.assertEquals("message_key", context.get("message_key"));
    }

    @Test
    public void testConvertFail() throws Exception {
        Context context = new Context();
        KVRecords kvRecords = converter.convert(context, "unknown");
        Assert.assertTrue(kvRecords == null);
    }
}
