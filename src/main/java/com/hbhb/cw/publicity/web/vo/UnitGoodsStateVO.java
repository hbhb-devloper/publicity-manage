package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitGoodsStateVO implements Serializable {
    private static final long serialVersionUID = -213345646498657058L;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "分公司提交状态")
    private String state;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "物料名称")
    private String goodsName;
}
