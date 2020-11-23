package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintResVO implements Serializable {
    private static final long serialVersionUID = 7049861070718204635L;

    @Schema(description = "申请单号")
    private String printNum;

    @Schema(description = "申请单名称")
    private String printName;

    @Schema(description = "申请部门单位id")
    private Integer unitId;

    @Schema(description = "申请部门单位名称")
    private Integer unitName;

    @Schema(description = "申请时间")
    private String applyTime;

    @Schema(description = "申请人id")
    private Integer userId;

    @Schema(description = "申请人姓名")
    private Integer userName;

    @Schema(description = "预估金额")
    private BigDecimal predictAmount;

    @Schema(description = "流程状态值")
    private Integer state;

    @Schema(description = "流程状态")
    private Integer stateLabel;
}
