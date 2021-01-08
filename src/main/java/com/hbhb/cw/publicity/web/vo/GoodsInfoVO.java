package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsInfoVO implements Serializable {
    private static final long serialVersionUID = -7983687287161087272L;

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
    @Schema(description = "物料审核人Id")
    private Integer checker;
    /**
     * 物料审核人名字
     */
    @Schema(description = "无聊审核员名字")
    private String checkerName;
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
    private String hasSealLable;

    /**
     * 是否加盖杭州分公司合同章
     */
    @Schema(description = "是否加盖杭州分公司合同章")
    private Boolean hasSeal;
    /**
     * 是否有编号
     */
    @Schema(description = "是否又编号")
    private String hasNumLable;

    /**
     * 是否有编号
     */
    @Schema(description = "是否又编号")
    private Boolean hasNum;
    /**
     * 是否使用
     */
    @Schema(description = "是否使用")
    private String stateLable;

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
    private String updateTime;
    /**
     * 版面联系人Id
     */
    @Schema(description = "版面联系人Id")
    private Integer updateBy;
    /**
     * 版面联系人
     */
    @Schema(description = "版面联系人")
    private String updateName;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;


    @Schema(description = "true为列别/false为物料")
    private Boolean mold;

    @Schema(description = "图片")
    private PublicityPictureVO file;
}
