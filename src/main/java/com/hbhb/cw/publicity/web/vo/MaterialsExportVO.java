package com.hbhb.cw.publicity.web.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialsExportVO implements Serializable {
    private static final long serialVersionUID = 6972467700655170579L;

    @ExcelIgnore
    private Long id;

    @ExcelIgnore
    private Integer userId;

    @ExcelIgnore
    private Integer unitId;

    @ColumnWidth(30)
    @ExcelProperty(value = "申请单名称", index = 0)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String materialsName;

    @ColumnWidth(20)
    @ExcelProperty(value = "申请单号", index = 1)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String materialsNum;

    @ColumnWidth(20)
    @ExcelProperty(value = "用户名称", index = 2)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String nickName;

    @ColumnWidth(30)
    @ExcelProperty(value = "申请时间", index = 3)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    @Schema(description = "申请时间")
    private Date applyTime;

    @ColumnWidth(30)
    @ExcelProperty(value = "申请单位名称", index = 4)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String unitName;

    @ColumnWidth(30)
    @ExcelProperty(value = "是否有宽带（0-否，1-是）", index = 5)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private Integer wideBand;

    @ColumnWidth(20)
    @ExcelProperty(value = "制作商", index = 6)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String producers;

    @ColumnWidth(20)
    @ExcelProperty(value = "预算费用（元）", index = 7)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private BigDecimal predictAmount;

    @ColumnWidth(30)
    @ExcelProperty(value = "备注", index = 8)
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER)
    private String remark;

}
