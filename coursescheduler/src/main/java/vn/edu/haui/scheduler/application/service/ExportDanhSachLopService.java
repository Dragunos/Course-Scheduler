package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.ExportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.FileExporter;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.infrastructure.io.exports.CsvExporter;
import vn.edu.haui.scheduler.infrastructure.io.exports.ExcelExporter;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ExportDanhSachLopService implements ExportDanhSachLopUseCase
{

	private final DanhSachLopRepository danhSachRepo;

	private final FileExporter fileExporter;

	public ExportDanhSachLopService(
			DanhSachLopRepository danhSachRepo,
			FileExporter fileExporter)
	{
		this.danhSachRepo = danhSachRepo;
		this.fileExporter = fileExporter;
	}

	@Override
	public void exportDanhSachLop(
			Long nguoiDungId,
			Long danhSachId,
			String duongDanFile,
			String dinhDang)
			throws ValidationException, DataAccessException
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

			if(entity.getNguoiTao() == null || !entity.getNguoiTao().getId().equals(nguoiDungId))

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

			if(isExcel) {
				fileExporter.exportExcel(output, headers, rows);
			}
			else {
				fileExporter.exportCsv(output, headers, rows);
			}

		}
		catch(ValidationException e) {
			throw e;
		}
		catch(Exception e) {
			throw new DataAccessException("Lỗi khi xuất dữ liệu.", e);
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

			if(lhp.getDanhSachLichHoc() != null) {
				for(LichHoc lich : lhp.getDanhSachLichHoc()) {
					lichDtos.add(new LichHocDto(
							lich.getThu(),
							lich.getTietBatDau(),
							lich.getTietKetThuc()));
				}
			}

			DanhSachLopChiTietDto dto = new DanhSachLopChiTietDto(
					lhp.getId(),
					lhp.getMaLop(),
					hp.getMaHocPhan(),
					hp.getTenHocPhan(),
					hp.getSoTinChi(),
					gv == null ? "" : gv.getTenGiangVien(),
					lhp.getHinhThucDay().name(),
					lhp.getDiaDiem(),
					lichDtos,
					ct.isBatBuoc() ? 1 : 0);

			chiTietDtos.add(dto);
		}

		return new DanhSachLopDto(
				entity.getId(),
				entity.getTenDanhSach(),
				entity.getNguoiTao() == null ? null : entity.getNguoiTao().getId(),
				entity.isCongKhai() ? 1 : 0,
				entity.getHocKy() == null ? null : entity.getHocKy().getId(),
				entity.getNgayTao(),
				chiTietDtos);
	}

	private Map<String, String> buildRow(
			DanhSachLopDto ds,
			DanhSachLopChiTietDto ct,
			LichHocDto lich)
	{
		Map<String, String> row = new LinkedHashMap<>();

		row.put("ten_danh_sach", safe(ds.getTenDanhSach()));
		row.put("ma_lop", safe(ct.getMaLop()));
		row.put("ma_hoc_phan", safe(ct.getMaHocPhan()));
		row.put("ten_hoc_phan", safe(ct.getTenHocPhan()));
		row.put("so_tin_chi", ct.getSoTinChi() == null ? "" : ct.getSoTinChi().toString());
		row.put("ten_giang_vien", safe(ct.getTenGiangVien()));
		row.put("hinh_thuc_day", safe(ct.getHinhThucDay()));
		row.put("dia_diem", safe(ct.getDiaDiem()));

		if(lich != null) {
			row.put("thu", lich.getThu() == null ? "" : lich.getThu().toString());
			row.put("tiet_bat_dau", lich.getTietBatDau() == null ? "" : lich.getTietBatDau().toString());
			row.put("tiet_ket_thuc", lich.getTietKetThuc() == null ? "" : lich.getTietKetThuc().toString());
		}
		else {
			row.put("thu", "");
			row.put("tiet_bat_dau", "");
			row.put("tiet_ket_thuc", "");
		}

		row.put("bat_buoc",
				ct.getBatBuoc() == null
						? ""
						: (ct.getBatBuoc() == 1 ? "1" : "0"));

		return row;
	}
}
