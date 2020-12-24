deleteByBatchNum
===
```sql
delete from application_flow where batch_num = #{batchNum}
```

selectNodeByNodeId
===
```sql
select af.flow_role_id           as flowRoleId
    from application_flow af
    where af.flow_node_id =  #{flowNodeId}
    and af.batch_num = #{batchNum}
```