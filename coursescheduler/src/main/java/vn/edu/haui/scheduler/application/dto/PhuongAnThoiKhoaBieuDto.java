package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class PhuongAnThoiKhoaBieuDto
{
	private Long id;

	private Long nguoiDungId;

	private String tenPhuongAn;

	private Double diemDanhGia;

	private List<LopHocPhanDto> danhSachLopHocPhan;

	public PhuongAnThoiKhoaBieuDto()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getNguoiDungId()
	{
		return nguoiDungId;
	}

	public void setNguoiDungId(Long nguoiDungId)
	{
		this.nguoiDungId = nguoiDungId;
	}

	public String getTenPhuongAn()
	{
		return tenPhuongAn;
	}

	public void setTenPhuongAn(String tenPhuongAn)
	{
		this.tenPhuongAn = tenPhuongAn;
	}

	public Double getDiemDanhGia()
	{
		return diemDanhGia;
	}

	public void setDiemDanhGia(Double diemDanhGia)
	{
		this.diemDanhGia = diemDanhGia;
	}

	public List<LopHocPhanDto> getDanhSachLopHocPhan()
	{
		return danhSachLopHocPhan;
	}

	public void setDanhSachLopHocPhan(List<LopHocPhanDto> danhSachLopHocPhan)
	{
		this.danhSachLopHocPhan = danhSachLopHocPhan;
	}
}
