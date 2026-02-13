package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public interface XuatDanhSachLopUseCase
{
	/**
	 * Xuất danh sách lớp học phần của một danh sách (danhSachLopId) ra file CSV hoặc Excel.
	 *
	 * @param danhSachLopId id của danh sách lớp cần xuất
	 * @param dinhDang "CSV" hoặc "EXCEL" (không phân biệt hoa thường). Nếu null => mặc định CSV
	 * @param duongDanDuoiTen đường dẫn file đích (ví dụ "/tmp/danhsach.csv" hoặc "C:\tmp\ds.xlsx")
	 * @throws ValidationException nếu tham số không hợp lệ hoặc danh sách không tồn tại
	 * @throws PersistenceException nếu lỗi truy vấn CSDL hoặc lỗi ghi tệp
	 */
	void xuat(Long danhSachLopId, String dinhDang, String duongDanDuoiTen)
			throws ValidationException, PersistenceException;
}
