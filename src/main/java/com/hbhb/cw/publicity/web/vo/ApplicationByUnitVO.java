package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationByUnitVO implements Serializable {
    private static final long serialVersionUID = 3098733275352026270L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "物料id")
    private Long goodsId;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "审批者列表")
    private String checker;
}
