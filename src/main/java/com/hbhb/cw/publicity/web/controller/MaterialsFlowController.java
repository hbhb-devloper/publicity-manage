package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.flowcenter.vo.FlowWrapperVO;
import com.hbhb.cw.publicity.service.MaterialsFlowService;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-宣传物料设计流程")
@RestController
@RequestMapping("/materials/flow")
@Slf4j
public class MaterialsFlowController {
    @Resource
    private MaterialsFlowService flowService;

    @Operation(summary = "获取宣传物料设计流程详情")
    @GetMapping("/list/{materialsId}")
    public FlowWrapperVO getMaterialsFlow(@PathVariable Long materialsId,
                                          @Parameter(hidden = true) @UserId Integer userId) {
        return flowService.getMaterialsNodeList(materialsId, userId);
    }
}
