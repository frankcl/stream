package xin.manong.stream.boost.receiver.ots;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.RecordColumn;
import com.alicloud.openservices.tablestore.model.StreamRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import xin.manong.stream.sdk.common.StreamConstants;
import xin.manong.weapon.aliyun.ots.OTSConverter;
import xin.manong.weapon.base.common.Context;
import xin.manong.weapon.base.record.KVRecord;
import xin.manong.weapon.base.record.KVRecords;
import xin.manong.weapon.base.record.RecordType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author frankcl
 * @date 2022-08-12 11:56:11
 */
public class StreamRecordConverterSuite {

    private StreamRecordConverter converter;

    @Before
    public void setUp() {
        converter = new StreamRecordConverter(new HashMap<>());
        Assert.assertTrue(converter.init());
    }

    @After
    public void tearDown() {
        converter.destroy();
    }

    @Test
    public void testConvertNormal() throws Exception {
        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setRecordType(StreamRecord.RecordType.PUT);
        Map<String, Object> keyMap = new HashMap<String, Object>() { { put("key", "k"); put("group", "g"); } };
        PrimaryKey primaryKey = OTSConverter.convertPrimaryKey(keyMap);
        streamRecord.setPrimaryKey(primaryKey);
        Map<String, Object> columnMap = new HashMap<String, Object>() { { put("c", 123); } };
        List<Column> columns = OTSConverter.convertColumns(columnMap);
        List<RecordColumn> recordColumns = columns.stream().map(
                c -> new RecordColumn(c, RecordColumn.ColumnType.PUT)).collect(Collectors.toList());
        streamRecord.setColumns(recordColumns);
        Context context = new Context();
        KVRecords kvRecords = converter.convert(context, streamRecord);
        Assert.assertTrue(kvRecords != null && kvRecords.getRecordCount() == 1);
        KVRecord kvRecord = kvRecords.getRecord(0);
        Assert.assertEquals(2, kvRecord.getKeys().size());
        Assert.assertTrue(kvRecord.getKeys().contains("key"));
        Assert.assertTrue(kvRecord.getKeys().contains("group"));
        Assert.assertTrue(kvRecord.has("key"));
        Assert.assertTrue(kvRecord.has("group"));
        Assert.assertTrue(kvRecord.has("c"));
        Assert.assertEquals(RecordType.PUT, kvRecord.getRecordType());
        Assert.assertEquals("k", kvRecord.get("key"));
        Assert.assertEquals("g", kvRecord.get("group"));
        Assert.assertEquals(123L, kvRecord.get("c"));
        Assert.assertTrue(context.contains(StreamConstants.STREAM_RECORD_TYPE));
        Assert.assertEquals("PUT", context.get(StreamConstants.STREAM_RECORD_TYPE));
    }

    @Test
    public void testConvertFail() throws Exception {
        Context context = new Context();
        KVRecords kvRecords = converter.convert(context, "unknown");
        Assert.assertTrue(kvRecords == null);
    }
}
