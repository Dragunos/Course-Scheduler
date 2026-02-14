package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class PhuongAnThoiKhoaBieuDto
{
	private Long id;

	private List<Long> lopHocPhanIds;

	private double diemDanhGia;

	public PhuongAnThoiKhoaBieuDto(Long id,
			List<Long> lopHocPhanIds,
			double diemDanhGia)
	{
		this.id = id;
		this.lopHocPhanIds = lopHocPhanIds;
		this.diemDanhGia = diemDanhGia;
	}

	public Long getId()
	{
		return id;
	}

	public List<Long> getLopHocPhanIds()
	{
		return lopHocPhanIds;
	}

	public double getDiemDanhGia()
	{
		return diemDanhGia;
	}
}
