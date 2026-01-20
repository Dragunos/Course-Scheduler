package vn.edu.haui.scheduler.domain.model;

public class GiangVien
{
	private Long id;

	private String tenGiangVien;

	public GiangVien()
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

	public String getTenGiangVien()
	{
		return tenGiangVien;
	}

	public void setTenGiangVien(String tenGiangVien)
	{
		this.tenGiangVien = tenGiangVien;
	}
}