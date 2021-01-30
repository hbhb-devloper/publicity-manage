selectPictureInfoById
===
```sql
select p.id           as id,
       picture_name   as `pictureName`,
       picture_num    as `pictureNum`,
       date_format(p.apply_time,'%Y-%m-%d %H:%i:%s')       as applyTime,
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
select 
  -- @pageTag(){
       p.id           as `id`,
       unit_id        as unitId,
       user_id        as userId,
       picture_name   as `pictureName`,
       picture_num    as `pictureNum`,
       p.apply_time   as `applyTime`,
       p.wide_band    as `wideBand`,
       p.producers    as `producers`,
       predict_amount as `predictAmount`,
       state          as `state`
    -- @}
from picture p
    -- @where(){
        -- @if(isNotEmpty(cond.applyTime)){
          apply_time like concat('%', #{cond.applyTime}, '%')
        -- @}
        -- @if(isNotEmpty(cond.state)){
          and state = #{cond.state}
        -- @}
        -- @if(isNotEmpty(cond.pictureNum)){
          and picture_num =#{cond.pictureNum}
        -- @}
        -- @if(isNotEmpty(cond.pictureName)){
          and picture_name = #{cond.pictureName}
        -- @}
        -- @if(isNotEmpty(list)){
           and unit_id in (#{join(list)})
        -- @}
           and delete_flag =1
    -- @}
    -- @pageIgnoreTag(){
        order by  apply_time desc
    -- @}
```

selectPictureNumCountByUnitId
===
```sql
select IFNULL(max(right(picture_num, 4)),0)
from picture
where year(create_time) = #{createTime}
  and delete_flag = 1
  and unit_id = #{unitId}
```
