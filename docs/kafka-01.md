# Kafka

## Topic
A stream of data, like a table in a database (without the constraints). Identified by a name.

Are a splitted in N partitions. Each one is ordered, and each message gets an incremental id called **offset** (0-indexed).

Data is kept only for a limited amount of time

Once data is written to a partition it **can't be changed**.

If no key is provided, data can be assigned to any of the partitions.


## Brokers
A Kakfa cluster is composed of multiple brokers (AKA servers).

Each broker is identified only by an integer id

Each broker contains certain topic partitions, but not all

After connecting to any broker you will be connected to the entire cluster.

### Example

If I've 3 brokers, and a topic A with 3 partitions and topic B with 2 partitions Kafka will distribuite the partitions almost evenly:

- Broker 1
  - Topic-A-p0
  - Topic-B-p1

- Broker 2
  - Topic-A-p2
  - Topic-B-p0

- Broker 3
  - Topic-A-p1

# Replication
If a broker down another can serve the data:

### Example
If I've a topic A with 2 partitions
- Broker 1
  - Topic-A-p0

- Broker 2
  - Topic-A-p0
  - Topic-A-p1

- Broker 3
  - Topic-A-p1

If I lose any of the 3 brokers the data is still served by at least one broker.

## Leader and ISR
At any time only one broker can be a leader for a partition, only it will recieve requests and add data, the others will only synchronize from the leader (ISR: in-sync replica).

# Producers
The ones who add data to the topics.

The already know to which broker and partition to write to.

## Acknowledgment
Producers can opt to recieve confirmation of data writes.
- acks=0 Producer won't wait for confirmation (possible data loss)
- acks=1 Producer will wait for leader acknowledgment (limited data loss)
- acks=all Leader+ replicas acknowledgment (no data loss)

## Message keys
Producers can send a key with each message. No key means the brokers get chosen by round robin. If a key is used only a specific broker will recive the message.

# Consumers

They read data from a topic (identified by name), and know to which broker ask.

In case of broker failures Consumers know how to recover

The data will be read in order within each partition.

## Consumer groups
Consuymer read data inconsumer grouś

Each consumer within a group reads from exclusive partitions

if #consumers > #partitions some consumers will be inactive. (you should have the same amount)


## Consumer Offsets
kafka knows the offset where a consumer group last read, this is stored per topic.

# Delivery semantics
Consumer choose when to commit offsets:
- At most once: as soon as the message is received -> if something goes wrong the message will be lost (wont be read again)
- At least once: as soon as the message is processed -> if something goes wrong the message will be read again
- Exactly once: Kafka workflows using Kafka Streams API

# Broker Discovery

Each Kafka broker is called a "bootstrap server" -> You only need to connect on onte broker and you will connect to the entire cluster.
Each broker knos about all other brokers, topics and partitions.

---

# Zookeper
It manages the brokers and helps in performing a leader election for partitions, also notifies Kafka about structural changes (topics,. brokers).

**Kafka cant work without Zookeper**

Zookeper works in odd number of servers (1, 3, 5, ...). It has a *leader* (handle writes) and the rest are *followers* (handle reads)

# Start Zookeper
Inside the Kafka app dir create
> /data/zookeper

Edit
> nano config/zookeeper.properties

Change
```
dataDir=/tmp/zookeeper
```
to
```
dataDir=(YOURFULLPATH)/kafka_2.12-2.7.0/data/zookeper
```
(or the folder where you have the app dir)

> bin/zookeeper-server-start.sh config/zookeeper.properties

Create
> mkdir data/kafka

Edit Kafka properties:
> nano config/server.properties

Change
```
log.dirs=/tmp/kafka-logs
```
to
```
log.dirs=(YOURFULLPATH)/kafka_2.12-2.7.0/data/kafka
```

> bin/kafka-server-start.sh config/server.properties

# Topics
## Create a Topic
> kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1

or (deprecated)
> kafka-topics.sh --zookeper localhost:2181 --topic first_topic --create

--replication-factor cant be bigger than the ammount of brokers.

## Topic info
> kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --describe
```
Topic: first_topic	PartitionCount: 3	ReplicationFactor: 1	Configs: segment.bytes=1073741824
	Topic: first_topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
	Topic: first_topic	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
	Topic: first_topic	Partition: 2	Leader: 0	Replicas: 0	Isr: 0
```

- 0 is the Leader Broker

## List topics
> kafka-topics.sh --bootstrap-server localhost:9092 --topic second_topic --create --partitions 6 --replication-factor 1

> kafka-topics.sh --bootstrap-server localhost:9092 --list
```
first_topic
second_topic
```

## Delete topic

> kafka-topics.sh --bootstrap-server localhost:9092 --topic second_topic --delete

# Producer
> kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic

> Hello World!

> This is the end.

> ^C

## Properties
> kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic **--producer-property acks=all**

## Producing to non-existing topic
> kafka-console-producer.sh --bootstrap-server localhost:9092 --topic non_existing_topic

> Hello There
```
[2021-01-04 19:40:03,812] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {non_existing_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
```
> Oops

> ^C

What happened?

> kafka-topics.sh --bootstrap-server localhost:9092 --list
```
first_topic
non_existing_topic
```

Kakfa created the topic! 

> kafka-topics.sh --bootstrap-server localhost:9092 --topic non_existing_topic --describe
```
Topic: non_existing_topic	PartitionCount: 1	ReplicationFactor: 1	Configs: segment.bytes=1073741824
	Topic: non_existing_topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```

It was created with cuestionable configurations... ( 1 partition... )

This default configurations can be changed in config/server.properties

## Consumer

> kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic

Nothing appears, a new consumer will only read new messages (created after the consumer) If with this session open I launch....

> kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic

> Hello There!

> And bye bye

> ^C

The first session will show
```
Hello There!
And bye bye
```

### Read from the beginning

> kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --from-beginning
```
Hello There!
This is the end
Hello World!
And bye bye
```

This will only work once, Kafka remembers the consumer group offset!

## Belong to a group

> kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first_topic --group my-fav-group

If two consumers belong to the same group they won't consume the same messages (pseudo round-robin)

## Consumer group offset

> kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```
console-consumer-21578
my-fav-group
```

If no consumer group is specified one is created (ex console-consumer-21578)

> kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-fav-group
```
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
my-fav-group    first_topic     0          0               0               0               -               -               -
my-fav-group    first_topic     1          1               1               0               -               -               -
my-fav-group    first_topic     2          2               2               0               -               -               -
```

 - LAG -> pending messages to read.

### Reset Consumer group offset

> kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-fav-group --reset-offsets --to-earliest --execute --topic first_topic
```
GROUP                          TOPIC                          PARTITION  NEW-OFFSET     
my-fav-group                   first_topic                    0          0              
my-fav-group                   first_topic                    1          0              
my-fav-group                   first_topic                    2          0     
```

> kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-fav-group
```
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
my-fav-group    first_topic     0          0               0               0               -               -               -
my-fav-group    first_topic     1          0               1               1               -               -               -
my-fav-group    first_topic     2          0               2               2               -               -               -
```

Alternatives
- --to-eartliest
- --shift-by N (N number, move N forward, ex --shift-by -2, go backwards)
(...)


