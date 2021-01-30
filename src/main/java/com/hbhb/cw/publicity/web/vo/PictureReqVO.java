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

    private Long id;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "申请时间:年-月")
    private String applyTime;

    @Schema(description = "审批状态")
    private Integer state;

    @Schema(description = "画面类型")
    private Integer pictureType;

    @Schema(description = "申请单名称")
    private String pictureName;

    @Schema(description = "申请单号")
    private String pictureNum;

}
