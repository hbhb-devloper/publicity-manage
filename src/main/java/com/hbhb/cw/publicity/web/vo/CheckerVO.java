package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2020-12-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckerVO implements Serializable {
    private static final long serialVersionUID = -5986302165582382125L;

    @Schema(description = "前物料审核员")
    private Integer beforeId;

    @Schema(description = "后物料审核员")
    private Integer afterId;
}
