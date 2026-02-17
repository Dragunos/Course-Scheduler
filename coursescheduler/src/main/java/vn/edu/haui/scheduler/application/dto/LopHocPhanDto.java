package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class LopHocPhanDto
{
	private Long id;

	private String maLop;

	private Long giangVienId;

	private List<LichHocDto> lichHocDanhSach;

	private GiangVienDto giangVien;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getMaLop()
	{
		return maLop;
	}

	public void setMaLop(String maLop)
	{
		this.maLop = maLop;
	}

	public Long getGiangVienId()
	{
		return giangVienId;
	}

	public void setGiangVienId(Long giangVienId)
	{
		this.giangVienId = giangVienId;
	}

	public List<LichHocDto> getLichHocDanhSach()
	{
		return lichHocDanhSach;
	}

	public void setLichHocDanhSach(List<LichHocDto> lichHocDanhSach)
	{
		this.lichHocDanhSach = lichHocDanhSach;
	}

	public GiangVienDto getGiangVien()
	{
		return giangVien;
	}

	public void setGiangVien(GiangVienDto giangVien)
	{
		this.giangVien = giangVien;
	}
}