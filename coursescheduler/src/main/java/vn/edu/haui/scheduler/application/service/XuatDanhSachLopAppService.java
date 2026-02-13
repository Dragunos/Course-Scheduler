package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.XuatDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepositoryPort;
import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.infrastructure.io.exports.CsvExporter;
import vn.edu.haui.scheduler.infrastructure.io.exports.ExcelExporter;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Service thực hiện xuất danh sách lớp học phần. Sử dụng DanhSachLopRepositoryPort#findByIdWithDetails để lấy
 * DanhSachLopDto (với chi tiết).
 */
public class XuatDanhSachLopAppService implements XuatDanhSachLopUseCase
{

	private final DanhSachLopRepositoryPort danhSachRepo;

	private final CsvExporter csvExporter;

	private final ExcelExporter excelExporter;

	public XuatDanhSachLopAppService(DanhSachLopRepositoryPort danhSachRepo,
			CsvExporter csvExporter,
			ExcelExporter excelExporter)
	{
		this.danhSachRepo = danhSachRepo;
		this.csvExporter = csvExporter;
		this.excelExporter = excelExporter;
	}

	@Override
	public void xuat(Long danhSachLopId, String dinhDang, String duongDanDuoiTen)
			throws ValidationException, PersistenceException
	{
		if(danhSachLopId == null) {
			throw new ValidationException("Danh sách lớp không hợp lệ (null id).");
		}
		if(duongDanDuoiTen == null || duongDanDuoiTen.trim().isEmpty()) {
			throw new ValidationException("Đường dẫn file xuất không được để trống.");
		}

		try {
			Optional<DanhSachLopDto> opt = danhSachRepo.findByIdWithDetails(danhSachLopId);
			if(!opt.isPresent()) {
				throw new ValidationException("Không tìm thấy danh sách lớp với id = " + danhSachLopId);
			}

			DanhSachLopDto ds = opt.get();

			// Header cố định
			List<String> headers = Arrays.asList(
					"ten_danh_sach",
					"ma_lop",
					"ma_hoc_phan", // DTO hiện tại không cung cấp ma_hoc_phan -> giữ cột nhưng để trống
					"ten_hoc_phan",
					"so_tin_chi",
					"ten_giang_vien",
					"hinh_thuc_day",
					"dia_diem",
					"thu",
					"tiet_bat_dau",
					"tiet_ket_thuc",
					"bat_buoc");

			List<Map<String, String>> rows = new ArrayList<>();

			List<DanhSachLopChiTietDto> chiTiets = ds.getChiTiet();
			if(chiTiets == null) chiTiets = Collections.emptyList();

			for(DanhSachLopChiTietDto ct : chiTiets) {
				int batBuoc = (ct.getBatBuoc() == null) ? 0 : ct.getBatBuoc();

				String maLop = safe(ct.getMaLop());
				// DTO không có maHocPhan, giữ rỗng
				String maHocPhan = "";
				String tenHocPhan = safe(ct.getTenHocPhan());
				String soTinChi = ct.getSoTinChi() == null ? "" : String.valueOf(ct.getSoTinChi());
				String tenGiangVien = safe(ct.getTenGiangVien());
				String hinhThucDay = safe(ct.getHinhThucDay());
				String diaDiem = safe(ct.getDiaDiem());

				List<LichHocDto> lichs = ct.getLichHoc();
				if(lichs == null || lichs.isEmpty()) {
					Map<String, String> r = new LinkedHashMap<>();
					r.put("ten_danh_sach", safe(ds.getTenDanhSach()));
					r.put("ma_lop", maLop);
					r.put("ma_hoc_phan", maHocPhan);
					r.put("ten_hoc_phan", tenHocPhan);
					r.put("so_tin_chi", soTinChi);
					r.put("ten_giang_vien", tenGiangVien);
					r.put("hinh_thuc_day", hinhThucDay);
					r.put("dia_diem", diaDiem);
					r.put("thu", "");
					r.put("tiet_bat_dau", "");
					r.put("tiet_ket_thuc", "");
					r.put("bat_buoc", String.valueOf(batBuoc));
					rows.add(r);
				}
				else {
					for(LichHocDto l : lichs) {
						Map<String, String> r = new LinkedHashMap<>();
						r.put("ten_danh_sach", safe(ds.getTenDanhSach()));
						r.put("ma_lop", maLop);
						r.put("ma_hoc_phan", maHocPhan);
						r.put("ten_hoc_phan", tenHocPhan);
						r.put("so_tin_chi", soTinChi);
						r.put("ten_giang_vien", tenGiangVien);
						r.put("hinh_thuc_day", hinhThucDay);
						r.put("dia_diem", diaDiem);
						r.put("thu", l == null || l.getThu() == null ? "" : String.valueOf(l.getThu()));
						r.put("tiet_bat_dau",
								l == null || l.getTietBatDau() == null ? "" : String.valueOf(l.getTietBatDau()));
						r.put("tiet_ket_thuc",
								l == null || l.getTietKetThuc() == null ? "" : String.valueOf(l.getTietKetThuc()));
						r.put("bat_buoc", String.valueOf(batBuoc));
						rows.add(r);
					}
				}
			}

			Path output = Paths.get(duongDanDuoiTen);
			String fmt = (dinhDang == null) ? "CSV" : dinhDang.trim().toUpperCase();

			if("EXCEL".equals(fmt) || duongDanDuoiTen.toLowerCase().endsWith(".xlsx")) {
				excelExporter.export(output, headers, rows);
			}
			else {
				csvExporter.export(output, headers, rows);
			}
		}
		catch(ValidationException v) {
			throw v;
		}
		catch(RuntimeException re) {
			throw new PersistenceException("Lỗi khi truy vấn dữ liệu: " + re.getMessage(), re);
		}
		catch(Exception e) {
			throw new PersistenceException("Lỗi khi xuất dữ liệu: " + e.getMessage(), e);
		}
		catch(Error er) {
			throw new PersistenceException("Lỗi hệ thống khi xuất dữ liệu: " + er.getMessage(), er);
		}
	}

	private String safe(String v)
	{
		return v == null ? "" : v;
	}
}
