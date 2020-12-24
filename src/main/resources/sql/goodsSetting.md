selectByDate
===
```sql
    select * 
    from goods_setting 
    where deadline like concat(#{time},'%')
```

selectSetByDate
===
```sql
    select gs.* 
    from goods_setting gs 
    where gs.deadline >= #{time}
    order by deadline limit 1;
```


