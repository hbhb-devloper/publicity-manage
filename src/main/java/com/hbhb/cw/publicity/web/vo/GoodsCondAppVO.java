package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCondAppVO implements Serializable {
    private static final long serialVersionUID = 949730420746136363L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "时间")
    private String time;

    @Schema(description = "营业厅id")
    private Long hallId;

    @Schema(description = "次序")
    private Integer goodsIndex;

    List<ApplicationVO> list;
}
