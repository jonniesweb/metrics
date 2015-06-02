package ca.jonsimpson.metrics;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.readytalk.metrics.StatsDReporter;

public class Main {
	
	private static final String HOST_NAME = "host";
	private static final String APP_NAME = "app";



	public Main() {
		
		// create a new MetricRegistry
		MetricRegistry registry = new MetricRegistry();
		
		// Configure reporting metrics to StatsD
		StatsDReporter.forRegistry(registry)
				.prefixedWith(APP_NAME + "." + HOST_NAME)
				.filter(MetricFilter.ALL)
				.build("localhost", 8125)
				.start(5, TimeUnit.SECONDS);
		
		registry.register(MetricRegistry.name("requests"),
				new Gauge<Integer>() {
					@Override
					public Integer getValue() {
						System.out.println("got new value");
						return new Random().nextInt(100);
					}
				});
		
		final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
		reporter.start(5, TimeUnit.SECONDS);
		
		registerAll("jvm.gc", new GarbageCollectorMetricSet(), registry);
		registerAll("jvm.memory", new MemoryUsageGaugeSet(), registry);
		registerAll("jvm.threads", new ThreadStatesGaugeSet(), registry);
		registerAll("jvm.classes", new ClassLoadingGaugeSet(), registry);
		registry.register("jvm.fd", new FileDescriptorRatioGauge());
		
		HealthCheckRegistry healthChecks = new HealthCheckRegistry();
		healthChecks.register("database", healthCheck());
		
		while (true) {
			try {
				Thread.sleep(1000);
				printHealthChecks(healthChecks);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Helper method to recursively register all {@link Metric}s that are a part
	 * of the {@link MetricSet}.
	 * 
	 * @param prefix
	 * @param metricSet
	 * @param registry
	 */
	private static void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry) {
	    for (Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
	        if (entry.getValue() instanceof MetricSet) {
	            registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry);
	        } else {
	            registry.register(prefix + "." + entry.getKey(), entry.getValue());
	        }
	    }
	}
	
	/**
	 * A randomly occurring health check
	 * @return
	 */
	public HealthCheck healthCheck() {
		return new HealthCheck() {
			
			@Override
			protected Result check() throws Exception {
				if (new Random().nextBoolean()) {
					return Result.healthy();
				} else
					return Result.unhealthy("Remote api is down!");
			}
		};
	}
	
	/**
	 * Print all health checks in the {@link HealthCheckRegistry} to
	 * stdout/stderr.
	 * 
	 * @param healthChecks
	 */
	private static void printHealthChecks(HealthCheckRegistry healthChecks) {
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
	
	
	
	public static void main(String[] args) {
		new Main();
	}
}
