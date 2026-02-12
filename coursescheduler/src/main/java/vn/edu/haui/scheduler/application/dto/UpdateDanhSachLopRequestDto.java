package vn.edu.haui.scheduler.application.dto;

import java.util.List;

public class UpdateDanhSachLopRequestDto
{
	public Long id;

	public String tenDanhSach;

	public Long hocKyId;

	public List<Long> lopHocPhanIds;
}
