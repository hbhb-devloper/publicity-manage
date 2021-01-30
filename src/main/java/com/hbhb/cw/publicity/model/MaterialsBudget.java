package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Year;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialsBudget implements Serializable {
    private static final long serialVersionUID = 527657024065298828L;

    /**
     * id
     */
    private Long id;
    /**
     * 单位id
     */
    private Integer unitId;
    /**
     * 年初预算金额（元）
     */
    private BigDecimal budget;
    /**
     * 备注
     */
    private String remark;
    /**
     * 预算年份
     */
    private Year budgetYear;

}
