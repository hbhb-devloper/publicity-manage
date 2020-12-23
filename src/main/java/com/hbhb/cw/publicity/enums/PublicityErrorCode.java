package com.hbhb.cw.publicity.enums;

import lombok.Getter;

/**
 * @author wangxiaogang
 */
@Getter
public enum PublicityErrorCode {
    // 该类型不存在流程
    NOT_EXIST_FLOW("85002", "not.exist.flow"),
    // 该类型下的流程超过限定流程
    EXCEED_LIMIT_FLOW("85003", "exceed.limit.flow"),
    // 没有对应的流程
    LACK_OF_FLOW("85004", "lack.of.flow"),
    // 没有对应的节点属性
    LACK_OF_NODE_PROP("85005", "lack.of.node.prop"),
    // 没有权限发起流程
    LACK_OF_FLOW_ROLE("85007", "lack.of.flow.role"),
    // 没有审批权限
    LOCK_OF_APPROVAL_ROLE("85008", "lock.of.approval.role"),
    // 请指定所有审批人
    NOT_ALL_APPROVERS_ASSIGNED("85009", "not.all.approvers.assigned"),
    // 下一节点未指定审批人，请联系管理员
    NEXT_NODE_NO_USER("85010", "next.node.no.user"),
    //  该单位下物料制作费用预算不足
    BUDGET_INSUFFICIENT("80011", "budget.insufficient"),
    // 导入失败
    INPUT_DATA_ERROR("80012", "input.data.error"),


    // 无营业厅!
    NOT_SERVICE_HALL("84000", "not.service.hall"),
    // 本次已截止
    ALREADY_CLOSE("84001", "already.close"),
    // 本次已截止
    PARENT_NOT_ACTIVITY("84002", "parent.not.activity"),
    // 必填项未填
    NOT_FILLED_IN("84003", "not.filled.in"),
    // 相关流程异常
    NOT_RELEVANT_FLOW("84004","not.relevant.flow"),
    // 请在二级目录添加
    PLEASE_ADD_SECONDARY_DIRECTORY("84005","please.add.secondary.directory"),;


    private String code;

    private String message;

    PublicityErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
