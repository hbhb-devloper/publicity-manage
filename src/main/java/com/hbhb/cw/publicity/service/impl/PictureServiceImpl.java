package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.service.PictureService;
import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
public class PictureServiceImpl implements PictureService {
    @Override
    public PageResult<PictureResVO> getPictureList(PictureReqVO reqVO, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public PictureResVO getPicture(Long id) {
        return null;
    }

    @Override
    public void addPicture(PictureInfoVO infoVO, Integer userId) {

    }
}
