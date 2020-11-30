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
public class PurchaseGoods implements Serializable {
    private static final long serialVersionUID = 1119035345191386751L;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "物料名称")
    private String goodsName;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "申请数量")
    private Long modifyAmount;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "联次")
    private String attribute;

    @Schema(description = "纸张")
    private String paper;

    @Schema(description = "物料归属单位")
    private String unitName;

    @Schema(description = "物料负责人")
    private String checker;
}
