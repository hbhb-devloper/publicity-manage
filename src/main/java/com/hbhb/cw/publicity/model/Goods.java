package com.hbhb.cw.publicity.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods implements Serializable {
    private static final long serialVersionUID = 476064199771710497L;
    private Long id;
    /**
     * 宣传活动id
     */
    private Long activityId;
    /**
     * 宣传产品
     */
    private String goodsName;
    /**
     * 宣传编号
     */
    private String goodsNum;
    /**
     * 物料审核人
     */
    private String checker;
    /**
     * 计量单位
     */
    private String unit;
    /**
     * 尺寸
     */
    private String size;
    /**
     * 纸张
     */
    private String paper;
    /**
     * 联次
     */
    private String attribute;
    /**
     * 是否加盖杭州分公司合同章
     */
    private Integer hasSeal;
    /**
     * 是否有编号
     */
    private Integer hasNum;
    /**
     * 是否使用
     */
    private Integer state;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图片
     */
    private String picture;
    /**
     * 添加页面提示内容
     */
    private String tips;
    /**
     * 0业务单式，1宣传单页
     */
    private Integer type;
    /**
     * 编辑时间
     */
    private Date updateTime;
    /**
     * 版面联系人
     */
    private String updateBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建者
     */
    private String createBy;
}
