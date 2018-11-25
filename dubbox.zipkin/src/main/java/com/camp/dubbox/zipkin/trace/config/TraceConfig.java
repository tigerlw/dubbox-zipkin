package com.camp.dubbox.zipkin.trace.config;




public class TraceConfig {

    private boolean enabled=true;

    private int connectTimeout;

    private int readTimeout;

    private int flushInterval=0;

    private boolean compressionEnabled=true;

    private String zipkinUrl;
    
    private String kafkaServers;


    private int serverPort;


    private String applicationName;

    public int getServerPort(){
        return this.serverPort;
    }

    public String getApplicationName(){
        return this.applicationName;
    }
    
    public void setApplicationName(String applicationName)
    {
    	this.applicationName = applicationName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public void setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    public String getZipkinUrl() {
        return zipkinUrl;
    }

    public void setZipkinUrl(String zipkinUrl) {
        this.zipkinUrl = zipkinUrl;
    }

	public String getKafkaServers() {
		return kafkaServers;
	}

	public void setKafkaServers(String kafkaServers) {
		this.kafkaServers = kafkaServers;
	}
}
