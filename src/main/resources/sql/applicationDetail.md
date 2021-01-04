selectSummaryUnitByType
===
```sql
    select  g.id as `goodsId`,
            sum(ad.modify_amount) as `amount`,
            g.goods_name as `goodsName`,
            g.unit as `unit`,
            a.unit_id as unitId,
            ad.modify_amount as `modifyAmount`,
            ad.approved_state as approvedState
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
            left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
            where a.submit = 1
            and ad.state in (1,2)
            and g.mold = 0
                    -- @if(isNotEmpty(unitId)){
                         and g.unit_id = #{unitId}
                    -- @}
                    -- @if(isNotEmpty(batchNum)){
                         and a.batch_num = #{batchNum}
                    -- @}
                    -- @if(isNotEmpty(type)){
                         and g.type = #{type}
                    -- @}
    group by a.unit_id,g.id;
```

selectApplicationSumByType
===
```sql
    select  g.id as `goodsId`,
            sum(ad.modify_amount) as `simplexAmount`,
            sum(ad.modify_amount) as `singleAmount`,
            a.unit_id as unitId,
            ad.modify_amount as `modifyAmount`,
            ad.approved_state as approvedState
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
            left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
            where a.submit = 1
            and ad.state in (1,2)
            and g.mold = 0
                    -- @if(isNotEmpty(unitId)){
                         and g.unit_id = #{unitId}
                    -- @}
                    -- @if(isNotEmpty(batchNum)){
                         and a.batch_num = #{batchNum}
                    -- @}
                    -- @if(isNotEmpty(type)){
                         and g.type = #{type}
                    -- @}
    group by a.unit_id;
```