### 配置主从

配置文件

redis6379.conf

redis6380.conf



启动redis

```
redis-server redis6379.conf
```

```
redis-server redis6380.conf
```

设置主从

```slaveof 127.0.0.1 6379
slaveof 127.0.0.1 6379
```

成功

```bash
17763:M 25 Jul 2021 16:12:05.588 * Replica 127.0.0.1:6380 asks for synchronization
17763:M 25 Jul 2021 16:12:05.588 * Partial resynchronization not accepted: Replication ID mismatch (Replica asked for '6a42706c5442bbd4b1af47da9cc31df3241f62ee', my replication IDs are '98179da1a951fc43e8a9ee44782496fe765f6028' and '0000000000000000000000000000000000000000')
17763:M 25 Jul 2021 16:12:05.588 * Replication backlog created, my new replication IDs are '93a56c31c8e6d240167b7a9753282b26415243c5' and '0000000000000000000000000000000000000000'
17763:M 25 Jul 2021 16:12:05.588 * Starting BGSAVE for SYNC with target: disk
17763:M 25 Jul 2021 16:12:05.589 * Background saving started by pid 22806
22806:C 25 Jul 2021 16:12:05.591 * DB saved on disk
17763:M 25 Jul 2021 16:12:05.658 * Background saving terminated with success
17763:M 25 Jul 2021 16:12:05.658 * Synchronization with replica 127.0.0.1:6380 succeeded
```

#### sentinel配置

启动

```
redis-sentinel  sentinel0.conf
redis-sentinel  sentinel1.conf
```

断开主库,自动切换成功

```bash
27790:S 25 Jul 2021 17:24:24.504 # Error condition on socket for SYNC: Connection refused
27790:S 25 Jul 2021 17:24:25.551 * Connecting to MASTER 127.0.0.1:6379
27790:S 25 Jul 2021 17:24:25.551 * MASTER <-> REPLICA sync started
27790:S 25 Jul 2021 17:24:25.551 # Error condition on socket for SYNC: Connection refused
27790:S 25 Jul 2021 17:24:26.584 * Connecting to MASTER 127.0.0.1:6379
27790:S 25 Jul 2021 17:24:26.584 * MASTER <-> REPLICA sync started
27790:S 25 Jul 2021 17:24:26.584 # Error condition on socket for SYNC: Connection refused
27790:M 25 Jul 2021 17:24:27.069 * Discarding previously cached master state.
27790:M 25 Jul 2021 17:24:27.069 # Setting secondary replication ID to 8afcabeea88b6362034bf1e00d88f81c344e7730, valid up to offset: 3153. New replication ID is fcf656177a094415780175d92594ac3c552c89b3
27790:M 25 Jul 2021 17:24:27.069 * MASTER MODE enabled (user request from 'id=8 addr=127.0.0.1:62191 laddr=127.0.0.1:6380 fd=12 name=sentinel-8d992c54-cmd age=30 idle=1 flags=x db=0 sub=0 psub=0 multi=4 qbuf=188 qbuf-free=44862 argv-mem=4 obl=45 oll=0 omem=0 tot-mem=62500 events=r cmd=exec user=default redir=-1')
27790:M 25 Jul 2021 17:24:27.073 # CONFIG REWRITE executed with success.

```

