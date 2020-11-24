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
public class GoodsReqVO implements Serializable {
    private static final long serialVersionUID = -6469305293332933203L;

    @Schema(description = "单位id（政企/市场部）")
    private Integer unitId;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "营业厅id")
    private Long hallId;
}
