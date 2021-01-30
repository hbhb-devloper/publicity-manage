package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.MaterialsNoticeService;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.NoticeReqVO;
import com.hbhb.cw.publicity.web.vo.NoticeResVO;
import com.hbhb.cw.publicity.web.vo.NoticeVO;
import com.hbhb.cw.publicitymanage.api.MaterialsApi;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-物料制作工作台")
@RestController
@RequestMapping("/materials/notice")
public class MaterialsNoticeController implements MaterialsApi {

    @Resource
    private MaterialsNoticeService noticeService;
    @Resource
    private MaterialsService materialsService;

    @Operation(summary = "统计待办提醒数量")
    @Override
    public Long countNotice(@Parameter(description = "用户id") Integer userId) {
        return noticeService.countNotice(userId);
    }

    @Operation(summary = "获取登录用户的待办提醒")
    @GetMapping("/summary")
    public List<NoticeVO> getUserInvoiceNotice(@Parameter(hidden = true) @UserId Integer userId) {
        return noticeService.listInvoiceNotice(userId);
    }

    @Operation(summary = "根据接收人id查询提醒列表")
    @GetMapping("/list")
    public PageResult<NoticeResVO> getBudgetProjectNoticeList(
            @Parameter(hidden = true) @UserId Integer userId,
            @Parameter(description = "筛选条件") NoticeReqVO noticeVo,
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize) {
        noticeVo.setUserId(userId);
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return noticeService.pagePrintNotice(noticeVo, pageNum, pageSize);
    }

    @Operation(summary = "更新提醒消息为已读")
    @PutMapping("/{id}")
    public void changeNoticeState(@Parameter(description = "id") @PathVariable Long id) {
        noticeService.changeNoticeState(id);
    }

    @Operation(summary = "定时任务新增每年预算")
    @Override
    public void saveBudget() {
        materialsService.saveMaterialsBudget();
    }
}
