package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVO implements Serializable {
    private static final long serialVersionUID = 5322118140771174224L;

    @Schema(description ="产品id")
    private Long goodsId;

    @Schema(description = "产品名称")
    private String goodsName;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "申请数量")
    private Long applyAmount;

    @Schema(description = "图片")
    private PublicityPictureVO pic;
}
