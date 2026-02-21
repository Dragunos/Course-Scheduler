package vn.edu.haui.scheduler.application.dto;

public class HocPhanDto
{
	private Long id;

	private String maHocPhan;

	private String tenHocPhan;

	private Integer soTinChi;

	public HocPhanDto()
	{
	}

	public HocPhanDto(Long id,
			String maHocPhan,
			String tenHocPhan,
			Integer soTinChi)
	{
		this.id = id;
		this.maHocPhan = maHocPhan;
		this.tenHocPhan = tenHocPhan;
		this.soTinChi = soTinChi;
	}

	public Long getId()
	{
		return id;
	}

	public String getMaHocPhan()
	{
		return maHocPhan;
	}

	public String getTenHocPhan()
	{
		return tenHocPhan;
	}

	public Integer getSoTinChi()
	{
		return soTinChi;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setMaHocPhan(String maHocPhan)
	{
		this.maHocPhan = maHocPhan;
	}

	public void setTenHocPhan(String tenHocPhan)
	{
		this.tenHocPhan = tenHocPhan;
	}

	public void setSoTinChi(Integer soTinChi)
	{
		this.soTinChi = soTinChi;
	}
}