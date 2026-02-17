package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.XuatDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepositoryPort;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
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

public class XuatDanhSachLopAppService implements XuatDanhSachLopUseCase
{

	private final DanhSachLopRepositoryPort danhSachRepo;

	private final CsvExporter csvExporter;

	private final ExcelExporter excelExporter;

	public XuatDanhSachLopAppService(
			DanhSachLopRepositoryPort danhSachRepo,
			CsvExporter csvExporter,
			ExcelExporter excelExporter)
	{
		this.danhSachRepo = danhSachRepo;
		this.csvExporter = csvExporter;
		this.excelExporter = excelExporter;
	}

	@Override
	public void xuatDanhSach(
			Long nguoiDungId,
			Long danhSachId,
			String duongDanFile,
			String dinhDang)
			throws ValidationException, PersistenceException
	{
		if(nguoiDungId == null)
			throw new ValidationException("Người dùng không hợp lệ.");

		if(danhSachId == null)
			throw new ValidationException("Danh sách lớp không hợp lệ.");

		if(duongDanFile == null || duongDanFile.trim().isEmpty())
			throw new ValidationException("Đường dẫn file không hợp lệ.");

		try {

			DanhSachLop entity = danhSachRepo.findByIdWithDetails(danhSachId)
					.orElseThrow(() -> new ValidationException("Không tìm thấy danh sách lớp."));

			if(!entity.getNguoiTaoId().equals(nguoiDungId))
				throw new ValidationException("Không có quyền xuất danh sách này.");

			DanhSachLopDto ds = mapToDto(entity);

			List<String> headers = Arrays.asList(
					"ten_danh_sach",
					"ma_lop",
					"ma_hoc_phan",
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

			List<DanhSachLopChiTietDto> chiTiets = ds.getChiTiet() == null ? Collections.emptyList() : ds.getChiTiet();

			for(DanhSachLopChiTietDto ct : chiTiets) {

				List<LichHocDto> lichs = ct.getLichHoc() == null ? Collections.emptyList() : ct.getLichHoc();

				if(lichs.isEmpty()) {
					rows.add(buildRow(ds, ct, null));
				}
				else {
					for(LichHocDto lich : lichs) {
						rows.add(buildRow(ds, ct, lich));
					}
				}
			}

			Path output = Paths.get(duongDanFile);

			String fmt = dinhDang == null ? "CSV" : dinhDang.trim().toUpperCase();

			boolean isExcel = "EXCEL".equals(fmt)
					|| duongDanFile.toLowerCase().endsWith(".xlsx");

			if(isExcel) excelExporter.export(output, headers, rows);
			else csvExporter.export(output, headers, rows);

		}
		catch(ValidationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new PersistenceException("Lỗi khi xuất dữ liệu.", e);
		}
	}

	private String safe(String v)
	{
		return v == null ? "" : v;
	}

	private DanhSachLopDto mapToDto(DanhSachLop entity)
	{

		List<DanhSachLopChiTietDto> chiTietDtos = new ArrayList<>();

		for(DanhSachLopChiTiet ct : entity.getChiTietList()) {

			LopHocPhan lhp = ct.getLopHocPhan();
			HocPhan hp = lhp.getHocPhan();
			GiangVien gv = lhp.getGiangVien();

			List<LichHocDto> lichDtos = new ArrayList<>();
			for(LichHoc lich : lhp.getDanhSachLichHoc()) {
				lichDtos.add(new LichHocDto(
						lich.getThu(),
						lich.getTietBatDau(),
						lich.getTietKetThuc()));

			}

			chiTietDtos.add(new DanhSachLopChiTietDto(
					lhp.getId(),
					lhp.getMaLop(),
					hp.getTenHocPhan(),
					hp.getSoTinChi(),
					gv == null ? "" : gv.getTenGiangVien(),
					lhp.getHinhThucDay().name(),
					lhp.getDiaDiem(),
					lichDtos,
					ct.getBatBuoc()));
		}

		return new DanhSachLopDto(
				entity.getId(),
				entity.getTenDanhSach(),
				entity.getNguoiTaoId(),
				entity.getLaCongKhai(),
				entity.getHocKyId(),
				entity.getNgayTao(),
				chiTietDtos);
	}

	private Map<String, String> buildRow(
			DanhSachLopDto ds,
			DanhSachLopChiTietDto ct,
			LichHocDto lich)
	{
		Map<String, String> r = new LinkedHashMap<>();

		r.put("ten_danh_sach", safe(ds.getTenDanhSach()));
		r.put("ma_lop", safe(ct.getMaLop()));
		r.put("ma_hoc_phan", "");
		r.put("ten_hoc_phan", safe(ct.getTenHocPhan()));
		r.put("so_tin_chi",
				ct.getSoTinChi() == null ? "" : String.valueOf(ct.getSoTinChi()));
		r.put("ten_giang_vien", safe(ct.getTenGiangVien()));
		r.put("hinh_thuc_day", safe(ct.getHinhThucDay()));
		r.put("dia_diem", safe(ct.getDiaDiem()));

		r.put("thu",
				lich == null || lich.getThu() == null ? "" : String.valueOf(lich.getThu()));

		r.put("tiet_bat_dau",
				lich == null || lich.getTietBatDau() == null
						? ""
						: String.valueOf(lich.getTietBatDau()));

		r.put("tiet_ket_thuc",
				lich == null || lich.getTietKetThuc() == null
						? ""
						: String.valueOf(lich.getTietKetThuc()));

		r.put("bat_buoc",
				ct.getBatBuoc() == null ? "0" : String.valueOf(ct.getBatBuoc()));

		return r;
	}
}
