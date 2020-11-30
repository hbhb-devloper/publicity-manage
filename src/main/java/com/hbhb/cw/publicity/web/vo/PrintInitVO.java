package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintInitVO implements Serializable {
    private static final long serialVersionUID = -2614659372873662253L;
    @Schema(description = "宣传印刷品id")
    Integer printId;
    @Schema(description = "流程类型id")
    Long flowTypeId;
    @Schema(description = "用户id")
    Integer userId;
}
