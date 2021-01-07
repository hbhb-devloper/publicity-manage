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
    // 导入类型错误
    IMPORT_DATA_TYPE_ERROR("80013", "import.data.type.error"),
    // 非申请人无权限进行操作
    NO_OPERATION_PERMISSION("80014", "no.operation.permission"),
    // 导入数据不可以为空
    IMPORT_DATA_NULL_ERROR("80015", "import.data.nul.error"),


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
    // 请在二级目录下添加物料
    PLEASE_ADD_SECONDARY_DIRECTORY("84005","please.add.secondary.directory"),
    // 无权发起审批
    NOT_POWER_TO_APPROVER("84006","not.power.to.approver"),
    // 没有流程再改流程类型下
    NOT_HAVE_FLOW("84007","not.have.flow"),
    // 无法在二级目录下添加活动
    NOT_ADD_SECONDARY_DIRECTORY("84008","not.add.secondary.directory"),
    // 本月已无审批次数
    NOT_NUMBER_IN_MONTH("84009","not.number.in.month"),
    // 无法在禁用的类别中操作
    DO_NOT_OPERATE("84010","do.mot.operate")
    ;


    private String code;

    private String message;

    PublicityErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
