###single-file-channel

！[test-with-rolling](apache-flume-1.7.0-bin/myconf/test-singlefilechannel.bash)


```
time: 4008, props:{"num":100000, "batchSize":500, "singleDataSize":100} 10m
time: 122916, props:{"num":100000, "batchSize":10, "singleDataSize":1000, "totalBytes":100000000}
time: 28183, props:{"num":100000, "batchSize":100, "singleDataSize":1000 },
time: 19278, props:{"num":100000, "batchSize":200, "singleDataSize":1000, "totalMB":100.0}
time: 16886, props:{"num":100000, "batchSize":300, "singleDataSize":1000, "totalMB":100.00}   
time: 19000 props:{"num":100000, "batchSize":500, "singleDataSize":1000}
time: 18438  props:{"num":100000, "batchSize":1000, "singleDataSize":1000 }
```

#### conclusion
 b>=300 网路因素可以忽略不计

### memory-only test

### with-rollingfile
！[test-with-rollingfile](apache-flume-1.7.0-bin/myconf/test-with-rollingfile.bash)

```
{"time":1624,"fail":0,"num":100000,"batchSize":300,"singleDataSize":1000,"totalMB":100.0}
{"time":3401,"fail":0,"num":100000,"batchSize":300,"singleDataSize":10000,"totalMB":1000.0}
```