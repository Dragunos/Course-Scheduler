package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;

public interface ManageThoiKhoaBieuUseCase
{
	List<ThoiKhoaBieuDto> layDanhSachTheoNguoiDung(long nguoiDungId) throws Exception;

	ThoiKhoaBieuDto xemChiTiet(long thoiKhoaBieuId, long nguoiDungId) throws Exception;

	ThoiKhoaBieuDto doiTen(long thoiKhoaBieuId, long nguoiDungId, String tenMoi) throws Exception;

	void xoa(long thoiKhoaBieuId, long nguoiDungId) throws Exception;
}
