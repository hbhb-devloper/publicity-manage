package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

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
public class SummaryGoodsVO implements Serializable {
    private static final long serialVersionUID = 8649705922317903666L;

    @Schema(description = "序号")
    private Long lineNum;

    @Schema(description = "物料申请id")
    private Long applicationId;

    @Schema(description = "营业厅Id")
    private Long hallId;

    @Schema(description = "营业厅")
    private String hallName;

    @Schema(description = "物料名称")
    private String goodsName;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "物料归属单位")
    private String unitName;

    @Schema(description = "申请数量")
    private Long applyAmount;

    @Schema(description = "修改后申请数量")
    private Long modifyAmount;

    @Schema(description = "业务单式/宣传单页(0/1)")
    private Integer type;

    @Schema(description = "状态")
    private String state;

    @Schema(description = "日期")
    private String time;
}
