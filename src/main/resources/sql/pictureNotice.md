selectPageByCond
===
 ```sql
    select
    -- @pageTag(){
        pn.id                                            as id,
        pn.print_id                                      as businessId,
        pn.content                                       as content,
        ROUND(p.predict_amount, 2)                       as amount,
        pn.flow_type_id                                  as flowTypeId,
        p.user_id                                        as userName,
        p.unit_id                                        as unitId,
        date_format(pn.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
        p.state                                          as state
    -- @}
    from print_notice pn
          left join print p on pn.print_id = p.id
    where pn.state = 0
    -- @if(isNotEmpty(cond.userId)){
        and pn.receiver = #{cond.userId}
    -- @}
    -- @if(isNotEmpty(cond.num)){
        and p.print_num like concat('%', #{cond.num}, '%')
    -- @}
    -- @if(isNotEmpty(cond.amountMin)){
        and p.predict_amount >= #{cond.amountMin}
    -- @}
    -- @if(isNotEmpty(cond.amountMax)){
        and p.predict_amount <= #{cond.amountMax}
    -- @}
    -- @pageIgnoreTag(){
        order by pn.create_time desc, pn.state
    -- @}
```