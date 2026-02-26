package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

import java.util.List;

public interface GenerateThoiKhoaBieuUseCase
{

	List<ThoiKhoaBieuDto> generate(
			Long nguoiDungId,
			Long danhSachLopId,
			List<String> maHocPhanDangKy,
			List<RangBuocToiUuDto> rangBuocDtos,
			int topK,
			long timeLimitMillis);

	List<ThoiKhoaBieuDto> reGenerate(
			Long nguoiDungId,
			Long thoiKhoaBieuId,
			List<RangBuocToiUuDto> updatedConstraints,
			int topK,
			long timeLimitMillis);

	void save(
			Long nguoiDungId,
			Long danhSachLopId,
			List<ThoiKhoaBieuDto> selectedResults,
			boolean overwrite);
}