package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class DanhSachLopChiTietDto
{
	private final Long lopHocPhanId;

	private final String maLop;

	private final String tenHocPhan;

	private final Integer soTinChi;

	private final String tenGiangVien;

	private final String hinhThucDay;

	private final String diaDiem;

	private final List<LichHocDto> lichHoc;

	public DanhSachLopChiTietDto(Long lopHocPhanId,
			String maLop,
			String tenHocPhan,
			Integer soTinChi,
			String tenGiangVien,
			String hinhThucDay,
			String diaDiem,
			List<LichHocDto> lichHoc)
	{
		this.lopHocPhanId = lopHocPhanId;
		this.maLop = maLop;
		this.tenHocPhan = tenHocPhan;
		this.soTinChi = soTinChi;
		this.tenGiangVien = tenGiangVien;
		this.hinhThucDay = hinhThucDay;
		this.diaDiem = diaDiem;
		this.lichHoc = lichHoc;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public String getMaLop()
	{
		return maLop;
	}

	public String getTenHocPhan()
	{
		return tenHocPhan;
	}

	public Integer getSoTinChi()
	{
		return soTinChi;
	}

	public String getTenGiangVien()
	{
		return tenGiangVien;
	}

	public String getHinhThucDay()
	{
		return hinhThucDay;
	}

	public String getDiaDiem()
	{
		return diaDiem;
	}

	public List<LichHocDto> getLichHoc()
	{
		return lichHoc;
	}
}
