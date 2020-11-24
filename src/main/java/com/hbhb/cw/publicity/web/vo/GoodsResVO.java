package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

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
public class GoodsResVO implements Serializable {
    private static final long serialVersionUID = 3929777807614093346L;

    @Schema(description = "产品列表")
    private List<GoodsVO> list;

    @Schema(description = "保存按钮是否可用")
    private Boolean flag;
}
