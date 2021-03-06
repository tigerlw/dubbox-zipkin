package com.jim.framework.dubbo.core.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.google.common.base.Stopwatch;
import com.jim.framework.dubbo.core.context.TraceContext;
import com.jim.framework.dubbo.core.trace.TraceAgent;
import com.jim.framework.dubbo.core.utils.IdUtils;
import com.jim.framework.dubbo.core.utils.NetworkUtils;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.BinaryAnnotation;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;

import zipkin.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
* 服务端日志过滤器
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/13
* 修改日期:2017/4/13
*/
@Activate
public class TraceProviderFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private Span startTrace(Map<String, String> attaches,Invocation invocation) {

        Long traceId = Long.valueOf(attaches.get(TraceContext.TRACE_ID_KEY));
        Long parentSpanId = Long.valueOf(attaches.get(TraceContext.SPAN_ID_KEY));
        
        String csSender = attaches.get(Constants.CLIENT_ADDR);
        

        TraceContext.start();
        TraceContext.setTraceId(traceId);
        TraceContext.setSpanId(parentSpanId);
        
        String servName = TraceContext.getTraceConfig().getApplicationName()+"."
				+ invocation.getInvoker().getInterface().getSimpleName() + "." + invocation.getMethodName();
        TraceContext.setServName(servName);

        Span providerSpan = new Span();

        long id = IdUtils.get();
        providerSpan.setId(id);
        providerSpan.setParent_id(parentSpanId);
        providerSpan.setTrace_id(traceId);
        providerSpan.setName(servName);
        long timestamp = System.currentTimeMillis()*1000;
        providerSpan.setTimestamp(timestamp);

        providerSpan.addToAnnotations(
                Annotation.create(timestamp, TraceContext.ANNO_SR,
                        Endpoint.create(servName,
                                NetworkUtils.ip2Num(NetworkUtils.getSiteIp()),
                                TraceContext.getTraceConfig().getServerPort() )));
        
        Endpoint endpoint = Endpoint.create(csSender, NetworkUtils.ip2Num(NetworkUtils.getSiteIp()), TraceContext.getTraceConfig().getServerPort());
        
        providerSpan.addToBinary_annotations(BinaryAnnotation.create(Constants.CLIENT_ADDR, "true",endpoint));

        TraceContext.addSpan(providerSpan);
        return providerSpan;
    }

    private void endTrace(Span span, Stopwatch watch,String method) {

        span.addToAnnotations(
                Annotation.create(System.currentTimeMillis()*1000, TraceContext.ANNO_SS,
                        Endpoint.create(
                                span.getName(),
                                NetworkUtils.ip2Num(NetworkUtils.getSiteIp()),
                                TraceContext.getTraceConfig().getServerPort())));

        span.setDuration(watch.stop().elapsed(TimeUnit.MICROSECONDS));
        TraceAgent traceAgent=TraceAgent.builder();

        traceAgent.send(TraceContext.getSpans());

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	
    	String method = invocation.getMethodName();
    	
        if(!TraceContext.getTraceConfig().isEnabled()){
            return invoker.invoke(invocation);
        }

        Map<String, String> attaches = invocation.getAttachments();
        if (!attaches.containsKey(TraceContext.TRACE_ID_KEY)){
            return invoker.invoke(invocation);
        }
        Stopwatch watch = Stopwatch.createStarted();
        Span providerSpan= this.startTrace(attaches,invocation);

        Result result = invoker.invoke(invocation);
        this.endTrace(providerSpan,watch,method);

        return result;

    }
}
