## XXL-JOB & Spring Batch

### 运行项目，通过 XXL-JOB 调度中心触发, 执行 Spring Batch 任务

#### 1. 初始化sql脚本

- [tables_xxl_job.sql](doc/db/tables_xxl_job.sql)
- [tables_spring_batch.sql](doc/db/tables_spring_batch.sql)

#### 2. 启动项目

1. [xxl-job-admin](xxl-job-admin/src/main/java/com/xxl/job/admin/XxlJobAdminApplication.java)
2. [xxl-job-executor](xxl-job-executor/src/main/java/com/xxl/job/executor/XxlJobExecutorApplication.java)

#### 3. [访问控制台](http://127.0.0.1:8080/xxl-job-admin/jobinfo)
- 默认登录账号 admin/123456

#### 4. 触发任务与查看任务执行日志
任务管理：操作 --> 执行任务一次
任务管理：操作 --> 查询日志
