package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

import java.util.List;

public interface GenerateThoiKhoaBieuUseCase
{
	long createYeuCau(long nguoiDungId, long danhSachLopId, String tenYeuCau) throws Exception;

	List<ThoiKhoaBieuDto> generateThoiKhoaBieu(long yeuCauId, int topK, long timeLimitMillis) throws Exception;

	long saveThoiKhoaBieu(long nguoiDungId, long danhSachLopId, String tenPhuongAn, double diemDanhGia,
			List<Long> lopHocPhanIds)
			throws Exception;

	List<ThoiKhoaBieuDto> regenerateThoiKhoaBieu(long thoiKhoaBieuId, int topK, long timeLimitMillis) throws Exception;
}
