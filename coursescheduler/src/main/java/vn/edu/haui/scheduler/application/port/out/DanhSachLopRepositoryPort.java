package vn.edu.haui.scheduler.application.port.out;

import java.sql.Connection;

public interface DanhSachLopRepositoryPort
{
	int save(Connection conn, String tenDanhSach, int nguoiTaoId, boolean laCongKhai, Integer hocKyId) throws Exception;

	void addChiTiet(Connection conn, int danhSachId, int lopHocPhanId) throws Exception;
}