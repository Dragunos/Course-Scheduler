package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

public interface ManageThoiKhoaBieuUseCase
{
	List<ThoiKhoaBieuDto> getAllThoiKhoaBieuByNguoiDungId(long nguoiDungId) throws Exception;

	ThoiKhoaBieuDto getThoiKhoaBieuById(long thoiKhoaBieuId, long nguoiDungId) throws Exception;

	ThoiKhoaBieuDto updateTenThoiKhoaBieu(long thoiKhoaBieuId, long nguoiDungId, String tenMoi) throws Exception;

	void deleteThoiKhoaBieu(long thoiKhoaBieuId, long nguoiDungId) throws Exception;
}
