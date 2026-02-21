package vn.edu.haui.scheduler.application.dto;

public class HocKyDto
{
	private Long id;

	private String tenHocKy;

	private String namHoc;

	public HocKyDto()
	{
	}

	public HocKyDto(Long id, String tenHocKy, String namHoc)
	{
		this.id = id;
		this.tenHocKy = tenHocKy;
		this.namHoc = namHoc;
	}

	public Long getId()
	{
		return id;
	}

	public String getTenHocKy()
	{
		return tenHocKy;
	}

	public String getNamHoc()
	{
		return namHoc;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setTenHocKy(String tenHocKy)
	{
		this.tenHocKy = tenHocKy;
	}

	public void setNamHoc(String namHoc)
	{
		this.namHoc = namHoc;
	}
}