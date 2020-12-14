package com.hbhb.cw.publicity.web.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
public class MaterialsImportVO implements Serializable {
    private static final long serialVersionUID = 6434956745531006105L;

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

    @Schema(description = "接收人")
    @ExcelProperty(index = 7)
    private String receivedBy;

    @Schema(description = "电话")
    @ExcelProperty(index = 8)
    private String receivedPhone;

    @Schema(description = "需送达日期")
    @ExcelProperty(index = 9)
    private Date deliveryDate;

    @Schema(description = "备注")
    @ExcelProperty(index = 10)
    private String remake;


    @Schema(description = "印刷品id")
    @ExcelIgnore
    private Long materialsId;
}
