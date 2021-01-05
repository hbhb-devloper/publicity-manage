package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeTemp;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.PictureFileMapper;
import com.hbhb.cw.publicity.mapper.PictureMapper;
import com.hbhb.cw.publicity.model.Picture;
import com.hbhb.cw.publicity.model.PictureFile;
import com.hbhb.cw.publicity.model.PictureFlow;
import com.hbhb.cw.publicity.model.PictureNotice;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.FlowApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodeApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodePropApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.PictureFlowService;
import com.hbhb.cw.publicity.service.PictureNoticeService;
import com.hbhb.cw.publicity.service.PictureService;
import com.hbhb.cw.publicity.web.vo.PictureFileVO;
import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureInitVO;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
@SuppressWarnings(value = {"unchecked"})
public class PictureServiceImpl implements PictureService {
    @Resource
    private PictureMapper pictureMapper;
    @Resource
    private PictureFileMapper fileMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private FileApiExp fileApi;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private FlowNodeApiExp nodeApi;
    @Resource
    private PictureNoticeService noticeService;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private PictureFlowService flowService;
    @Resource
    private FlowNodePropApiExp propApi;


    @Override
    public PageResult<PictureResVO> getPictureList(PictureReqVO reqVO, Integer pageNum, Integer pageSize) {
        // 获取所有下属单位
        List<Integer> unitIds = unitApi.getSubUnit(reqVO.getUnitId());
        PageRequest<PictureResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<PictureResVO> list = pictureMapper.selectPictureListByCond(reqVO, unitIds, request);
        List<Integer> userIds = new ArrayList<>();
        Map<Integer, String> unitMapById = unitApi.getUnitMapById();
        list.getList().forEach(item -> userIds.add(item.getUserId()));
        Map<Integer, String> userMap = userApi.getUserMapById(userIds);
        list.getList().forEach(item -> {
            item.setUserName(userMap.get(item.getUserId()));
            item.setUnitName(unitMapById.get(item.getUnitId()));
        });
        return list;

    }

