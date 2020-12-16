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
public class MaterialsNotice implements Serializable {
    private static final long serialVersionUID = 3728932370026859549L;
    private Long id;
    /**
     * 宣传画面id
     */
    private Integer materialsId;
    /**
     * 接收人id
     */
    private Integer receiver;
    /**
     * 发起人id
     */
    private Integer promoter;
    /**
     * 提醒内容
     */
    private String content;
    /**
     * 提醒状态（0-未读、1-已读）
     */
    private Integer state;
    /**
     * 优先级（0-普通、1-紧急）
     */
    private Integer priority;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 流程类型id
     */
    private Long flowTypeId;
}