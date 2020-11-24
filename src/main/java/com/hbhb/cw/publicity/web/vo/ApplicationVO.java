package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationVO implements Serializable {
    private static final long serialVersionUID = 5322118140771174224L;

    @Schema(description = "产品id")
    private Long goodsId;

    @Schema(description = "申请数量")
    private Long applyAmount;
}
