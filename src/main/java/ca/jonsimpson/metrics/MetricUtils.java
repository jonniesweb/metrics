package ca.jonsimpson.metrics;

import java.util.Map.Entry;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;

public class MetricUtils {

	/**
	 * Helper method to recursively register all {@link Metric}s that are a part
	 * of the {@link MetricSet}.
	 * 
	 * @param prefix
	 * @param metricSet
	 * @param registry
	 */
	public static void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry) {
	    for (Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
	        if (entry.getValue() instanceof MetricSet) {
	            registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry);
	        } else {
	            registry.register(prefix + "." + entry.getKey(), entry.getValue());
	        }
	    }
	}
	
}
