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

selectMaterialsBudgetByUnitId
===
```sql
select t1.*, t2.declaration, t3.amountPaid
    from (
             select sum(predict_amount)          as useAmount,
                    m.unit_id                    as unitId,
                    mb.budget                    as budget,
                    budget - sum(predict_amount) as balance,
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

selectMaterialsNumCountByUnitId
===
```sql
select IFNULL(max(right(print_num, 4)),0)
from materials
where year(create_time) = #{createTime}
  and delete_flag = 1
  and unit_id = #{unitId}
```
