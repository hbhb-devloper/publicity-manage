package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsReqVO implements Serializable {
    private static final long serialVersionUID = -6469305293332933203L;

    @Schema(description = "营业厅")
    private Long hallId;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "次序")
    private Integer goodsIndex;

    @Schema(description = "申请状态")
    private Integer detailState;

    @Schema(description = "提交是否置灰")
    private Boolean flag;
}
