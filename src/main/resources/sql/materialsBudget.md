
selectBudgetList
===
```sql
select mb.id                                                                                           as id,
       mb.unit_id                                                                                      as unitId,
       budget                                                                                          as budget,
       mb.remark                                                                                       as remark,
       IFNULL(sum(case
                      when state in (10, 20, 31)
                          and delete_flag = 1 then predict_amount end), 0)                             as amountPaid,
       ROUND(IFNULL(sum(case
                            when state in (10, 20, 31)
                                and delete_flag = 1 then predict_amount end) / budget, 0.00), 4) * 100 as proportion
from materials_budget mb
         left join materials m on mb.unit_id = m.unit_id
    -- @pageIgnoreTag(){
    group by mb.unit_id
    -- @}
```