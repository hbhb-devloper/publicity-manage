
selectBudgetList
===
```sql
    select mb.id                        as id,
           mb.unit_id                   as unitId,
           budget                       as budget,
           mb.remark                    as remark,
           sum(predict_amount)          as amountPaid,
           sum(predict_amount) / budget as proportion
    from materials_budget mb
             left join materials m on mb.unit_id = m.unit_id
    -- @pageIgnoreTag(){
    group by mb.unit_id
    -- @}
```