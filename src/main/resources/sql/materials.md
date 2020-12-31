selectMaterialsById
===
```sql
select m.id           as id,
       unit_id        as unitId,
       materials_name as materialsName,
       date_format(apply_time,'%Y-%m-%d %H:%i:%s')       as applyTime,
       wide_band      as wideBand,
       predict_amount as predictAmount,
       producers      as producers,
       state          as state,
from materials m
where  m.id = #{id}
```
selectMaterialsListByCond
====
```sql
select 
    -- @pageTag(){
       id           as id,
       unit_id        as unitId,
       user_id        as userId,
       materials_num  as materialsNum,
       materials_name as materialsName,
       apply_time     as applyTime,
       wide_band      as wideBand,
       predict_amount as predictAmount,
       producers      as producers,
       state          as state
    -- @}
       from materials
    -- @where(){
        -- @if(isNotEmpty(cond.applyTime)){
          apply_time = concat('%', #{cond.applyTime}, '%')
        -- @}
        -- @if(isNotEmpty(cond.state)){
          and state = #{cond.state}
        -- @}
        -- @if(isNotEmpty(cond.materialsNum)){
          and materials_num  =#{cond.materialsNum}
        -- @}
        -- @if(isNotEmpty(cond.materialsName)){
          and materials_name = #{materialsName}
        -- @}
        -- @if(isNotEmpty(list)){
           and unit_id in (#{join(list)})
        -- @}
    -- @}
        order by apply_time
```

selectMaterialsBudgetByUnitId
===
```sql
select t1.*, t2.declaration, t3.amountPaid
    from (
             select sum(predict_amount)          as useAmount,
                    m.unit_id                    as unitId,
                    mb.budget                    as budget,
                   ROUND(budget - sum(predict_amount),4) as balance,
                    sum(predict_amount)/budget   as proportion
             from materials m
                      left join materials_budget mb on m.unit_id = mb.unit_id
             where state in (10, 20, 31)
         ) t1
             left join (
        select sum(predict_amount) as declaration,
               unit_id
        from materials
        where state in (10, 20)
    ) t2 on t1.unitId = t2.unit_id
             left join (
        select sum(predict_amount) as amountPaid,
               unit_id
        from materials
        where state = 31
    ) t3 on t1.unitId = t3.unit_id
where unitId = #{unitId}
```

selectPictureNumCountByUnitId
===
```sql
select IFNULL(max(right(materials_num, 4)),0)
from materials
where year(create_time) = #{createTime}
  and delete_flag = 1
  and unit_id = #{unitId}
```
