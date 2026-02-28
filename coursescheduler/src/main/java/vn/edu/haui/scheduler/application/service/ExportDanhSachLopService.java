package vn.edu.haui.scheduler.application.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ExportDanhSachLopException;
import vn.edu.haui.scheduler.application.exception.UnauthorizedAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ExportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.infrastructure.io.exports.CompositeFileExporter;
import vn.edu.haui.scheduler.infrastructure.io.exports.FileExporter;

public class ExportDanhSachLopService implements ExportDanhSachLopUseCase
{
	private final DanhSachLopRepository danhSachLopRepository;

	private final NguoiDungRepository nguoiDungRepository;

	private final FileExporter fileExporter;

	public ExportDanhSachLopService(
			DanhSachLopRepository danhSachLopRepository,
			NguoiDungRepository nguoiDungRepository,
			CompositeFileExporter fileExporter)
	{
		this.danhSachLopRepository = danhSachLopRepository;
		this.nguoiDungRepository = nguoiDungRepository;
		this.fileExporter = fileExporter;
	}

	@Override
	public void exportDanhSach(
			Long nguoiDungId,
			Long danhSachLopId,
			String format,
			String outputPath)
	{
		if(nguoiDungId == null)
			throw new ValidationException("Người Dùng ID không được phép NULL");

		if(danhSachLopId == null)
			throw new ValidationException("Danh Sách Lớp ID không được phép NULL");

		if(format == null || format.isBlank())
			throw new ValidationException("Loại định dạng không được để trống");

		if(outputPath == null || outputPath.isBlank())
			throw new ValidationException("Đầu ra không được để trống");

		NguoiDung nguoiDung = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		DanhSachLop danhSach = danhSachLopRepository
				.findById(danhSachLopId)
				.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", danhSachLopId));

		if(!danhSach.isLaCongKhai() &&
				!danhSach.getNguoiTao().getId().equals(nguoiDung.getId())) {
			throw new UnauthorizedAccessException("Bạn không có đủ quyền hạn để can thiệp đến Danh Sách Lớp này");
		}

		List<String> headers = buildHeaders();
		List<Map<String, String>> rows = buildRows(danhSach);

		try {
			Path path = Path.of(outputPath);

			String normalized = format.trim().toUpperCase();

			switch(normalized) {
				case "CSV" -> fileExporter.exportCsv(path, headers, rows);

				case "EXCEL" -> fileExporter.exportExcel(path, headers, rows);

				case "PDF" -> fileExporter.exportPdf(
						path,
						"Danh sách lớp học phần",
						headers,
						rows);

				default -> throw new ValidationException("Định dạng Export khong được hỗ trợ: " + format);
			}
		}
		catch(IOException e) {
			throw new ExportDanhSachLopException("Lỗi Xuất File", e);
		}
	}

	private List<String> buildHeaders()
	{
		List<String> headers = new ArrayList<>();

		headers.add("Ma Lop");
		headers.add("Ma Hoc Phan");
		headers.add("Ten Hoc Phan");
		headers.add("Giang Vien");
		headers.add("Hinh Thuc Day");
		headers.add("Dia Diem");
		headers.add("Lich Hoc");

		return headers;
	}

	private List<Map<String, String>> buildRows(DanhSachLop danhSach)
	{
		List<Map<String, String>> rows = new ArrayList<>();

		for(DanhSachLopChiTiet chiTiet : danhSach.getChiTietList()) {

			LopHocPhan lop = chiTiet.getLopHocPhan();

			Map<String, String> row = new HashMap<>();

			row.put("Ma Lop", safe(lop.getMaLop()));
			row.put("Ma Hoc Phan", safe(lop.getHocPhan().getMaHocPhan()));
			row.put("Ten Hoc Phan", safe(lop.getHocPhan().getTenHocPhan()));
			row.put("Giang Vien",
					lop.getGiangVien() != null
							? lop.getGiangVien().getTenGiangVien()
							: "");
			row.put("Hinh Thuc Day", safe(lop.getHinhThucDay()));
			row.put("Dia Diem", safe(lop.getDiaDiem()));
			row.put("Lich Hoc", buildLichHocString(lop));

			rows.add(row);
		}

		return rows;
	}

	private String buildLichHocString(LopHocPhan lop)
	{
		StringBuilder sb = new StringBuilder();

		for(LichHoc lh : lop.getLichHocList()) {
			sb.append("Thu ")
					.append(lh.getThu())
					.append(" (")
					.append(lh.getTietBatDau())
					.append("-")
					.append(lh.getTietKetThuc())
					.append("); ");
		}

		return sb.toString().trim();
	}

	private String safe(String value)
	{
		return value == null ? "" : value;
	}
}