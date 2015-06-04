package ca.jonsimpson.metrics;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.readytalk.metrics.StatsDReporter;

public class Main {
	
	private MetricsConfig metrics;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		
		// create a new MetricsConfig with the hostName and appName
		metrics = new MetricsConfig(getHostName(), getAppName());
		
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
	
	/**
	 * Get a unique name for the machine running this application. Gets the name
	 * from <code>hostName</code>, or if null automatically from the computer's
	 * hostname, or if null default to <code>host</code>.
	 * 
	 * @return The name of the computer running this application
	 */
	public String getHostName() {
		try {
			if (System.getProperty("hostName") != null) {
				return System.getProperty("hostName");
			} else {
				return InetAddress.getLocalHost().getHostName();
				
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "host";
	
	}
	
	/**
	 * Get the name of this application from the JVM parameter
	 * <code>appName</code>. Defaults to <code>app</code> if not set.
	 * 
	 * @return The name of this app
	 */
	public String getAppName() {
		String appName = System.getProperty("appName");
		if (appName == null) {
			return "app";
		}
		return appName;
	}

	/**
	 * Register the following JVM metrics with the given {@link MetricRegistry}:
	 * <li>Garbage collection
	 * <li>Memory
	 * <li>Threads
	 * <li>Classes
	 * <li>File descriptors
	 * 
	 * @param registry
	 */
	private void registerJvmMetrics(MetricRegistry registry) {
		MetricUtils.registerAll("jvm.gc", new GarbageCollectorMetricSet(), registry);
		MetricUtils.registerAll("jvm.memory", new MemoryUsageGaugeSet(), registry);
		MetricUtils.registerAll("jvm.threads", new ThreadStatesGaugeSet(), registry);
		MetricUtils.registerAll("jvm.classes", new ClassLoadingGaugeSet(), registry);
		registry.register("jvm.fd", new FileDescriptorRatioGauge());
	}
}
