# Name the components on this agent
###############################
a1.sources = r1
a1.channels = c1 c2
a1.sinks = k1 k2

# Define / Configure Source (multiport seems to support newer "stuff")
###############################
# Exec Source
a1.sources.r1.type = exec
a1.sources.r1.channels = c1 c2
a1.sources.r1.shell = /bin/bash -c
a1.sources.r1.command = /home/arshaq/flume/catdata.sh

# Channels
###############################
a1.channels.c1.type = memory
a1.channels.c1.capacity = 20000
a1.channels.c1.transactionCapacity = 1000

a1.channels.c2.type = memory
a1.channels.c2.capacity = 20000
a1.channels.c2.transactionCapacity = 1000

# (Aplha) HDFS File Sink
###############################
a1.sinks.k1.channel = c1
a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.callTimeout = 180000
a1.sinks.k1.hdfs.fileType = DataStream
a1.sinks.k1.hdfs.filePrefix = flumetest
a1.sinks.k1.hdfs.fileSuffix = .txt
a1.sinks.k1.hdfs.inUseSuffix = .tmp
a1.sinks.k1.hdfs.useLocalTimeStamp = true
a1.sinks.k1.hdfs.path = hdfs:///user/arshaq/input22/%Y%m%d%H%M
a1.sinks.k1.hdfs.writeFormat = Text
# Number of seconds to wait before rolling current file (in seconds)
# Wait for N seconds
a1.sinks.k1.hdfs.rollInterval =5 
# Number of rows to trigger roll
a1.sinks.k1.hdfs.rollCount = 100


#####KAFKA########
 a1.sinks.k2.type = org.apache.flume.sink.kafka.KafkaSink
 a1.sinks.k2.topic = k2 
 a1.sinks.k2.brokerList = localhost:9092
 a1.sinks.k2.channel = c2
 a1.sinks.k2.batchSize = 20
 a1.sinks.k2.batchDurationMillis=5000

## TERMINA AGENTE
