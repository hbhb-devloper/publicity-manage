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
@AllArgsConstructor
@NoArgsConstructor
public class MaterialsInitVO implements Serializable {
    private static final long serialVersionUID = 8750147626397804360L;
    @Schema(description = "物料id")
    Integer materialsId;
    @Schema(description = "流程类型id")
    Long flowTypeId;
    @Schema(description = "用户id")
    Integer userId;
}
