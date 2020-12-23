package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintReqVO implements Serializable {
    private static final long serialVersionUID = 577459651284293766L;

    private Long id;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "年份-月份")
    private String applyTime;

    @Schema(description = "审批状态")
    private Integer state;

    @Schema(description = "申请单名称")
    private String printName;

    @Schema(description = "申请单号")
    private String printNum;
}
