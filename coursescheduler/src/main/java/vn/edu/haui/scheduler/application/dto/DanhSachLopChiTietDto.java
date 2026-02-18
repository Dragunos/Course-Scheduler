package vn.edu.haui.scheduler.application.dto;

import java.util.ArrayList;
import java.util.List;

public class DanhSachLopChiTietDto
{

	private Long lopHocPhanId;

	private String maLop;

	private String maHocPhan;

	private String tenHocPhan;

	private Integer soTinChi;

	private String tenGiangVien;

	private String hinhThucDay;

	private String diaDiem;

	private List<LichHocDto> lichHoc;

	private Integer batBuoc;

	public DanhSachLopChiTietDto()
	{
		this.lichHoc = new ArrayList<>();
		this.batBuoc = 0;
	}

	public DanhSachLopChiTietDto(
			Long lopHocPhanId,
			String maLop,
			String maHocPhan,
			String tenHocPhan,
			Integer soTinChi,
			String tenGiangVien,
			String hinhThucDay,
			String diaDiem,
			List<LichHocDto> lichHoc,
			Integer batBuoc)
	{
		this.lopHocPhanId = lopHocPhanId;
		this.maLop = maLop;
		this.maHocPhan = maHocPhan;
		this.tenHocPhan = tenHocPhan;
		this.soTinChi = soTinChi;
		this.tenGiangVien = tenGiangVien;
		this.hinhThucDay = hinhThucDay;
		this.diaDiem = diaDiem;
		this.lichHoc = (lichHoc == null) ? new ArrayList<>() : new ArrayList<>(lichHoc);
		this.batBuoc = (batBuoc == null) ? 0 : batBuoc;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
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

	public Integer getSoTinChi()
	{
		return soTinChi;
	}

	public void setSoTinChi(Integer soTinChi)
	{
		this.soTinChi = soTinChi;
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

	public List<LichHocDto> getLichHoc()
	{
		return lichHoc;
	}

	public void setLichHoc(List<LichHocDto> lichHoc)
	{
		this.lichHoc = (lichHoc == null) ? new ArrayList<>() : new ArrayList<>(lichHoc);
	}

	public Integer getBatBuoc()
	{
		return batBuoc;
	}

	public void setBatBuoc(Integer batBuoc)
	{
		this.batBuoc = (batBuoc == null) ? 0 : batBuoc;
	}

	public boolean isBatBuoc()
	{
		return batBuoc != null && batBuoc == 1;
	}
}
