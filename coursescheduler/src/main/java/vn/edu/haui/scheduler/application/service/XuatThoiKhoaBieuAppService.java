package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.XuatThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.FileExportPort;
import vn.edu.haui.scheduler.application.port.out.GiangVienRepositoryPort;
import vn.edu.haui.scheduler.application.port.out.HocPhanRepositoryPort;
import vn.edu.haui.scheduler.application.port.out.LichHocRepositoryPort;
import vn.edu.haui.scheduler.application.port.out.LopHocPhanRepositoryPort;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepositoryPort;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.PhuongAnThoiKhoaBieu;
import vn.edu.haui.scheduler.infrastructure.io.exports.IcsExporter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class XuatThoiKhoaBieuAppService implements XuatThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepositoryPort thoiKhoaBieuRepo;

	private final LopHocPhanRepositoryPort lopRepo;

	private final LichHocRepositoryPort lichRepo;

	private final HocPhanRepositoryPort hocPhanRepo;

	private final GiangVienRepositoryPort giangVienRepo;

	private final FileExportPort fileExportPort;

	private final IcsExporter icsExporter;

	public XuatThoiKhoaBieuAppService(
			ThoiKhoaBieuRepositoryPort thoiKhoaBieuRepo,
			LopHocPhanRepositoryPort lopRepo,
			LichHocRepositoryPort lichRepo,
			HocPhanRepositoryPort hocPhanRepo,
			GiangVienRepositoryPort giangVienRepo,
			FileExportPort fileExportPort,
			IcsExporter icsExporter)
	{
		this.thoiKhoaBieuRepo = thoiKhoaBieuRepo;
		this.lopRepo = lopRepo;
		this.lichRepo = lichRepo;
		this.hocPhanRepo = hocPhanRepo;
		this.giangVienRepo = giangVienRepo;
		this.fileExportPort = fileExportPort;
		this.icsExporter = icsExporter;
	}

	@Override
	public void xuatThoiKhoaBieu(Long nguoiDungId, Long thoiKhoaBieuId, String duongDanFile, String dinhDang)
			throws ValidationException, PersistenceException
	{
		if(nguoiDungId == null)
			throw new ValidationException("Người dùng không hợp lệ.");
		if(thoiKhoaBieuId == null)
			throw new ValidationException("Thời khóa biểu không hợp lệ.");
		if(duongDanFile == null || duongDanFile.trim().isEmpty())
			throw new ValidationException("Đường dẫn file không hợp lệ.");

		try {
			PhuongAnThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
					.orElseThrow(() -> new ValidationException("Không tìm thấy thời khóa biểu."));

			if(!Objects.equals(pa.getNguoiDungId(), nguoiDungId))
				throw new ValidationException("Không có quyền xuất thời khóa biểu này.");

			List<Long> lopIds = thoiKhoaBieuRepo.findChiTietByThoiKhoaBieuId(thoiKhoaBieuId);
			List<LopHocPhan> lops = lopIds == null || lopIds.isEmpty()
					? Collections.emptyList()
					: lopRepo.findByIds(lopIds);

			List<String> headers = Arrays.asList(
					"ten_phuong_an",
					"ma_lop",
					"ma_hoc_phan",
					"ten_hoc_phan",
					"so_tin_chi",
					"ten_giang_vien",
					"hinh_thuc_day",
					"dia_diem",
					"thu",
					"tiet_bat_dau",
					"tiet_ket_thuc");

			List<Map<String, String>> rows = new ArrayList<>();

			for(LopHocPhan lhp : lops) {
				HocPhan hp = lhp.getHocPhan();
				GiangVien gv = lhp.getGiangVien();
				List<LichHoc> lichs = lichRepo.findByLopHocPhanId(lhp.getId());

				if(lichs == null || lichs.isEmpty()) {
					rows.add(buildRow(pa.getTenPhuongAn(), lhp, hp, gv, null));
				}
				else {
					for(LichHoc lich : lichs) {
						rows.add(buildRow(pa.getTenPhuongAn(), lhp, hp, gv, lich));
					}
				}
			}

			Path output = Paths.get(duongDanFile);
			String fmt = dinhDang == null ? "CSV" : dinhDang.trim().toUpperCase();
			boolean isPdf = "PDF".equals(fmt) || duongDanFile.toLowerCase().endsWith(".pdf");
			boolean isIcs = "ICS".equals(fmt) || duongDanFile.toLowerCase().endsWith(".ics");
			boolean isExcel = "EXCEL".equals(fmt) || duongDanFile.toLowerCase().endsWith(".xlsx");

			if(isIcs) {
				icsExporter.export(output, pa.getTenPhuongAn(), rows);
			}
			else if(isPdf) {
				fileExportPort.exportPdf(output,
						"THỜI KHÓA BIỂU - " + pa.getTenPhuongAn(),
						headers,
						rows);
			}
			else if(isExcel) {
				throw new ValidationException("Định dạng EXCEL cho thời khóa biểu chưa được triển khai trong lớp này.");
			}
			else {
				fileExportPort.exportCsv(output, headers, rows);
			}
		}
		catch(ValidationException e) {
			throw e;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new PersistenceException("Lỗi khi xuất thời khóa biểu.", e);
		}
	}

	private Map<String, String> buildRow(
			String tenPhuongAn,
			LopHocPhan lhp,
			HocPhan hp,
			GiangVien gv,
			LichHoc lich)
	{
		Map<String, String> r = new LinkedHashMap<>();
		r.put("ten_phuong_an", safe(tenPhuongAn));
		r.put("ma_lop", safe(lhp.getMaLop()));
		r.put("ma_hoc_phan", hp == null ? "" : safe(hp.getMaHocPhan()));
		r.put("ten_hoc_phan", hp == null ? "" : safe(hp.getTenHocPhan()));
		r.put("so_tin_chi", hp == null || hp.getSoTinChi() == null ? "" : String.valueOf(hp.getSoTinChi()));
		r.put("ten_giang_vien", gv == null ? "" : safe(gv.getTenGiangVien()));
		r.put("hinh_thuc_day", lhp.getHinhThucDay() == null ? "" : lhp.getHinhThucDay().name());
		r.put("dia_diem", safe(lhp.getDiaDiem()));
		r.put("thu", lich == null || lich.getThu() == null ? "" : String.valueOf(lich.getThu()));
		r.put("tiet_bat_dau", lich == null || lich.getTietBatDau() == null ? "" : String.valueOf(lich.getTietBatDau()));
		r.put("tiet_ket_thuc",
				lich == null || lich.getTietKetThuc() == null ? "" : String.valueOf(lich.getTietKetThuc()));
		return r;
	}

	private String safe(String v)
	{
		return v == null ? "" : v;
	}
}
