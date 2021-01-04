selectApplicationByUnitId
===
```sql
       select * from application
       -- @where(){
           -- @if(isNotEmpty(unitId)){
                and unit_id = #{unitId}
           -- @}
           -- @if(isNotEmpty(batchNum)){
                and batch_num = #{batchNum}
           -- @}
           -- @if(isNotEmpty(hallId)){
                and hall_id = #{hallId}
           -- @}
       -- @}
```

selectByHall
===
```sql
        select * from application
           -- @where(){
               -- @if(isNotEmpty(unitId)){
                    and unit_id = #{unitId}
               -- @}
               -- @if(isNotEmpty(batchNum)){
                    and batch_num = #{batchNum}
               -- @}
               -- @if(isNotEmpty(hallId)){
                    and hall_id = #{hallId}
               -- @} 
           -- @}
```


selectByUnit
===
```sql
select a.unit_id                             as unitId,
       ad.goods_id                           as goodsId,
       ad.state                              as state,
       g.checker                             as checker
from application a
         left join application_detail ad on a.id = ad.application_id
         left join goods g on ad.goods_id = g.id
 -- @where(){
               -- @if(isNotEmpty(unitId)){
                    and g.unit_id = #{unitId}
               -- @}
               -- @if(isNotEmpty(batchNum)){
                    and a.batch_num = #{batchNum}
               -- @}
           -- @}
group by a.unit_id, ad.goods_id, ad.state, g.checker
```

selectByCond
===
```sql
     select g.id         as  goodsId,
            g.goods_name  as goodsName,
            g.unit       as unit,
            ifnull(sum(ad.apply_amount),0) as applyAmount,
            a.editable   as  editable
     from goods g
              left join application_detail ad on g.id = ad.goods_id
              left join application a on a.id = ad.application_id
              left join goods_setting gs on CONCAT(date_format(gs.deadline,'%y%m%d'),gs.goods_index) = a.batch_num
     where g.mold = 0
     and g.state = 1
            -- @if(isNotEmpty(unitId)){
                and a.unit_id = #{unitId}
            -- @}
            -- @if(isNotEmpty(batchNum)){
                and a.batch_num = #{batchNum}
            -- @}
            -- @if(isNotEmpty(hallId)){
                and a.hall_id = #{hallId}
            -- @}
     group by g.id;
```