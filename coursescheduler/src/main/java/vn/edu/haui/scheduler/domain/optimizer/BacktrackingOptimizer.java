package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.YeuCauDangKy;
import vn.edu.haui.scheduler.domain.model.PhuongAnThoiKhoaBieu;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.function.ToDoubleFunction;

public final class BacktrackingOptimizer implements Optimizer
{

	private final ToDoubleFunction<List<LopHocPhan>> scorer;

	public BacktrackingOptimizer(ToDoubleFunction<List<LopHocPhan>> scorer)
	{
		this.scorer = scorer;
	}

	@Override
	public OptimizationResult optimize(YeuCauDangKy request, OptimizationConfig config)
	{
		int k = config.getTopK();
		Duration timeout = config.getTimeout();
		long seed = config.getRandomSeed();
		boolean prune = config.isEnablePruning();
		ProgressListener listener = config.getProgressListener().orElse(null);

		Instant deadline = Instant.now(Clock.systemUTC()).plus(timeout);
		Random rnd = new Random(seed);

		PriorityQueue<PhuongAnThoiKhoaBieu> topK = new PriorityQueue<>(k,
				(a, b) -> Double.compare(a.getDiemDanhGia(), b.getDiemDanhGia()));
		Deque<LopHocPhan> stack = new ArrayDeque<>();
		List<LopHocPhan> pool = new ArrayList<>(request.getDanhSachLopHocPhan());
		int nodesVisited = 0;

		dfs(0, pool, stack, topK, k, deadline, rnd, prune, listener, nodesVisited);

		List<PhuongAnThoiKhoaBieu> result = new ArrayList<>(topK.size());
		while(!topK.isEmpty()) {
			result.add(topK.poll());
		}
		return new OptimizationResult(result);
	}

	private void dfs(int index,
			List<LopHocPhan> pool,
			Deque<LopHocPhan> stack,
			PriorityQueue<PhuongAnThoiKhoaBieu> topK,
			int k,
			java.time.Instant deadline,
			Random rnd,
			boolean prune,
			ProgressListener listener,
			int nodesVisited)
	{

		if(java.time.Instant.now().isAfter(deadline)) {
			return;
		}

		nodesVisited++;

		if(index >= pool.size()) {
			List<LopHocPhan> chosen = new ArrayList<>(stack);
			double score = scorer.applyAsDouble(chosen);
			PhuongAnThoiKhoaBieu pa = new PhuongAnThoiKhoaBieu(Set.copyOf(chosen), score);
			if(topK.size() < k) {
				topK.add(pa);
			}
			else if(k > 0 && score > topK.peek().getDiemDanhGia()) {
				topK.poll();
				topK.add(pa);
			}
			if(listener != null) {
				listener.onProgress(new ProgressSnapshot(0.0, nodesVisited, topK.size()));
			}
			return;
		}

		LopHocPhan candidate = pool.get(index);

		stack.push(candidate);
		if(!conflicts(stack)) {
			if(!prune || heuristicUpperBound(stack) > lowerBound(topK)) {
				dfs(index + 1, pool, stack, topK, k, deadline, rnd, prune, listener, nodesVisited);
			}
		}
		stack.pop();

		if(java.time.Instant.now().isAfter(deadline)) {
			return;
		}

		dfs(index + 1, pool, stack, topK, k, deadline, rnd, prune, listener, nodesVisited);
	}

	private boolean conflicts(Deque<LopHocPhan> stack)
	{
		return false;
	}

	private double heuristicUpperBound(Deque<LopHocPhan> stack)
	{
		return Double.POSITIVE_INFINITY;
	}

	private double lowerBound(PriorityQueue<PhuongAnThoiKhoaBieu> topK)
	{
		return topK.isEmpty() ? Double.NEGATIVE_INFINITY : topK.peek().getDiemDanhGia();
	}
}
