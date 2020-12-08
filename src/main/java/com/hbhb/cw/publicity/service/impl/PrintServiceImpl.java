package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.PrintFileMapper;
import com.hbhb.cw.publicity.mapper.PrintMapper;
import com.hbhb.cw.publicity.model.Print;
import com.hbhb.cw.publicity.model.PrintFile;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.web.vo.PrintFileVO;
import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import com.hbhb.cw.systemcenter.model.File;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    @Override
    public PageResult<PrintResVO> getPrintList(PrintReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<PrintResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<PrintResVO> list = printMapper.selectPrintByCond(reqVO, request);
        // todo 含字典，单位信息，人员姓名需填充
        return list;
    }

    @Override
    public PrintInfoVO getPrint(Long id) {
        PrintInfoVO info = printMapper.selectPrintInfoById(id);
        if (info != null) {
            List<PrintFileVO> files = info.getFile();
            List<Integer> fileIds = new ArrayList<>();
            for (PrintFileVO fileVO : files) {
                fileIds.add(Math.toIntExact(fileVO.getFileId()));
            }
            // 获取文件详情
            List<File> fileInfo = fileApi.getFileInfoBatch(fileIds);
            for (File file : fileInfo) {
                for (PrintFileVO fileVO : files) {
                    if (fileVO.getFileId().equals(file.getId())) {
                        fileVO.setFileName(file.getFileName());
                        fileVO.setFilePath(file.getFilePath());
                        fileVO.setFileSize(file.getFileSize());
                        fileVO.setFileId(file.getId());
                        fileVO.setPrintId(info.getId());
                    }
                }
            }
        }
        return info;
    }

    @Override
    public void addPrint(PrintInfoVO infoVO, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        Print print = new Print();
        BeanUtils.copyProperties(print, infoVO);
        // 新增印刷用品
        printMapper.insert(print);
        List<PrintFileVO> fileVOList = infoVO.getFile();
        List<PrintFile> fileList = new ArrayList<>();
        for (PrintFileVO fileVO : fileVOList) {
            PrintFile printFile = new PrintFile();
            printFile.setFileId(fileVO.getFileId());
            printFile.setPrintId(print.getId());
            printFile.setCreateTime(new Date());
            printFile.setCreateBy(user.getNickName());
            fileList.add(printFile);
        }
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
    }


}
