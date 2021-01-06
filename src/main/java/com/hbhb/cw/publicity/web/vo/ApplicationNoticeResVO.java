package com.hbhb.cw.publicity.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
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
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationNoticeResVO implements Serializable {

    private static final long serialVersionUID = -2963184819678898269L;

    private Long id;

    @Schema(description = "项目签报id")
    private String batchNum;

    @JsonIgnore
    @Schema(description = "发起单位id")
    private Integer unitId;

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

    @Schema(description = "状态名称")
    private String stateLabel;

    @Schema(description = "提醒类型")
    private String noticeType;

    @Schema(description = "流程类型名称")
    private String flowType;

    @Schema(description = "发起单位名称")
    private String unitName;
}