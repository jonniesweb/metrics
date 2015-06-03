package ca.jonsimpson.metrics;

import java.util.Random;

import com.codahale.metrics.Gauge;

/**
 * A {@link Gauge} that returns random Integers.
 */
class RandomGauge implements Gauge<Integer> {
	@Override
	public Integer getValue() {
		return new Random().nextInt(100);
	}
}