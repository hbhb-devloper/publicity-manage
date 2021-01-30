package com.hbhb.cw.publicity.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class PrintMaterialsImportVO implements Serializable {
    private static final long serialVersionUID = 8477813639763803692L;

    @ExcelProperty(value = "申请数量", index = 0)
    private Integer applyCount;

    @ExcelProperty(value = "计量单位", index = 1)
    private String units;

    @ExcelProperty(value = "纸张样式", index = 2)
    private String style;

    @ExcelProperty(value = "制作工艺", index = 3)
    private String craft;

    @ExcelProperty(value = "部门", index = 4)
    private String unitName;

    @ExcelProperty(value = "分配地方（地址）", index = 5)
    private String address;

    @ExcelProperty(value = "是否加盖合同分公司印章", index = 6)
    private Integer isSeal;

    @ExcelProperty(value = "接收人/电话", index = 7)
    private String receivedBy;

    @ExcelProperty(value = "是否有合同编号", index = 8)
    private Integer isNum;

    @ExcelProperty(value = "需送达日期", index = 9)
    private String deliveryDate;

    @ExcelProperty(value = "备注", index = 10)
    private String remake;

    @ExcelProperty(value = "物料名称", index = 11)
    private String materialsName;

}
