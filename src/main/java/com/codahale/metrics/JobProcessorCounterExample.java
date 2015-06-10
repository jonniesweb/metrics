package com.codahale.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * An example of using the Metrics {@link Counter} for better efficiency of
 * counting values compared to using a {@link Gauge}.
 * 
 * This example is of a job processor where jobs are grabbed in bunches, then
 * processed one by one, then repeating the process infinitely. Metrics are
 * collected by incrementing the counter when jobs are grabbed, and decremented
 * when jobs are processed.
 * 
 * This class implements {@link Metric} and {@link Counting} to easily allow for
 * a {@link MetricRegistry} to collect metrics.
 */
public class JobProcessorCounterExample implements Metric, Counting {
	
	/**
	 * A queue to store jobs in before being processed
	 */
	private Queue<Job> queue = new LinkedList<Job>();
	
	/**
	 * A {@link Counter} to track the performance of this job processor
	 */
	private Counter counter = new Counter();

	private static final String[] customerNames = { "AAA", "BBB", "CCC", "DDD", "EEE" };
	
	public static final String metricPrefix = "jobProcessor";
	private MetricRegistry registry;
	
	public JobProcessorCounterExample(MetricRegistry registry) {
		this.registry = registry;
	}
	
	
	/**
	 * Infinitely process jobs, getting more when there are no more left.
	 */
	public void process() {
		// grab jobs initially
		getJobs();
		
		// process jobs until the queue is empty, then get more jobs
		while (true) {
			if (queue.size() > 0) {
				processSingleJob();
			} else {
				getJobs();
			}
		}
	}

	/**
	 * Get more jobs to process.		
	 */
	private void getJobs() {
		List<Job> jobs = new LinkedList<Job>();
		
		// create 50 jobs
		final int numJobs = 50;
		for (int i = 0; i < numJobs; i++) {
			jobs.add(Job.getRandomJob());
		}
		
		// add jobs to queue
		queue.addAll(jobs);
		
		// increment the counter with the number of jobs added to the queue
		counter.inc(jobs.size());
	}
	
	/**
	 * Process a single job, decrementing the counter when done.
	 */
	private void processSingleJob() {
		// get a job
		Job job = queue.poll();
		
		try {
			// process job
//			System.out.println("Processing: " + job);
			Thread.sleep(new Random().nextInt(50));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// decrement counter
			counter.dec();
			
			// get the job's gauge for the job type and customer
			Meter meter = registry.meter(metricPrefix + "." + job.customer + "." + job.jobType);
			
			// mark that the job is done processing
			meter.mark();
		}
		
	}

	/**
	 * Metrics specific method to get the current reading for this
	 * {@link Counter}.
	 * 
	 * @see com.codahale.metrics.Counting#getCount()
	 */
	@Override
	public long getCount() {
		return counter.getCount();
	}
	
	public MetricRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(MetricRegistry registry) {
		this.registry = registry;
	}

	/**
	 * A representation of a job. Jobs have different types. Jobs also know of
	 * which customer they are for.
	 */
	public static class Job {
		
		public enum JobType { TYPE1, TYPE2, TYPE3 };
		
		private JobType jobType;
		private String customer;
		private static Random random = new Random();
		
		public Job(JobType jobType, String customer) {
			this.jobType = jobType;
			this.customer = customer;
		}

		public static Job getRandomJob() {
			return new Job(
					JobType.values()[random.nextInt(JobType.values().length)],
					JobProcessorCounterExample.customerNames[random
							.nextInt(JobProcessorCounterExample.customerNames.length)]);
		}

		public JobType getJobType() {
			return jobType;
		}

		@Override
		public String toString() {
			return "Job [jobType=" + jobType + ", customer=" + customer + "]";
		}
		
	}
	
	public static void main(String[] args) {
		MetricRegistry registry = new MetricRegistry();
		ConsoleReporter.forRegistry(registry).build().start(5, TimeUnit.SECONDS);
		
		JobProcessorCounterExample ex = new JobProcessorCounterExample(registry);
		
		
		ex.process();
	}
}
