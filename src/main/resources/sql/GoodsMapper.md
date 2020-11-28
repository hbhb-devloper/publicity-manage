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
                   and g.parent_id in #{id}
              -- @}
         -- @}
```

selectById
===
```sql
     select * from goods where id = #{id}
```

selectByCond
===
```sql
     select g.id            as goodsId,
            g.goods_name    as goodsName,
            g.unit          as unit,
            ad.apply_amount as applyAmount,
            a.editable      as editable
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

selectPurchaseGoods
===
```sql
        
```

selectSummaryByType
===
```sql

```


selectSummaryByState
===
```sql

```

selectSummaryUnitByType
===
```sql

```
