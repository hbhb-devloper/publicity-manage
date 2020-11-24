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
public class UserGoodsVO implements Serializable {
    private static final long serialVersionUID = 8658857851897419907L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "营业厅id")
    private Integer hallId;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "次序")
    private Integer goodsIndex;
}
