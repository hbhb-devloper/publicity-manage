package com.hbhb.cw.publicity.web.controller;

import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;
import com.hbhb.web.annotation.UserId;

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

    @GetMapping("/purchase")
    @Operation(summary = "营业厅物料采购需求汇总")
    public List<PurchaseGoodsResVO> getPurchaseGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getPurchaseGoodsList(goodsReqVO);
    }


    @Operation(summary = "导出营业厅物料采购需求汇总")
    @PostMapping("/export")
    public void templateWrite(HttpServletRequest request, HttpServletResponse response,
                              @RequestBody GoodsReqVO cond,
                              @UserId Integer userId) {
        List<List<String>> list = goodsService.getPurchaseGoodsExport(cond);
        List<List<String>> head = goodsService.getHead(cond);
        String fileName = ExcelUtil.encodingFileName(request, "采购需求");
        ExcelUtil.export2WebWithHead(response, fileName, fileName, head, list);
    }
}
