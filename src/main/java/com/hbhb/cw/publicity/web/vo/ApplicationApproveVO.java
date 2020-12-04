package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationApproveVO implements Serializable {
    private static final long serialVersionUID = 6308905364605992433L;

    @Schema(description = "项目签报流程信息id")
    private Long id;

    @Schema(description = "操作")
    private Integer operation;

    @Schema(description = "审批意见")
    private String suggestion;

    @Schema(description = "审批者列表")
    private List<ApplicationFlowNodeVO> approvers;
}
