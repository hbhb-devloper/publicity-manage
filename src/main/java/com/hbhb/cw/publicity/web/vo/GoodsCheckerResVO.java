package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCheckerResVO implements Serializable {
    private static final long serialVersionUID = 2435131721347699394L;

    private List<GoodsCheckerVO> list;

    @Schema(description = "审核操作")
    private Integer detailState;
}
