package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCheckerVO implements Serializable {
    private static final long serialVersionUID = -9002986448943402423L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "物料id")
    private Long goodsId;
}
