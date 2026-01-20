package vn.edu.haui.scheduler.domain.optimizer;

public final class ProgressSnapshot
{
	private final double fractionComplete;

	private final int nodesVisited;

	private final int bestSizeSoFar;

	public ProgressSnapshot(double fractionComplete, int nodesVisited, int bestSizeSoFar)
	{
		this.fractionComplete = fractionComplete;
		this.nodesVisited = nodesVisited;
		this.bestSizeSoFar = bestSizeSoFar;
	}

	public double getFractionComplete()
	{
		return fractionComplete;
	}

	public int getNodesVisited()
	{
		return nodesVisited;
	}

	public int getBestSizeSoFar()
	{
		return bestSizeSoFar;
	}
}
