# canal-kafka-elasticsearch

#### 介绍
canal-kafka-elasticsearch
canal的原理是伪装自己为一个MySQL Slave服务。

接收MySQL dump协议请求的binary log日志到canal(slave),canal通过Google 的protobuf对象序列化byte[]。

Binlog Event深度解析Insert/Update/Delete,
生成CanalEntry后给到Canal处理。

首先看下binlog张啥样？
>hxr-mac:mysql houxiurong$ sudo /usr/local/mysql/bin/mysqlbinlog --no-defaults /usr/local/mysql/data/mysql-bin.000002

```bash
#190730 11:36:35 server id 1  end_log_pos 44399 CRC32 0x5e6dce21 	Query	thread_id=149	exec_time=0	error_code=0
SET TIMESTAMP=1564457795/*!*/;
BEGIN
/*!*/;
# at 44399
#190730 11:36:35 server id 1  end_log_pos 44487 CRC32 0x6d931387 	Table_map: `yibao_health`.`yb_patient_doctor_relation` mapped to number 1884
# at 44487
#190730 11:36:35 server id 1  end_log_pos 44593 CRC32 0x83226026 	Update_rows: table id 1884 flags: STMT_END_F

BINLOG '
Q7s/XRMBAAAAWAAAAMetAAAAAFwHAAAAAAEADHlpYmFvX2hlYWx0aAAaeWJfcGF0aWVudF9kb2N0
b3JfcmVsYXRpb24ACQMBEhIDAwMDAQIAAAAAhxOTbQ==
Q7s/XR8BAAAAagAAADGuAAAAAFwHAAAAAAEAAgAJ/////wD+XwAAAACZo7bsT5mjvLkBAgAAAAIA
AABkAAAAFAAAAAEA/l8AAAAAmaO27E+Zo7y5IwIAAAACAAAAZAAAABQAAAACJmAigw==
'/*!*/;
# at 44593
#190730 11:36:35 server id 1  end_log_pos 44624 CRC32 0x4c56fb11 	Xid = 16295
COMMIT/*!*/;
# at 44624
#190730 14:10:30 server id 1  end_log_pos 44689 CRC32 0xc384c38e 	Anonymous_GTID	last_committed=55	sequence_number=56
SET @@SESSION.GTID_NEXT= 'ANONYMOUS'/*!*/;
# at 44689
#190730 14:10:30 server id 1  end_log_pos 44777 CRC32 0x1e5b1a46 	Query	thread_id=149	exec_time=0	error_code=0
SET TIMESTAMP=1564467030/*!*/;
BEGIN
/*!*/;
# at 44777
#190730 14:10:30 server id 1  end_log_pos 44865 CRC32 0x74c3ff8b 	Table_map: `yibao_health`.`yb_patient_doctor_relation` mapped to number 1884
# at 44865
#190730 14:10:30 server id 1  end_log_pos 44971 CRC32 0x56f85ee5 	Update_rows: table id 1884 flags: STMT_END_F

BINLOG '
Vt8/XRMBAAAAWAAAAEGvAAAAAFwHAAAAAAEADHlpYmFvX2hlYWx0aAAaeWJfcGF0aWVudF9kb2N0
b3JfcmVsYXRpb24ACQMBEhIDAwMDAQIAAAAAi//DdA==
Vt8/XR8BAAAAagAAAKuvAAAAAFwHAAAAAAEAAgAJ/////wD+XgAAAACZo1kO2JmjttQ7AgAAAAIA
AAAkAAAADQAAAAIA/l4AAAAAmaNZDtiZo7zingIAAAACAAAAJAAAAA0AAAAD5V74Vg==
'/*!*/;
# at 44971
#190730 14:10:30 server id 1  end_log_pos 45002 CRC32 0xbb61951f 	Xid = 16602
COMMIT/*!*/;
SET @@SESSION.GTID_NEXT= 'AUTOMATIC' /* added by mysqlbinlog */ /*!*/;
DELIMITER ;
# End of log file
/*!50003 SET COMPLETION_TYPE=@OLD_COMPLETION_TYPE*/;
/*!50530 SET @@SESSION.PSEUDO_SLAVE_MODE=0*/;
```
##### BinlogParser过程:
Canal解析过程:

