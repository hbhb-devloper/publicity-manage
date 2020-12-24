package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyHallGoodsVO implements Serializable {
    private static final long serialVersionUID = 8330322602506888852L;

    @Schema(description = "物料申请id")
    private Long applicationDetailId;

    @Schema(description = "单位名称")
    private Integer unitId;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "营业厅id")
    private Long hallId;

    @Schema(description = "营业厅")
    private String hallName;

    @Schema(description = "物料名称")
    private String goodsName;

    @Schema(description = "申请数量")
    private Long modifyAmount;
}
