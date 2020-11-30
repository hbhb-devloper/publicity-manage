package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialsInfoVO implements Serializable {
    private static final long serialVersionUID = -4925013973478233876L;

    @Schema(description = "申请单名称")
    private String materialsName;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "是否有宽带")
    private Integer wideBand;

    @Schema(description = "制作商")
    private String producers;

    @Schema(description = "预算费用（元）")
    private BigDecimal predictAmount;

    @Schema(description = "备注")
    private String remark;
}
