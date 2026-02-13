package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public interface XuatDanhSachLopUseCase
{
	/**
	 * Xuất danh sách lớp học phần ra file (CSV hoặc Excel).
	 *
	 * @param nguoiDungId id của người dùng thực hiện hành động (dùng để kiểm tra quyền / context)
	 * @param danhSachId id của danh sách lớp cần xuất
	 * @param duongDanFile đường dẫn file đích (ví dụ "/tmp/danhsach.csv" hoặc "C:\\temp\\ds.xlsx")
	 * @param dinhDang "CSV" hoặc "EXCEL" (không phân biệt hoa thường). Nếu null => mặc định là "CSV"
	 * @throws ValidationException nếu tham số không hợp lệ hoặc danh sách không tồn tại / không có quyền
	 * @throws PersistenceException nếu lỗi truy vấn CSDL hoặc lỗi khi ghi tệp
	 */
	void xuatDanhSach(Long nguoiDungId, Long danhSachId, String duongDanFile, String dinhDang)
			throws ValidationException, PersistenceException;
}
