package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.api.PublicityApplicationApi;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeReqVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeResVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;
import com.hbhb.web.annotation.UserId;

import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author yzc
 * @since 2021-01-05
 */
@Tag(name = "宣传管理-物料工作台")
@RestController
@RequestMapping("/application/notice")
public class ApplicationNoticeController implements PublicityApplicationApi {

    @Resource
    private ApplicationNoticeService noticeService;

    @Operation(summary = "统计待办提醒数量")
    @Override
    public Long countNotice(@Parameter(description = "用户id") Integer userId) {
        return noticeService.countNotice(userId);
    }

    @Operation(summary = "获取登录用户的待办提醒")
    @GetMapping("/summary")
    public List<ApplicationNoticeVO> getUserInvoiceNotice(@Parameter(hidden = true) @UserId Integer userId) {
        return noticeService.listInvoiceNotice(userId);
    }

    @Operation(summary = "根据接收人id查询提醒列表")
    @GetMapping("/list")
    public PageResult<ApplicationNoticeResVO> getBudgetProjectNoticeList(
            @Parameter(hidden = true) @UserId Integer userId,
            @Parameter(description = "筛选条件") ApplicationNoticeReqVO noticeVo,
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize) {
        noticeVo.setReceiver(userId);
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return noticeService.pagePrintNotice(noticeVo, pageNum, pageSize);
    }

    @Operation(summary = "更新提醒消息为已读")
    @PutMapping("/{id}")
    public void changeNoticeState(@Parameter(description = "id") @PathVariable Long id) {
        noticeService.changeNoticeState(id);
    }

}
