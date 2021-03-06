package com.hbhb.cw.publicity.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeTemp;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.EnableCond;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.MaterialsBudgetMapper;
import com.hbhb.cw.publicity.mapper.MaterialsFileMapper;
import com.hbhb.cw.publicity.mapper.MaterialsInfoMapper;
import com.hbhb.cw.publicity.mapper.MaterialsMapper;
import com.hbhb.cw.publicity.model.Materials;
import com.hbhb.cw.publicity.model.MaterialsBudget;
import com.hbhb.cw.publicity.model.MaterialsFile;
import com.hbhb.cw.publicity.model.MaterialsFlow;
import com.hbhb.cw.publicity.model.MaterialsInfo;
import com.hbhb.cw.publicity.model.MaterialsNotice;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.FlowApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodeApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodePropApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.SysDictApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.MaterialsFlowService;
import com.hbhb.cw.publicity.service.MaterialsNoticeService;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.*;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.DictVO;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
public class MaterialsServiceImpl implements MaterialsService {
    @Resource
    private MaterialsMapper materialsMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private MaterialsFileMapper fileMapper;
    @Resource
    private FileApiExp fileApi;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private MaterialsInfoMapper materialsInfoMapper;
    @Resource
    private FlowNodeApiExp nodeApi;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private MaterialsNoticeService noticeService;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private MaterialsFlowService flowService;
    @Resource
    private FlowNodePropApiExp propApi;
    @Resource
    private SysDictApiExp dictApi;
    @Resource
    private MaterialsBudgetMapper budgetMapper;
    private StringBuffer id = new StringBuffer();


    @Override
    public PageResult<MaterialsResVO> getMaterialsList(MaterialsReqVO reqVO, Integer pageNum,
                                                       Integer pageSize) {
        // ????????????????????????
        List<Integer> unitIds = unitApi.getSubUnit(reqVO.getUnitId());
        PageRequest<MaterialsResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<MaterialsResVO> materialsList = materialsMapper.selectMaterialsListByCond(reqVO, unitIds, request);
        // ?????????????????????????????????
        List<Integer> userIds = new ArrayList<>();
        Map<Integer, String> unitMapById = unitApi.getUnitMapById();
        materialsList.getList().forEach(item -> userIds.add(item.getUserId()));
        Map<Integer, String> userMapById = userApi.getUserMapById(userIds);
        materialsList.getList().forEach(item -> {
            item.setNickName(userMapById.get(item.getUserId()));
            item.setUnitName(unitMapById.get(item.getUnitId()));

        });
        return materialsList;
    }

