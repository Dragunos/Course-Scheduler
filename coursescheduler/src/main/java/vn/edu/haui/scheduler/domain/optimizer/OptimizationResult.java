package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.PhuongAnThoiKhoaBieu;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class OptimizationResult
{
	private final List<PhuongAnThoiKhoaBieu> topSolutions;

	private final double bestScore;

	public OptimizationResult(List<PhuongAnThoiKhoaBieu> solutions)
	{
		Objects.requireNonNull(solutions);

		List<PhuongAnThoiKhoaBieu> sorted = solutions.stream()
				.sorted(Comparator.comparingDouble(
						PhuongAnThoiKhoaBieu::getDiemDanhGia)
						.reversed())
				.toList();

		this.topSolutions = Collections.unmodifiableList(sorted);
		this.bestScore = sorted.isEmpty()
				? Double.NEGATIVE_INFINITY
				: sorted.get(0).getDiemDanhGia();
	}

	public List<PhuongAnThoiKhoaBieu> getTopSolutions()
	{
		return topSolutions;
	}

	public double getBestScore()
	{
		return bestScore;
	}

	public int size()
	{
		return topSolutions.size();
	}

	public boolean isEmpty()
	{
		return topSolutions.isEmpty();
	}
}
