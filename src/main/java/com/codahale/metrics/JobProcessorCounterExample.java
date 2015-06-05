package com.codahale.metrics;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
	private Queue<String> queue = new LinkedList<String>();
	
	/**
	 * A {@link Counter} to track the performance of this job processor
	 */
	private Counter counter = new Counter();
	
	
	
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
		
		// get up to 1000 jobs
		List<String> jobs = null;
		
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
		String job = queue.poll();
		
		try {
			// process job
			System.out.println("Processing: " + job);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// decrement counter
			counter.dec();
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
}
