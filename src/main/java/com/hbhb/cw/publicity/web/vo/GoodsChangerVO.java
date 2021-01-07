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
public class GoodsChangerVO implements Serializable {
    private static final long serialVersionUID = 4874055501533876549L;

    @Schema(description = "申领货物详情id（detailId）")
    private Long id;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "修改申请数量")
    private Long modifyAmount;
}
