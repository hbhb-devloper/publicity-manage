package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyHallGoodsReqVO implements Serializable {
    private static final long serialVersionUID = -8901875360455945846L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "批次号")
    private String batchNum;
}
