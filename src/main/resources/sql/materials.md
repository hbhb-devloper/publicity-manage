selectMaterialsById
===
```sql
select m.id           as id,
       unit_id        as unitId,
       materials_name as materialsName,
       apply_time     as applyTime,
       wide_band      as wideBand,
       predict_amount as predictAmount,
       producers      as producers,
       state          as state,
       mf.id          as `mf.id`,
       mf.file_id     as `mf.file_id`,
       mf.create_by   as `mf.create_by`
from materials m
         left join materials_file mf on m.id = mf.materials_id
where  m.id = #{id}
```
selectMaterialsListByCond
====
```sql
select id           as id,
       unit_id        as unitId,
       materials_name as materialsName,
       apply_time     as applyTime,
       wide_band      as wideBand,
       predict_amount as predictAmount,
       producers      as producers,
       state          as state
       from materials
    -- @where(){
        -- @if(!isEmpty(cond.applyTime)){
          apply_time = concat('%', #{cond.applyTime}, '%')
        -- @}
        -- @if(!isEmpty(cond.state)){
          and state = #{cond.state}
        -- @}
        -- @if(!isEmpty(cond.materialsNum)){
          and materials_num  =#{cond.materialsNum}
        -- @}
        -- @if(!isEmpty(cond.materialsName)){
          and materials_name = #{materialsName}
        -- @}
    -- @}
```
