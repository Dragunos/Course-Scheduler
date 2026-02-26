package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.*;

final class BacktrackingEngine
{
	private final OptimizationInput input;

	private final Map<Long, List<LopHocPhan>> grouped;

	private final PriorityQueue<OptimizationResult> topResults;

	private final long deadlineNano;

	private final TimeConflictChecker conflictChecker = new TimeConflictChecker();

	private boolean stopped = false;

	BacktrackingEngine(OptimizationInput input)
	{

		this.input = input;
		this.grouped = groupByCourse(input);
		this.topResults = new PriorityQueue<>();
		this.deadlineNano = System.nanoTime() + input.timeLimit().toNanos();
	}

	List<OptimizationResult> solve()
	{

		List<Long> courseOrder = new ArrayList<>(grouped.keySet());
		backtrack(courseOrder, 0, new ArrayList<>());

		List<OptimizationResult> results = new ArrayList<>(topResults);
		results.sort(Comparator.comparingDouble(OptimizationResult::score).reversed());
		return results;
	}

	private void backtrack(List<Long> courseOrder,
			int index,
			List<LopHocPhan> current)
	{

		if(System.nanoTime() > deadlineNano) {
			stopped = true;
			return;
		}

		if(index == courseOrder.size()) {
			evaluateAndStore(current);
			return;
		}

		Long courseId = courseOrder.get(index);
		for(LopHocPhan section : grouped.get(courseId)) {

			current.add(section);

			if(!conflictChecker.hasConflict(current)
					&& satisfiesHardConstraints(current)) {

				backtrack(courseOrder, index + 1, current);
			}

			current.remove(current.size() - 1);

			if(stopped) return;
		}
	}

	private void evaluateAndStore(List<LopHocPhan> solution)
	{

		double score = input.softConstraints().stream()
				.mapToDouble(c -> c.evaluate(solution))
				.sum();

		OptimizationResult result = new OptimizationResult(solution, score);

		if(topResults.size() < input.topK()) {
			topResults.add(result);
		}
		else if(result.score() > topResults.peek().score()) {
			topResults.poll();
			topResults.add(result);
		}
	}

	private boolean satisfiesHardConstraints(List<LopHocPhan> solution)
	{
		return input.hardConstraints()
				.stream()
				.allMatch(c -> c.isSatisfied(solution));
	}

	private Map<Long, List<LopHocPhan>> groupByCourse(OptimizationInput input)
	{

		Map<Long, List<LopHocPhan>> map = new HashMap<>();

		for(LopHocPhan section : input.availableSections()) {
			if(input.requiredCourseIds()
					.contains(section.getHocPhan().getId())) {

				map.computeIfAbsent(
						section.getHocPhan().getId(),
						k -> new ArrayList<>())
						.add(section);
			}
		}

		return map;
	}
}