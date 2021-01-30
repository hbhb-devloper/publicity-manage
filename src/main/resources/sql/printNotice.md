selectPageByCond
===
```sql
    select
    -- @pageTag(){
        *
    -- @}
    from (
             select *
             from (
                      select pn.id                                            as id,
                             pn.print_id                                      as businessId,
                             pn.content                                       as content,
                             ROUND(p.predict_amount, 2)                       as amount,
                             pn.flow_type_id                                  as flowTypeId,
                             p.user_id                                        as userName,
                             p.predict_amount                                 as predictAmount,
                             pn.receiver                                      as receiver,
                             p.print_num                                      as num,
                             p.unit_id                                        as unitId,
                             date_format(pn.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
                             p.state                                          as state
                      from print_notice pn
                               left join print p on pn.print_id = p.id
                      where pn.state = 0
                  ) t1
             union
             (
                 select pn.id                                            as id,
                        pn.print_id                                      as businessId,
                        pn.content                                       as content,
                        ROUND(p.predict_amount, 2)                       as amount,
                        pn.flow_type_id                                  as flowTypeId,
                        p.user_id                                        as userName,
                        p.predict_amount                                 as predictAmount,
                        pn.receiver                                      as receiver,
                        p.print_num                                      as num,
                        p.unit_id                                        as unitId,
                        date_format(pn.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
                        p.state                                          as state
                 from print_notice pn
                          left join print p on pn.print_id = p.id
                 where pn.state = 0
             )
             union
             (
                 select mn.id                                            as id,
                        mn.materials_id                                  as businessId,
                        mn.content                                       as content,
                        ROUND(m.predict_amount, 2)                       as amount,
                        mn.flow_type_id                                  as flowTypeId,
                        m.user_id                                        as userName,
                        m.predict_amount                                 as predictAmount,
                        mn.receiver                                      as receiver,
                        m.materials_num                                  as num,
                        m.unit_id                                        as unitId,
                        date_format(mn.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
                        m.state                                          as state
                 from materials_notice mn
                          left join materials m on mn.materials_id = m.id
                 where mn.state = 0
             )) t2
    where
    -- @if(isNotEmpty(cond.userId)){
         t2.receiver = #{cond.userId}
        -- @}
        -- @if(isNotEmpty(cond.num)){
        and t2.num like concat('%', #{cond.num}, '%')
        -- @}
        -- @if(isNotEmpty(cond.amountMin)){
        and t2.predictAmount >= #{cond.amountMin}
        -- @}
        -- @if(isNotEmpty(cond.amountMax)){
        and t2.predictAmount <= #{cond.amountMax}
        -- @}
        -- @pageIgnoreTag(){
        order by t2.createTime desc, state
    -- @}
```