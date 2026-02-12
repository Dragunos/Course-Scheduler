package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DanhSachLopDto
{
	public Long id;

	public String tenDanhSach;

	public Long nguoiTaoId;

	public Integer laCongKhai;

	public Long hocKyId;

	public LocalDateTime ngayTao;

	public List<DanhSachLopChiTietDto> chiTiet;
}
