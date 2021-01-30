package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2021-01-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryUnitApplicationVO implements Serializable {
    private static final long serialVersionUID = 1597663091229221475L;

    @Schema(description = "序号")
    private Long lineNum;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "单位")
    private String unitName;

    @Schema(description = "业务单式申请数量")
    private Long simplexAmount;

    @Schema(description = "宣传单页申请数量")
    private Long singleAmount;

    @Schema(description = "业务单式/宣传单页(0/1)")
    private Integer type;

    /**
     * 审批状态
     */
    @Schema(description = "审批状态")
    private Integer approvedState;
}