    @Override
    public PictureInfoVO getPicture(Long id) {
        Picture picture = pictureMapper.single(id);
        PictureInfoVO info = new PictureInfoVO();
        BeanUtils.copyProperties(picture, info);
        // 转换用户信息
        UserInfo user = userApi.getUserInfoById(picture.getUserId());
        info.setNickName(user.getNickName());
        // 转换单位信息
        Unit unit = unitApi.getUnitInfo(picture.getUnitId());
        info.setUnitName(unit.getUnitName());
        // 获取文件列表信息
        List<PictureFile> files = fileMapper.createLambdaQuery().andEq(PictureFile::getPictureId, id).select();
        if (files.size() != 0) {
            List<Integer> fileIds = new ArrayList<>();
            files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
            List<SysFile> fileInfoList = fileApi.getFileInfoBatch(fileIds);
            Map<Long, SysFile> fileInfoMap = fileInfoList.stream()
                    .collect(Collectors.toMap(SysFile::getId, Function.identity()));
            List<PictureFileVO> fileVo = Optional.of(files)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(file -> PictureFileVO.builder()
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
        // 获取画面设计文件信息
        if (picture.getFileId() != null) {
            SysFile file = fileApi.getFileInfo(picture.getFileId());
            info.setFilePath(file.getFilePath());
            info.setFileName(file.getFileName());
        }
        return info;
    }

    @Override
    public void addPicture(PictureInfoVO infoVO, Integer userId) {
        //获取用户姓名
        Picture picture = new Picture();
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unit = unitApi.getUnitInfo(user.getUnitId());
        BeanUtils.copyProperties(infoVO, picture);
        // 印刷单号 = ”HM +单位简称+四位年份+ 自增序号“
        // 获取增长序号
        Integer count = pictureMapper.selectPictureNumCountByUnitId(new Date(), infoVO.getUnitId());
        String num = String.format("%0" + 4 + "d", (count + 1));
        picture.setPictureNum("HM" + unit.getAbbr() + DateUtil.dateToStringY(new Date()) + num);
        // 印刷单名称 = 单位名称+临时申请+时间（yyyy/mm/dd）
        picture.setPictureName(unit.getUnitName() + "画面申请" + DateUtil.dateToStringYmd(new Date()));
        picture.setUnitId(user.getUnitId());
        picture.setUserId(userId);
        picture.setApplyTime(new Date());
        picture.setCreateTime(new Date());
        picture.setUpdateTime(new Date());
        picture.setState(NodeState.NOT_APPROVED.value());
        picture.setDeleteFlag(true);
        pictureMapper.insert(picture);
        // 保存附件
        if (!isEmpty(infoVO.getFiles())) {
            List<PictureFile> fileList = setPictureFile(infoVO.getFiles(), userId, picture.getId());
            fileMapper.insertBatch(fileList);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePicture(PictureInfoVO infoVO, Integer userId) {
        Picture single = pictureMapper.single(infoVO.getId());
        if (userId.equals(single.getUserId())) {
            throw new PublicityException(PublicityErrorCode.NO_OPERATION_PERMISSION);
        }
        // 修改宣传画面
        Picture picture = new Picture();
        BeanUtils.copyProperties(infoVO, picture);
        pictureMapper.updateTemplateById(picture);
        // 修改保存附件
        if (infoVO.getFiles().size() != 0) {
            List<PictureFileVO> fileVOList = infoVO.getFiles();
            List<PictureFileVO> files = new ArrayList<>();
            for (PictureFileVO fileVo : fileVOList) {
                if (isEmpty(fileVo.getId())) {
                    files.add(fileVo);
                }
            }
            List<PictureFile> fileList = setPictureFile(files, userId, infoVO.getId());
            fileMapper.insertBatch(fileList);
        }
    }

    @Override
    public void deletePicture(Long id, Integer userId) {
        Picture single = pictureMapper.single(id);
        if (!userId.equals(single.getUserId())) {
            throw new PublicityException(PublicityErrorCode.NO_OPERATION_PERMISSION);
        }
        Picture picture = new Picture();
        picture.setId(id);
        picture.setDeleteFlag(false);
        pictureMapper.deleteById(id);
    }


    private List<PictureFile> setPictureFile(List<PictureFileVO> fileVOList, Integer userId, Long pictureId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        List<PictureFile> fileList = new ArrayList<>();
        fileVOList.forEach(item -> fileList.add(PictureFile.builder()
                .createBy(user.getNickName())
                .createTime(new Date())
                .fileId(item.getFileId())
                .pictureId(pictureId)
                .build()));
        return fileList;
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(PictureInitVO initVO, Integer userId) {
        Picture picture = pictureMapper.single(initVO.getPictureId());
        //  1.判断登录用户是否与申请人一致
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        UserInfo userInfo = userApi.getUserInfoById(picture.getUserId());
        if (!user.getNickName().equals(userInfo.getNickName())) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  2.获取流程id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId());
        // 通过流程id得到流程节点属性
        List<FlowNodePropVO> flowProps = propApi.getNodeProps(flowId);
        //  3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, picture.getUnitId(), initVO.getUserId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  4.同步节点属性
        syncPrintFlow(flowProps, picture.getId(), userId);
        // 得到推送模板
        String inform = flowService.getInform(flowProps.get(0).getFlowNodeId(),
                FlowNodeNoticeState.DEFAULT_REMINDER.value());

        // 跟据流程id获取流程名称
        Flow flow = flowApi.getFlowById(flowId);
        inform = inform.replace(FlowNodeNoticeTemp.TITLE.value()
                , picture.getPictureName() + picture.getPictureNum() + "_" + flow.getFlowName());
        // 推送提醒给发起人
        noticeService.addPictureNotice(
                PictureNotice.builder()
                        .pictureId(picture.getId())
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build());
        //  6.更改印刷品流程状态
        picture.setId(initVO.getPictureId());
        picture.setState(NodeState.APPROVING.value());
        picture.setUpdateBy(initVO.getUserId());
        pictureMapper.updateTemplateById(picture);
    }


    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer unitId, Integer userId) {

        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // 第一个节点属性
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // 判断是有默认用户
        // 如果设定了默认用户，且为当前登录用户，则有发起权限
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        } else {
            // 如果没有设定默认用户，则通过流程角色判断
            // 如果角色范围为全杭州
            if (UnitEnum.HANGZHOU.value().equals(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
                // 如果角色范围为本部
            } else if (UnitEnum.BENBU.value().equals(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApi.getSubUnit(UnitEnum.BENBU.value());
                return unitIds.contains(unitId) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
                // 如果为确定某个单位
            } else {
                // 必须单位和流程角色都匹配，才可判定为有发起权限
                return unitId.equals(firstNodeProp.getUnitId()) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    private void syncPrintFlow(List<FlowNodePropVO> flowProps, Long id, Integer userId) {

        // 用来存储同步节点的list
        List<PictureFlow> pictureFlowList = new ArrayList<>();
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
            pictureFlowList.add(PictureFlow.builder()
                    .flowNodeId(flowPropVO.getFlowNodeId())
                    .pictureId(id)
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
        flowService.insertBatch(pictureFlowList);
    }

    private Long getRelatedFlow(Long flowTypeId) {
        // 流程节点数量 => 流程id
        Map<Long, Long> flowMap = new HashMap<>(5);
        List<Flow> flowList = flowApi.getFlowsByTypeId(flowTypeId);
        // 流程有效性校验（发票预开流程存在两条）
        if (flowList.size() == 0) {
            throw new PublicityException(PublicityErrorCode.NOT_EXIST_FLOW);
        } else if (flowList.size() > 1) {
            throw new PublicityException(PublicityErrorCode.EXCEED_LIMIT_FLOW);
        }
        flowList.forEach(flow -> flowMap.put(nodeApi.getNodeNum(flow.getId()), flow.getId()));
        // 画面设计
        // 画面设计流程默认为4个节点流程
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
        pictureMapper.updateById(Picture.builder()
                .id(printId)
                .state(projectState)
                .build());
    }

}
