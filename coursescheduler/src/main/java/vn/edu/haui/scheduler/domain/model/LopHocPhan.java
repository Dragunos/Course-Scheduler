package vn.edu.haui.scheduler.domain.model;

import java.util.List;

public class LopHocPhan
{
	private Long id;

	private String maLop;

	private Integer hocPhanId;

	private Integer giangVienId;

	private String hinhThucDay;

	private String diaDiem;

	private List<Object> danhSachBuoiHoc;

	public LopHocPhan()
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

	public String getMaLop()
	{
		return maLop;
	}

	public void setMaLop(String maLop)
	{
		this.maLop = maLop;
	}

	public Integer getHocPhanId()
	{
		return hocPhanId;
	}

	public void setHocPhanId(Integer hocPhanId)
	{
		this.hocPhanId = hocPhanId;
	}

	public Integer getGiangVienId()
	{
		return giangVienId;
	}

	public void setGiangVienId(Integer giangVienId)
	{
		this.giangVienId = giangVienId;
	}

	public String getHinhThucDay()
	{
		return hinhThucDay;
	}

	public void setHinhThucDay(String hinhThucDay)
	{
		this.hinhThucDay = hinhThucDay;
	}

	public String getDiaDiem()
	{
		return diaDiem;
	}

	public void setDiaDiem(String diaDiem)
	{
		this.diaDiem = diaDiem;
	}

	public List<Object> getDanhSachBuoiHoc()
	{
		return danhSachBuoiHoc;
	}

	public void setDanhSachBuoiHoc(List<Object> danhSachBuoiHoc)
	{
		this.danhSachBuoiHoc = danhSachBuoiHoc;
	}
}
