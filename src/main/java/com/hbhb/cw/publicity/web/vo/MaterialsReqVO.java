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
public class MaterialsReqVO implements Serializable {
    private static final long serialVersionUID = -1378190020536072789L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "申请时间:年-月")
    private String applyTime;

    @Schema(description = "审批状态")
    private Integer state;

    @Schema(description = "申请单名称")
    private String materialsName;

    @Schema(description = "申请单号")
    private String materialsNum;
}
