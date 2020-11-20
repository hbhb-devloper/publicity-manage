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
@AllArgsConstructor
@NoArgsConstructor
public class PublicityGoodsSet implements Serializable {
    private static final long serialVersionUID = 8280311819381564938L;
    private Long id;
    /**
     * 截至时间
     */
    private Date deadline;
    /**
     * 物料产品id
     */
    private Long goodsId;
    /**
     * 次级
     */
    private Integer goodsIndex;
    /**
     * 是否发起签报
     */
    private Date isEnd;
}
