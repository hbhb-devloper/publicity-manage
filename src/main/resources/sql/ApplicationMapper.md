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
      where id in ()  
```
updateSubmit
===
```sql
      update application
      set submit = 0 
      where id in ()  
```