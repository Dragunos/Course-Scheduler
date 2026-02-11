package vn.edu.haui.scheduler.application.dto;

public class ImportDanhSachLopRequestDto
{
	private final String filePath;

	private final String tenDanhSach;

	private final Integer nguoiTaoId;

	private final Integer hocKyId;

	private final boolean laCongKhai;

	public ImportDanhSachLopRequestDto(String filePath, String tenDanhSach, Integer nguoiTaoId, Integer hocKyId,
			boolean laCongKhai)
	{
		this.filePath = filePath;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTaoId = nguoiTaoId;
		this.hocKyId = hocKyId;
		this.laCongKhai = laCongKhai;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public String getTenDanhSach()
	{
		return tenDanhSach;
	}

	public Integer getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public Integer getHocKyId()
	{
		return hocKyId;
	}

	public boolean isLaCongKhai()
	{
		return laCongKhai;
	}
}