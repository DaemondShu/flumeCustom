

# Name the components on this agent
a1.sources = r1
a1.sinks = k1 k2
a1.channels = mem1 file1

# Describe/configure the source
a1.sources.r1.type = org.apache.flume.source.MyAvroSource
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 44444
a1.sources.r1.channels = mem1 file1
a1.sources.r1.selector.type = org.apache.flume.channel.MyChannelSelector
a1.sources.r1.selector.channels = mem1 file1


# Describe the sink 有问题，不能模拟单点瓶颈
a1.sinks.k1.type = org.apache.flume.sink.FakeRollingFileSink
a1.sinks.k1.channel = mem1
a1.sinks.k1.sink.bpms = 100000
a1.sinks.k1.sink.directory = rollSinkData
a1.sinks.k1.sink.pathManager.prefix = mem1-
#a1.sinks.k1.batchSize = 10
a1.sinks.k1.sink.rollInterval = 0


a1.sinks.k2.type = org.apache.flume.sink.FakeRollingFileSink
a1.sinks.k2.channel = file1
a1.sinks.k2.sink.bpms = 100000
a1.sinks.k2.sink.directory = rollSinkData
a1.sinks.k2.sink.pathManager.prefix = file1-
#a1.sinks.k2.batchSize = 10
a1.sinks.k2.sink.rollInterval = 0

#a1.sinks.k2.type = null
#a1.sinks.k2.channel = file1

# channel selector

#a1.sources.r1.selector.file_channels = file1

# Use a channel which buffers events in memory  http://flume.apache.org/FlumeUserGuide.html#memory-channel
a1.channels.mem1.type = org.apache.flume.channel.MyMemoryChannel
a1.channels.mem1.capacity = 10000
a1.channels.mem1.transactionCapacity = 500
a1.channels.mem1.byteCapacitySlotSize = 100


a1.channels.file1.type = file
a1.channels.file1.checkpointDir = fileChannel/checkpoint
a1.channels.file1.dataDirs = fileChannel/data
a1.channels.file1.checkpointInterval = 10000000

# Bind the source and sink to the channel


