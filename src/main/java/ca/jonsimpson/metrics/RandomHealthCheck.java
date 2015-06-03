package ca.jonsimpson.metrics;

import java.util.Random;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;

/**
 * A randomly occurring {@link HealthCheck}
 */
final class RandomHealthCheck extends HealthCheck {
	@Override
	protected Result check() throws Exception {
		if (new Random().nextBoolean()) {
			return Result.healthy();
		} else
			return Result.unhealthy("Remote api is down!");
	}
}