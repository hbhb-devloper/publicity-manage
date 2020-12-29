package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.flowcenter.vo.FlowApproveVO;
import com.hbhb.cw.flowcenter.vo.FlowWrapperVO;
import com.hbhb.cw.publicity.service.PrintFlowService;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-宣传印刷用品流程")
@RestController
@RequestMapping("/print/flow")
@Slf4j
public class PrintFlowController {
    @Resource
    private PrintFlowService printFlowService;

    @Operation(summary = "获取宣传印刷用品流程详情")
    @GetMapping("/list/{printId}")
    public FlowWrapperVO getPrintFlow(@PathVariable Long printId,
                                      @Parameter(hidden = true)
                                      @UserId Integer userId) {
        return printFlowService.getInvoiceNodeList(printId, userId);
    }

    @Operation(summary = "审批流程")
    @PostMapping("/approve")
    public void approve(@Parameter(description = "审批") @RequestBody FlowApproveVO flowApproveVO,
                        @Parameter(hidden = true) @UserId Integer userId) {
        printFlowService.approve(flowApproveVO, userId);
    }

}
