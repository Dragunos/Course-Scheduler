package vn.edu.haui.scheduler.application.dto;

public class ThoiKhoaBieuChiTietDto
{
	private Long thoiKhoaBieuId;

	private Long lopHocPhanId;

	public ThoiKhoaBieuChiTietDto()
	{
	}

	public ThoiKhoaBieuChiTietDto(Long thoiKhoaBieuId, Long lopHocPhanId)
	{
		this.thoiKhoaBieuId = thoiKhoaBieuId;
		this.lopHocPhanId = lopHocPhanId;
	}

	public Long getThoiKhoaBieuId()
	{
		return thoiKhoaBieuId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public void setThoiKhoaBieuId(Long thoiKhoaBieuId)
	{
		this.thoiKhoaBieuId = thoiKhoaBieuId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}
}