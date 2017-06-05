package com.ucloudlink.dubbox.zipkin.trace.collector;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.github.kristofa.brave.AbstractSpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;

import com.twitter.zipkin.gen.SpanCodec;
import com.ucloudlink.dubbox.zipkin.context.TraceContext;

public class KafkaCollector extends AbstractSpanCollector
{
	private Producer<byte[], byte[]> producer;
	
	private SpanCodec codec;
	
	private String topic;

	public KafkaCollector(SpanCodec codec, SpanCollectorMetricsHandler metrics, int flushInterval) {
		super(codec, metrics, flushInterval);
		// TODO Auto-generated constructor stub
		this.codec = codec;
		Properties props = new Properties();  
    	props.put("bootstrap.servers", TraceContext.getTraceConfig().getKafkaServers());  
    	props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");  
    	props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");  
    	props.put("acks", "all");  
    	props.put("retries", 1);  
    	topic="zipkin";
    	producer = new KafkaProducer<byte[], byte[]>(props);  
	}

	@Override
	protected void sendSpans(byte[] encoded) throws IOException {
		// TODO Auto-generated method stub
		//byte[] result = codec.writeSpans(spans);
		
		producer.send(new ProducerRecord<byte[], byte[]>(topic, encoded));
	}

}
