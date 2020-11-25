package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitGoodsStateVO implements Serializable {
    private static final long serialVersionUID = -213345646498657058L;

    @Schema(description = "单位名称")
    private Integer unitName;

    @Schema(description = "分公司提交状态")
    private Integer state;
}
