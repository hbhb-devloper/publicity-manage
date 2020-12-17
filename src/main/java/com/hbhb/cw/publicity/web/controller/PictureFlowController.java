package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.flowcenter.vo.FlowApproveInfoVO;
import com.hbhb.cw.publicity.service.PictureFlowService;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
    public List<FlowApproveInfoVO> getPrintFlow(@PathVariable Long pictureId, @UserId Integer userId) {
        return flowService.getInvoiceNodeList(pictureId, userId);
    }
}
