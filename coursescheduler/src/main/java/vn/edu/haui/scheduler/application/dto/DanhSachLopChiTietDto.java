package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class DanhSachLopChiTietDto
{
	private Long danhSachLopId;

	private Long lopHocPhanId;

	private Boolean batBuoc;

	private String maLop;

	private String maHocPhan;

	private String tenHocPhan;

	private String tenGiangVien;

	private String hinhThucDay;

	private String diaDiem;

	private List<LichHocDto> lichHocList;

	public DanhSachLopChiTietDto()
	{
	}

	public DanhSachLopChiTietDto(
			Long danhSachLopId,
			Long lopHocPhanId,
			Boolean batBuoc,
			String maLop,
			String maHocPhan,
			String tenHocPhan,
			String tenGiangVien,
			String hinhThucDay,
			String diaDiem,
			List<LichHocDto> lichHocList)
	{
		this.danhSachLopId = danhSachLopId;
		this.lopHocPhanId = lopHocPhanId;
		this.batBuoc = batBuoc;
		this.maLop = maLop;
		this.maHocPhan = maHocPhan;
		this.tenHocPhan = tenHocPhan;
		this.tenGiangVien = tenGiangVien;
		this.hinhThucDay = hinhThucDay;
		this.diaDiem = diaDiem;
		this.lichHocList = lichHocList;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}

	public Boolean getBatBuoc()
	{
		return batBuoc;
	}

	public void setBatBuoc(Boolean batBuoc)
	{
		this.batBuoc = batBuoc;
	}

	public String getMaLop()
	{
		return maLop;
	}

	public void setMaLop(String maLop)
	{
		this.maLop = maLop;
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

	public String getTenGiangVien()
	{
		return tenGiangVien;
	}

	public void setTenGiangVien(String tenGiangVien)
	{
		this.tenGiangVien = tenGiangVien;
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

	public List<LichHocDto> getLichHocList()
	{
		return lichHocList;
	}

	public void setLichHocList(List<LichHocDto> lichHocList)
	{
		this.lichHocList = lichHocList;
	}
}