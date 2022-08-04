package com.manong.stream.sdk.common;

import com.manong.weapon.base.record.KVRecord;
import com.manong.weapon.base.record.KVRecords;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 数据处理结果
 *
 * @author frankcl
 * @create 2019-05-27 16:23
 */
public class ProcessResult implements Serializable {

    private Map<String, KVRecords> forkMap;

    public ProcessResult() {
        forkMap = new HashMap<>();
    }

    /**
     * 获取分支数量
     *
     * @return 分支数量
     */
    public int getForkCount() {
        return forkMap.size();
    }

    /**
     * 获取分支集合
     *
     * @return 分支集合
     */
    public Set<String> getForks() {
        return forkMap.keySet();
    }

    /**
     * 获取分支数据
     *
     * @param fork 分支名称
     * @return 如果分支存在返回分支数据，否则返回null
     */
    public KVRecords getRecords(String fork) {
        if (StringUtils.isEmpty(fork)) return null;
        return forkMap.getOrDefault(fork, null);
    }

    /**
     * 添加分支数据
     * 如果分支名和数据为空，则不产生任何效果
     *
     * @param fork 分支名称
     * @param kvRecord 数据
     */
    public void addRecord(String fork, KVRecord kvRecord) {
        if (StringUtils.isEmpty(fork) || kvRecord == null) return;
        if (!forkMap.containsKey(fork)) forkMap.put(fork, new KVRecords());
        forkMap.get(fork).addRecord(kvRecord);
    }

    /**
     * 添加分支数据
     * 如果分支名和数据为空，则不产生任何效果
     *
     * @param fork 分支名称
     * @param kvRecords 数据集合
     */
    public void addRecord(String fork, KVRecords kvRecords) {
        if (StringUtils.isEmpty(fork) || kvRecords == null) return;
        if (!forkMap.containsKey(fork)) forkMap.put(fork, new KVRecords());
        forkMap.get(fork).addRecords(kvRecords);
    }

    /**
     * 添加处理结果
     * 如果处理结果为空，则不产生任何效果
     *
     * @param processResult 处理结果
     */
    public void addResult(ProcessResult processResult) {
        for (Map.Entry<String, KVRecords> entry : processResult.forkMap.entrySet()) {
            addRecord(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, KVRecords> entry : forkMap.entrySet()) {
            buffer.append("fork=").append(entry.getKey()).append(", records=").
                    append(entry.getValue().getRecordCount()).append("\n");
            buffer.append(entry.getValue().toString());
        }
        return buffer.toString();
    }
}
