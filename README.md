异步任务自动化调度平台

项目介绍：该项目是基于Spring Boot开发的支持任务自动调度、自动续做与重试、灵活任务配置的异步处理框架

	框架分为两层，服务端负责产生、调度任务，客户端负责消费任务，可多机部署，轻量高效；

	水平分表和垂直分表相结合，分为冷表和热表，实现海量数据下对任务高性能精准定位；

	使用ProxySQL实现MySQL主从模式，实现读写分离，加快创建任务和拉取任务的效率；

	使用Kafka消息队列存放拉取的任务，客户端从消息队列中占据任务，解决了多机竞争问题；

	使用AOP设计幂等注解，根据参数生成Relis唯一键避免重复任务生成，可升级为数据库持久化键；

	服务端开启子线程，使用Spring Scheduler实现对数据量过大的表的处理和异常任务排查并记录日志

	客户端开启多线程，提高批量任务的执行效率，并使用反射机制获取执行任务方法，实现完全解耦合
