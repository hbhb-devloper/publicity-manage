package com.hbhb.cw.publicity.web.vo;

import com.hbhb.cw.flowcenter.vo.FlowApproverVO;
import com.hbhb.cw.flowcenter.vo.FlowOperationVO;
import com.hbhb.cw.flowcenter.vo.FlowRoleResVO;
import com.hbhb.cw.flowcenter.vo.FlowSuggestionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrintFlowInfoVo implements Serializable {
    private static final long serialVersionUID = -7850168364490264486L;

    @Schema(description = "发票流程信息表id")
    private Long id;

    @Schema(description = "方法id")
    private Long printId;

    @Schema(description = "节点id")
    private String flowNodeId;

    @Schema(description = "流程名称")
    private String projectFlowName;

    @Schema(description = "审批人角色名称")
    private String approverRole;

    @Schema(description = "审批人角色描述")
    private String roleDesc;

    @Schema(description = "审批人名称")
    private String nickName;

    @Schema(description = "审批人")
    private FlowApproverVO approver;

    @Schema(description = "操作按钮（同意/拒绝）")
    private FlowOperationVO operation;

    @Schema(description = "审批意见")
    private FlowSuggestionVO suggestion;

    @Schema(description = "是否可以填写（0-可填写、1-不可填写）")
    private Boolean input;

    @Schema(description = "是否能够自定义流程（0-否、1-是）")
    private Boolean controlAccess;

    @Schema(description = "是否允许被设置不参与流程（0-不参与、1-参与）")
    private Boolean isJoin;

    @Schema(description = "审批人下拉框选项")
    private List<FlowRoleResVO> approverSelect;

    @Schema(description = "更新时间")
    private String updateTime;
}
