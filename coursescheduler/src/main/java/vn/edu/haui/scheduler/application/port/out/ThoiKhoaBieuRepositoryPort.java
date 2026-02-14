package vn.edu.haui.scheduler.application.port.out;

import java.util.List;

public interface ThoiKhoaBieuRepositoryPort
{
	long save(long nguoiDungId, long danhSachLopId, String tenPhuongAn, double diem) throws Exception;

	void saveChiTiet(long thoiKhoaBieuId, List<Long> lopHocPhanIds) throws Exception;

	List<Long> findChiTietByThoiKhoaBieuId(long id) throws Exception;

	long findDanhSachLopId(long thoiKhoaBieuId) throws Exception;
}
