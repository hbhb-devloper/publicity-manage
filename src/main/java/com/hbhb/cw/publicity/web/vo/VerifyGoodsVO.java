package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyGoodsVO implements Serializable {
    private static final long serialVersionUID = 8658857851897419907L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "物料名称")
    private String goodsName;

    @Schema(description = "申请数量")
    private Long modifyAmount;
}
