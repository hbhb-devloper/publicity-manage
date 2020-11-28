GoodsSettingMapper
===
```sql
    select * 
    from goods_setting 
    where deadline like concat(#{time},'%')
```

