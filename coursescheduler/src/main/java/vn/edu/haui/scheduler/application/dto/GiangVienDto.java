package vn.edu.haui.scheduler.application.dto;

import vn.edu.haui.scheduler.domain.model.GiangVien;

public class GiangVienDto
{
	private Long id;

	private String tenGiangVien;

	public GiangVienDto()
	{
	}

	public GiangVienDto(Long id, String tenGiangVien)
	{
		this.id = id;
		this.tenGiangVien = tenGiangVien;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTenGiangVien()
	{
		return tenGiangVien;
	}

	public void setTenGiangVien(String tenGiangVien)
	{
		this.tenGiangVien = tenGiangVien;
	}

	public static GiangVienDto fromDomain(GiangVien gv)
	{
		if(gv == null) return null;
		return new GiangVienDto(gv.getId(), gv.getTenGiangVien());
	}
}
