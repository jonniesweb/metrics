package ca.jonsimpson.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * All metrics action goes down here. Keeps track of the namespaces used for
 * this application. Every metric is prefixed with appName.hostName. Register
 * metrics and reporters to this object.
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

	public String getHostName() {
		return hostName;
	}

	public String getAppName() {
		return appName;
	}
}