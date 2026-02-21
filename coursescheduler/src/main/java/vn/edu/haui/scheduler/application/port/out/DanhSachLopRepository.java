package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.DanhSachLop;

import java.util.List;
import java.util.Optional;

public interface DanhSachLopRepository
{
	DanhSachLop save(DanhSachLop danhSachLop);

	Optional<DanhSachLop> findById(Long id);

	List<DanhSachLop> findByNguoiTaoId(Long nguoiTaoId);

	List<DanhSachLop> findPublicLists();

	void deleteById(Long id);
}