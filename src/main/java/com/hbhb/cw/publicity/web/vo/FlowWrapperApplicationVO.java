package com.hbhb.cw.publicity.web.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 * @since 2021-01-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowWrapperApplicationVO implements Serializable {
    private static final long serialVersionUID = 6637865398792006586L;
    @Schema(description = "当前节点序号")
    private Integer index;
    @Schema(description = "节点列表")
    private List<ApplicationFlowInfoVO> nodes;
}
