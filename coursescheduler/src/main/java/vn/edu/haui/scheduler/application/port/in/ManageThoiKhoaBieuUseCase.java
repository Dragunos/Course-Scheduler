package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

public interface ManageThoiKhoaBieuUseCase
{
	List<ThoiKhoaBieuDto> findAllByUser(Long nguoiDungId);

	ThoiKhoaBieuDto findDetail(Long nguoiDungId, Long thoiKhoaBieuId);

	ThoiKhoaBieuDto rename(Long nguoiDungId,
			Long thoiKhoaBieuId,
			String newName);

	void delete(Long nguoiDungId, Long thoiKhoaBieuId);
}