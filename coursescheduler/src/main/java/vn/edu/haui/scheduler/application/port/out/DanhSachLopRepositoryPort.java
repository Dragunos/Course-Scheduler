package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Optional;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;

public interface DanhSachLopRepositoryPort
{
	Long save(String tenDanhSach, Long nguoiTaoId, boolean laCongKhai, Long hocKyId) throws Exception;

	void addChiTiet(Long danhSachId, Long lopHocPhanId) throws Exception;

	List<DanhSachLopDto> findByNguoiTaoOrShared(Long nguoiDungId) throws Exception;

	Optional<DanhSachLopDto> findByIdWithDetails(Long danhSachId) throws Exception;

	boolean isCreator(Long danhSachId, Long nguoiDungId) throws Exception;

	boolean isShared(Long danhSachId, Long nguoiDungId) throws Exception;

	void updateHeader(Long danhSachId, String tenDanhSach, Long hocKyId) throws Exception;

	void deleteAllChiTiet(Long danhSachId) throws Exception;

	void deleteDanhSach(Long danhSachId) throws Exception;
}
