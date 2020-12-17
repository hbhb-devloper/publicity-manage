package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wxg
 * @since 2020-09-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintNoticeVO implements Serializable {
    private static final long serialVersionUID = -7749355714186806298L;
    private Long id;

    @Schema(description = "印刷品id")
    private Long printId;

    @Schema(description = "接收人id")
    private Integer receiver;

    @Schema(description = "发起人id")
    private Integer promoter;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "流程类型id")
    private Long flowTypeId;
}
