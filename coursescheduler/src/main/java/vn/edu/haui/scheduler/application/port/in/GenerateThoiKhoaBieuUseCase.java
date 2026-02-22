package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

public interface GenerateThoiKhoaBieuUseCase
{
	List<ThoiKhoaBieuDto> generate(
			Long nguoiDungId,
			Long yeuCauId,
			int topK);

	ThoiKhoaBieuDto regenerate(
			Long nguoiDungId,
			Long thoiKhoaBieuId,
			Long yeuCauId,
			int topK);

	void saveAll(
			Long nguoiDungId,
			List<ThoiKhoaBieuDto> selectedDtos,
			boolean overwrite);
}