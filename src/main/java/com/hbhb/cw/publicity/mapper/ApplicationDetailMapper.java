package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.ApplicationDetail;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationDetailMapper extends BaseMapper<ApplicationDetail> {
    void updateStateByGoods(Long goodsId, List<Integer> list);
}
