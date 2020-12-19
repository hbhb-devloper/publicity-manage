updateBatchByNodeId
===
```sql
 -- @for(item in approver){
          update  materials_flow set 
          user_id = #{item.userId}
          where picture_id = #{materialsId}
          and flow_node_id = #{item.flowNodeId}
 -- @}
```

