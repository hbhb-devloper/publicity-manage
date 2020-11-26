package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

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
public class SummaryUnitGoodsResVO implements Serializable {
    private static final long serialVersionUID = 8649705922317903666L;

    private List<SummaryUnitGoodsVO> list;

    private Boolean flag;
}
