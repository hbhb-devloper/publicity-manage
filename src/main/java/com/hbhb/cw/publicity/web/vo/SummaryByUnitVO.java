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
 * @since 2020-12-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryByUnitVO implements Serializable {
    private static final long serialVersionUID = 8855478827435299291L;

    @Schema(description = "业务单式申请数量")
    private List<SummaryUnitGoodsVO> simList;

    @Schema(description = "宣传单页申请数量")
    private List<SummaryUnitGoodsVO> singList;
}
