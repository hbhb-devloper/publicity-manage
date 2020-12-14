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
public class GoodsSettingVO implements Serializable {
    private static final long serialVersionUID = -2548897131214535399L;

    @Schema(description = "截止时间")
    private List<String> deadlineList;

    @Schema(description = "提示内容")
    private String contents;
}
