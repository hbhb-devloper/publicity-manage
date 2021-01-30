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
          apply_time like concat('%', #{cond.applyTime}, '%')
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
           and delete_flag =1
    -- @}
    -- @pageIgnoreTag(){
        order by  apply_time desc 
    -- @}
```

selectMaterialsBudgetByUnitId
===
```sql
select mb.unit_id                                                                                        as unitId,
       budget                                                                                            as budget,
       IFNULL(predict_amount, 0)                                                                         as predictAmount,
       IFNULL(sum(case
                      when state in (10, 20, 31)
                          and delete_flag = 1 then predict_amount end),0)                                as useAmount,
       IFNULL(sum(case
                      when state in (10, 20)
                          and delete_flag = 1 then predict_amount end), 0)                               as declaration,
       IFNULL(sum(case
                      when state in (31)
                          and delete_flag = 1 then predict_amount end), 0)                               as amountPaid,
       ROUND(budget -
             IFNULL(sum(case
                            when state in (10, 20, 31)
                                and delete_flag = 1 then predict_amount end), 0), 4)                     as balance,
       ROUND(
               IFNULL(sum(case
                              when state in (10, 20, 31)
                                  and delete_flag = 1 then predict_amount end) / budget, 0.00), 4) * 100 as proportion
from materials_budget mb
         left join materials m on mb.unit_id = m.unit_id
where budget_year = year(now()) and mb.unit_id = #{unitId}
group by mb.unit_id
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
