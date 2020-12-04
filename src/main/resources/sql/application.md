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
updateSubmit
===
```sql
      update application
      set submit = 0 
      where id in ()  
```

updateByBatchNum
===
```sql
    update application
    set approved_state = #{state}
    where batch_num = #{batchNum}
```