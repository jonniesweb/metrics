package ca.jonsimpson.metrics;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * All metrics action goes down here. Keeps track of the namespaces used for
 * this application. Every metric is prefixed with APPNAME.HOSTNAME. Register
 * metrics and reporters here.
 */
public class MetricsConfig {
	private String hostName;
	private String appName;
	
	private MetricRegistry registry;
	private HealthCheckRegistry healthChecks;
	
	public MetricsConfig(String hostName, String appName, MetricRegistry registry, HealthCheckRegistry healthChecks) {
		this(hostName, appName);
		this.registry = registry;
		this.healthChecks = healthChecks;
	}
	
	/**
	 * Creates a {@link MetricsConfig} with the given hostName and appName.
	 * @param hostName
	 * @param appName
	 */
	public MetricsConfig(String hostName, String appName) {
		this.hostName = hostName;
		this.appName = appName;
		
		registry = new MetricRegistry();
		healthChecks = new HealthCheckRegistry();
	}
	
	/**
	 * Creates a {@link MetricsConfig} 
	 */
	public MetricsConfig() {
	}
	
	public MetricRegistry getRegistry() {
		return registry;
	}
	
	public void setRegistry(MetricRegistry registry) {
		this.registry = registry;
	}
	
	public HealthCheckRegistry getHealthChecks() {
		return healthChecks;
	}
	
	public void setHealthChecks(HealthCheckRegistry healthChecks) {
		this.healthChecks = healthChecks;
	}

	/**
	 * Get the host name of this machine from the JVM parameter
	 * <code>hostName</code> or automatically from the computer's hostname.
	 * 
	 * @return The name of this computer
	 */
	public String getHostName() {
	
		if (hostName == null) {
			try {
				if (System.getProperty("hostName") != null) {
					hostName = System.getProperty("hostName");
				} else {
					hostName = InetAddress.getLocalHost().getHostName();
					
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return hostName;
	
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * Get the name of this application from the JVM parameter
	 * <code>appName</code>
	 * 
	 * @return The name of this app
	 */
	public String getAppName() {
		if (appName == null) {
			appName = System.getProperty("appName");
		}
		return appName;
	}
}