    @Override
    public MaterialsVO getMaterials(Long id) {
        Materials materials = materialsMapper.single(id);
        List<MaterialsFile> files = fileMapper
                .createLambdaQuery()
                .andEq(MaterialsFile::getMaterialsId, id)
                .select();
        MaterialsVO info = new MaterialsVO();
        BeanUtils.copyProperties(materials, info);
        info.setApplyTime(DateUtil.dateToString(materials.getApplyTime()));
        // ??????????????????
        UserInfo user = userApi.getUserInfoById(materials.getUserId());
        info.setNickName(user.getNickName());
        // ??????????????????
        Unit unit = unitApi.getUnitInfo(materials.getUnitId());
        info.setUnitName(unit.getUnitName());
        // ????????????????????????
        if (files.size() != 0) {
            List<Integer> fileIds = new ArrayList<>();
            files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
            List<SysFile> fileInfoList = fileApi.getFileInfoBatch(fileIds);
            Map<Long, SysFile> fileInfoMap = fileInfoList.stream()
                    .collect(Collectors.toMap(SysFile::getId, Function.identity()));
            List<MaterialsFileVO> fileVo = Optional.of(files)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(file -> MaterialsFileVO.builder()
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
        List<MaterialsInfo> materialsList = materialsInfoMapper.createLambdaQuery()
                .andEq(MaterialsInfo::getMaterialsId, id)
                .select();
        info.setMaterialsInfo(materialsList);
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaterials(MaterialsVO infoVO, Integer userId) {
        // ????????????????????????????????????????????????????????????
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unit = unitApi.getUnitInfo(user.getUnitId());
        MaterialsBudgetVO materialsBudget = getMaterialsBudget(user.getUnitId());
        int i;
        if (!isEmpty(materialsBudget)) {
            MaterialsBudget single = budgetMapper.createLambdaQuery()
                    .andEq(MaterialsBudget::getUnitId, user.getUnitId()).single();
            if (materialsBudget.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new PublicityException(PublicityErrorCode.BUDGET_INSUFFICIENT);
            }
            i = single.getBudget().compareTo(infoVO.getPredictAmount());
        } else {
            i = materialsBudget.getBalance().compareTo(infoVO.getPredictAmount());
        }

        if (i < 0) {
            throw new PublicityException(PublicityErrorCode.BUDGET_INSUFFICIENT);
        }
        Materials materials = new Materials();
        BeanUtils.copyProperties(infoVO, materials);
        // ???????????? = ???WL +????????????+????????????+ ???????????????
        // ??????????????????
        Integer count = materialsMapper.selectPictureNumCountByUnitId(new Date(), unit.getId());
        String num = String.format("%0" + 4 + "d", (count + 1));
        materials.setMaterialsNum("WL" + unit.getAbbr() + DateUtil.dateToStringY(new Date()) + num);
        // ??????????????? = ????????????+????????????+?????????yyyy/mm/dd???
        materials.setMaterialsName((unit.getUnitName() + "????????????" + DateUtil.dateToStringYmd(new Date())));
        materials.setUnitId(user.getUnitId());
        materials.setUserId(userId);
        materials.setApplyTime(new Date());
        materials.setCreateTime(new Date());
        materials.setUpdateTime(new Date());
        materials.setState(NodeState.NOT_APPROVED.value());
        materials.setDeleteFlag(true);
        // ??????????????????
        materialsMapper.insert(materials);
        if (infoVO.getFiles() != null) {
            List<MaterialsFile> fileList = setMaterialsFile(infoVO.getFiles(), userId, materials.getId());
            fileMapper.insertBatch(fileList);
        }
        // ?????????????????????????????????????????????????????????
        if (!isEmpty(infoVO.getImportDateId())) {
            List<MaterialsInfoVO> materialsList = getMaterialsInfoList(infoVO.getImportDateId());
            List<MaterialsInfo> materialsInfos = BeanConverter.copyBeanList(materialsList, MaterialsInfo.class);
            materialsInfos.forEach(item -> item.setMaterialsId(materials.getId()));
            materialsInfoMapper.insertBatch(materialsInfos);
        }
    }

    @Override
    public void deleteMaterials(Long id, Integer userId) {
        Materials single = materialsMapper.single(id);
        if (!userId.equals(single.getUserId())) {
            throw new PublicityException(PublicityErrorCode.NO_OPERATION_PERMISSION);
        }
        Materials materials = new Materials();
        materials.setId(id);
        materials.setDeleteFlag(false);
        materialsMapper.updateTemplateById(materials);
    }

    @Override
    public void updateMaterials(MaterialsVO infoVO, Integer userId) {
        Materials single = materialsMapper.single(infoVO.getId());
        if (!userId.equals(single.getUserId())) {
            throw new PublicityException(PublicityErrorCode.NO_OPERATION_PERMISSION);
        }
        Materials materials = new Materials();
        BeanUtils.copyProperties(infoVO, materials);
        // ??????????????????
        materialsMapper.updateTemplateById(materials);
        // ????????????
        List<MaterialsFileVO> fileVOList = infoVO.getFiles();
        List<MaterialsFileVO> files = new ArrayList<>();
        for (MaterialsFileVO fileVo : fileVOList) {
            if (isEmpty(fileVo.getId())) {
                files.add(fileVo);
            }
        }
        List<MaterialsFile> fileList = setMaterialsFile(files, userId, infoVO.getId());
        fileMapper.insertBatch(fileList);
        //
        if (!isEmpty(infoVO.getImportDateId())) {
            List<MaterialsInfoVO> materialsList = getMaterialsInfoList(infoVO.getImportDateId());
            List<MaterialsInfo> materialsInfos = BeanConverter.copyBeanList(materialsList, MaterialsInfo.class);
            materialsInfos.forEach(item -> item.setMaterialsId(materials.getId()));
            materialsInfoMapper.insertBatch(materialsInfos);
        }
    }

    @Override
    public void saveMaterials(List<MaterialsImportVO> dataList, List<String> headerList) {
        // ???????????????????????????
        if (headerList.size() != 11) {
            throw new PublicityException(PublicityErrorCode.IMPORT_DATE_TEMPLATE_ERROR);
        }
        //??????
        List<MaterialsInfoVO> materialsList = new ArrayList<>();
        Map<String, Integer> unitNameMap = unitApi.getUnitMapByUnitName();
        for (MaterialsImportVO importVo : dataList) {
            MaterialsInfoVO materials = new MaterialsInfoVO();
            BeanUtils.copyProperties(importVo, materials);
            materials.setUnitId(unitNameMap.get(importVo.getUnitName()));
            materials.setDeliveryDate(DateUtil.string3DateYMD(importVo.getDeliveryDate()));
            materialsList.add(materials);
        }
        if (isEmpty(materialsList)) {
            throw new PublicityException(PublicityErrorCode.PLEASE_ADD_SECONDARY_DIRECTORY);
        }
        // ??????????????????????????????mongodb???
        String uuId = UUID.randomUUID().toString();
        MaterialsInfoImportDataVO data = new MaterialsInfoImportDataVO();
        data.setId(uuId);
        data.setMaterialsInfo(materialsList);
        mongoTemplate.insert(data);
        id = new StringBuffer(uuId);
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(MaterialsInitVO initVO, Integer userId) {
        Materials materials = materialsMapper.single(initVO.getMaterialsId());
        //  1.??????????????????????????????????????????
        UserInfo user = userApi.getUserInfoById(userId);
        UserInfo userInfo = userApi.getUserInfoById(materials.getUserId());
        if (!user.getNickName().equals(userInfo.getNickName())) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  3.????????????id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId());
        // ????????????id????????????????????????
        List<FlowNodePropVO> flowProps = propApi.getNodeProps(flowId);
        //  4.??????????????????????????????
        boolean hasAccess = hasAccess2Approve(flowProps, materials.getUnitId(), initVO.getUserId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  5.??????????????????
        syncMaterialsFlow(flowProps, materials.getId(), userId);
        // ??????????????????
        String inform = flowService.getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // ????????????id??????????????????
        Flow flow = flowApi.getFlowById(flowId);
        inform = inform.replace(FlowNodeNoticeTemp.TITLE.value()
                , materials.getMaterialsName() + "_" + "_" + flow.getFlowName());
        noticeService.addMaterialsNotice(
                MaterialsNotice.builder()
                        .materialsId(materials.getId())
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build());

        //  6.????????????????????????
        materials.setId(initVO.getMaterialsId());
        materials.setState(NodeState.APPROVING.value());
        materials.setUpdateBy(initVO.getUserId());
        materialsMapper.updateTemplateById(materials);
    }

    @Override
    public void updateState(Long materialsId, Integer projectState) {
        materialsMapper.updateById(Materials.builder()
                .id(materialsId)
                .state(projectState)
                .build());
    }

    @Override
    public void updateBudget(List<MaterialsBudget> budget) {
        budgetMapper.updateBatchTempById(budget);
    }

    @Override
    public List<MaterialsBudgetResVO> getMaterialsBudgetList() {
        List<MaterialsBudgetResVO> list = budgetMapper.selectBudgetList();
        Map<Integer, String> userMap = unitApi.getUnitMapById();
        list.forEach(item -> item.setUnitName(userMap.get(item.getUnitId())));
        return list;
    }

    @Override
    public MaterialsBudgetVO getMaterialsBudget(Integer unitId) {
        MaterialsBudgetVO materialsBudgetVO = materialsMapper.selectMaterialsBudgetByUnitId(unitId);
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        if (!isEmpty(materialsBudgetVO)) {
            materialsBudgetVO.setUnitName(unitMap.get(materialsBudgetVO.getUnitId()));
        }
        return materialsBudgetVO;
    }

    @Override
    public List<MaterialsInfoVO> getMaterialsInfoList(String uuId) {
        Query query = new Query(Criteria.where("id").is(uuId));
        MaterialsInfoImportDataVO data = mongoTemplate.findOne(query, MaterialsInfoImportDataVO.class);
        if (data != null) {
            return data.getMaterialsInfo();
        }
        return null;
    }

    @Override
    public String getImportDataId() {
        return String.valueOf(this.id);
    }

    @Override
    public void deleteMaterialsInfo(Long materialsId) {
        materialsInfoMapper.createLambdaQuery().
                andEq(MaterialsInfo::getMaterialsId, materialsId)
                .delete();
    }

    @Override
    public void judgeFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue()
                .equals(name))) {
            throw new PublicityException(PublicityErrorCode.IMPORT_DATA_NULL_ERROR);
        }
    }

    @Override
    public List<MaterialsExportVO> export(MaterialsReqVO reqVO) {
        PageResult<MaterialsResVO> page = this.getMaterialsList(reqVO, 1, Integer.MAX_VALUE);
        return Optional.ofNullable(page.getList())
                .orElse(new ArrayList<>())
                .stream()
                .map(vo -> BeanConverter.convert(vo, MaterialsExportVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void saveMaterialsBudget() {
        List<Integer> allUnitId = unitApi.getAllUnitId();
        List<MaterialsBudget> materialsBudgets = new ArrayList<>();
        for (Integer item : allUnitId) {
            MaterialsBudget build = MaterialsBudget.builder()
                    .budget(BigDecimal.ZERO)
                    .unitId(item)
                    .budgetYear(DateUtil.dateToStringY(new Date()))
                    .build();
            materialsBudgets.add(build);
        }
        budgetMapper.insertBatch(materialsBudgets);
    }

    private List<MaterialsFile> setMaterialsFile(List<MaterialsFileVO> fileVOList, Integer userId,
                                                 Long materialsId) {
        //??????????????????
        UserInfo user = userApi.getUserInfoById(userId);
        List<MaterialsFile> fileList = new ArrayList<>();
        if (fileVOList != null && !fileVOList.isEmpty()) {
            fileVOList.forEach(item -> fileList.add(MaterialsFile.builder()
                    .createBy(user.getNickName())
                    .createTime(new Date())
                    .fileId(item.getFileId())
                    .materialsId(materialsId)
                    .build()));
        }
        return fileList;
    }

    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer unitId, Integer userId) {
        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // ?????????????????????
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // ????????????????????????
        // ???????????????????????????????????????????????????????????????????????????
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        }
        // ????????????????????????????????????????????????????????????
        else {
            // ??????????????????????????????
            if (UnitEnum.HANGZHOU.value().equals(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // ???????????????????????????
            else if (UnitEnum.BENBU.value().equals(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApi.getSubUnit(UnitEnum.BENBU.value());
                return unitIds.contains(unitId) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // ???????????????????????????
            else {
                // ?????????????????????????????????????????????????????????????????????
                return unitId.equals(firstNodeProp.getUnitId()) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    private void syncMaterialsFlow(List<FlowNodePropVO> flowProps, Long id, Integer userId) {

        // ???????????????????????????list
        List<MaterialsFlow> materialsFlowList = new ArrayList<>();
        // ?????????????????????????????????
        for (FlowNodePropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new PublicityException(PublicityErrorCode.LACK_OF_NODE_PROP);
            }
        }
        // ???????????????????????????????????????????????????????????????????????????
        if (flowProps.get(0).getUserId() == null) {
            flowProps.get(0).setUserId(userId);
        }
        // ????????????????????????????????????????????????
        flowService.deleteMaterialsFlow(id);
        // ??????????????????????????????????????????????????????
        Materials materials = materialsMapper.single(id);
        BigDecimal amount = materials.getPredictAmount();
        // ?????????????????????4?????????????????????????????????????????????????????? ????????????????????????????????????????????????????????????
        int num = 0;
        FlowNodePropVO oneNode = flowProps.get(1);
        FlowNodePropVO twoNode = flowProps.get(2);
        FlowNodePropVO threeNode = flowProps.get(3);
        if (isEnableCond(amount, oneNode)) {
            num = 1;
        }
        if (isEnableCond(amount, twoNode)) {
            num = 2;
        }
        if (isEnableCond(amount, threeNode)) {
            num = 3;
        }

        for (int i = 0; i <= num; i++) {
            FlowNodePropVO flowPropVO = flowProps.get(i);
            materialsFlowList.add(MaterialsFlow.builder()
                    .flowNodeId(flowPropVO.getFlowNodeId())
                    .materialsId(id)
                    .userId(flowPropVO.getUserId())
                    .flowRoleId(flowPropVO.getFlowRoleId())
                    .roleDesc(flowPropVO.getRoleDesc())
                    .controlAccess(flowPropVO.getControlAccess())
                    .isJoin(flowPropVO.getIsJoin())
                    .assigner(flowPropVO.getAssigner())
                    .operation(OperationState.UN_EXECUTED.value())
                    .createTime(new Date())
                    .build());
        }
        flowService.insertBatch(materialsFlowList);
    }

    private Long getRelatedFlow(Long flowTypeId) {
        // ?????????????????? => ??????id
        Map<Long, Long> flowMap = new HashMap<>(5);
        List<Flow> flowList = flowApi.getFlowsByTypeId(flowTypeId);
        // ????????????????????????????????????????????????1??????
        if (flowList.size() == 0) {
            throw new PublicityException(PublicityErrorCode.NOT_EXIST_FLOW);
        } else if (flowList.size() > 1) {
            throw new PublicityException(PublicityErrorCode.EXCEED_LIMIT_FLOW);
        }
        flowList.forEach(flow -> flowMap.put(nodeApi.getNodeNum(flow.getId()), flow.getId()));
        // ???????????????????????????4???????????????
        Long flowId;
        flowId = flowMap.get(4L);
        if (flowId == null) {
            throw new PublicityException(PublicityErrorCode.LACK_OF_FLOW);
        }
        // ????????????????????????????????????????????????????????????????????????
        return flowId;

    }

    private Map<String, String> enableCondMap() {
        List<DictVO> dict = dictApi.getDict(TypeCode.FLOW.value(), DictCode.FLOW_NODE_PROP_ENABLE_COND.value());
        return dict.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
    }

    private boolean isEnableCond(BigDecimal amount, FlowNodePropVO propVO) {
        if (!isEmpty(propVO.getEnableCond())) {
            Map<String, String> enableMap = enableCondMap();
            String enable = enableMap.get(propVO.getEnableCond().toString());
            boolean flag = false;
            if (EnableCond.GREATER.value().equals(enable)) {
                if (amount.compareTo(propVO.getAmount()) > 0) {
                    flag = true;
                }
            }
            if (EnableCond.EQUAL_GREATER.value().equals(enable)) {
                if (amount.compareTo(propVO.getAmount()) >= 0) {
                    flag = true;
                }
            }
            if (EnableCond.LESS.value().equals(enable)) {
                if (amount.compareTo(propVO.getAmount()) < 0) {
                    flag = true;
                }
            }
            if (EnableCond.EQUAL_LESS.value().equals(enable)) {
                if (amount.compareTo(propVO.getAmount()) <= 0) {
                    flag = true;
                }
            }
            if (EnableCond.EQUAL.value().equals(enable)) {
                if (amount.compareTo(propVO.getAmount()) == 0) {
                    flag = true;
                }
            }
            return flag;
        } else {
            return true;
        }
    }


}
