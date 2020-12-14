package com.hbhb.cw.publicity.service;

/**
 * @author wangxiaogang
 */
public interface PrintFlowService {
    /**
     * 获取提醒
     *
     * @param flowNodeId 节点id
     * @param value      默认提醒
     * @return 提醒信息
     */
    String getInform(String flowNodeId, Integer value);
}
