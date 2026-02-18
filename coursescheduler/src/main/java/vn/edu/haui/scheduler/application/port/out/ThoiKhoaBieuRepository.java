package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Optional;

import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;

public interface ThoiKhoaBieuRepository
{
	long save(long nguoiDungId, long danhSachLopId, String tenPhuongAn, double diem) throws Exception;

	void saveChiTiet(long thoiKhoaBieuId, List<Long> lopHocPhanIds) throws Exception;

	List<Long> findChiTietByThoiKhoaBieuId(long id) throws Exception;

	long findDanhSachLopId(long thoiKhoaBieuId) throws Exception;

	List<ThoiKhoaBieu> findByNguoiDungId(long nguoiDungId) throws Exception;

	Optional<ThoiKhoaBieu> findById(long id) throws Exception;

	void updateTenPhuongAn(long id, String tenMoi) throws Exception;

	void deleteById(long id) throws Exception;
}
