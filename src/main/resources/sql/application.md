selectApplicationByUnitId
===
```sql
       select * from application
       -- @where(){
           -- @if(isNotEmpty(unitId)){
                and unit_id = #{unitId}
           -- @}
           -- @if(isNotEmpty(time)){
                and create_time = #{time}
           -- @}
           -- @if(isNotEmpty(goodsIndex)){
                and goods_index = #{goodsIndex}
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
               -- @if(isNotEmpty(time)){
                    and create_time = #{time}
               -- @}
               -- @if(isNotEmpty(goodsIndex)){
                    and goods_index = #{goodsIndex}
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
               -- @if(isNotEmpty(time)){
                    and create_time = #{time}
               -- @}
               -- @if(isNotEmpty(hallId)){
                    and hall_id = #{hallId}
               -- @} 
               -- @if(isNotEmpty(goodsIndex)){
                    and goods_index = #{goodsIndex}
               -- @}
           -- @}
```

updateEditable
===
```sql
      update application
      set editable = 0 
      where id = 
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
                    and a.unit_id = #{unitId}
               -- @}
               -- @if(isNotEmpty(time)){
                    and a.create_time = #{time}
               -- @}
               -- @if(isNotEmpty(goodsIndex)){
                    and a.goods_index = #{goodsIndex}
               -- @}
           -- @}
group by a.unit_id, ad.goods_id, ad.state, g.checker
```