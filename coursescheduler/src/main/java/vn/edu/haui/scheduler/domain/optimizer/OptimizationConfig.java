package vn.edu.haui.scheduler.domain.optimizer;

import java.time.Duration;
import java.util.Optional;

public final class OptimizationConfig
{
	private final int topK;

	private final Duration timeout;

	private final long randomSeed;

	private final boolean enablePruning;

	private final Optional<ProgressListener> progressListener;

	public OptimizationConfig(int topK, Duration timeout, long randomSeed, boolean enablePruning,
			ProgressListener progressListener)
	{
		this.topK = topK;
		this.timeout = timeout;
		this.randomSeed = randomSeed;
		this.enablePruning = enablePruning;
		this.progressListener = Optional.ofNullable(progressListener);
	}

	public int getTopK()
	{
		return topK;
	}

	public Duration getTimeout()
	{
		return timeout;
	}

	public long getRandomSeed()
	{
		return randomSeed;
	}

	public boolean isEnablePruning()
	{
		return enablePruning;
	}

	public Optional<ProgressListener> getProgressListener()
	{
		return progressListener;
	}
}
