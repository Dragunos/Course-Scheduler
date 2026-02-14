package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.constraint.RangBuocToiUu;

import java.util.*;

public class BacktrackingOptimizer
{
	private final long timeLimit;

	private final int topK;

	private final long start;

	private final PriorityQueue<PhuongAn> best = new PriorityQueue<>(Comparator.comparingDouble(p -> p.diem));

	public BacktrackingOptimizer(int topK, long timeLimitMillis)
	{
		this.topK = topK;
		this.timeLimit = timeLimitMillis;
		this.start = System.currentTimeMillis();
	}

	public List<PhuongAn> solve(List<List<Long>> groups,
			List<RangBuocToiUu> rangBuocs)
	{
		backtrack(0, groups, new ArrayList<>(), rangBuocs);
		return new ArrayList<>(best);
	}

	private void backtrack(int index,
			List<List<Long>> groups,
			List<Long> current,
			List<RangBuocToiUu> rangBuocs)
	{
		if(System.currentTimeMillis() - start > timeLimit)
			return;

		if(index == groups.size()) {
			double score = evaluate(current, rangBuocs);

			if(best.size() < topK)
				best.add(new PhuongAn(current, score));
			else if(score > best.peek().diem) {
				best.poll();
				best.add(new PhuongAn(current, score));
			}
			return;
		}

		for(Long candidate : groups.get(index)) {
			current.add(candidate);

			if(!viPhamRangBuocCung(current, rangBuocs)) backtrack(index + 1, groups, current, rangBuocs);

			current.remove(current.size() - 1);
		}
	}

	private boolean viPhamRangBuocCung(List<Long> current,
			List<RangBuocToiUu> rangBuocs)
	{
		return false;
	}

	private double evaluate(List<Long> current,
			List<RangBuocToiUu> rangBuocs)
	{
		double score = 0;
		for(RangBuocToiUu rb : rangBuocs) {
			if(!rb.isLaCung())
				score += rb.getTrongSo();
		}
		return score;
	}

	public static class PhuongAn
	{
		public final List<Long> lopIds;

		public final double diem;

		public PhuongAn(List<Long> lopIds, double diem)
		{
			this.lopIds = new ArrayList<>(lopIds);
			this.diem = diem;
		}
	}
}