###### Binlog接收 --> Binlog Event解析 --> Insert/Update/Delete深度解析 --> 生成CanalEntry ==>Canal处理

#### 基础功能说明
canal-kafka-elasticsearch是基于阿里的Canal监控数据变化解析数据后发送到kafka,

kafka消费数据到Elasticsearch的过程，实现Elasticsearch的近实时数查询需求。

>Note：kafka消费者提前添加topicEnum到System.setProperty,SpEL语言解析。

>其实canal里面也有解析topic的方式,动态topic加载(MQMessageUtils.messageTopics).

1.mysql Binlog解析过程:
MysqlEventParser ---> new SlaveEntryPosition(binlog, Long.valueOf(position), masterHost, masterPort);

最后解析为: CanalEntry.Entry

2.Kafka动态配置topic配置文件:
>MQMessageUtils ---> 
```
 public static boolean matchDynamicTopic(String name, String dynamicTopicConfigs) {
        if (StringUtils.isEmpty(dynamicTopicConfigs)) {
            return false;
        }
        boolean res = false;
        List<DynamicTopicData> datas = dynamicTopicDatas.get(dynamicTopicConfigs);
        for (DynamicTopicData data : datas) {
            if (data.simpleName != null) {
                if (data.simpleName.equalsIgnoreCase(name)) {
                    res = true;
                    break;
                }
            } else if (name.contains(".")) {
                if (data.tableRegexFilter != null && data.tableRegexFilter.filter(name)) {
                    res = true;
                    break;
                }
            } else {
                if (data.schemaRegexFilter != null && data.schemaRegexFilter.filter(name)) {
                    res = true;
                    break;
                }
            }
        }
        return res;
    }
```

#### 安装教程

