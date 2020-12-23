package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.PrintFileMapper;
import com.hbhb.cw.publicity.mapper.PrintMapper;
import com.hbhb.cw.publicity.mapper.PrintMaterialsMapper;
import com.hbhb.cw.publicity.model.Print;
import com.hbhb.cw.publicity.model.PrintFile;
import com.hbhb.cw.publicity.model.PrintFlow;
import com.hbhb.cw.publicity.model.PrintMaterials;
import com.hbhb.cw.publicity.rpc.*;
import com.hbhb.cw.publicity.service.PrintFlowService;
import com.hbhb.cw.publicity.service.PrintNoticeService;
import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.web.vo.*;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
@SuppressWarnings(value = {"unchecked"})
public class PrintServiceImpl implements PrintService {


    @Resource
    private FileApiExp fileApi;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private FlowNodeApiExp nodeApi;
    @Resource
    private PrintMapper printMapper;
    @Resource
    private PrintFileMapper fileMapper;
    @Resource
    private FlowNodePropApiExp propApi;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private PrintFlowService flowService;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private PrintNoticeService noticeService;
    @Resource
    private PrintMaterialsMapper printMaterialsMapper;
    private StringBuffer id = new StringBuffer();


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
        // 获取印刷品详情
        Print print = printMapper.single(id);
        PrintInfoVO info = new PrintInfoVO();
        BeanUtils.copyProperties(print, info);
        // 获取印刷品文件列表信息
        List<PrintFile> files = fileMapper.createLambdaQuery().andEq(PrintFile::getPrintId, id).select();
        if (files.size() != 0) {
            List<Integer> fileIds = new ArrayList<>();
            files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
            List<SysFile> fileInfoList = fileApi.getFileInfoBatch(fileIds);
            Map<Long, SysFile> fileInfoMap = fileInfoList.stream()
                    .collect(Collectors.toMap(SysFile::getId, Function.identity()));
            List<PrintFileVO> fileVo = Optional.of(files)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(file -> PrintFileVO.builder()
                            .author(file.getCreateBy())
                            .createTime(DateUtil.dateToStringYmd(file.getCreateTime()))
                            .id(file.getId())
                            .fileName(fileInfoMap.get(file.getFileId()).getFileName())
                            .filePath(fileInfoMap.get(file.getFileId()).getFilePath())
                            .fileSize(fileInfoMap.get(file.getFileId()).getFileSize())
                            .build())
                    .collect(Collectors.toList());
            info.setFiles(fileVo);
        }
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPrint(PrintInfoVO infoVO, Integer userId) {
        Print print = new Print();
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unit = unitApi.getUnitInfo(user.getUnitId());

        BeanUtils.copyProperties(infoVO, print);
        // 印刷单号 = ”YS +单位简称+四位年份+ 自增序号“
        // 获取增长序号
        Integer count = printMapper.selectPrintNumCountByUnitId(new Date(), unit.getId());
        if (count == null) {
            count = 0;
        }
        String num = String.format("%0" + 4 + "d", (count + 1));
        print.setPrintNum("YS" + unit.getAbbr() + DateUtil.dateToStringY(new Date()) + num);
        // 印刷单名称 = 单位名称+临时申请+时间（yyyy/mm/dd）
        print.setPrintName(unit.getUnitName() + "临时申请" + DateUtil.dateToStringYmd(new Date()));
        print.setUnitId(user.getUnitId());
        print.setUserId(userId);
        print.setApplyTime(new Date());
        print.setCreateTime(new Date());
        print.setUpdateTime(new Date());
        print.setState(NodeState.NOT_APPROVED.value());
        print.setDeleteFlag(true);
        // 新增印刷用品
        printMapper.insert(print);
        if (infoVO.getFiles() != null) {
            List<PrintFile> fileList = setPrintFile(infoVO.getFiles(), userId, print.getId());
            fileMapper.insertBatch(fileList);
        }
        // 新增印刷用品导入业务单式或宣传单页数据
        if (!isEmpty(infoVO.getImportDateId())) {
            List<PrintMaterials> materialsList = getPrintMaterialsList(infoVO.getImportDateId());
            printMaterialsMapper.insertBatch(materialsList);
        }
    }

