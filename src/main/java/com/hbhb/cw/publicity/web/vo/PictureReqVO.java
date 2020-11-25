package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureReqVO implements Serializable {
    private static final long serialVersionUID = -6391317459017840130L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "年份")
    private String year;

    @Schema(description = "月份")
    private String month;

    @Schema(description = "审批状态")
    private Integer state;

    @Schema(description = "画面类型")
    private Integer pictureType;
}
