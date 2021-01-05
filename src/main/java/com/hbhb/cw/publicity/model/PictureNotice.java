package com.hbhb.cw.publicity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.annotation.entity.AutoID;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PictureNotice implements Serializable {
    private static final long serialVersionUID = -7055460174256972511L;
    @AutoID
    private Long id;
    /**
     * 宣传画面id
     */
    private Long pictureId;
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
