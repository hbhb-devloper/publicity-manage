package com.hbhb.cw.publicity.web.controller;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Tag(name = "宣传管理-物料申请")
@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @GetMapping("/list")
    @Operation(summary = "营业厅物料产品申请列表")
    public GoodsResVO getGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getList(goodsReqVO);
    }

    @PostMapping("/apply")
    @Operation(summary = "新增申请数量")
    public void addGoods(@RequestBody List<ApplicationVO> list, @RequestBody GoodsReqVO goodsReqVO ) {
        goodsService.applyGoods(list, goodsReqVO);
    }

    @GetMapping("time")
    @Operation(summary = "通过时间得到第几月的第几次")
    public Integer getUserGoods(String time) {
        return goodsService.getGoodsSetting(time);
    }

    @GetMapping("/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总")
    public SummaryGoodsResVO getSimplexGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getSimplexList(goodsReqVO);
    }

    @GetMapping("/single")
    @Operation(summary = "营业厅物料宣传单页分公司汇总")
    public SummaryGoodsResVO getSingleGoods( GoodsReqVO goodsReqVO) {
        return goodsService.getSingleList(goodsReqVO);
    }

    @GetMapping("/audit/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总(审核)")
    public SummaryGoodsResVO getAuditSimplexGoods(GoodsReqVO goodsReqVO, Integer state) {
        if (goodsReqVO.getTime()==null){
            goodsReqVO.setTime(DateUtil.dateToString(new Date()));
        }
        return goodsService.getAuditSimplexList(goodsReqVO, state);
    }

    @GetMapping("/audit/single")
    @Operation(summary = "营业厅物料宣传单页分公司汇总(审核)")
    public SummaryGoodsResVO getAuditSingleGoods( GoodsReqVO goodsReqVO, Integer state) {
        if (goodsReqVO.getTime()==null){
            goodsReqVO.setTime(DateUtil.dateToString(new Date()));
        }
        return goodsService.getAuditSingleList(goodsReqVO, state);
    }

    @PostMapping("/save")
    @Operation(summary = "分公司保存物料")
    public void saveGoods(@RequestBody List<Long> list, @UserId Integer userId){
        goodsService.saveGoods(list,userId);
    }

    @PostMapping("/submit")
    @Operation(summary = "分公司保存物料")
    public void submitGoods(@RequestBody List<Long> list){
        goodsService.submitGoods(list);
    }

    @PutMapping("/changer")
    @Operation(summary = "分公司修改修改后申请数量")
    public void changerModifyAmount(List<GoodsChangerVO> list){
        goodsService.changerModifyAmount(list);
    }

    @GetMapping("/unit/goods")
    @Operation(summary = "营业厅物料分公司汇总（政企/市场部）")
    public SummaryUnitGoodsResVO getUnitGoods(GoodsReqVO goodsReqVO, Integer state) {
        return goodsService.getUnitGoodsList(goodsReqVO);
    }

    @GetMapping("/unit/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总（政企/市场部）")
    public List<SummaryUnitGoodsVO> getUnitSimplexGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getUnitSimplexList(goodsReqVO);
    }

    @GetMapping("/unit/single")
    @Operation(summary = "营业厅物料宣传单页分公司汇总（政企/市场部）")
    public List<SummaryUnitGoodsVO> getUnitSingleGoods( GoodsReqVO goodsReqVO) {
        return goodsService.getUnitSingleList(goodsReqVO);
    }

    @GetMapping("/unit/state")
    @Operation(summary = "营业厅物料宣传单页分公司汇总状态（政企/市场部）")
    public List<UnitGoodsStateVO> getUnitGoodsState(GoodsReqVO goodsReqVO) {
        return goodsService.getUnitGoodsStateList(goodsReqVO);
    }

    @GetMapping("/purchase")
    @Operation(summary = "营业厅物料采购需求汇总")
    public List<PurchaseGoods> getPurchaseGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getPurchaseGoodsList(goodsReqVO);
    }

}
