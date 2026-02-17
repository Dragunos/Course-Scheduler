package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.PhuongAnThoiKhoaBieuDto;

import java.util.List;

public interface SinhThoiKhoaBieuUseCase
{
	long taoYeuCau(long nguoiDungId, long danhSachLopId, String tenYeuCau) throws Exception;

	List<PhuongAnThoiKhoaBieuDto> chayToiUu(long yeuCauId, int topK, long timeLimitMillis) throws Exception;

	long luuPhuongAn(long nguoiDungId, long danhSachLopId, String tenPhuongAn, double diem, List<Long> lopHocPhanIds)
			throws Exception;

	List<PhuongAnThoiKhoaBieuDto> toiUuLai(long thoiKhoaBieuId, int topK, long timeLimitMillis) throws Exception;
}
