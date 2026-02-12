package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class UpdateDanhSachLopRequestDto
{
	private final Long id;

	private final String tenDanhSach;

	private final Long hocKyId;

	private final List<Long> lopHocPhanIds;

	public UpdateDanhSachLopRequestDto(Long id,
			String tenDanhSach,
			Long hocKyId,
			List<Long> lopHocPhanIds)
	{
		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.hocKyId = hocKyId;
		this.lopHocPhanIds = lopHocPhanIds;
	}

	public Long getId()
	{
		return id;
	}

	public String getTenDanhSach()
	{
		return tenDanhSach;
	}

	public Long getHocKyId()
	{
		return hocKyId;
	}

	public List<Long> getLopHocPhanIds()
	{
		return lopHocPhanIds;
	}
}
