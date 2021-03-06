package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.flowcenter.vo.FlowApproveVO;
import com.hbhb.cw.flowcenter.vo.FlowWrapperVO;
import com.hbhb.cw.publicity.service.PictureFlowService;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-宣传画面设计流程")
@RestController
@RequestMapping("/picture/flow")
@Slf4j
public class PictureFlowController {
    @Resource

    private PictureFlowService flowService;

    @Operation(summary = "获取宣传画面流程详情")
    @GetMapping("/list/{pictureId}")
    public FlowWrapperVO getPrintFlow(@PathVariable Long pictureId, @Parameter(hidden = true) @UserId Integer userId) {
        return flowService.getInvoiceNodeList(pictureId, userId);
    }

    @Operation(summary = "审批流程")
    @PostMapping("/approve")
    public void approve(@Parameter(description = "审批") @RequestBody FlowApproveVO flowApproveVO,
                        @Parameter(hidden = true) @UserId Integer userId) {
        flowService.approve(flowApproveVO, userId);
    }

}
