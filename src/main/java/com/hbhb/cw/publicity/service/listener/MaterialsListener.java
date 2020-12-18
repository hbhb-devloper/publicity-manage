package com.hbhb.cw.publicity.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.MaterialsImportVO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangxiaogang
 */
@Slf4j
@SuppressWarnings(value = {"rawtypes"})
public class MaterialsListener extends AnalysisEventListener {
    /**
     * 批处理条数，每隔多少条清理一次list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5000;

    private final Map<Integer, String> importHeadMap = new HashMap<>();

    /**
     * 数据行
     */
    private final List<MaterialsImportVO> dataList = new ArrayList<>();

    private final MaterialsService materialsService;

    private final AtomicLong printId;


    public MaterialsListener(MaterialsService materialsService, AtomicLong printId) {
        this.materialsService = materialsService;
        this.printId = printId;
    }

    /**
     * 每次读取一条数据时调用该方法
     */
    @Override
    public void invoke(Object object, AnalysisContext context) {
        dataList.add((MaterialsImportVO) object);
        if (dataList.size() >= BATCH_COUNT) {
            saveData();
            dataList.clear();
        }
    }

    /**
     * 所有数据解析完成后调用该方法
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 确保最后一次的数据入库
        saveData();
        dataList.clear();
    }

    /**
     * 保存预算数据
     */
    private void saveData() {

        materialsService.saveMaterials(dataList, importHeadMap, printId);

    }


    /**
     * 获取表头
     */
    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        if (headMap != null) {
            importHeadMap.putAll(headMap);
        }
        // 根据自己的情况去做表头的判断即可
    }

}
