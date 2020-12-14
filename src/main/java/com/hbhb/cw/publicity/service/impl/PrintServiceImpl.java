package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.Exception.PublicityException;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.mapper.PrintFileMapper;
import com.hbhb.cw.publicity.mapper.PrintMapper;
import com.hbhb.cw.publicity.mapper.PrintMaterialsMapper;
import com.hbhb.cw.publicity.model.Print;
import com.hbhb.cw.publicity.model.PrintFile;
import com.hbhb.cw.publicity.model.PrintMaterials;
import com.hbhb.cw.publicity.rpc.*;
import com.hbhb.cw.publicity.service.PrintFlowService;
import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.web.vo.*;
import com.hbhb.cw.systemcenter.model.File;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
public class PrintServiceImpl implements PrintService {
    @Resource
    private PrintMapper printMapper;
    @Resource
    private PrintFileMapper fileMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private FileApiExp fileApi;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private PrintMaterialsMapper printMaterialsMapper;
    @Resource
    private FlowNodeApiExp nodeApi;
    @Resource
    private FlowNoticeApiExp noticeApi;
    @Resource
    private FlowTypeApiExp typeApi;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private PrintFlowService flowService;


    @Override
    public PageResult<PrintResVO> getPrintList(PrintReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<PrintResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<PrintResVO> list = printMapper.selectPrintByCond(reqVO, request);
        List<Integer> userIds = new ArrayList<>();
        Map<Integer, String> unitMapById = unitApi.getUnitMapById();
        list.getList().forEach(item -> userIds.add(item.getUserId()));
        Map<Integer, String> userMapById = userApi.getUserMapById(userIds);
        list.getList().forEach(item -> {
            item.setUserName(userMapById.get(item.getUserId()));
            item.setUnitName(unitMapById.get(item.getUnitId()));
        });
        return list;
    }

    @Override
    public PrintInfoVO getPrint(Long id) {
        Print print = printMapper.single(id);
        List<PrintFile> files = fileMapper.createLambdaQuery().andEq(PrintFile::getPrintId, id).select();
        // 获取文件列表信息
        List<Integer> fileIds = new ArrayList<>();
        files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
        List<File> fileInfoList = fileApi.getFileInfoBatch(fileIds);
        List<PrintFileVO> fileVo = Optional.of(files)
                .orElse(new ArrayList<>())
                .stream()
                .map(file -> PrintFileVO.builder()
                        .author(file.getCreateBy())
                        .createTime(file.getCreateTime().toString())
                        .id(file.getId())
                        .build())
                .collect(Collectors.toList());
        BeanConverter.copyBeanList(fileVo, fileInfoList.getClass());
        PrintInfoVO info = new PrintInfoVO();
        BeanConverter.convert(info, print.getClass());
        info.setFiles(fileVo);
        return info;
    }

    @Override
    public void addPrint(PrintInfoVO infoVO, Integer userId) {
        Print print = new Print();
        BeanUtils.copyProperties(print, infoVO);
        // 新增印刷用品
        printMapper.insert(print);
        List<PrintFile> fileList = setPrintFile(infoVO.getFiles(), userId);
        fileMapper.insertBatch(fileList);
    }

    @Override
    public void deletePrint(Long id) {
        printMapper.deleteById(id);
    }

    @Override
    public void updatePrint(PrintInfoVO infoVO, Integer userId) {
        Print print = new Print();
        BeanUtils.copyProperties(infoVO, print);
        printMapper.updateById(print);
        // 保存附件
        List<PrintFileVO> fileVOList = infoVO.getFiles();
        List<PrintFileVO> files = new ArrayList<>();
        for (PrintFileVO fileVo : fileVOList) {
            if (isEmpty(fileVo.getId())) {
                files.add(fileVo);
            }
        }
        List<PrintFile> fileList = setPrintFile(files, userId);
        fileMapper.insertBatch(fileList);
    }

    private List<PrintFile> setPrintFile(List<PrintFileVO> fileVOList, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        List<PrintFile> fileList = new ArrayList<>();
        if (fileVOList != null && !fileVOList.isEmpty()) {
            fileVOList.forEach(item -> fileList.add(PrintFile.builder()
                    .createBy(user.getNickName())
                    .createTime(new Date())
                    .fileId(item.getFileId())
                    .printId(item.getPrintId())
                    .build()));
        }
        return fileList;
    }

    @Override
    public void savePrint(List<PrintImportVO> dataList, Map<Integer, String> importHeadMap) {
        //导入
        List<PrintMaterials> materialsList = new ArrayList<>();
        Map<String, Integer> unitNameMap = unitApi.getUnitMapByUnitName();
        for (PrintImportVO importVo : dataList) {
            PrintMaterials materials = new PrintMaterials();
            BeanUtils.copyProperties(materials, importVo);
            materials.setUnitId(unitNameMap.get(importVo.getUnitName()));
            materialsList.add(materials);
            // TODO 缺少类型  缺少printId
        }
        printMaterialsMapper.insertBatch(materialsList);

    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(PrintInitVO initVO) {
        Print print = printMapper.single(initVO.getPrintId());
        //  1.判断登录用户是否与发票客户经理一致
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        if (!user.getNickName().equals(print.getCreateBy())) {
            throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
        }
        //  1.1 客户经理判断发票是否符合要求
        //  2.获取流程id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId(), print.getUserId());
        // 通过流程id得到流程节点属性
        List<FlowNodePropVO> flowProps = nodeApi.getFlowProp(flowId);
        //  3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, print.getUnitId(), initVO.getUserId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
        }
        //  4.同步节点属性
        syncBudgetProjectFlow(flowProps, print.getId(), initVO.getUserId());
        // 得到推送模板
        String inform = flowService.getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // 跟据流程id获取流程名称
        Flow flow = flowApi.getFlow(flowId);
        // todo 修改推送模板
        inform = inform.replace(""
                , print.getPrintNum() + "_" + print.getPrintName() + "_" + flow.getFlowName());
        // todo 推送消息给发起人


        //  6.更改发票流程状态
        print.setId(initVO.getPrintId());
        print.setState(NodeState.APPROVING.value());
        printMapper.updateById(print);

    }

    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer unitId, Integer userId) {
        return false;
    }

    private void syncBudgetProjectFlow(List<FlowNodePropVO> flowProps, Long id, Integer userId) {
    }

    private Long getRelatedFlow(Long flowTypeId, Integer userId) {
        return null;
    }
}
