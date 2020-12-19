updateBatchByNodeId
===
```sql
 -- @for(item in approver){
          update  print_flow set 
          user_id = #{item.userId}
          where picture_id = #{printId}
          and flow_node_id = #{item.flowNodeId}
 -- @}
```

