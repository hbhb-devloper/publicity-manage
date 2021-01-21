package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
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
public class PrintMaterialsVO implements Serializable {
    private static final long serialVersionUID = -1586909602654261432L;
    @Schema(description = "申请数量")
    private Integer applyCount;

    @Schema(description = "计量单位")
    private String units;

    @Schema(description = "纸张样式")
    private String style;

    @Schema(description = "制作工艺")
    private String craft;

    @Schema(description = "部门")
    private Integer unitId;

    @Schema(description = "部门")
    private String unitName;

    @Schema(description = "分配地方（地址）")
    private String address;

    @Schema(description = "是否加盖合同分公司印章")
    private String isSeal;

    @Schema(description = "接收人/电话")
    private String receivedBy;

    @Schema(description = "是否有合同编号")
    private String isNum;

    @Schema(description = "需送达日期")
    private Date deliveryDate;

    @Schema(description = "备注")
    private String remake;

    @Schema(description = "物料名称")
    private String materialsName;

    @Schema(description = "类型（1-业务单式、2-宣传单页）")
    private Integer type;
}
