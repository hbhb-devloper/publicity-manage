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
public class GoodsApproveVO implements Serializable {
    private static final long serialVersionUID = -869856906131546038L;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "次序")
    private Integer goodsIndex;

    @Schema(description = "流程类型id")
    private Long flowTypeId;

    @Schema(description = "归属单位")
    private Integer underUnitId;
}
