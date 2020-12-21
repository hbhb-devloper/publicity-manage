package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.*;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.MaterialsBudgetMapper;
import com.hbhb.cw.publicity.mapper.MaterialsFileMapper;
import com.hbhb.cw.publicity.mapper.MaterialsInfoMapper;
import com.hbhb.cw.publicity.mapper.MaterialsMapper;
import com.hbhb.cw.publicity.model.*;
import com.hbhb.cw.publicity.rpc.*;
import com.hbhb.cw.publicity.service.MaterialsFlowService;
import com.hbhb.cw.publicity.service.MaterialsNoticeService;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.*;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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

    @Override
    public PageResult<MaterialsResVO> getMaterialsList(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<MaterialsResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        Map<Integer, String> unitMapById = unitApi.getUnitMapById();
        PageResult<MaterialsResVO> materialsList = materialsMapper.selectMaterialsListByCond(request, request);
        List<Integer> userIds = new ArrayList<>();
        materialsList.getList().forEach(item -> userIds.add(item.getUserId()));
        Map<Integer, String> userMapById = userApi.getUserMapById(userIds);
        materialsList.getList().forEach(item -> {
            item.setNickName(userMapById.get(item.getUserId()));
            item.setUnitName(unitMapById.get(item.getUnitId()));

        });
        return materialsList;
    }

    @Override
    public MaterialsInfoVO getMaterials(Long id) {
        Materials materials = materialsMapper.single(id);
        List<MaterialsFile> files = fileMapper.createLambdaQuery().andEq(MaterialsFile::getMaterialsId, id).select();
        // 获取文件列表信息
        List<Integer> fileIds = new ArrayList<>();
        files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
        List<SysFile> fileInfoList = fileApi.getFileInfoBatch(fileIds);
        List<MaterialsFileVO> fileVo = Optional.of(files)
                .orElse(new ArrayList<>())
                .stream()
                .map(file -> MaterialsFileVO.builder()
                        .author(file.getCreateBy())
                        .createTime(file.getCreateTime().toString())
                        .id(file.getId())
                        .build())
                .collect(Collectors.toList());
        BeanConverter.copyBeanList(fileVo, fileInfoList.getClass());
        MaterialsInfoVO info = new MaterialsInfoVO();
        BeanConverter.convert(info, materials.getClass());
        info.setFiles(fileVo);
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaterials(MaterialsInfoVO infoVO, Integer userId) {
        // 判断该用户所属单位下物料制作费用是否充足
        UserInfo user = userApi.getUserInfoById(userId);
        MaterialsBudgetVO materialsBudget = getMaterialsBudget(user.getUnitId());
        int i = materialsBudget.getBalance().compareTo(infoVO.getPredictAmount());
        if (i < 0) {
            throw new PublicityException(PublicityErrorCode.BUDGET_INSUFFICIENT);
        }
        Materials materials = new Materials();
        BeanUtils.copyProperties(infoVO, materials);
        materials.setApplyTime(DateUtil.string2DateYMD(infoVO.getApplyTime()));
        materials.setCreateTime(new Date());
        materials.setUpdateTime(new Date());
        materials.setState(NodeState.NOT_APPROVED.value());
        // 新增印刷用品
        materialsMapper.insert(materials);
        if (infoVO.getFiles() != null) {
            List<MaterialsFile> fileList = setMaterialsFile(infoVO.getFiles(), userId);
            fileMapper.insertBatch(fileList);
        }
    }

    @Override
    public void deleteMaterials(Long id) {
        materialsMapper.deleteById(id);
    }

    @Override
    public void updateMaterials(MaterialsInfoVO infoVO, Integer userId) {
        Materials materials = new Materials();
        BeanUtils.copyProperties(materials, infoVO);
        // 新增印刷用品
        materialsMapper.insert(materials);
        // 保存附件
        List<MaterialsFileVO> fileVOList = infoVO.getFiles();
        List<MaterialsFileVO> files = new ArrayList<>();
        for (MaterialsFileVO fileVo : fileVOList) {
            if (isEmpty(fileVo.getId())) {
                files.add(fileVo);
            }
        }
        List<MaterialsFile> fileList = setMaterialsFile(files, userId);
        fileMapper.insertBatch(fileList);
    }

    @Override
    public void saveMaterials(List<MaterialsImportVO> dataList, Map<Integer, String> importHeadMap, AtomicLong materialsId) {
        //导入
        List<MaterialsInfo> materialsList = new ArrayList<>();
        Map<String, Integer> unitNameMap = unitApi.getUnitMapByUnitName();
        for (MaterialsImportVO importVo : dataList) {
            MaterialsInfo materials = new MaterialsInfo();
            BeanUtils.copyProperties(materials, importVo);
            materials.setUnitId(unitNameMap.get(importVo.getUnitName()));
            materialsList.add(materials);
            materials.setMaterialsId(materialsId.get());
        }
        materialsInfoMapper.insertBatch(materialsList);
    }

    private List<MaterialsFile> setMaterialsFile(List<MaterialsFileVO> fileVOList, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        List<MaterialsFile> fileList = new ArrayList<>();
        if (fileVOList != null && !fileVOList.isEmpty()) {
            fileVOList.forEach(item -> fileList.add(MaterialsFile.builder()
                    .createBy(user.getNickName())
                    .createTime(new Date())
                    .fileId(item.getFileId())
                    .materialsId(item.getMaterialsId())
                    .build()));
        }
        return fileList;
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(MaterialsInitVO initVO) {
        Materials materials = materialsMapper.single(initVO.getMaterialsId());
        //  1.判断登录用户是否与申请人一致
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        UserInfo userInfo = userApi.getUserInfoById(materials.getUserId());
        if (!user.getNickName().equals(userInfo.getNickName())) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }


        //  3.获取流程id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId());
        // 通过流程id得到流程节点属性
        List<FlowNodePropVO> flowProps = propApi.getNodeProps(flowId);
        //  4.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, materials.getUnitId(), initVO.getUserId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  5.同步节点属性
        syncMaterialsFlow(flowProps, materials.getId(), initVO.getUserId());
        // 得到推送模板
        String inform = flowService.getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // 跟据流程id获取流程名称
        Flow flow = flowApi.getFlowById(flowId);
        // todo 修改推送模板
        inform = inform.replace(""
                , materials.getMaterialsName() + "_" + "_" + flow.getFlowName());
        noticeService.addMaterialsNotice(
                MaterialsNotice.builder()
                        .materialsId(materials.getId())
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build());

        //  6.更改发票流程状态
        materials.setId(initVO.getMaterialsId());
        materials.setState(NodeState.APPROVING.value());
        materials.setUpdateBy(initVO.getUserId());

    }


    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer unitId, Integer userId) {
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

    private void syncMaterialsFlow(List<FlowNodePropVO> flowProps, Long id, Integer userId) {

        // 用来存储同步节点的list
        List<MaterialsFlow> materialsFlowList = new ArrayList<>();
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
        flowService.deleteMaterialsFlow(id);
        // 判断物料设计预估金额是否大于预设阀值
        Materials materials = materialsMapper.single(id);
        BigDecimal amount = materials.getPredictAmount();
        // 物料制作流程共4个节点，若预估金额对比第二、第三节点 点设置金额阀值条件为真则返回需审批节点数
        int num = 1;
        FlowNodePropVO twoNode = flowProps.get(1);
        boolean twoEnableCond = isEnableCond(amount, twoNode);
        FlowNodePropVO threeNode = flowProps.get(2);
        boolean threeEnableCond = isEnableCond(amount, threeNode);
        if (threeEnableCond) {
            num = 3;
        } else if (twoEnableCond) {
            num = 2;
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
                    .build());
        }
        flowService.insertBatch(materialsFlowList);
    }

    private Long getRelatedFlow(Long flowTypeId) {
        // 流程节点数量 => 流程id
        Map<Long, Long> flowMap = new HashMap<>(5);
        List<Flow> flowList = flowApi.getFlowsByTypeId(flowTypeId);
        // 流程有效性校验（物料制作流程存在1条）
        if (flowList.size() == 0) {
            throw new PublicityException(PublicityErrorCode.NOT_EXIST_FLOW);
        } else if (flowList.size() > 1) {
            throw new PublicityException(PublicityErrorCode.EXCEED_LIMIT_FLOW);
        }
        flowList.forEach(flow -> flowMap.put(nodeApi.getNodeNum(flow.getId()), flow.getId()));
        // 物料制作流程默认为4个节点流程
        Long flowId;
        flowId = flowMap.get(4L);
        if (flowId == null) {
            throw new PublicityException(PublicityErrorCode.LACK_OF_FLOW);
        }
        // 校验流程是否匹配，如果没有匹配的流程，则抛出提示
        return flowId;

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
        return budgetMapper.selectBudgetList();
    }

    @Override
    public MaterialsBudgetVO getMaterialsBudget(Integer unitId) {
        return materialsMapper.selectMaterialsBudgetByUnitId(unitId);
    }


    private Map<String, String> enableCondMap() {
        List<DictVO> dict = dictApi.getDict(TypeCode.FLOW.value(), DictCode.FLOW_NODE_PROP_ENABLE_COND.value());
        return dict.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
    }


    private boolean isEnableCond(BigDecimal amount, FlowNodePropVO propVO) {
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
    }


}
