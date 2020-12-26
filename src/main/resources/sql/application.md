selectApplicationByUnitId
===
```sql
       select * from application
       -- @where(){
           -- @if(isNotEmpty(unitId)){
                and unit_id = #{unitId}
           -- @}
           -- @if(isNotEmpty(goodsIndex)){
                and batch_num = #{batchNum}
           -- @}
           -- @if(isNotEmpty(hallId)){
                and hall_id = #{hallId}
           -- @}
       -- @}
```

selectByCond
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
           -- @}
        group by hall_id;
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

updateEditable
===
```sql
      update application
      set editable = 1
      where id in (#{join(list)})
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