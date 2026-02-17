package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;

import java.util.List;
import java.util.Optional;

public interface RangBuocToiUuRepositoryPort
{
	RangBuocToiUuDto save(RangBuocToiUuDto dto) throws Exception;

	void deleteById(Long id) throws Exception;

	Optional<RangBuocToiUuDto> findById(Long id) throws Exception;

	List<RangBuocToiUuDto> findByYeuCauId(Long yeuCauId) throws Exception;

	List<RangBuocToiUuDto> findByThoiKhoaBieuId(Long thoiKhoaBieuId) throws Exception;
}
