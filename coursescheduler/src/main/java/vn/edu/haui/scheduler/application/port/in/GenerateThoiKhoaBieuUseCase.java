package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;

public interface GenerateThoiKhoaBieuUseCase
{
	List<ThoiKhoaBieuDto> generate(Long nguoiDungId,
			Long danhSachLopId,
			List<RangBuocToiUuDto> rangBuocDtos,
			int topK);

	ThoiKhoaBieuDto regenerate(Long nguoiDungId,
			Long thoiKhoaBieuId,
			List<RangBuocToiUuDto> newConstraints,
			int topK);

	void saveAll(Long nguoiDungId,
			List<ThoiKhoaBieuDto> selectedDtos,
			boolean overwrite);
}