package vn.edu.haui.scheduler.domain.optimizer;

import java.util.List;

public final class Optimizer
{
	public List<OptimizationResult> optimize(OptimizationInput input)
	{
		if(input.requiredCourseIds().isEmpty()) {
			throw new IllegalArgumentException("No required courses.");
		}

		BacktrackingEngine engine = new BacktrackingEngine(input);
		return engine.solve();
	}
}