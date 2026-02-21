package vn.edu.haui.scheduler.application.port.out.temp;

import vn.edu.haui.scheduler.domain.model.RangBuocToiUu;

import java.util.List;
import java.util.Optional;

public interface RangBuocToiUuRepository
{
	Optional<RangBuocToiUu> findById(Long id);

	List<RangBuocToiUu> findByThoiKhoaBieuId(Long thoiKhoaBieuId);

	List<RangBuocToiUu> findByYeuCauId(Long yeuCauId);

	RangBuocToiUu save(RangBuocToiUu rangBuoc);

	void deleteById(Long id);
}