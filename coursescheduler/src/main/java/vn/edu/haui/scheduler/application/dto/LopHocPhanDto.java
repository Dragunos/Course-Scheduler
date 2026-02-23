package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class LopHocPhanDto
{
	private Long id;

	private String maLop;

	private Long hocPhanId;

	private Long giangVienId;

	private String hinhThucDay;

	private String diaDiem;

	private GiangVienDto giangVien;

	private List<LichHocDto> lichHocDanhSach;

	public LopHocPhanDto()
	{
	}

	public LopHocPhanDto(Long id,
			String maLop,
			Long hocPhanId,
			Long giangVienId,
			String hinhThucDay,
			String diaDiem)
	{
		this.id = id;
		this.maLop = maLop;
		this.hocPhanId = hocPhanId;
		this.giangVienId = giangVienId;
		this.hinhThucDay = hinhThucDay;
		this.diaDiem = diaDiem;
	}

	public Long getId()
	{
		return id;
	}

	public String getMaLop()
	{
		return maLop;
	}

	public Long getHocPhanId()
	{
		return hocPhanId;
	}

	public Long getGiangVienId()
	{
		return giangVienId;
	}

	public String getHinhThucDay()
	{
		return hinhThucDay;
	}

	public String getDiaDiem()
	{
		return diaDiem;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setMaLop(String maLop)
	{
		this.maLop = maLop;
	}

	public void setHocPhanId(Long hocPhanId)
	{
		this.hocPhanId = hocPhanId;
	}

	public void setGiangVienId(Long giangVienId)
	{
		this.giangVienId = giangVienId;
	}

	public void setHinhThucDay(String hinhThucDay)
	{
		this.hinhThucDay = hinhThucDay;
	}

	public void setDiaDiem(String diaDiem)
	{
		this.diaDiem = diaDiem;
	}

	public GiangVienDto getGiangVien()
	{
		return giangVien;
	}

	public void setGiangVien(GiangVienDto giangVien)
	{
		this.giangVien = giangVien;
	}

	public List<LichHocDto> getLichHocDanhSach()
	{
		return lichHocDanhSach;
	}

	public void setLichHocDanhSach(List<LichHocDto> lichHocDanhSach)
	{
		this.lichHocDanhSach = lichHocDanhSach;
	}
	
	
}