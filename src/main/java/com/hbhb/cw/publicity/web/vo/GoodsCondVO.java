package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCondVO implements Serializable {
    private static final long serialVersionUID = -6469305293332933203L;
    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "营业厅id")
    private Long hallId;

    @Schema(description = "次序")
    private Integer goodsIndex;
}
