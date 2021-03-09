selectPageByCond
===
```sql
    select
    -- @pageTag(){
        mn.id                                            as id,
        mn.materials_id                                   as businessId,
        mn.content                                       as content,
        ROUND(m.predict_amount, 2)                       as amount,
        mn.flow_type_id                                  as flowTypeId,
        m.user_id                                        as userName,
        m.unit_id                                        as unitId,
        date_format(mn.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
        m.state                                          as state
    -- @}
    from materials_notice mn
          left join materials m on mn.materials_id = m.id
    where mn.state = 0
    -- @if(isNotEmpty(cond.userId)){
        and mn.receiver = #{cond.userId}
    -- @}
    -- @if(isNotEmpty(cond.num)){
        and m.materials_num like concat('%', #{cond.num}, '%')
    -- @}
    -- @if(isNotEmpty(cond.amountMin)){
        and m.predict_amount >= #{cond.amountMin}
    -- @}
    -- @if(isNotEmpty(cond.amountMax)){
        and m.predict_amount <= #{cond.amountMax}
    -- @}
    -- @pageIgnoreTag(){
        order by mn.create_time desc, mn.state
    -- @}
```
updateNoticeState
===
```sql
update  materials_notice set state = #{state} where materials_id =#{materialsId}
```