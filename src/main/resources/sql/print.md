selectPrintByCond
===
```sql
select
-- @pageTag(){
       id             as id ,
       print_num      as printNum,
       print_name     as printName,
       unit_id        as unitId,
       material_type  as materialType,
       date_format(apply_time,'%Y-%m-%d %H:%i:%s')       as applyTime,
       user_id        as userId,
       predict_amount as predictAmount,
       state          as state
    -- @}
    from print
    -- @where(){
    -- @if(isNotEmpty(list)){
       and unit_id in (#{join(list)})
    -- @}
    -- @if(isNotEmpty(cond.applyTime)){
      and apply_time like concat('%', #{cond.applyTime}, '%')
    -- @}
    -- @if(isNotEmpty(cond.state)){
      and state = #{cond.state}
    -- @}
    -- @if(isNotEmpty(cond.printName)){
      and print_name = #{cond.printName}
    -- @}
    -- @if(isNotEmpty(cond.printNum)){
      and print_num = #{cond.printNum}
    -- @}
      and delete_flag =1
    -- @}
      order by  apply_time
```

selectPrintInfoById
===
```sql
select p.id             as `p.id`,
       p.print_num      as `p.printNum`,
       p.print_name     as `p.printName`,
       p.predict_amount as `p.predictAmount`,
       p.material_type  as `p.materialsType`,
       p.apply_time     as `p.applyTime`,
from print p
where p.id = #{id}
```

selectPrintNumCountByUnitId
===
```sql
select IFNULL(max(right(print_num, 4)),0)
from print
where year(create_time) = #{createTime}
  and delete_flag = 1
  and unit_id = #{unitId}
```