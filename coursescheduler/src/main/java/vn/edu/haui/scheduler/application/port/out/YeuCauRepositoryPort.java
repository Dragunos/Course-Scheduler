package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.YeuCau;

import java.util.List;
import java.util.Optional;

public interface YeuCauRepositoryPort
{
	long save(long nguoiTaoId, long danhSachLopId, String tenYeuCau) throws Exception;

	Optional<YeuCau> findById(long id) throws Exception;

	List<YeuCau> findByNguoiTaoId(long nguoiTaoId) throws Exception;

	void deleteById(long id) throws Exception;

	long createFromThoiKhoaBieu(long thoiKhoaBieuId) throws Exception;

}
