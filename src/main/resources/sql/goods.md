selectGoodsList
===
```sql
 select g.id         as  goodsId,
            g.goods_name  as goodsName,
            g.unit       as unit,
            0           as applyAmount,
            a.editable   as  editable
     from goods g
              left join application_detail ad on g.id = ad.goods_id
              left join application a on a.id = ad.application_id
              left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
     where g.mold = 0
     and g.state = 1
 group by g.id;
```


selectByUnitId
===
```sql
    select g.id as `id`,
           g.goods_name as `label`,
           g.parent_id as parentId,
           g.mold  as mold,
           g.state as state
    from goods g
    where mold = 1
    and unit_id = #{unitId}
    and parent_id is null
```
selectGoodsByActIds
===
```sql
      select id as id,
             goods_name as label,
             parent_id as parentId,
             mold as mold,
             state as state
             from goods
             where unit_id = #{unitId}
             and parent_id in (#{join(list)})
```


selectSummaryByState
===
```sql
    select  ad.id  as `applicationDetailId`,
            g.id as goodsId,
            a.id as applicationId,
            g.goods_name as `goodsName`,
            g.unit as `unit`,
            g.unit_id as unitId,
            a.hall_id as hallId,
            ifnull(sum(ad.apply_amount),0) as `applyAmount`,
            ifnull(sum(ad.modify_amount),0) as `modifyAmount`,
            g.type as `type`,
            ad.state as `state`,
            a.create_time as `time` 
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where a.batch_num = #{batchNum}
            and g.mold = 0
                -- @if(isNotEmpty(unitId)){
                    and a.unit_id = #{unitId}
                -- @}
                -- @if(isNotEmpty(type)){
                    and g.type = #{type}
                -- @}
                -- @if(isNotEmpty(state)){
                    and ad.state = #{state}
                -- @}
                -- @if(isNotEmpty(hallId)){
                    and a.hall_id = #{hallId}
                -- @}
    group by a.hall_id,g.id;
```


selectIdsByCond
===
```sql
    select a.id 
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where  ad.state in (0,3)
                -- @if(isNotEmpty(unitId)){
                    and a.unit_id = #{unitId}
                -- @}
                -- @if(isNotEmpty(batchNum)){
                    and a.batch_num = #{batchNum}
                -- @}
                -- @if(isNotEmpty(type)){
                    and g.type = #{type}
                -- @}
                -- @if(isNotEmpty(hallId)){
                    and a.hall_id = #{hallId}
                -- @}
    group by a.id;
```

selectPurchaseGoods
===
```sql
    select 
        -- @pageTag(){
            g.id  as `goodsId`,
            g.goods_name as `goodsName`,
            g.unit as `unit`,
            ad.modify_amount as `modifyAmount`,
            g.size  as  `size`,
            g.attribute as `attribute`,
            g.paper as `paper`,
            g.checker as `checkerId`,
            g.unit_id as unitId
        -- @}
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
     where ad.state = 2
        and ad.approved_state  = 31
                -- @if(isNotEmpty(batchNum)){
                    and a.batch_num = #{batchNum}
                -- @}
                -- @if(isNotEmpty(unitId)){
                    and g.unit_id = #{unitId}
                -- @}
                -- @pageIgnoreTag(){
                    group by g.id
                -- @}
```

selectGoodsByHallId
===
```sql
    select  g.id  as `goodsId`,
            g.goods_name as `goodsName`,
            g.unit as `unit`,
            ifnull(sum(ad.modify_amount), 0) as `modifyAmount`,
            g.size  as  `size`,
            g.attribute as `attribute`,
            g.paper as `paper`,
            g.checker as `checker`,
            a.hall_id as hallId
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where ad.state = 2
            and ad.approved_state  = 31
                    -- @if(isNotEmpty(batchNum)){
                        and a.batch_num = #{batchNum}
                    -- @}
                    -- @if(isNotEmpty(unitId)){
                        and g.unit_id = #{unitId}
                    -- @}
    group by g.id,a.hall_id;
```

selectIdGoodsByHallId
===
```sql
    select  g.id  as `goodsId`,
            g.goods_name as `goodsName`,
            g.unit as `unit`,
            ifnull(sum(ad.modify_amount), 0) as `modifyAmount`,
            g.size  as  `size`,
            g.attribute as `attribute`,
            g.paper as `paper`,
            g.checker as `checker`,
            a.hall_id as hallId
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where ad.state = 2
            and ad.approved_state  = 31
                    -- @if(isNotEmpty(batchNum)){
                        and a.batch_num = #{batchNum}
                    -- @}
                    -- @if(isNotEmpty(unitId)){
                        and g.unit_id = #{unitId}
                    -- @}
    group by a.hall_id;
```

sumGoodsByHallId
===
```sql
    select  g.id  as `id`,
            ifnull(sum(ad.modify_amount), 0) as `label`
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where ad.state = 2
            and ad.approved_state  = 31
                    -- @if(isNotEmpty(batchNum)){
                        and a.batch_num = #{batchNum}
                    -- @}
    group by g.id;
```

selectVerifyList
===
```sql
    select  g.id  as `goodsId`,
            g.goods_name as `goodsName`,
            a.unit_id as `unitId`,
            ad.modify_amount as `modifyAmount`
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where a.submit = 1
    and ad.state = 1
                -- @if(isNotEmpty(batchNum)){
                    and a.batch_num = #{batchNum}
                -- @}
                -- @if(isNotEmpty(userId)){
                    and g.checker = #{userId}
                -- @}
    group by g.id;
```

selectVerifyHallList
===
```sql
   select g.id  as `goodsId`,
          g.goods_name    as `goodsName`,
          a.hall_id       as `hallId`,
          ad.modify_amount as `modifyAmount`,
          a.unit_id       as unitId,
          ad.id           as `applicationDetailId`
    from goods g
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
             left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
    where a.submit = 1
          and g.mold = 0
          and ad.state = 1
                -- @if(isNotEmpty(batchNum)){
                   and a.batch_num = #{batchNum}
                -- @}
                -- @if(isNotEmpty(goodsId)){
                   and g.id = #{goodsId}
                -- @}
                -- @if(isNotEmpty(unitId)){
                   and a.unit_id = #{unitId}
                -- @}
    group by a.hall_id;
```
