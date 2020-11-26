package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;

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
public class LibraryReqVO implements Serializable {
    private static final long serialVersionUID = -6469305293332933203L;

    @Schema(description = "父类id")
    private Long parentId;

    @Schema(description = "为活动/为产品")
    private Boolean flag;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "是否启用")
    private Boolean state;
}
