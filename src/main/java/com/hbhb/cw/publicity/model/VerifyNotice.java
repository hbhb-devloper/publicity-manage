package com.hbhb.cw.publicity.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2021-01-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyNotice implements Serializable {
    private static final long serialVersionUID = 6589763292149461612L;
    private Long id;
    /**
     * 批次号
     */
    private String batchNum;
    /**
     * 接收人id
     */
    private Integer receiver;
    /**
     * 归属单位
     */
    private Integer unitId;
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
}
