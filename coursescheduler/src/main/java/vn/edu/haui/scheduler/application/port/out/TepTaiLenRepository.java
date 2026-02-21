package vn.edu.haui.scheduler.application.port.out;

import vn.edu.haui.scheduler.domain.model.TepTaiLen;

import java.util.List;
import java.util.Optional;

public interface TepTaiLenRepository
{
	TepTaiLen save(TepTaiLen tepTaiLen);

	Optional<TepTaiLen> findById(Long id);

	List<TepTaiLen> findByNguoiTaoId(Long nguoiTaoId);

	void deleteById(Long id);
}