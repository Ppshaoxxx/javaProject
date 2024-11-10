# AsyncFlow数据治理
## 功能
1. 处理当前写入表记录数量大于阈值时创建新表并向前滚动
2. 处理已经没有可执行任务的表向前滚动
3. 处理超时任务，将任务更新为失败状态或者等待状态
## 配置
可同过application.yml配置如下参数：
```yaml
# 项目自定义配置
deal:
  timeout-tasks:
    # 超时任务最多一次处理1000个任务
    limit-count: 1000
  tasks:
    # 每个表的最大限制,不填默认为500w
    table-limit: 50

```
需要配置数据库相关参数
```yaml
spring:
  datasource:
    url: 你的数据库地址
    username: 你的用户名
    password: 你的密码
```
数据库地址需要添加allowMultiQueries=true,主要是因为我更新超时任务时是批量更新，多条更新语句。