package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

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
public class ApplicationFlowNodeVO implements Serializable {
    private static final long serialVersionUID = -6252024254707120891L;

    @Schema(description = "节点id")
    private Long id;
    @Schema(description = "流程节点id")
    private String flowNodeId;
    @Schema(description = "审批人id")
    private Integer userId;
}
