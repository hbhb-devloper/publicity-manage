package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyGoodsVO implements Serializable {
    private static final long serialVersionUID = 8658857851897419907L;

    private Integer unitId;

    private String unitName;

    private Long goodsId;

    private String goodsName;

    private Long applyAmount;
}
