package ca.jonsimpson.metrics;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.readytalk.metrics.StatsDReporter;

public class Main {
	
	MetricsConfig metrics = new MetricsConfig();

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		
		// create a new MetricRegistry
		
		// Configure reporting metrics to StatsD
		StatsDReporter.forRegistry(metrics.getRegistry())
				.prefixedWith(metrics.getAppName() + "." + metrics.getHostName())
				.filter(MetricFilter.ALL)
				.build("localhost", 8125)
				.start(5, TimeUnit.SECONDS);
		
		// configure reporting metrics to the console
		ConsoleReporter.forRegistry(metrics.getRegistry())
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build()
				.start(5, TimeUnit.SECONDS);

		// register a random gauge
		metrics.getRegistry().register(MetricRegistry.name("requests"), new RandomGauge());
		
		// register JVM metrics
		registerJvmMetrics(metrics.getRegistry());
		
		// register health checks
		metrics.getHealthChecks().register("database", new RandomHealthCheck());
		
		// looping infinitely, sleep for a second and report health checks
		while (true) {
			try {
				Thread.sleep(1000);
				HealthCheckUtils.printHealthChecks(metrics.getHealthChecks());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void registerJvmMetrics(MetricRegistry registry) {
		MetricUtils.registerAll("jvm.gc", new GarbageCollectorMetricSet(), registry);
		MetricUtils.registerAll("jvm.memory", new MemoryUsageGaugeSet(), registry);
		MetricUtils.registerAll("jvm.threads", new ThreadStatesGaugeSet(), registry);
		MetricUtils.registerAll("jvm.classes", new ClassLoadingGaugeSet(), registry);
		registry.register("jvm.fd", new FileDescriptorRatioGauge());
	}
}
