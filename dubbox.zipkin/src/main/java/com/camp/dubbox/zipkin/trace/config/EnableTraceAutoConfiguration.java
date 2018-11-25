package com.camp.dubbox.zipkin.trace.config;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.camp.dubbox.zipkin.context.TraceContext;



public class EnableTraceAutoConfiguration implements InitializingBean {

 
    private TraceConfig traceConfig;

    public TraceConfig getTraceConfig() {
		return traceConfig;
	}

	public void setTraceConfig(TraceConfig traceConfig) {
		this.traceConfig = traceConfig;
	}

	/*@PostConstruct
    public void init() throws Exception {
        TraceContext.init(this.traceConfig);
    }*/

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		TraceContext.init(this.traceConfig);
	}
}
