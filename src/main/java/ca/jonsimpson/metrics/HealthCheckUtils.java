package ca.jonsimpson.metrics;

import java.util.Map;
import java.util.Map.Entry;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

public class HealthCheckUtils {

	/**
	 * Print all health checks in the {@link HealthCheckRegistry} to
	 * stdout/stderr.
	 * 
	 * @param healthChecks
	 */
	public static void printHealthChecks(HealthCheckRegistry healthChecks) {
		final Map<String, HealthCheck.Result> results = healthChecks.runHealthChecks();
		for (Entry<String, HealthCheck.Result> entry : results.entrySet()) {
		    if (entry.getValue().isHealthy()) {
		        System.out.println(entry.getKey() + " is healthy");
		    } else {
		        System.err.println(entry.getKey() + " is UNHEALTHY: " + entry.getValue().getMessage());
		        final Throwable e = entry.getValue().getError();
		        if (e != null) {
		            e.printStackTrace();
		        }
		    }
		}
	}
	
}
