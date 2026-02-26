package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.List;

public final class OptimizationResult implements Comparable<OptimizationResult>
{
	private final List<LopHocPhan> selectedSections;

	private final double score;

	public OptimizationResult(List<LopHocPhan> selectedSections, double score)
	{
		this.selectedSections = List.copyOf(selectedSections);
		this.score = score;
	}

	public List<LopHocPhan> selectedSections()
	{
		return selectedSections;
	}

	public double score()
	{
		return score;
	}

	@Override
	public int compareTo(OptimizationResult other)
	{
		return Double.compare(this.score, other.score);
	}
}