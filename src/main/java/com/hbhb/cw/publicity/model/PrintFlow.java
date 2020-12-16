package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class PrintFlow implements Serializable {
    private static final long serialVersionUID = 2989186283780499645L;
    private Long id;
    /**
     * 印刷品id
     */
    private Long printId;
    /**
     * 流程节点id
     */
    private String flowNodeId;
    /**
     * 流程角色id
     */
    private Long flowRoleId;
    /**
     * 审批者
     */
    private Integer userId;
    /**
     * 角色详情
     */
    private String roleDesc;
    /**
     * 分配者
     */
    private Long assigner;
    /**
     * 是否能够自定义流程（0-否、1-是）
     */
    private Boolean controlAccess;
    /**
     * 是否允许被设置不参与流程（0-不参与、1-参与）
     */
    private Boolean isJoin;
    /**
     * 操作（0-拒绝、1-同意）
     */
    private Integer operation;
    /**
     * 审批意见
     */
    private String suggestion;
    /**
     * 是否删除（0无/1删）
     */
    private Integer isDelete;
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
     * 修改者
     */
    private String updateBy;
}
