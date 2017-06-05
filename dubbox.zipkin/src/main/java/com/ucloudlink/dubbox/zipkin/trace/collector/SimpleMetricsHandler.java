package com.ucloudlink.dubbox.zipkin.trace.collector;

import com.github.kristofa.brave.SpanCollectorMetricsHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleMetricsHandler implements SpanCollectorMetricsHandler {

    final AtomicInteger acceptedSpans = new AtomicInteger();
    final AtomicInteger droppedSpans = new AtomicInteger();


    public void incrementAcceptedSpans(int quantity) {
        acceptedSpans.addAndGet(quantity);
    }

    
    public void incrementDroppedSpans(int quantity) {
        droppedSpans.addAndGet(quantity);
    }
}