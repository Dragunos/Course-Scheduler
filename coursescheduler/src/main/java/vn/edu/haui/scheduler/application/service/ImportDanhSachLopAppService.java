package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelCourseImporter;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopRow;
import vn.edu.haui.scheduler.application.port.out.*;
import java.io.File;
import java.util.*;

public class ImportDanhSachLopAppService implements ImportDanhSachLopUseCase
{
	private final ExcelCourseImporter importer;

	private final HocPhanRepositoryPort hocPhanRepo;

	private final GiangVienRepositoryPort giangVienRepo;

	private final LopHocPhanRepositoryPort lopRepo;

	private final LichHocRepositoryPort lichRepo;

	private final DanhSachLopRepositoryPort danhSachRepo;

	private final TepTaiLenRepositoryPort tepRepo;

	public ImportDanhSachLopAppService(
			ExcelCourseImporter importer,
			HocPhanRepositoryPort hocPhanRepo,
			GiangVienRepositoryPort giangVienRepo,
			LopHocPhanRepositoryPort lopRepo,
			LichHocRepositoryPort lichRepo,
			DanhSachLopRepositoryPort danhSachRepo,
			TepTaiLenRepositoryPort tepRepo)
	{
		this.importer = importer;
		this.hocPhanRepo = hocPhanRepo;
		this.giangVienRepo = giangVienRepo;
		this.lopRepo = lopRepo;
		this.lichRepo = lichRepo;
		this.danhSachRepo = danhSachRepo;
		this.tepRepo = tepRepo;
	}

	@Override
	public void importDanhSach(ImportDanhSachLopRequestDto request) throws Exception
	{
		File f = new File(request.getFilePath());
		if(!f.exists()) throw new IllegalArgumentException("File not found: " + request.getFilePath());

		List<ImportedLopRow> rows = importer.importFrom(f);

		Map<String, ImportedLopAggregate> map = new LinkedHashMap<>();
		for(ImportedLopRow r : rows) {
			ImportedLopAggregate agg = map.computeIfAbsent(r.maLop, k -> new ImportedLopAggregate(r));
			agg.merge(r);
		}

		try {
			int tepId = tepRepo.saveMetadata(request.getNguoiTaoId(), f, "application/vnd.ms-excel");
			int danhSachId = danhSachRepo.save(request.getTenDanhSach(), request.getNguoiTaoId(),
					request.isLaCongKhai(), request.getHocKyId());

			for(ImportedLopAggregate agg : map.values()) {
				int hocPhanId = resolveHocPhan(agg);
				int giangVienId = resolveGiangVien(agg);
				int lopId = resolveLopHocPhan(agg, hocPhanId, giangVienId);
				List<ImportedLopRow.Buoi> dedupBuoi = dedupeBuoi(agg.getBuoiList());
				lichRepo.saveAll(lopId, dedupBuoi);
				danhSachRepo.addChiTiet(danhSachId, lopId);
			}
		}
		catch(Exception ex) {
			// Không thể rollback toàn bộ vì mỗi repository tự quản lý connection.
			throw new PersistenceException("Import failed: " + ex.getMessage(), ex);
		}
	}

	private int resolveHocPhan(ImportedLopAggregate agg) throws Exception
	{
		if(agg.maHocPhan != null) {
			Optional<Integer> idOpt = hocPhanRepo.findIdByMaHocPhan(agg.maHocPhan);
			if(idOpt.isPresent()) return idOpt.get();
		}
		HocPhan hp = new HocPhan();
		hp.setMaHocPhan(agg.maHocPhan);
		hp.setTenHocPhan(agg.tenHocPhan != null ? agg.tenHocPhan : "");
		hp.setSoTinChi(agg.soTinChi);
		return hocPhanRepo.save(hp);
	}

	private int resolveGiangVien(ImportedLopAggregate agg) throws Exception
	{
		if(agg.tenGiangVien != null && !agg.tenGiangVien.isEmpty()) {
			Optional<Integer> idOpt = giangVienRepo.findIdByTen(agg.tenGiangVien);
			if(idOpt.isPresent()) return idOpt.get();
			return giangVienRepo.save(agg.tenGiangVien);
		}
		return -1;
	}

	private int resolveLopHocPhan(ImportedLopAggregate agg, int hocPhanId, int giangVienId)
			throws Exception
	{
		Optional<Integer> idOpt = lopRepo.findIdByMaAndHocPhanId(agg.maLop, hocPhanId);
		if(idOpt.isPresent()) return idOpt.get();
		LopHocPhan lop = new LopHocPhan();
		lop.setMaLop(agg.maLop);
		lop.setHocPhanId(hocPhanId);
		if(giangVienId > 0) lop.setGiangVienId(giangVienId);
		else lop.setGiangVienId(null);
		lop.setHinhThucDay(agg.hinhThucDay);
		lop.setDiaDiem(agg.diaDiem);
		return lopRepo.save(lop);
	}

	private static List<ImportedLopRow.Buoi> dedupeBuoi(List<ImportedLopRow.Buoi> src)
	{
		Set<String> seen = new HashSet<>();
		List<ImportedLopRow.Buoi> out = new ArrayList<>();
		for(ImportedLopRow.Buoi b : src) {
			String key = b.thu + "-" + b.tietBatDau + "-" + b.tietKetThuc;
			if(!seen.contains(key)) {
				seen.add(key);
				out.add(b);
			}
		}
		return out;
	}

	private static class ImportedLopAggregate
	{
		public final String maLop;

		public String maHocPhan;

		public String tenHocPhan;

		public Integer soTinChi;

		public String tenGiangVien;

		public String hinhThucDay;

		public String diaDiem;

		private final List<ImportedLopRow.Buoi> buoiList = new ArrayList<>();

		public ImportedLopAggregate(ImportedLopRow r)
		{
			this.maLop = r.maLop;
			this.maHocPhan = r.maHocPhan;
			this.tenHocPhan = r.tenHocPhan;
			this.soTinChi = r.soTinChi;
			this.tenGiangVien = r.tenGiangVien;
			this.hinhThucDay = r.hinhThucDay;
			this.diaDiem = r.diaDiem;
			if(r.buoiList != null) this.buoiList.addAll(r.buoiList);
		}

		public void merge(ImportedLopRow r)
		{
			if(this.maHocPhan == null && r.maHocPhan != null) this.maHocPhan = r.maHocPhan;
			if((this.tenHocPhan == null || this.tenHocPhan.isEmpty()) && r.tenHocPhan != null)
				this.tenHocPhan = r.tenHocPhan;
			if(this.soTinChi == null && r.soTinChi != null) this.soTinChi = r.soTinChi;
			if((this.tenGiangVien == null || this.tenGiangVien.isEmpty()) && r.tenGiangVien != null)
				this.tenGiangVien = r.tenGiangVien;
			if((this.hinhThucDay == null || this.hinhThucDay.isEmpty()) && r.hinhThucDay != null)
				this.hinhThucDay = r.hinhThucDay;
			if((this.diaDiem == null || this.diaDiem.isEmpty()) && r.diaDiem != null) this.diaDiem = r.diaDiem;
			if(r.buoiList != null) this.buoiList.addAll(r.buoiList);
		}

		public List<ImportedLopRow.Buoi> getBuoiList()
		{
			return buoiList;
		}
	}
}
