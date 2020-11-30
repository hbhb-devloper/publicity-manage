package com.hbhb.cw.publicity.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangxiaogang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureInitVO implements Serializable {
    private static final long serialVersionUID = -2756881099791962433L;
    @Schema(description = "宣传画面id")
    Integer printId;
    @Schema(description = "流程类型id")
    Long flowTypeId;
    @Schema(description = "用户id")
    Integer userId;
}
