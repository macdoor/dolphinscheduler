# 数据迁移：FLINK_SQLGATEWAY → FLINK_SGW

将任务类型 `FLINK_SQLGATEWAY` 重命名为 `FLINK_SGW` 的数据库迁移方案。

## 影响表

| 表名 | 说明 |
|------|------|
| `t_ds_task_definition` | 任务定义 |
| `t_ds_task_definition_log` | 任务定义版本日志 |
| `t_ds_task_instance` | 任务实例（运行记录） |
| `t_ds_fav_task` | 用户收藏的任务类型 |

## 迁移步骤

### 1. 备份

```bash
# MySQL 备份相关表（根据实际库名调整）
mysqldump -u<user> -p<pass> <database> \
  t_ds_task_definition t_ds_task_definition_log t_ds_task_instance t_ds_fav_task \
  > backup_task_type_$(date +%Y%m%d).sql
```

### 2. 执行迁移 SQL

**MySQL：**

```sql
-- FLINK_SQLGATEWAY 任务类型重命名为 FLINK_SGW
UPDATE t_ds_task_definition SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
UPDATE t_ds_task_definition_log SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
UPDATE t_ds_task_instance SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
UPDATE t_ds_fav_task SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
```

**PostgreSQL：**

```sql
-- FLINK_SQLGATEWAY 任务类型重命名为 FLINK_SGW
UPDATE t_ds_task_definition SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
UPDATE t_ds_task_definition_log SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
UPDATE t_ds_task_instance SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
UPDATE t_ds_fav_task SET task_type = 'FLINK_SGW' WHERE task_type = 'FLINK_SQLGATEWAY';
```

### 3. 校验

```sql
-- 确认不存在残留的 FLINK_SQLGATEWAY
SELECT 't_ds_task_definition' t, COUNT(*) FROM t_ds_task_definition WHERE task_type = 'FLINK_SQLGATEWAY'
UNION ALL
SELECT 't_ds_task_definition_log', COUNT(*) FROM t_ds_task_definition_log WHERE task_type = 'FLINK_SQLGATEWAY'
UNION ALL
SELECT 't_ds_task_instance', COUNT(*) FROM t_ds_task_instance WHERE task_type = 'FLINK_SQLGATEWAY'
UNION ALL
SELECT 't_ds_fav_task', COUNT(*) FROM t_ds_fav_task WHERE task_type = 'FLINK_SQLGATEWAY';
-- 期望各表均为 0
```

### 4. 部署新代码

迁移完成后再部署使用 `FLINK_SGW` 的应用代码。

## 回滚（可选）

如迁移后发现问题，可回滚：

```sql
UPDATE t_ds_task_definition SET task_type = 'FLINK_SQLGATEWAY' WHERE task_type = 'FLINK_SGW';
UPDATE t_ds_task_definition_log SET task_type = 'FLINK_SQLGATEWAY' WHERE task_type = 'FLINK_SGW';
UPDATE t_ds_task_instance SET task_type = 'FLINK_SQLGATEWAY' WHERE task_type = 'FLINK_SGW';
UPDATE t_ds_fav_task SET task_type = 'FLINK_SQLGATEWAY' WHERE task_type = 'FLINK_SGW';
```

（回滚后需切回旧版本代码。）
