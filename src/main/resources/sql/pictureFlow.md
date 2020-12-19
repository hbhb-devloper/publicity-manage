updateBatchByNodeId
===
```sql
 -- @for(item in approver){
          update  picture_flow set 
          user_id = #{item.userId}
          where picture_id = #{pictureId}
          and flow_node_id = #{item.flowNodeId}
 -- @}
```