    @Override
    public void deletePrint(Long id) {
        Print print = new Print();
        print.setDeleteFlag(false);
        print.setId(id);
        printMapper.updateById(print);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrint(PrintInfoVO infoVO, Integer userId) {
        Print print = new Print();
        BeanUtils.copyProperties(infoVO, print);
        printMapper.updateTemplateById(print);
        // 保存附件
        List<PrintFileVO> fileVOList = infoVO.getFiles();
        List<PrintFileVO> files = new ArrayList<>();
        for (PrintFileVO fileVo : fileVOList) {
            if (isEmpty(fileVo.getId())) {
                files.add(fileVo);
            }
        }
        List<PrintFile> fileList = setPrintFile(files, userId, infoVO.getId());
        fileMapper.insertBatch(fileList);
    }

    private List<PrintFile> setPrintFile(List<PrintFileVO> fileVOList, Integer userId, Long printId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        List<PrintFile> fileList = new ArrayList<>();
        fileVOList.forEach(item -> fileList.add(PrintFile.builder()
                .createBy(user.getNickName())
                .createTime(new Date())
                .fileId(item.getFileId())
                .printId(printId)
                .build()));
        return fileList;
    }

    @Override
    public void savePrint(List<PrintImportVO> dataList, Map<Integer, String> importHeadMap, AtomicLong printId, AtomicInteger type) {
        //导入
        List<PrintMaterials> materialsList = new ArrayList<>();
        Map<String, Integer> unitNameMap = unitApi.getUnitMapByUnitName();
        for (PrintImportVO importVo : dataList) {
            PrintMaterials materials = new PrintMaterials();
            BeanUtils.copyProperties(importVo, materials);
            materials.setUnitId(unitNameMap.get(importVo.getUnitName()));
            materials.setDeliveryDate(DateUtil.string3DateYMD(importVo.getDeliveryDate()));
            materials.setType(type.get());
            materials.setPrintId(printId.get());
            materialsList.add(materials);
        }
        // 将读取到的数据存放入mongodb中
        String uuId = UUID.randomUUID().toString();
        PrintMaterialsImportDataVO data = new PrintMaterialsImportDataVO();
        data.setId(uuId);
        data.setMaterials(materialsList);
        mongoTemplate.insert(data);
        id = new StringBuffer(uuId);
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(PrintInitVO initVO) {
        Print print = printMapper.single(initVO.getPrintId());
        //  1.判断登录用户是否与申请人一致
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        UserInfo userInfo = userApi.getUserInfoById(print.getUserId());
        if (!user.getNickName().equals(userInfo.getNickName())) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  2.获取流程id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId());
        // 通过流程id得到流程节点属性
        List<FlowNodePropVO> flowProps = propApi.getNodeProps(flowId);
        //  3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, print.getUnitId(), initVO.getUserId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  4.同步节点属性
        syncPrintFlow(flowProps, print.getId(), initVO.getUserId());
        // 得到推送模板
        String inform = flowService.getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // 跟据流程id获取流程名称
        Flow flow = flowApi.getFlowById(flowId);
        inform = inform.replace(""
                , print.getPrintNum() + "_" + print.getPrintName() + "_" + flow.getFlowName());
        //  推送消息给发起人
        noticeService.addPrintNotice(
                PrintNoticeVO.builder()
                        .printId(print.getId())
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build()
        );

        //  6.更改发票流程状态
        print.setId(initVO.getPrintId());
        print.setState(NodeState.APPROVING.value());
        printMapper.updateById(print);

    }


    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer unitId, Integer userId) {
        // 获取用户所有角色
        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // 第一个节点属性
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // 判断是有默认用户
        // 如果设定了默认用户，且为当前登录用户，则有发起权限
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        }
        // 如果没有设定默认用户，则通过流程角色判断
        else {
            // 如果角色范围为全杭州
            if (UnitEnum.HANGZHOU.value().equals(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // 如果角色范围为本部
            else if (UnitEnum.BENBU.value().equals(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApi.getSubUnit(UnitEnum.BENBU.value());
                return unitIds.contains(unitId) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // 如果为确定某个单位
            else {
                // 必须单位和流程角色都匹配，才可判定为有发起权限
                return unitId.equals(firstNodeProp.getUnitId()) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    private void syncPrintFlow(List<FlowNodePropVO> flowProps, Long id, Integer userId) {
        // 用来存储同步节点的list
        List<PrintFlow> printFlowList = new ArrayList<>();
        // 判断节点是否有保存属性
        for (FlowNodePropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new PublicityException(PublicityErrorCode.LACK_OF_NODE_PROP);
            }
        }
        // 判断第一个节点是否有默认用户，如果没有则为当前用户
        if (flowProps.get(0).getUserId() == null) {
            flowProps.get(0).setUserId(userId);
        }
        // 所以需要先清空节点，再同步节点。
        flowService.deletePrintFlow(id);
        for (FlowNodePropVO flowPropVO : flowProps) {
            printFlowList.add(PrintFlow.builder()
                    .flowNodeId(flowPropVO.getFlowNodeId())
                    .printId(id)
                    .userId(flowPropVO.getUserId())
                    .flowRoleId(flowPropVO.getFlowRoleId())
                    .roleDesc(flowPropVO.getRoleDesc())
                    .controlAccess(flowPropVO.getControlAccess())
                    .isJoin(flowPropVO.getIsJoin())
                    .assigner(flowPropVO.getAssigner())
                    .operation(OperationState.UN_EXECUTED.value())
                    .build());
        }
        flowService.insertBatch(printFlowList);
    }

    private Long getRelatedFlow(Long flowTypeId) {
        // 流程节点数量 => 流程id
        Map<Long, Long> flowMap = new HashMap<>();
        List<Flow> flowList = flowApi.getFlowsByTypeId(flowTypeId);
        // 流程有效性校验（印刷用品流程存在1条）
        if (flowList.size() == 0) {
            throw new PublicityException(PublicityErrorCode.NOT_EXIST_FLOW);
        } else if (flowList.size() > 1) {
            throw new PublicityException(PublicityErrorCode.EXCEED_LIMIT_FLOW);
        }
        flowList.forEach(flow -> flowMap.put(nodeApi.getNodeNum(flow.getId()), flow.getId()));
        // 印刷品流程默认为4个节点流程
        Long flowId;
        flowId = flowMap.get(3L);
        if (flowId == null) {
            throw new PublicityException(PublicityErrorCode.LACK_OF_FLOW);
        }
        // 校验流程是否匹配，如果没有匹配的流程，则抛出提示
        return flowId;
    }

    @Override
    public void updateState(Long printId, Integer projectState) {
        printMapper.updateById(Print.builder()
                .id(printId)
                .state(projectState)
                .build());
    }

    @Override
    public List<PrintMaterials> getPrintMaterialsList(String uuId) {
        Query query = new Query(Criteria.where("id").is(uuId));
        PrintMaterialsImportDataVO data = mongoTemplate.findOne(query, PrintMaterialsImportDataVO.class);
        if (data != null) {
            return data.getMaterials();
        }
        return null;
    }

    @Override
    public String getImportDataId() {
        return String.valueOf(this.id);
    }
}
