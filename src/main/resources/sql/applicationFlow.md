deleteByBatchNum
===
```sql
delete from application_flow where batch_num = #{batchNum}
```

selectNodeByNodeId
===
```sql
select af.user_id              as userId
    from application_flow af
    where af.flow_node_id =  #{flowNodeId}
    and af.batch_num = #{batchNum}
```