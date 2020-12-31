selectPageByCond
===
```sql
    select
    -- @pageTag(){
        pn.id                                            as id,
        pn.picture_id                                    as businessId,
        pn.content                                       as content,
        ROUND(p.predict_amount, 2)                       as amount,
        pn.flow_type_id                                  as flowTypeId,
        p.user_id                                        as userName,
        p.unit_id                                        as unitId,
        date_format(pn.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
        p.state                                          as state
    -- @}
    from picture_notice pn
          left join picture p on pn.picture_id = p.id
    where pn.state = 0
    -- @if(isNotEmpty(cond.userId)){
        and mn.receiver = #{cond.userId}
    -- @}
    -- @if(isNotEmpty(cond.num)){
        and p.picture_num like concat('%', #{cond.num}, '%')
    -- @}
    -- @if(isNotEmpty(cond.amountMin)){
        and m.predict_amount >= #{cond.amountMin}
    -- @}
    -- @if(isNotEmpty(cond.amountMax)){
        and m.predict_amount <= #{cond.amountMax}
    -- @}
    -- @pageIgnoreTag(){
        order by pn.create_time desc, pn.state
    -- @}
```