##### 1. 要启动的项目，需要先安装阿里的canal服务。
[canal安装教程](https://github.com/alibaba/canal/wiki/Canal-Kafka-RocketMQ-QuickStart)
> 本人安装mac本地:canal.deployer-1.1.3.tar.gz
> 首先下载canal的最新[release](https://github.com/alibaba/canal/releases)版本。

canal.deployer-1.1.3.tar.gz

> 1.安装路径: meApp/canal位置。

> 2.下面是配置canal的meApp/canal/conf/路径下,$vim canal.properties
```bash
#canal.manager.jdbc.password=121212
canal.id = 2
canal.ip =
canal.port = 11111
canal.metrics.pull.port = 11112
canal.zkServers =
# flush data to zk
canal.zookeeper.flush.period = 1000
canal.withoutNetty = false
# tcp, kafka, RocketMQ
canal.serverMode = kafka
# flush meta cursor/parse position to file
canal.file.data.dir = ${canal.conf.dir}
canal.file.flush.period = 1000
## memory store RingBuffer size, should be Math.pow(2,n)
canal.instance.memory.buffer.size = 16384
## memory store RingBuffer used memory unit size , default 1kb
canal.instance.memory.buffer.memunit = 1024
## meory store gets mode used MEMSIZE or ITEMSIZE
canal.instance.memory.batch.mode = MEMSIZE
canal.instance.memory.rawEntry = true

## detecing config
canal.instance.detecting.enable = false
#canal.instance.detecting.sql = insert into retl.xdual values(1,now()) on duplicate key update x=now()
canal.instance.detecting.sql = select 1
canal.instance.detecting.interval.time = 3
canal.instance.detecting.retry.threshold = 3
canal.instance.detecting.heartbeatHaEnable = false

# support maximum transaction size, more than the size of the transaction will be cut into multiple transactions delivery
canal.instance.transaction.size =  1024
# mysql fallback connected to new master should fallback times
canal.instance.fallbackIntervalInSeconds = 60
# network config
canal.instance.network.receiveBufferSize = 16384
canal.instance.network.sendBufferSize = 16384
canal.instance.network.soTimeout = 30

# binlog filter config
canal.instance.filter.druid.ddl = true
canal.instance.filter.query.dcl = false
canal.instance.filter.query.dml = false
canal.instance.filter.query.ddl = false
canal.instance.filter.table.error = false
canal.instance.filter.rows = false
canal.instance.filter.transaction.entry = false

# binlog format/image check
canal.instance.binlog.format = ROW,STATEMENT,MIXED
canal.instance.binlog.image = FULL,MINIMAL,NOBLOB

# binlog ddl isolation
canal.instance.get.ddl.isolation = false

# parallel parser config
canal.instance.parser.parallel = true
## concurrent thread number, default 60% available processors, suggest not to exceed Runtime.getRuntime().availableProcessors()
canal.instance.parser.parallelThreadSize = 16
## disruptor ringbuffer size, must be power of 2
canal.instance.parser.parallelBufferSize = 256

# table meta tsdb info
canal.instance.tsdb.enable = true
canal.instance.tsdb.dir = ${canal.file.data.dir:../conf}/${canal.instance.destination:}
canal.instance.tsdb.url = jdbc:h2:${canal.instance.tsdb.dir}/h2;CACHE_SIZE=1000;MODE=MYSQL;
canal.instance.tsdb.dbUsername = canal
canal.instance.tsdb.dbPassword = canal
# dump snapshot interval, default 24 hour
canal.instance.tsdb.snapshot.interval = 24
# purge snapshot expire , default 360 hour(15 days)
canal.instance.tsdb.snapshot.expire = 360
# aliyun ak/sk , support rds/mq
canal.aliyun.accessKey =
canal.aliyun.secretKey =

#################################################
#########               destinations            #############
#################################################
canal.destinations = example
# conf root dir
canal.conf.dir = ../conf
# auto scan instance dir add/remove and start/stop instance
canal.auto.scan = true
canal.auto.scan.interval = 5

canal.instance.tsdb.spring.xml = classpath:spring/tsdb/h2-tsdb.xml
#canal.instance.tsdb.spring.xml = classpath:spring/tsdb/mysql-tsdb.xml

canal.instance.global.mode = spring
canal.instance.global.lazy = false
#canal.instance.global.manager.address = 127.0.0.1:1099
#canal.instance.global.spring.xml = classpath:spring/memory-instance.xml
canal.instance.global.spring.xml = classpath:spring/file-instance.xml
#canal.instance.global.spring.xml = classpath:spring/default-instance.xml

##################################################
#########                    MQ                      #############
##################################################
canal.mq.servers = 127.0.0.1:9092
canal.mq.retries = 0
canal.mq.batchSize = 16384
canal.mq.maxRequestSize = 1048576
canal.mq.lingerMs = 100
canal.mq.bufferMemory = 33554432
canal.mq.canalBatchSize = 50
canal.mq.canalGetTimeout = 100
canal.mq.flatMessage = true
canal.mq.compressionType = none
canal.mq.acks = all
# use transaction for kafka flatMessage batch produce
canal.mq.transaction = false
#canal.mq.properties. =
```
canal example配置:app/canal/conf/example
```bash
#################################################
## mysql serverId , v1.0.26+ will autoGen
canal.instance.mysql.slaveId=2

# enable gtid use true/false
canal.instance.gtidon=false

# position info
canal.instance.master.address=127.0.0.1:3306
canal.instance.master.journal.name=
canal.instance.master.position=
canal.instance.master.timestamp=
canal.instance.master.gtid=

# rds oss binlog
canal.instance.rds.accesskey=
canal.instance.rds.secretkey=
canal.instance.rds.instanceId=

# table meta tsdb info
canal.instance.tsdb.enable=true
#canal.instance.tsdb.url=jdbc:mysql://127.0.0.1:3306/canal_tsdb
#canal.instance.tsdb.dbUsername=canal
#canal.instance.tsdb.dbPassword=canal

#canal.instance.standby.address =
#canal.instance.standby.journal.name =
#canal.instance.standby.position =
#canal.instance.standby.timestamp =
#canal.instance.standby.gtid=

# username/password
canal.instance.dbUsername=canal
canal.instance.dbPassword=canal
canal.instance.connectionCharset = UTF-8
# enable druid Decrypt database password
canal.instance.enableDruid=false
#canal.instance.pwdPublicKey=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALK4BUxdDltRRE5/zXpVEVPUgunvscYFtEip3pmLlhrWpacX7y7GCMo2/JM6LeHmiiNdH1FWgGCpUfircSwlWKUCAwEAAQ==
# table regex
#canal.instance.filter.regex=.*\\..*
canal.instance.filter.regex=yibao_health\\.yb_patient_doctor_relation,yibao_health\\.yb_patient_scan_log
#多表用逗号分开
#canal.instance.filter.regex=yibao_health\\.yb_patient_doctor_relation,yibao_health\\.yb_patient_scan_log,yibao_health\\.yb_doctor_patient_grouping_instance
# table black regex
canal.instance.filter.black.regex=

# mq config
#canal.mq.topic=example
# dynamic topic route by schema or table regex
#canal.mq.dynamicTopic=mytest1.user,mytest2\\..*,.*\\..*
canal.mq.dynamicTopic=yibao_health.yb_patient_doctor_relation
#canal动态生成topic,一个表对应一个topic,多个用逗号分开
#canal.mq.dynamicTopic=yibao_health.yb_patient_doctor_relation,yibao_health.yb_patient_scan_log
canal.mq.partition=0
# hash partition config
#canal.mq.partitionsNum=3
#canal.mq.partitionHash=test.table:id^name,.*\\..*
#################################################
```
以上配置完毕后需要启动canal-server,启动命令如下:本人安装canal-server路径:meApp/canal
> 1.启动
```bash
cd meApp/canal/
sh bin/startup.sh
```
> 2.停止
```bash
cd meApp/canal/
sh bin/stop.sh
```
> 3.查看 logs/canal/canal.log
```bash
vi logs/canal/canal.log
```
> 4.查看instance的日志：
```bash
vi logs/example/example.log
```



##### 2.配置MySQL开启binlog模式
如果是mac本地MySQL数据库,还需要开启mysql的binlog模式,因为mac安装的MySQL 5.7 没有my.cnf文件,
需要配置MySQL binlog开启状态。具体如下:
```bash
# Example MySQL config file for medium systems.
#
# This is for a system with little memory (32M - 64M) where MySQL plays
# an important part, or systems up to 128M where MySQL is used together with
# other programs (such as a web server)
#
# MySQL programs look for option files in a set of
# locations which depend on the deployment platform.
# You can copy this option file to one of those
# locations. For information about these locations, see:
# http://dev.mysql.com/doc/mysql/en/option-files.html
#
# In this file, you can use all long options that a program supports.
# If you want to know which options a program supports, run the program
# with the "--help" option.
# The following options will be passed to all MySQL clients
[client]
default-character-set=utf8
#password   = your_password
port        = 3306
socket      = /tmp/mysql.sock
# Here follows entries for some specific programs
# The MySQL server

[mysqld]
character-set-server=utf8
port        = 3306
socket      = /tmp/mysql.sock
skip-external-locking
key_buffer_size = 16M
max_allowed_packet = 1M
table_open_cache = 64
sort_buffer_size = 512K
net_buffer_length = 8K
read_buffer_size = 256K
read_rnd_buffer_size = 512K
myisam_sort_buffer_size = 8M
character-set-server=utf8
init_connect='SET NAMES utf8'

# Don't listen on a TCP/IP port at all. This can be a security enhancement,
# if all processes that need to connect to mysqld run on the same host.
# All interaction with mysqld must be made via Unix sockets or named pipes.
# Note that using this option without enabling named pipes on Windows
# (via the "enable-named-pipe" option) will render mysqld useless!
#
#skip-networking

# Replication Master Server (default)
# binary logging is required for replication
log-bin=mysql-bin

# binary logging format - mixed recommended ROW
binlog_format=ROW

# required unique id between 1 and 2^32 - 1
# defaults to 1 if master-host is not set
# but will not function as a master if omitted
server-id   = 1

# Replication Slave (comment out master section to use this)
#
# To configure this host as a replication slave, you can choose between
# two methods :
#
# 1) Use the CHANGE MASTER TO command (fully described in our manual) -
#    the syntax is:
#
#    CHANGE MASTER TO MASTER_HOST=<host>, MASTER_PORT=<port>,
#    MASTER_USER=<user>, MASTER_PASSWORD=<password> ;
#
#    where you replace <host>, <user>, <password> by quoted strings and
#    <port> by the master's port number (3306 by default).
#
#    Example:
#
#    CHANGE MASTER TO MASTER_HOST='125.564.12.1', MASTER_PORT=3306,
#    MASTER_USER='joe', MASTER_PASSWORD='secret';

# OR
#
# 2) Set the variables below. However, in case you choose this method, then
#    start replication for the first time (even unsuccessfully, for example
#    if you mistyped the password in master-password and the slave fails to
#    connect), the slave will create a master.info file, and any later
#    change in this file to the variables' values below will be ignored and
#    overridden by the content of the master.info file, unless you shutdown
#    the slave server, delete master.info and restart the slaver server.
#    For that reason, you may want to leave the lines below untouched
#    (commented) and instead use CHANGE MASTER TO (see above)
#
# required unique id between 2 and 2^32 - 1
# (and different from the master)
# defaults to 2 if master-host is set
# but will not function as a slave if omitted
#server-id       = 2
#
# The replication master for this slave - required
#master-host     =   <hostname>
#
# The username the slave will use for authentication when connecting
# to the master - required
#master-user     =   <username>
#
# The password the slave will authenticate with when connecting to
# the master - required
#master-password =   <password>
#
# The port the master is listening on.
# optional - defaults to 3306
#master-port     =  <port>
#
# binary logging - not required for slaves, but recommended
#log-bin=mysql-bin

# Uncomment the following if you are using InnoDB tables
#innodb_data_home_dir = /usr/local/mysql/data
#innodb_data_file_path = ibdata1:10M:autoextend
#innodb_log_group_home_dir = /usr/local/mysql/data
# You can set .._buffer_pool_size up to 50 - 80 %
# of RAM but beware of setting memory usage too high
#innodb_buffer_pool_size = 16M
#innodb_additional_mem_pool_size = 2M
# Set .._log_file_size to 25 % of buffer pool size
#innodb_log_file_size = 5M
#innodb_log_buffer_size = 8M
#innodb_flush_log_at_trx_commit = 1
#innodb_lock_wait_timeout = 50

[mysqldump]
quick
max_allowed_packet = 16M

[mysql]
no-auto-rehash
# Remove the next comment character if you are not familiar with SQL
#safe-updates
default-character-set=utf8

[myisamchk]
key_buffer_size = 20M
sort_buffer_size = 20M
read_buffer = 2M
write_buffer = 2M

[mysqlhotcopy]
interactive-timeout
```
然后重启MySQL数据库:
重启命令如下:
a.启动MySQL服务
>sudo /usr/local/MySQL/support-files/mysql.server start

b.停止MySQL服务
>sudo /usr/local/mysql/support-files/mysql.server stop

c.重启MySQL服务
>sudo /usr/local/mysql/support-files/mysql.server restart


##### 3. 安装kafka
1.下载kafka_2.12-2.2.0.tg安装包，解压缩kafka_2.12-2.2.0,我的本地路径:
$pwd
meApp/kafka_2.12-2.2.0

2.启动kafka需要安装zookeeper服务，如果没有安装启动zookeeper，则kafka自带的命令启动一个zookeeper.
进入kafka路径下:
a.启动zookeeper：
> bin/zookeeper-server-start.sh config/zookeeper.properties

b.启动kafka服务：
> bin/kafka-server-start.sh config/server.properties

c.创建一个topic
> bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test-topic

d.查看启动的kafka topic list.
> ./bin/kafka-topics.sh --zookeeper 192.168.1.114:2181 --list

##### 4.安装zookeeper
传送门:[zookeeper安装](https://www.jianshu.com/p/5491d16e6abd)
 
##### 5. 安装Elasticsearch
传送门:[Mac install elasticsearch](https://linux.cn/article-11125-1.html)
a.Elasticsearch官网下载安装包elasticsearch-6.5.0.tar.gz,[release](https://www.elastic.co/downloads/elasticsearch)

b.解压缩安装包到安装路径:meApp/elasticsearch-6.5.0
> tar -xzvf elasticsearch-6.5.0.tar.gz

c.进入elasticsearch-6.5.0路径下,启动
> ./bin/elasticsearch 
```bash
[node1] publish_address {192.168.2.102:9300}, bound_addresses {[::]:9300}
```
如上则Elasticsearch启动ok

d.自定义配置Elasticsearch:进入meApp/elasticsearch-6.5.0 config路径
> $ vim elasticsearch.yml

```bash
#
cluster.name: houxiurong
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#
node.name: node1
#
# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
#
network.host: 0.0.0.0
#
# Set a custom port for HTTP:
#
http.port: 9200
```
e.浏览器输入:http://localhost:9200 
```bash
{
  "name" : "node1",
  "cluster_name" : "houxiurong",
  "cluster_uuid" : "1KbiVT1LTxqXMmksehEPeg",
  "version" : {
    "number" : "6.5.0",
    "build_flavor" : "default",
    "build_type" : "tar",
    "build_hash" : "816e6f6",
    "build_date" : "2018-11-09T18:58:36.352602Z",
    "build_snapshot" : false,
    "lucene_version" : "7.5.0",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
```

#### 使用说明

1. clone 项目到本地
2. 修改application.properties配置
3. 启动spring-boot主入口 main方法。
4. 使用MySQL客户端工具数据库表添加几条数据，查看es情况。查看Elasticsearch数据可以按照Elasticsearch-head Chrome浏览器插件。
5. 本示例中需要的表:
```sql
CREATE TABLE `yb_patient_doctor_relation` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `is_deleted` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否删除（0:未删除 1:已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录修改时间',
  `creator` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建人,0表示无创建人值',
  `modifier` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '修改人,如果为0则表示纪录未修改',
  `patient_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '患者ID',
  `doctor_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '医生ID',
  `source_type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '关系来源（1:扫码关注 2:服务订单）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_patient_doctor_id` (`patient_id`,`doctor_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者和医生关联表';
```
其他的表暂时不提供。
#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

> Canal-kafka-FlatMessage:
```
{
    "data":[
        {
            "id":"121",
            "is_deleted":"0",
            "create_time":"2019-07-26 16:14:28",
            "modify_time":"2019-07-26 16:14:28",
            "creator":"2",
            "modifier":"3",
            "patient_id":"103",
            "doctor_id":"13",
            "source_type":"1"
        }
    ],
    "database":"yibao_health",
    "es":1564128868000,
    "id":650,
    "isDdl":false,
    "mysqlType":{
        "id":"int(11) unsigned",
        "is_deleted":"tinyint(3) unsigned",
        "create_time":"datetime",
        "modify_time":"datetime",
        "creator":"int(11) unsigned",
        "modifier":"int(11) unsigned",
        "patient_id":"int(11) unsigned",
        "doctor_id":"int(11) unsigned",
        "source_type":"tinyint(3) unsigned"
    },
    "old":null,
    "pkNames":[
        "id"
    ],
    "sql":"",
    "sqlType":{
        "id":4,
        "is_deleted":-6,
        "create_time":93,
        "modify_time":93,
        "creator":4,
        "modifier":4,
        "patient_id":4,
        "doctor_id":4,
        "source_type":-6
    },
    "table":"yb_patient_doctor_relation",
    "ts":1564128869047,
    "type":"INSERT"
}
```

```
{
    "data":[
        {
            "id":"117",
            "is_deleted":"0",
            "create_time":"2019-07-17 15:50:55",
            "modify_time":"2019-07-26 18:10:09",
            "creator":"0",
            "modifier":"0",
            "patient_id":"2",
            "source_type":"1",
            "source_user_id":"1",
            "record_id":"231",
            "advice_type":"8",
            "advice_sub_type":"0",
            "advice_desc":"医生叮嘱：dasdas 313 33333",
            "visit_time_status":"0",
            "visit_time":"2019-07-17 15:50:55",
            "hospital_id":"0",
            "drug_id":"67",
            "drug_user_method":"3",
            "prescribed_dose":"33.0",
            "prescribed_dose_unit":"粒",
            "single_dose":"333.0",
            "single_dose_unit":"包/次",
            "use_frequency":"33.0",
            "execute_status":"10",
            "execute_reason":"33123123"
        }
    ],
    "database":"yibao_health",
    "es":1564135809000,
    "id":711,
    "isDdl":false,
    "mysqlType":{
        "id":"int(11) unsigned",
        "is_deleted":"tinyint(3) unsigned",
        "create_time":"datetime",
        "modify_time":"datetime",
        "creator":"int(11) unsigned",
        "modifier":"int(11) unsigned",
        "patient_id":"int(11) unsigned",
        "source_type":"tinyint(3) unsigned",
        "source_user_id":"int(10) unsigned",
        "record_id":"int(11) unsigned",
        "advice_type":"tinyint(3) unsigned",
        "advice_sub_type":"int(11) unsigned",
        "advice_desc":"varchar(1024)",
        "visit_time_status":"tinyint(3) unsigned",
        "visit_time":"datetime",
        "hospital_id":"int(11) unsigned",
        "drug_id":"int(11) unsigned",
        "drug_user_method":"tinyint(4)",
        "prescribed_dose":"decimal(6,2)",
        "prescribed_dose_unit":"varchar(10)",
        "single_dose":"decimal(6,2)",
        "single_dose_unit":"varchar(10)",
        "use_frequency":"decimal(6,2)",
        "execute_status":"tinyint(3) unsigned",
        "execute_reason":"varchar(1024)"
    },
    "old":[
        {
            "modify_time":"2019-07-26 18:04:08",
            "visit_time_status":"1"
        }
    ],
    "pkNames":[
        "id"
    ],
    "sql":"",
    "sqlType":{
        "id":4,
        "is_deleted":-6,
        "create_time":93,
        "modify_time":93,
        "creator":4,
        "modifier":4,
        "patient_id":4,
        "source_type":-6,
        "source_user_id":4,
        "record_id":4,
        "advice_type":-6,
        "advice_sub_type":4,
        "advice_desc":12,
        "visit_time_status":-6,
        "visit_time":93,
        "hospital_id":4,
        "drug_id":4,
        "drug_user_method":-6,
        "prescribed_dose":3,
        "prescribed_dose_unit":12,
        "single_dose":3,
        "single_dose_unit":12,
        "use_frequency":3,
        "execute_status":-6,
        "execute_reason":12
    },
    "table":"yb_patient_visit_record_doctor_advice",
    "ts":1564135809169,
    "type":"UPDATE"
}
```
