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
public class MaterialsBudgetResVO implements Serializable {
    private static final long serialVersionUID = -3122586893281095009L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "年初预算金额（元）")
    private BigDecimal budget;

    @Schema(description = "已使用金额（元）")
    private BigDecimal amountPaid;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "预算使用比例")
    private BigDecimal proportion;
}
