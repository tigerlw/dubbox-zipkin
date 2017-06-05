package com.jim.framework.dubbo.core.trace;

import com.github.kristofa.brave.AbstractSpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;
import com.jim.framework.dubbo.core.context.TraceContext;
import com.jim.framework.dubbo.core.trace.collector.HttpCollector;
import com.jim.framework.dubbo.core.trace.collector.KafkaCollector;
import com.jim.framework.dubbo.core.trace.collector.SimpleMetricsHandler;
import com.twitter.zipkin.gen.Span;
import com.twitter.zipkin.gen.SpanCodec;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
* 日志追踪代理器
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
public class TraceAgent {
    private final AbstractSpanCollector collector;

    private final int THREAD_POOL_COUNT=5;
    
    private static TraceAgent traceAgent;
    
    private static Lock lock = new ReentrantLock();
    
    public static TraceAgent builder()
    {
    	if(traceAgent == null)
    	{
    		synchronized(lock)
    		{
    		   if(traceAgent == null)
    		   {
    		     traceAgent = new TraceAgent();
    		   }
    		}
    	}
    	
    	return traceAgent;
    }

    private final ExecutorService executor =
            Executors.newFixedThreadPool(this.THREAD_POOL_COUNT, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread worker = new Thread(r);
                    worker.setName("TRACE-AGENT-WORKER");
                    worker.setDaemon(true);
                    return worker;
                }
            });

    public TraceAgent() {

        SpanCollectorMetricsHandler metrics = new SimpleMetricsHandler();

        //collector = HttpCollector.create(server, TraceContext.getTraceConfig(), metrics);
        collector = new KafkaCollector(SpanCodec.JSON,metrics,TraceContext.getTraceConfig().getFlushInterval());
    }

    public void send(final List<Span> spans){
        if (spans != null && !spans.isEmpty()){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (Span span : spans){
                        collector.collect(span);
                    }
                    collector.flush();
                }
            });
        }
    }
}
