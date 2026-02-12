package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class DanhSachLop
{
	public Long id;

	public String tenDanhSach;

	public Long nguoiTaoId;

	public Integer laCongKhai;

	public Long hocKyId;

	public LocalDateTime ngayTao;

	public List<Long> lopHocPhanIds;
}
