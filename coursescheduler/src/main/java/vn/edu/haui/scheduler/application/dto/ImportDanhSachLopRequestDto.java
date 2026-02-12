package vn.edu.haui.scheduler.application.dto;

public class ImportDanhSachLopRequestDto
{
	private final String filePath;

	private final String tenDanhSach;

	private final Long nguoiTaoId;

	private final Long hocKyId;

	private final boolean laCongKhai;

	public ImportDanhSachLopRequestDto(String filePath,
			String tenDanhSach,
			Long nguoiTaoId,
			Long hocKyId,
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

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public Long getHocKyId()
	{
		return hocKyId;
	}

	public boolean isLaCongKhai()
	{
		return laCongKhai;
	}
}
