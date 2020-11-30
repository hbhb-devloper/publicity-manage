selectByCond
===
```sql
     select g.id         as  goodsId,
            g.goodsName  as goodsName,
            g.unit       as unit,
            ifnull(sum(ad.apply_amount),0) as applyAmount,
            a.editable   as  editable
     from goods g
              left join goods_setting gs on g.id = gs.goods_id
              left join application_detail ad on g.id = ad.goods_id
              left join application a on a.id = ad.application_id
         -- @where(){
            -- @if(isNotEmpty(unitId)){
                and g.unit_id = #{unitId}
            -- @}
            -- @if(isNotEmpty(time)){
                and gs.deadline like concat(#{time},'%')
            -- @}
            -- @if(isNotEmpty(goodsIndex)){
                and gs.goods_index = #{goodsIndex}
            -- @}
            -- @if(isNotEmpty(hallId)){
                and a.hall_id = #{hallId}
            -- @}
         -- @}
     group by a.hall_id,g.id;
```

selectById
===
```sql
     select * from goods where id = #{id}
```

selectByUnitId
===
```sql
    select g.id as id,
           g.goods_name as goodsName,
           g.id as g.id,
           g.goods_name as g.goods_name
    from goods g      
    -- @where(){
         -- @if(isNotEmpty(unitId)){
              and g.unit_id = #{unitId}
         -- @}
    -- @}
```
selectGoodsByActIds
===
```sql
     select g.id as id,
                g.goods_name as goodsName,
                g.id as g.id,
                g.goods_name as g.goods_name
         from goods g      
         -- @where(){
              -- @if(isNotEmpty(unitId)){
                   and g.parent_id in #{list}
              -- @}
         -- @}
```

selectSummaryUnitByType
===
```sql
    select  g.id as goodsId,
            ifnull(sum(ad.modify_amount),0) as simplexAmountAmount,
            ifnull(sum(ad.modify_amount),0) as singleAmount,
            g.goods_name as goodsName,
            g.unit as unit,
            ad.modify_amount as modifyAmount
    from goods g
             left join goods_setting gs on g.id = gs.goods_id
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
    -- @where(){
                -- @if(isNotEmpty(unitId)){
                    and g.unit_id = #{unitId}
                -- @}
                -- @if(isNotEmpty(time)){
                    and gs.deadline like concat(#{time},'%')
                -- @}
                -- @if(isNotEmpty(goodsIndex)){
                    and gs.goods_index = #{goodsIndex}
                -- @}
                -- @if(isNotEmpty(hallId)){
                    and a.hall_id = #{hallId}
                -- @}
                -- @if(isNotEmpty(type)){
                    and g.type = #{type}
                -- @}
             -- @}
    group by g.unit_id,g.id;
```

selectSummaryByType
===
```sql
    select a.id  as applicationId,
           g.goods_name as goodsName,
           g.unit as unit,
           ad.apply_amount as applyAmount,
           ad.modify_amount as modifyAmount,
           g.type as type,
           ad.state as state,
           a.create_time as time  
    from goods g
             left join goods_setting gs on g.id = gs.goods_id
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
    -- @where(){
                -- @if(isNotEmpty(unitId)){
                    and a.unit_id = #{unitId}
                -- @}
                -- @if(isNotEmpty(time)){
                    and gs.deadline like concat(#{time},'%')
                -- @}
                -- @if(isNotEmpty(goodsIndex)){
                    and gs.goods_index = #{goodsIndex}
                -- @}
                -- @if(isNotEmpty(hallId)){
                    and a.hall_id = #{hallId}
                -- @}
                -- @if(isNotEmpty(type)){
                    and g.type = #{type}
                -- @}
             -- @}
    group by g.unit_id,g.id;
```


selectSummaryByState
===
```sql
    select  a.id  as applicationId,
            g.goods_name as goodsName,
            g.unit as unit,
            ad.apply_amount as applyAmount,
            ad.modify_amount as modifyAmount,
            g.type as type,
            ad.state as state,
            a.create_time as time  
    from goods g
             left join goods_setting gs on g.id = gs.goods_id
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
    -- @where(){
                -- @if(isNotEmpty(unitId)){
                    and a.unit_id = #{unitId}
                -- @}
                -- @if(isNotEmpty(time)){
                    and gs.deadline like concat(#{time},'%')
                -- @}
                -- @if(isNotEmpty(goodsIndex)){
                    and gs.goods_index = #{goodsIndex}
                -- @}
                -- @if(isNotEmpty(hallId)){
                    and a.hall_id = #{hallId}
                -- @}
                -- @if(isNotEmpty(type)){
                    and g.type = #{type}
                -- @}
                -- @if(isNotEmpty(state)){
                    and a.approved_state = #{state}
                -- @}
             -- @}
    group by g.unit_id,g.id;
```

selectPurchaseGoods
===
```sql
    select  g.id  as goodsId,
            g.goods_name as goodsName,
            g.unit as unit,
            ad.modify_amount as modifyAmount,
            g.size  as  size,
            g.attribute as attribute,
            h.paper as paper,
            g.checker as checker
    from goods g
             left join goods_setting gs on g.id = gs.goods_id
             left join application_detail ad on g.id = ad.goods_id
             left join application a on a.id = ad.application_id
    -- @where(){
                -- @if(isNotEmpty(time)){
                    and gs.deadline like concat(#{time},'%')
                -- @}
                -- @if(isNotEmpty(goodsIndex)){
                    and gs.goods_index = #{goodsIndex}
                -- @}
                -- @if(isNotEmpty(approvedState)){
                    and a.approved_state = #{state}
                -- @}
                -- @if(isNotEmpty(state)){
                    and ap.state = 2
                -- @}
             -- @}
    group by g.id;
```
