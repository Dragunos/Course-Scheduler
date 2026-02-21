package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.enums.HinhThucDay;

import java.util.*;

public class Optimizer
{
	private final List<LopHocPhan> allLopHocPhan;

	private final Set<String> requiredHocPhanCodes;

	private final Set<String> preferredLopIds;

	private final Set<HinhThucDay> avoidHinhThuc;

	private final Set<Integer> avoidTiet;

	private final Set<Integer> avoidThu;

	private final int topK;

	private final long timeLimitMillis;

	private List<PhuongAnThoiKhoaBieu> topPhuongAn;

	public Optimizer(List<LopHocPhan> allLopHocPhan,
			Set<String> requiredHocPhanCodes,
			Set<String> preferredLopIds,
			Set<HinhThucDay> avoidHinhThuc,
			Set<Integer> avoidTiet,
			Set<Integer> avoidThu,
			int topK,
			long timeLimitMillis)
	{
		this.allLopHocPhan = allLopHocPhan;
		this.requiredHocPhanCodes = requiredHocPhanCodes;
		this.preferredLopIds = preferredLopIds;
		this.avoidHinhThuc = avoidHinhThuc;
		this.avoidTiet = avoidTiet;
		this.avoidThu = avoidThu;
		this.topK = topK;
		this.timeLimitMillis = timeLimitMillis;
		this.topPhuongAn = new ArrayList<>();
	}

	public List<PhuongAnThoiKhoaBieu> optimize()
	{
		long startTime = System.currentTimeMillis();
		backtrack(new ArrayList<>(), new HashSet<>(), startTime);
		topPhuongAn.sort(PhuongAnThoiKhoaBieu::compareTo);
		if(topPhuongAn.size() > topK) {
			return topPhuongAn.subList(0, topK);
		}
		return topPhuongAn;
	}

	private void backtrack(List<LopHocPhan> current, Set<String> usedHocPhan, long startTime)
	{
		if(System.currentTimeMillis() - startTime > timeLimitMillis) return;

		if(usedHocPhan.containsAll(requiredHocPhanCodes)) {
			double score = evaluateScore(current);
			PhuongAnThoiKhoaBieu phuongAn = new PhuongAnThoiKhoaBieu(current, score);
			addTopPhuongAn(phuongAn);
		}

		for(LopHocPhan lop : allLopHocPhan) {
			if(usedHocPhan.contains(lop.getMaLop())) continue;
			if(!canAddLopHocPhan(current, lop)) continue;

			current.add(lop);
			usedHocPhan.add(lop.getMaLop());
			backtrack(current, usedHocPhan, startTime);
			current.remove(current.size() - 1);
			usedHocPhan.remove(lop.getMaLop());
		}
	}

	private boolean canAddLopHocPhan(List<LopHocPhan> current, LopHocPhan candidate)
	{
		for(LopHocPhan existing : current) {
			for(var buoiExisting : existing.getDanhSachLichHoc()) {
				for(var buoiCandidate : candidate.getDanhSachLichHoc()) {
					if(buoiExisting.getThu() == buoiCandidate.getThu()
							&& buoiExisting.getTietBatDau() <= buoiCandidate.getTietKetThuc()
							&& buoiCandidate.getTietBatDau() <= buoiExisting.getTietKetThuc()) {
						return false; // Xung đột thời gian -> prune
					}
				}
			}
		}
		return true;
	}

	private double evaluateScore(List<LopHocPhan> current)
	{
		double score = 0.0;
		for(LopHocPhan lop : current) {
			// Hình thức học
			if(avoidHinhThuc.contains(lop.getHinhThucDay())) score -= 1.0;
			else score += 0.5;
			// Lớp mong muốn
			if(preferredLopIds.contains(lop.getMaLop())) score += 1.0;
			else score -= 0.5;
			// Ngày và tiết tránh
			for(var buoi : lop.getDanhSachLichHoc()) {
				if(avoidThu.contains(buoi.getThu().getGiaTri())) score -= 1.0;
				if(avoidTiet.contains(buoi.getTietBatDau()) || avoidTiet.contains(buoi.getTietKetThuc())) score -= 0.5;
			}
		}
		return score;
	}

	private void addTopPhuongAn(PhuongAnThoiKhoaBieu phuongAn)
	{
		topPhuongAn.add(phuongAn);
		topPhuongAn.sort(PhuongAnThoiKhoaBieu::compareTo);
		if(topPhuongAn.size() > topK) {
			topPhuongAn = new ArrayList<>(topPhuongAn.subList(0, topK));
		}
	}
}