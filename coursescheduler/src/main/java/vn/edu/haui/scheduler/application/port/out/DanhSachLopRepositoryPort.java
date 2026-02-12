package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Optional;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;

public interface DanhSachLopRepositoryPort
{
	int save(String tenDanhSach, int nguoiTaoId, boolean laCongKhai, Integer hocKyId) throws Exception;

	void addChiTiet(int danhSachId, int lopHocPhanId) throws Exception;

	List<DanhSachLopDto> findByNguoiTaoOrShared(int nguoiDungId) throws Exception;

	Optional<DanhSachLopDto> findByIdWithDetails(int danhSachId) throws Exception;

	boolean isCreator(int danhSachId, int nguoiDungId) throws Exception;

	boolean isShared(int danhSachId, int nguoiDungId) throws Exception;

	void updateHeader(int danhSachId, String tenDanhSach, Integer hocKyId) throws Exception;

	void deleteAllChiTiet(int danhSachId) throws Exception;

	void deleteDanhSach(int danhSachId) throws Exception;
}
