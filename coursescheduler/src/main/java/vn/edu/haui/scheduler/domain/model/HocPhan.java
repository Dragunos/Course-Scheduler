package vn.edu.haui.scheduler.domain.model;

public class HocPhan
{
	private Long id;

	private String maHocPhan;

	private String tenHocPhan;

	private Integer soTinChi;

	public HocPhan()
	{
	}

	public HocPhan(Long id, String maHocPhan, String tenHocPhan, Integer soTinChi)
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

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getMaHocPhan()
	{
		return maHocPhan;
	}

	public void setMaHocPhan(String maHocPhan)
	{
		this.maHocPhan = maHocPhan;
	}

	public String getTenHocPhan()
	{
		return tenHocPhan;
	}

	public void setTenHocPhan(String tenHocPhan)
	{
		this.tenHocPhan = tenHocPhan;
	}

	public Integer getSoTinChi()
	{
		return soTinChi;
	}

	public void setSoTinChi(Integer soTinChi)
	{
		this.soTinChi = soTinChi;
	}

	@Override
	public String toString()
	{
		return "HocPhan{" +
				"id=" + id +
				", maHocPhan='" + maHocPhan + '\'' +
				", tenHocPhan='" + tenHocPhan + '\'' +
				", soTinChi=" + soTinChi +
				'}';
	}
}