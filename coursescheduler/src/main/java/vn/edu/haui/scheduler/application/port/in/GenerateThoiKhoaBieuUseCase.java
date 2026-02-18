package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

import java.util.List;

public interface GenerateThoiKhoaBieuUseCase
{
	long taoYeuCau(long nguoiDungId, long danhSachLopId, String tenYeuCau) throws Exception;

	List<ThoiKhoaBieuDto> chayToiUu(long yeuCauId, int topK, long timeLimitMillis) throws Exception;

	long luuPhuongAn(long nguoiDungId, long danhSachLopId, String tenPhuongAn, double diem, List<Long> lopHocPhanIds)
			throws Exception;

	List<ThoiKhoaBieuDto> toiUuLai(long thoiKhoaBieuId, int topK, long timeLimitMillis) throws Exception;
}
