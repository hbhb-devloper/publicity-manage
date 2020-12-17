package com.hbhb.cw.publicity.web.vo;

import com.hbhb.cw.flowcenter.vo.FlowApproveVO;
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
@AllArgsConstructor
@NoArgsConstructor
public class MaterialsApproveVO implements Serializable {


    private static final long serialVersionUID = 6342767301439159870L;
    @Schema(description = "物料id")
    private Long id;


    @Schema(description = "操作")
    private Integer operation;


    @Schema(description = "审批意见")
    private String suggestion;


    @Schema(description = "所有节点的审批人 用来校验分配者")
    private List<FlowApproveVO> approvers;
}
