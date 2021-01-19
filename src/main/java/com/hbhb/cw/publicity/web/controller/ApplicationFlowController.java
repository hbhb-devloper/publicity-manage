package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.ApplicationFlowService;
import com.hbhb.cw.publicity.web.vo.FlowWrapperApplicationVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-12-02
 */
@Tag(name = "宣传管理-审批申请")
@RestController
@RequestMapping("/application/flow")
@Slf4j
public class ApplicationFlowController {

    @Resource
    private ApplicationFlowService applicationFlowService;

    @GetMapping("")
    @Operation(summary = "得到流程中各个节点")
    public FlowWrapperApplicationVO getFlow(String batchNum,Integer unitId, @Parameter(hidden = true)@UserId Integer userId){
        return applicationFlowService.getInfoByBatchNum(batchNum,userId,unitId);
    }
}
