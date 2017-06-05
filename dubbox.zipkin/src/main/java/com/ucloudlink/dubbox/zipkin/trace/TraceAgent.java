package com.ucloudlink.dubbox.zipkin.trace;

import com.github.kristofa.brave.AbstractSpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;

import com.twitter.zipkin.gen.Span;
import com.twitter.zipkin.gen.SpanCodec;
import com.ucloudlink.dubbox.zipkin.context.TraceContext;
import com.ucloudlink.dubbox.zipkin.trace.collector.KafkaCollector;
import com.ucloudlink.dubbox.zipkin.trace.collector.SimpleMetricsHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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
