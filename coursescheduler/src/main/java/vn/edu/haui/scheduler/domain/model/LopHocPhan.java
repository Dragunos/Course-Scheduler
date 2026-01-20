package vn.edu.haui.scheduler.domain.model;

import java.util.List;

import vn.edu.haui.scheduler.domain.enums.HinhThucDay;

public class LopHocPhan
{
	private Long id;

	private String maLop;

	private HocPhan hocPhan;

	private GiangVien giangVien;

	private HinhThucDay hinhThucDay;

	private String diaDiem;

	private List<BuoiHoc> danhSachBuoiHoc;

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

	public HocPhan getHocPhan()
	{
		return hocPhan;
	}

	public void setHocPhan(HocPhan hocPhan)
	{
		this.hocPhan = hocPhan;
	}

	public GiangVien getGiangVien()
	{
		return giangVien;
	}

	public void setGiangVien(GiangVien giangVien)
	{
		this.giangVien = giangVien;
	}

	public HinhThucDay getHinhThucDay()
	{
		return hinhThucDay;
	}

	public void setHinhThucDay(HinhThucDay hinhThucDay)
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

	public List<BuoiHoc> getDanhSachBuoiHoc()
	{
		return danhSachBuoiHoc;
	}

	public void setDanhSachBuoiHoc(List<BuoiHoc> danhSachBuoiHoc)
	{
		this.danhSachBuoiHoc = danhSachBuoiHoc;
	}
}
