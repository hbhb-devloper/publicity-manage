selectPageByCond
===
```sql
     select
     -- @pageTag(){
         an.id                                            as id,
         an.batch_num                                     as batchNum,
         an.content                                       as content,
         an.flow_type_id                                  as flowTypeId,
         g.checker                                        as userName,
         ad.under_unit_id                                 as unitId,
         date_format(an.create_time, '%Y-%m-%d %H:%i:%s') as createTime,
         ad.approved_state                                as state
     -- @}
     from application_notice an
           left join application a on an.batch_num = a.batch_num
           left join application_detail ad on a.id = ad.application_id
           left join goods g on ad.goods_id = g.id
     where an.state = 0
     -- @if(isNotEmpty(cond.receiver)){
          and an.receiver = #{cond.receiver}
     -- @}
     -- @if(isNotEmpty(cond.batchNum)){
        and a.batch_num = #{cond.batchNum}
     -- @}
     -- @pageIgnoreTag(){
        order by an.create_time desc, an.state
     -- @}
```