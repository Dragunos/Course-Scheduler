package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public final class OptimizationInput
{
	private final List<LopHocPhan> availableSections;

	private final Set<Long> requiredCourseIds;

	private final List<HardConstraint> hardConstraints;

	private final List<SoftConstraint> softConstraints;

	private final int topK;

	private final Duration timeLimit;

	public OptimizationInput(
			List<LopHocPhan> availableSections,
			Set<Long> requiredCourseIds,
			List<HardConstraint> hardConstraints,
			List<SoftConstraint> softConstraints,
			int topK,
			Duration timeLimit)
	{
		this.availableSections = List.copyOf(availableSections);
		this.requiredCourseIds = Set.copyOf(requiredCourseIds);
		this.hardConstraints = List.copyOf(hardConstraints);
		this.softConstraints = List.copyOf(softConstraints);
		this.topK = topK;
		this.timeLimit = timeLimit;
	}

	public List<LopHocPhan> availableSections()
	{
		return availableSections;
	}

	public Set<Long> requiredCourseIds()
	{
		return requiredCourseIds;
	}

	public List<HardConstraint> hardConstraints()
	{
		return hardConstraints;
	}

	public List<SoftConstraint> softConstraints()
	{
		return softConstraints;
	}

	public int topK()
	{
		return topK;
	}

	public Duration timeLimit()
	{
		return timeLimit;
	}
}