package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-11-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryGoodsResVO implements Serializable {
    private static final long serialVersionUID = -8342592896230845102L;

    @Schema(description = "业务单式申请数量")
    private List<SummaryGoodsVO> simplexList;
    @Schema(description = "宣传单页申请数量")
    private List<SummaryGoodsVO> singleList;

    private Boolean flag;
    private String checkerState;
}
