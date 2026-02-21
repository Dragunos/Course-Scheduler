package vn.edu.haui.scheduler.application.dto.temp;

public class ImportDanhSachLopRequestDto
{
	private final String filePath;

	private final String tenDanhSach;

	private final Long nguoiTaoId;

	private final Long hocKyId;

	private final boolean laCongKhai;

	private final String mimeType;

	public ImportDanhSachLopRequestDto(
			String filePath,
			String tenDanhSach,
			Long nguoiTaoId,
			Long hocKyId,
			boolean laCongKhai,
			String mimeType)
	{
		this.filePath = filePath;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTaoId = nguoiTaoId;
		this.hocKyId = hocKyId;
		this.laCongKhai = laCongKhai;
		this.mimeType = mimeType;
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

	public String getMimeType()
	{
		return mimeType;
	}
}
