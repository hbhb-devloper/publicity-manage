package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryCondVO implements Serializable {
    private static final long serialVersionUID = -8342592896230845102L;

    /**
     * 单位id
     */
    private Integer unitId;

    /**
     * 批次号
     */
    private String batchNum;

    /**
     *
     */
    private Integer type;
    /**
     * 营业厅
     */
    private Long hallId;

    private Integer state;

}
