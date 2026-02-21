package vn.edu.haui.scheduler.application.dto;

public class ChiaSeDanhSachLopDto
{
	private Long danhSachLopId;

	private Long nguoiDungId;

	public ChiaSeDanhSachLopDto()
	{
	}

	public ChiaSeDanhSachLopDto(Long danhSachLopId, Long nguoiDungId)
	{
		this.danhSachLopId = danhSachLopId;
		this.nguoiDungId = nguoiDungId;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public Long getNguoiDungId()
	{
		return nguoiDungId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public void setNguoiDungId(Long nguoiDungId)
	{
		this.nguoiDungId = nguoiDungId;
	}
}