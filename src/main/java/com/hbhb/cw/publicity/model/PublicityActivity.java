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
public class PublicityActivity implements Serializable {
    private static final long serialVersionUID = -4458161997222595575L;
    private Long id;
    /**
     * 父类id
     */
    private Long pranetId;
    /**
     * 市场部或者政企
     */
    private Integer unitId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 是否启用
     */
    private Integer state;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private String updateBy;
}
