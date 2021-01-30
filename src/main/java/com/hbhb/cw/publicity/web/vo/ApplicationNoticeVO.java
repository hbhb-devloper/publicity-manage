package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2021-01-05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationNoticeVO implements Serializable {
    private static final long serialVersionUID = 7668254396519171210L;
    private Long id;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "批次号")
    private String batchNum;
    @Schema(description = "日期")
    private String date;
    @Schema(description = "签报人")
    private String userName;
    @Schema(description = "提醒类型")
    private String noticeType;
    @Schema(description = "单位")
    private Integer unitId;
}
