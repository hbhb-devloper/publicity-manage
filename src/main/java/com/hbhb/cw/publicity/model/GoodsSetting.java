package com.hbhb.cw.publicity.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangxiaogang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsSetting implements Serializable {
    private static final long serialVersionUID = 8280311819381564938L;
    private Long id;
    /**
     * 截至时间
     */
    @Schema(description = "截至时间")
    private String deadline;
    /**
     * 次级
     */
    @Schema(description = "次序")
    private Integer goodsIndex;
    /**
     * 是否发起签报
     */
    @Schema(description = "是否发起签报")
    private String isEnd;
    /**
     * 相关内容
     */
    @Schema(description = "相关内容")
    private String contents;
}
