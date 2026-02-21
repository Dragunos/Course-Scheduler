package vn.edu.haui.scheduler.application.dto;

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

	public String getTenGiangVien()
	{
		return tenGiangVien;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setTenGiangVien(String tenGiangVien)
	{
		this.tenGiangVien = tenGiangVien;
	}
}