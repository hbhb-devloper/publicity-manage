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
public class SummaryGoodsResVO implements Serializable {
    private static final long serialVersionUID = -8342592896230845102L;

    private List<SummaryGoodsVO> list;

    private Boolean flag;
}
