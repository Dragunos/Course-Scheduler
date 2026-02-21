package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.YeuCau;

import java.util.List;
import java.util.Optional;

public interface YeuCauRepository
{
	YeuCau save(YeuCau yeuCau);

	Optional<YeuCau> findById(Long id);

	List<YeuCau> findByNguoiTaoId(Long nguoiTaoId);

	List<YeuCau> findByDanhSachLopId(Long danhSachLopId);

	void deleteById(Long id);
}