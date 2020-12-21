package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
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
public class MaterialsBudgetVO implements Serializable {
    private static final long serialVersionUID = 8263895821938668542L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "年初预算金额（元）")
    private BigDecimal budget;

    @Schema(description = "已通过金额（元）")
    private BigDecimal amountPaid;

    @Schema(description = "申报中金额（元）")
    private BigDecimal declaration;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "共计已使用金额")
    private BigDecimal useAmount;

    @Schema(description = "预算使用比例")
    private BigDecimal proportion;

}
