异步任务自动化调度平台  

简单介绍：该项目是基于Spring Boot开发的支持任务自动调度、自动续做与重试、灵活任务配置的异步处理框架。实验室原创项目，主要用来处理异步任务，比如音视频数据的处理，实验数据的处理；  

该项目的优点：  
1、可以异步处理任务，某个任务执行失败，不会导致其他任务阻塞或者中断，所有只要把所有要处理的任务创建好（参数），就可以睡大觉，等起床起来查看任务执行状况；  
2、可以分阶段执行，一个任务有多个阶段，下一个阶段依赖或者不依赖前一个阶段，需要保存多阶段数据，将是该框架擅长处理的地方，支持查看任务所处阶段；  
3、可以同时处理不同类任务，一种worker可以处理一类任务，n种worker即可处理n类任务；  
4、业务代码与框架完全解耦合，容易接入，只需要在worker中的task目录创建任务类名中把逻辑代码和阶段方法即可，即可运行；  
5、支持设置失败重试，该框架有任务治理模块定时检查数据库是否有执行失败的任务，并且自动分表。  

例子：
![image](https://github.com/user-attachments/assets/d48bad81-523d-4fd1-aa93-e63633c44421)

各模块介绍：
flowsvr（服务端）：创建任务、调度任务、任务配置、查询某个任务的状态、占据任务、拉取任务等等  
async_deal（治理模块）：主要是治理服务端的数据库数据，检查是否有表超过一定的阈值  
worker（执行端）（客户端）：可多机部署，不同worker可以处理同类任务，也可以处理不同类任务  

![image](https://github.com/user-attachments/assets/f0661c00-5d23-4f49-bbea-1fed80f536d1)



如何使用？  
1、flowsvr（服务端）和async_deal（治理模块）部署在服务器上，开启子进程运行asyn_deal即可  
2、worker可以多机部署  
3、Kafka、MySQL在application.yml中配置，执行init.sql创建表结构  
4、在worker中的task文件夹中新建自己的任务类，写上你的任务的执行逻辑  
5、调用flowsvr中static中的html，打开前端页面或者用接口调用工具调用create_task接口，创建你的任务信息  
6、运行flowsvr、async_deal、worker，然后等待执行结果即可。  
（前端页面有设置重试时间，一次性拉取任务数量，最大重试次数等接口，可以根据需要配置参数）  

