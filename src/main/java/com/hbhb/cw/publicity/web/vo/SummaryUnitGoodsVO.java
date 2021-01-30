package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryUnitGoodsVO implements Serializable {
    private static final long serialVersionUID = 8649705922317903666L;

    @Schema(description = "序号")
    private Long lineNum;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "单位")
    private String unitName;

    @Schema(description = "物料名称")
    private String goodsName;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "申请数量")
    private Long amount;

    @Schema(description = "业务单式/宣传单页(0/1)")
    private Integer type;

    /**
     * 审批状态
     */
    @Schema(description = "审批状态")
    private Integer approvedState;
}
