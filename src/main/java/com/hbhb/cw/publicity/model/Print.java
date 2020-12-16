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
public class Print implements Serializable {
    private static final long serialVersionUID = -922348690978064283L;
    private Long id;
    /**
     * 申请单号
     */
    private String printNum;
    /**
     * 申请单名称
     */
    private String printName;
    /**
     * 申请部门单位id
     */
    private Integer unitId;
    /**
     * 申请人用户id
     */
    private Integer userId;
    /**
     * 申请时间
     */
    private Date applyTime;
    /**
     * 材料类型（0 业务单式/1 宣传单页）
     */
    private Integer materialType;
    /**
     * 市场部审核员
     */
    private Integer roleUserId;
    /**
     * 预估金额
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
     * 修改时间
     */
    private Date updateTime;
    /**
     * 更新者
     */
    private String updateUp;
    /**
     * 流程状态
     */
    private Integer state;
    /**
     * 删除状态
     */
    private Boolean deleteFlag;

}
