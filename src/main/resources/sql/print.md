selectPrintByCond
===
```sql
select
-- @pageTag(){
       print_num      as printNum,
       print_name     as printName,
       unit_id        as unitId,
       material_type  as materialType,
       apply_time     as applyTime,
       user_id        as userId,
       predict_amount as predictAmount,
       state          as state
    -- @}
    from print
    -- @where(){
    -- @if(!isEmpty(cond.unitId)){
      and unit_id = #{cond.unitId}
    -- @}
    -- @if(!isEmpty(cond.applyTime)){
      and apply_time like concat('%', #{cond.applyTime}, '%')
    -- @}
    -- @if(!isEmpty(cond.state)){
      and state = #{cond.state}
    -- @}
    -- @if(!isEmpty(cond.printName)){
      and print_name = #{cond.printName}
    -- @}
    -- @if(!isEmpty(cond.printNum)){
      and print_num = #{cond.printNum}
    -- @}
    -- @}
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
       pf.id            as `pf.id`,
       pf.file_id       as `pf.file_id`,
       pf.create_by     as `pf.create_by`
from print p
         left join print_file pf on p.id = pf.print_id
where p.id = #{id}
```