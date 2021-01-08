package com.hbhb.cw.publicity.web.controller;

import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;
import com.hbhb.web.annotation.UserId;

import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Tag(name = "宣传管理-物料采购")
@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private SysUserApiExp sysUserApiExp;

    @GetMapping("/purchase")
    @Operation(summary = "营业厅物料采购需求汇总")
    public PageResult<PurchaseGoodsResVO> getPurchaseGoods(GoodsReqVO goodsReqVO,
                                                           @Parameter(hidden = true)@UserId Integer userId,
                                                           Integer pageNum,
                                                           Integer pageSize) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        if (goodsReqVO.getUnitId()==null){
            goodsReqVO.setUnitId(sysUserApiExp.getUserInfoById(userId).getUnitId());
        }
        return goodsService.getPurchaseGoodsList(goodsReqVO, pageNum, pageSize);
    }


    @Operation(summary = "导出营业厅物料采购需求汇总")
    @PostMapping("/export")
    public void templateWrite(HttpServletRequest request, HttpServletResponse response,
                              @RequestBody GoodsReqVO cond,
                              @Parameter(hidden = true)@UserId Integer userId) {
        if (cond.getUnitId()==null){
            cond.setUnitId(sysUserApiExp.getUserInfoById(userId).getUnitId());
        }
        List<List<String>> list = goodsService.getPurchaseGoodsExport(cond);
        List<List<String>> head = goodsService.getHead(cond);
        String fileName = ExcelUtil.encodingFileName(request, "采购需求");
        ExcelUtil.export2WebWithHead(response, fileName, fileName, head, list);
    }
}
