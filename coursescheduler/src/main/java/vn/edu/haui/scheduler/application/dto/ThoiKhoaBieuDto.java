package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ThoiKhoaBieuDto
{
	private Long id;

	private Long nguoiDungId;

	private Long danhSachLopId;

	private String tenPhuongAn;

	private Double diemDanhGia;

	private LocalDateTime ngayTao;

	private List<Long> lopHocPhanIdList;

	private List<LopHocPhanDto> danhSachLopHocPhan;

	public ThoiKhoaBieuDto()
	{
	}

	public Long getId()
	{
		return id;
	}

	public Long getNguoiDungId()
	{
		return nguoiDungId;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public String getTenPhuongAn()
	{
		return tenPhuongAn;
	}

	public Double getDiemDanhGia()
	{
		return diemDanhGia;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public List<Long> getLopHocPhanIdList()
	{
		return lopHocPhanIdList;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setNguoiDungId(Long nguoiDungId)
	{
		this.nguoiDungId = nguoiDungId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public void setTenPhuongAn(String tenPhuongAn)
	{
		this.tenPhuongAn = tenPhuongAn;
	}

	public void setDiemDanhGia(Double diemDanhGia)
	{
		this.diemDanhGia = diemDanhGia;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}

	public void setLopHocPhanIdList(List<Long> lopHocPhanIdList)
	{
		this.lopHocPhanIdList = lopHocPhanIdList;
	}

	public List<LopHocPhanDto> getDanhSachLopHocPhan()
	{
		return danhSachLopHocPhan;
	}

	public void setDanhSachLopHocPhan(List<LopHocPhanDto> danhSachLopHocPhan)
	{
		this.danhSachLopHocPhan = danhSachLopHocPhan;
	}
}