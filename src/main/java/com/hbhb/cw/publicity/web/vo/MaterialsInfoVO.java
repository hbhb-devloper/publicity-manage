package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialsInfoVO implements Serializable {
    private static final long serialVersionUID = -4925013973478233876L;
    private Long id;

    @Schema(description = "物料名称")
    private String materialsName;

    @Schema(description = "申请数量")
    private Integer applyCount;

    @Schema(description = "计量单位")
    private String units;

    @Schema(description = "纸张样式")
    private String style;

    @Schema(description = "制作工艺")
    private String craft;

    @Schema(description = "部门")
    private String unitName;

    @Schema(description = "部门")
    private Integer unitId;

    @Schema(description = "分配地方（地址）")
    private String address;

    @Schema(description = "接收人")
    private String receivedBy;

    @Schema(description = "电话")
    private String receivedPhone;

    @Schema(description = "需送达日期")
    private Date deliveryDate;

    @Schema(description = "备注")
    private String remark;
}
