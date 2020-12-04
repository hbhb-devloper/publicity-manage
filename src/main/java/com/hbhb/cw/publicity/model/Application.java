package com.hbhb.cw.publicity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application implements java.io.Serializable {
    private static final long serialVersionUID = -1644775189499439324L;
    private Long id;
    /**
     * 单位id
     */
    private Integer unitId;
    /**
     * 营业厅id
     */
    private Long hallId;
    /**
     * 申请表单创建时间
     */
    private Date createTime;
    /**
     * 批次号（年份+月份+序号）
     */
    private String batchNum;
    /**
     * 表单内容是否可更改
     */
    private Boolean editable;
    /**
     * 是否提交
     */
    private Boolean submit;
    /**
     * 审批状态
     */
    private Integer approvedState;
}
