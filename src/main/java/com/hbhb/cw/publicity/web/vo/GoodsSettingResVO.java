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
 * @since 2020-12-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsSettingResVO implements Serializable {
    private static final long serialVersionUID = -2548897131214535399L;

    @Schema(description = "次序")
    private List<Integer> goodsIndexList;

    @Schema(description = "此次")
    private Integer goodsIndex;
}
