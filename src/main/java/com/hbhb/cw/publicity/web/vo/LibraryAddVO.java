package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryAddVO  implements Serializable {
    private static final long serialVersionUID = 3398982181580275078L;
    private Long id;
    /**
     * 分公司
     */
    @Schema(description = "分公司")
    private Integer unitId;
    /**
     * 宣传活动id
     */
    @Schema(description = "宣传活动id")
    private Long parentId;
    /**
     * 宣传产品
     */
    @Schema(description = "宣传产品")
    private String goodsName;
    /**
     * 宣传编号
     */
    @Schema(description = "宣传编号")
    private String goodsNum;
    /**
     * 物料审核人
     */
    @Schema(description = "物料审核人")
    private String checker;
    /**
     * 计量单位
     */
    @Schema(description = "计量单位")
    private String unit;
    /**
     * 尺寸
     */
    @Schema(description = "尺寸")
    private String size;
    /**
     * 纸张
     */
    @Schema(description = "纸张")
    private String paper;
    /**
     * 联次
     */
    @Schema(description = "联次")
    private String attribute;
    /**
     * 是否加盖杭州分公司合同章
     */
    @Schema(description = "是否加盖杭州分公司合同章")
    private Integer hasSeal;
    /**
     * 是否有编号
     */
    @Schema(description = "是否又编号")
    private Integer hasNum;
    /**
     * 是否使用
     */
    @Schema(description = "是否使用")
    private Boolean state;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    /**
     * 图片
     */
    @Schema(description = "图片")
    private String picture;
    /**
     * 添加页面提示内容
     */
    @Schema(description = "添加页面提示内容")
    private String tips;
    /**
     * 0业务单式，1宣传单页
     */
    @Schema(description = "0业务单式，1宣传单页")
    private Integer type;
    /**
     * 编辑时间
     */
    @Schema(description = "编辑时间")
    private Date updateTime;
    /**
     * 版面联系人
     */
    @Schema(description = "版面联系人")
    private String updateBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "是否为活动")
    private Boolean flag;
}
