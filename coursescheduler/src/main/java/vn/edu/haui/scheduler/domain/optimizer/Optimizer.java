package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.*;

public class Optimizer
{
	private final Map<String, List<LopHocPhan>> groupedByHocPhan;

	private final Set<String> requiredHocPhanCodes;

	private final Set<String> preferredLopIds;

	private final Set<String> avoidHinhThuc;

	private final Set<Integer> avoidTiet;

	private final Set<Integer> avoidThu;

	private final int topK;

	private final long timeLimitMillis;

	private final List<PhuongAnThoiKhoaBieu> topPhuongAn = new ArrayList<>();

	public Optimizer(
			List<LopHocPhan> allLopHocPhan,
			Set<String> requiredHocPhanCodes,
			Set<String> preferredLopIds,
			Set<String> avoidHinhThuc,
			Set<Integer> avoidTiet,
			Set<Integer> avoidThu,
			int topK,
			long timeLimitMillis)
	{
		this.groupedByHocPhan = groupByHocPhan(allLopHocPhan);
		this.requiredHocPhanCodes = requiredHocPhanCodes;
		this.preferredLopIds = preferredLopIds;
		this.avoidHinhThuc = avoidHinhThuc;
		this.avoidTiet = avoidTiet;
		this.avoidThu = avoidThu;
		this.topK = topK;
		this.timeLimitMillis = timeLimitMillis;
	}

	private Map<String, List<LopHocPhan>> groupByHocPhan(List<LopHocPhan> list)
	{
		Map<String, List<LopHocPhan>> map = new LinkedHashMap<>();

		for(LopHocPhan lop : list) {
			String maHocPhan = lop.getHocPhan().getMaHocPhan();
			map.computeIfAbsent(maHocPhan, k -> new ArrayList<>()).add(lop);
		}

		return map;
	}

	public List<PhuongAnThoiKhoaBieu> optimize()
	{
		long start = System.currentTimeMillis();

		List<String> requiredCourses = new ArrayList<>(requiredHocPhanCodes);

		backtrack(0, requiredCourses, new ArrayList<>(), start);

		topPhuongAn.sort(PhuongAnThoiKhoaBieu::compareTo);

		return topPhuongAn.size() > topK
				? topPhuongAn.subList(0, topK)
				: topPhuongAn;
	}

	private void backtrack(
			int index,
			List<String> courses,
			List<LopHocPhan> current,
			long start)
	{
		if(System.currentTimeMillis() - start > timeLimitMillis)
			return;

		if(index == courses.size()) {
			double score = evaluateScore(current);
			addTop(new PhuongAnThoiKhoaBieu(current, score));
			return;
		}

		String maHocPhan = courses.get(index);

		List<LopHocPhan> candidates = groupedByHocPhan.get(maHocPhan);

		if(candidates == null)
			return;

		for(LopHocPhan lop : candidates) {

			if(!canAdd(current, lop))
				continue;

			current.add(lop);
			backtrack(index + 1, courses, current, start);
			current.remove(current.size() - 1);
		}
	}

	private boolean canAdd(List<LopHocPhan> current, LopHocPhan candidate)
	{
		for(LopHocPhan existing : current) {
			for(var a : existing.getLichHocList()) {
				for(var b : candidate.getLichHocList()) {
					if(a.getThu() == b.getThu()
							&& a.getTietBatDau() <= b.getTietKetThuc()
							&& b.getTietBatDau() <= a.getTietKetThuc())
						return false;
				}
			}
		}
		return true;
	}

	private double evaluateScore(List<LopHocPhan> list)
	{
		double score = 0;

		for(LopHocPhan lop : list) {

			if(preferredLopIds.contains(lop.getMaLop()))
				score += 1.5;

			if(avoidHinhThuc.contains(lop.getHinhThucDay()))
				score -= 1;

			for(var lich : lop.getLichHocList()) {

				if(avoidThu.contains(lich.getThu()))
					score -= 1;

				if(avoidTiet.contains(lich.getTietBatDau())
						|| avoidTiet.contains(lich.getTietKetThuc()))
					score -= 0.5;
			}
		}

		return score;
	}

	private void addTop(PhuongAnThoiKhoaBieu p)
	{
		topPhuongAn.add(p);
		topPhuongAn.sort(PhuongAnThoiKhoaBieu::compareTo);

		if(topPhuongAn.size() > topK)
			topPhuongAn.remove(topPhuongAn.size() - 1);
	}
}