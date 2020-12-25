package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsSaveGoodsVO implements Serializable {
    private static final long serialVersionUID = -6878544639603686294L;

    @Schema(description = "保存参数")
    private GoodsReqVO goodsReqVO;

    @Schema(description = "修改参数")
    private List<GoodsChangerVO> list;
}
