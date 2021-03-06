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
        // ????????????????????????
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
        info.setApplyTime(DateUtil.dateToString(picture.getApplyTime()));
        // ??????????????????
        UserInfo user = userApi.getUserInfoById(picture.getUserId());
        info.setNickName(user.getNickName());
        // ??????????????????
        Unit unit = unitApi.getUnitInfo(picture.getUnitId());
        info.setUnitName(unit.getUnitName());
        // ????????????????????????
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
        // ??????????????????????????????
        if (picture.getFileId() != null) {
            SysFile file = fileApi.getFileInfo(picture.getFileId());
            info.setFilePath(file.getFilePath());
            info.setFileName(file.getFileName());
        }
        return info;
    }

    @Override
    public void addPicture(PictureInfoVO infoVO, Integer userId) {
        //??????????????????
        Picture picture = new Picture();
        UserInfo user = userApi.getUserInfoById(userId);
        Unit unit = unitApi.getUnitInfo(user.getUnitId());
        BeanUtils.copyProperties(infoVO, picture);
        // ???????????? = ???HM +????????????+????????????+ ???????????????
        // ??????????????????
        Integer count = pictureMapper.selectPictureNumCountByUnitId(new Date(), unit.getId());
        String num = String.format("%0" + 4 + "d", (count + 1));
        picture.setPictureNum("HM" + unit.getAbbr() + DateUtil.dateToStringY(new Date()) + num);
        // ??????????????? = ????????????+????????????+?????????yyyy/mm/dd???
        picture.setPictureName(unit.getUnitName() + "????????????" + DateUtil.dateToStringYmd(new Date()));
        picture.setUnitId(user.getUnitId());
        picture.setUserId(userId);
        picture.setApplyTime(new Date());
        picture.setCreateTime(new Date());
        picture.setUpdateTime(new Date());
        picture.setState(NodeState.NOT_APPROVED.value());
        picture.setDeleteFlag(true);
        pictureMapper.insert(picture);
        // ????????????
        if (!isEmpty(infoVO.getFiles())) {
            List<PictureFile> fileList = setPictureFile(infoVO.getFiles(), userId, picture.getId());
            fileMapper.insertBatch(fileList);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePicture(PictureInfoVO infoVO, Integer userId) {
        Picture single = pictureMapper.single(infoVO.getId());
        if (!userId.equals(single.getUserId())) {
            throw new PublicityException(PublicityErrorCode.NO_OPERATION_PERMISSION);
        }
        // ??????????????????
        Picture picture = new Picture();
        BeanUtils.copyProperties(infoVO, picture);
        pictureMapper.updateTemplateById(picture);
        // ??????????????????
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
        //??????????????????
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
        //  1.??????????????????????????????????????????
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        UserInfo userInfo = userApi.getUserInfoById(picture.getUserId());
        if (!user.getNickName().equals(userInfo.getNickName())) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  2.????????????id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId());
        // ????????????id????????????????????????
        List<FlowNodePropVO> flowProps = propApi.getNodeProps(flowId);
        //  3.??????????????????????????????
        boolean hasAccess = hasAccess2Approve(flowProps, picture.getUnitId(), initVO.getUserId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        //  4.??????????????????
        syncPrintFlow(flowProps, picture.getId(), userId);
        // ??????????????????
        String inform = flowService.getInform(flowProps.get(0).getFlowNodeId(),
                FlowNodeNoticeState.DEFAULT_REMINDER.value());

        // ????????????id??????????????????
        Flow flow = flowApi.getFlowById(flowId);
        inform = inform.replace(FlowNodeNoticeTemp.TITLE.value()
                , picture.getPictureName() + picture.getPictureNum() + "_" + flow.getFlowName());
        // ????????????????????????
        noticeService.addPictureNotice(
                PictureNotice.builder()
                        .pictureId(picture.getId())
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build());
        //  6.???????????????????????????
        picture.setId(initVO.getPictureId());
        picture.setState(NodeState.APPROVING.value());
        picture.setUpdateBy(initVO.getUserId());
        pictureMapper.updateTemplateById(picture);
    }


    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer unitId, Integer userId) {

        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // ?????????????????????
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // ????????????????????????
        // ???????????????????????????????????????????????????????????????????????????
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        } else {
            // ????????????????????????????????????????????????????????????
            // ??????????????????????????????
            if (UnitEnum.HANGZHOU.value().equals(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
                // ???????????????????????????
            } else if (UnitEnum.BENBU.value().equals(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApi.getSubUnit(UnitEnum.BENBU.value());
                return unitIds.contains(unitId) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
                // ???????????????????????????
            } else {
                // ?????????????????????????????????????????????????????????????????????
                return unitId.equals(firstNodeProp.getUnitId()) && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    private void syncPrintFlow(List<FlowNodePropVO> flowProps, Long id, Integer userId) {

        // ???????????????????????????list
        List<PictureFlow> pictureFlowList = new ArrayList<>();
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
        // ?????????????????? => ??????id
        Map<Long, Long> flowMap = new HashMap<>(5);
        List<Flow> flowList = flowApi.getFlowsByTypeId(flowTypeId);
        // ?????????????????????????????????????????????????????????
        if (flowList.size() == 0) {
            throw new PublicityException(PublicityErrorCode.NOT_EXIST_FLOW);
        } else if (flowList.size() > 1) {
            throw new PublicityException(PublicityErrorCode.EXCEED_LIMIT_FLOW);
        }
        flowList.forEach(flow -> flowMap.put(nodeApi.getNodeNum(flow.getId()), flow.getId()));
        // ????????????
        // ???????????????????????????4???????????????
        Long flowId;
        flowId = flowMap.get(3L);
        if (flowId == null) {
            throw new PublicityException(PublicityErrorCode.LACK_OF_FLOW);
        }
        // ????????????????????????????????????????????????????????????????????????
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
