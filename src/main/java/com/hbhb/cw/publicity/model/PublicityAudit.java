package com.hbhb.cw.publicity.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicityAudit implements Serializable {
    private static final long serialVersionUID = 3080175977395019122L;
    private Long id;
    /**
     * 产品id
     */
    private Long goodsId;
    /**
     * 营业厅id
     */
    private Long hallId;
    /**
     * 申请数量
     */
    private Long beforeAmount;
    /**
     * 修改后申请数量
     */
    private Long afterAmount;
    /**
     * 提交日期
     */
    private Date submitTime;
    /**
     * 次级
     */
    private Integer goodsIndex;
    /**
     * 分公司是否提交
     */
    private Integer submit;
    /**
     * 市场部或政企是否通过
     */
    private Integer state;
    /**
     * 审批是否通过
     */
    private Integer aproved;
}
