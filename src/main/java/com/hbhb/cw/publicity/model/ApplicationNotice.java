package com.hbhb.cw.publicity.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationNotice implements Serializable {
    private static final long serialVersionUID = -996134694532244214L;
    private Long id;
    /**
     * 项目签报id
     */
    private String batchNum;
    /**
     * 归口单位id
     */
    private Integer underUnitId;
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

