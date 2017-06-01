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
## true

### singlefile + memory
####true
```$xslt
over: {"time":22337,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}


over: {"time":25460,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
over: {"time":25879,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}


2017-06-01 08:08:42  INFO MyApp:180 - over: {"time":37946,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:08:43  INFO MyApp:180 - over: {"time":39018,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:08:48  INFO MyApp:180 - over: {"time":43360,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:08:50  INFO MyApp:180 - over: {"time":45401,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
占用率
5423700 
7064717700
0.007

2017-06-01 05:49:23  INFO MyApp:177 - over: {"time":79928,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:31  INFO MyApp:177 - over: {"time":88239,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:35  INFO MyApp:177 - over: {"time":91694,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:45  INFO MyApp:177 - over: {"time":102044,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:50  INFO MyApp:177 - over: {"time":106782,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:50  INFO MyApp:177 - over: {"time":106927,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:50  INFO MyApp:177 - over: {"time":107603,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 05:49:52  INFO MyApp:177 - over: {"time":109538,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
16978100
4964483300
```


####fakerolling 50000
```$xslt

2017-06-01 11:08:54  INFO MyApp:180 - done over: {"time":32936,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}

2017-06-01 11:13:19  INFO MyApp:180 - done over: {"time":83576,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}
2017-06-01 11:13:22  INFO MyApp:180 - done over: {"time":82364,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}



2017-06-01 09:53:44  INFO MyApp:180 - over: {"time":173325,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}
2017-06-01 09:53:46  INFO MyApp:180 - over: {"time":175122,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}
2017-06-01 09:53:47  INFO MyApp:180 - over: {"time":177778,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}
2017-06-01 09:53:48  INFO MyApp:180 - over: {"time":177502,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}




```



### single memory

```$xslt
2017-06-01 07:55:40  INFO MyApp:180 - over: {"time":22119,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
connect restart:2


2017-06-01 07:57:32  INFO MyApp:180 - over: {"time":38463,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 07:57:32  INFO MyRpcClientFacade:195 - counts:[21944,17260]connect restart:2
2017-06-01 07:57:32  INFO MyApp:180 - over: {"time":38581,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 07:57:32  INFO MyRpcClientFacade:195 - counts:[22710,15972]connect restart:2
2017-06-01 07:57:32  INFO MyApp:180 - over: {"time":38659,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 07:57:32  INFO MyRpcClientFacade:195 - counts:[21252,17765]connect restart:2
2017-06-01 07:57:33  INFO MyApp:180 - over: {"time":38904,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 07:57:33  INFO MyRpcClientFacade:195 - counts:[22419,16275]connect restart:2

2017-06-01 08:02:58  INFO MyApp:180 - over: {"time":68304,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:58  INFO MyRpcClientFacade:195 - counts:[21295,19906]connect restart:2
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":68739,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[22279,19242]connect restart:2
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":68892,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[20670,19884]connect restart:2
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":69232,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":69469,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[21778,18792]connect restart:2
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[21746,19646]connect restart:2
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":69367,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[21782,19993]connect restart:2
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":69527,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[21714,19639]connect restart:2
2017-06-01 08:02:59  INFO MyApp:180 - over: {"time":69767,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 08:02:59  INFO MyRpcClientFacade:195 - counts:[21227,20173]connect restart:2

```
####fakerolling 50000

```
重连数 3734
2017-06-01 10:32:18  INFO MyApp:180 - over: {"time":29759,"fail":0,"num":10000000,"batchSize":300,
"singleDataSize":100,"totalMB":1000.0}

重连数 14667
2017-06-01 10:32:18  INFO MyApp:180 - over: {"time":80769,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 10:32:18  INFO MyApp:180 - over: {"time":80815,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}

重连数 21333*4
2017-06-01 12:00:20  INFO MyApp:180 - done over: {"time":195118,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:00:22  INFO MyApp:180 - done over: {"time":197128,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:00:22  INFO MyApp:180 - done over: {"time":197267,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:00:22  INFO MyApp:180 - done over: {"time":197419,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}


重连数 22170*8
2017-06-01 12:08:53  INFO MyApp:180 - done over: {"time":373946,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:08:54  INFO MyApp:180 - done over: {"time":374339,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:08:55  INFO MyApp:180 - done over: {"time":375432,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:08:55  INFO MyApp:180 - done over: {"time":375844,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:08:55  INFO MyApp:180 - done over: {"time":376197,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:08:56  INFO MyApp:180 - done over: {"time":376335,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}
2017-06-01 12:08:56  INFO MyApp:180 - done over: {"time":376519,"fail":0,"num":10000000,"batchSize":300,"singleDataSize":100,"totalMB":1000.0}


```


