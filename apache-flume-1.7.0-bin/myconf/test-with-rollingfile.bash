

# Name the components on this agent
a1.sources = r1
a1.sinks = k1 k2
a1.channels = mem1 file1

# Describe/configure the source
a1.sources.r1.type = org.apache.flume.source.MyAvroSource
a1.sources.r1.bind = localhost
a1.sources.r1.port = 44444
a1.sources.r1.channels = mem1 file1
a1.sources.r1.selector.type = org.apache.flume.channel.MyChannelSelector
a1.sources.r1.selector.channels = mem1 file1


# Describe the sink
a1.sinks.k1.type = file_roll
a1.sinks.k1.channel = mem1
a1.sinks.k1.sink.directory = rollSinkData
a1.sinks.k1.sink.pathManager.prefix = mem1-
#a1.sinks.k1.batchSize = 10
a1.sinks.k1.sink.rollInterval = 0
#a1.sinks.k1.type = null
#a1.sinks.k1.channel = mem1
#a1.sinks.k1.batchSize = 1

a1.sinks.k2.type = null
a1.sinks.k2.channel = file1

# channel selector

#a1.sources.r1.selector.file_channels = file1

# Use a channel which buffers events in memory  http://flume.apache.org/FlumeUserGuide.html#memory-channel
a1.channels.mem1.type = org.apache.flume.channel.MyMemoryChannel
a1.channels.mem1.capacity = 1000
a1.channels.mem1.transactionCapacity = 500
a1.channels.mem1.byteCapacitySlotSize = 100


a1.channels.file1.type = file
a1.channels.file1.checkpointDir = fileChannel/checkpoint
a1.channels.file1.dataDirs = fileChannel/data

# Bind the source and sink to the channel


