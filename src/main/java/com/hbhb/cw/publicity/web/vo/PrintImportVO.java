package com.hbhb.cw.publicity.web.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
public class PrintImportVO implements Serializable {
    private static final long serialVersionUID = 7860082310457338110L;

    @ExcelIgnore
    private Long id;

    @Schema(description = "物料名称")
    @ExcelProperty(index = 0)
    private String materialsName;

    @Schema(description = "申请数量")
    @ExcelProperty(index = 1)
    private Integer applyCount;

    @Schema(description = "计量单位")
    @ExcelProperty(index = 2)
    private String units;

    @Schema(description = "纸张样式")
    @ExcelProperty(index = 3)
    private String style;

    @Schema(description = "制作工艺")
    @ExcelProperty(index = 4)
    private String craft;

    @Schema(description = "部门")
    @ExcelProperty(index = 5)
    private String unitName;

    @Schema(description = "分配地方（地址）")
    @ExcelProperty(index = 6)
    private String address;

    @Schema(description = "接收人/电话")
    @ExcelProperty(index = 7)
    private String receivedBy;

    @Schema(description = "是否加盖合同分公司印章")
    @ExcelProperty(index = 8)
    private Integer isSeal;

    @Schema(description = "是否有合同编号")
    @ExcelProperty(index = 9)
    private Integer isNum;

    @Schema(description = "需送达日期")
    @ExcelProperty(index = 10)
    private String deliveryDate;

    @Schema(description = "备注")
    @ExcelProperty(index = 11)
    private String remake;

    @Schema(description = "10-业务单式、20-宣传单页")
    @ExcelIgnore
    private Integer type;

    @Schema(description = "印刷品id")
    @ExcelIgnore
    private Long printId;


}
