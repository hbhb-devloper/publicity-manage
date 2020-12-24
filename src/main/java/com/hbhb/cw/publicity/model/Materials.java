package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Materials implements Serializable {
    private static final long serialVersionUID = -948088127398119727L;
    private Long id;
    /**
     * 单位id
     */
    private Integer unitId;
    /**
     * 申请单名称
     */
    private String materialsName;
    /**
     * 申请单号
     */
    private String materialsNum;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 申请时间
     */
    private Date applyTime;
    /**
     * 是否有宽带
     */
    private Integer wideBand;
    /**
     * 制作商
     */
    private String producers;
    /**
     * 预算费用（元）
     */
    private BigDecimal predictAmount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新者
     */
    private Integer updateBy;
    /**
     * 流程状态
     */
    private Integer state;
    /**
     * 删除状态
     */
    private Boolean deleteFlag;
}
