package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.ArrayList;
import java.util.List;

public class PhuongAnThoiKhoaBieu implements Comparable<PhuongAnThoiKhoaBieu>
{
	private List<LopHocPhan> lopHocPhans;

	private double diemDanhGia;

	public PhuongAnThoiKhoaBieu()
	{
		this.lopHocPhans = new ArrayList<>();
		this.diemDanhGia = 0.0;
	}

	public PhuongAnThoiKhoaBieu(List<LopHocPhan> lopHocPhans, double diemDanhGia)
	{
		this.lopHocPhans = new ArrayList<>(lopHocPhans);
		this.diemDanhGia = diemDanhGia;
	}

	public List<LopHocPhan> getLopHocPhans()
	{
		return lopHocPhans;
	}

	public void addLopHocPhan(LopHocPhan lop)
	{
		this.lopHocPhans.add(lop);
	}

	public double getDiemDanhGia()
	{
		return diemDanhGia;
	}

	public void setDiemDanhGia(double diemDanhGia)
	{
		this.diemDanhGia = diemDanhGia;
	}

	// So sánh theo điểm đánh giá (giúp sắp xếp top-K)
	@Override
	public int compareTo(PhuongAnThoiKhoaBieu o)
	{
		return Double.compare(o.diemDanhGia, this.diemDanhGia); // giảm dần
	}
}