selectPictureInfoById
===
```sql
select p.id           as id,
       picture_name   as `pictureName`,
       picture_num    as `pictureNum`,
       p.apply_time   as `p.applyTime`,
       p.wide_band    as `wideBand`,
       p.producers    as `producers`,
       predict_amount as `predictAmount`,
       state          as `state`,
from picture p
where p.id = #{id}
```
selectPictureListByCond
===
```sql
select p.id           as `id`,
       picture_name   as `pictureName`,
       picture_num    as `pictureNum`,
       p.apply_time   as `p.applyTime`,
       p.wide_band    as `wideBand`,
       p.producers    as `producers`,
       predict_amount as `predictAmount`,
       state          as `state`
from picture p
    -- @where(){
        -- @if(!isEmpty(cond.applyTime)){
          apply_time = concat('%', #{cond.applyTime}, '%')
        -- @}
        -- @if(!isEmpty(cond.state)){
          and state = #{cond.state}
        -- @}
        -- @if(!isEmpty(cond.pictureNum)){
          and picture_num =#{cond.pictureNum}
        -- @}
        -- @if(!isEmpty(cond.pictureName)){
          and picture_name = #{pictureName}
        -- @}
    -- @}
```


