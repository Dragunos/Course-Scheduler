package vn.edu.haui.scheduler.application.dto;

public class DanhSachLopChiTietDto
{
	private Long danhSachLopId;

	private Long lopHocPhanId;

	private Boolean batBuoc;

	public DanhSachLopChiTietDto()
	{
	}

	public DanhSachLopChiTietDto(Long danhSachLopId,
			Long lopHocPhanId,
			Boolean batBuoc)
	{
		this.danhSachLopId = danhSachLopId;
		this.lopHocPhanId = lopHocPhanId;
		this.batBuoc = batBuoc;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public Boolean getBatBuoc()
	{
		return batBuoc;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}

	public void setBatBuoc(Boolean batBuoc)
	{
		this.batBuoc = batBuoc;
	}
}