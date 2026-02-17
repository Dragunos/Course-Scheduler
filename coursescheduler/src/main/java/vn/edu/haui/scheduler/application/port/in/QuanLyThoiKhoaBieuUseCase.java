package vn.edu.haui.scheduler.application.port.in;

import java.util.List;
import vn.edu.haui.scheduler.application.dto.PhuongAnThoiKhoaBieuDto;

public interface QuanLyThoiKhoaBieuUseCase
{
	List<PhuongAnThoiKhoaBieuDto> layDanhSachTheoNguoiDung(long nguoiDungId) throws Exception;

	PhuongAnThoiKhoaBieuDto xemChiTiet(long thoiKhoaBieuId, long nguoiDungId) throws Exception;

	PhuongAnThoiKhoaBieuDto doiTen(long thoiKhoaBieuId, long nguoiDungId, String tenMoi) throws Exception;

	void xoa(long thoiKhoaBieuId, long nguoiDungId) throws Exception;
}
