package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import java.util.Optional;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;

public interface DanhSachLopRepository
{
	Long save(String tenDanhSach, Long nguoiTaoId, boolean laCongKhai, Long hocKyId);

	void addChiTiet(Long danhSachId, Long lopHocPhanId);

	List<DanhSachLop> findByNguoiTaoOrShared(Long nguoiDungId);

	Optional<DanhSachLop> findByIdWithDetails(Long danhSachId);

	boolean isCreator(Long danhSachId, Long nguoiDungId);

	boolean isShared(Long danhSachId, Long nguoiDungId);

	void updateHeader(Long danhSachId, String tenDanhSach, Long hocKyId);

	void deleteAllChiTiet(Long danhSachId);

	void deleteDanhSach(Long danhSachId);

	List<DanhSachLop> findAllPublic();

	boolean isPublic(Long danhSachId);
}
