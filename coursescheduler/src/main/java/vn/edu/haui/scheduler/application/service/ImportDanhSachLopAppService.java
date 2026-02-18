package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.enums.HinhThucDay;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
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
	public DanhSachLopDto importDanhSach(ImportDanhSachLopRequestDto request)
	{
		File f = new File(request.getFilePath());
		if(!f.exists()) {
			throw new IllegalArgumentException("File not found: " + request.getFilePath());
		}

		List<ImportedLopRow> rows;

		try {
			rows = importer.importFrom(f);
		}
		catch(Exception ex) {
			throw new PersistenceException("Failed to read file", ex);
		}

		Map<String, ImportedLopAggregate> map = new LinkedHashMap<>();
		for(ImportedLopRow r : rows) {
			ImportedLopAggregate agg = map.computeIfAbsent(r.maLop, k -> new ImportedLopAggregate(r));
			agg.merge(r);
		}

		Long danhSachId;

		try {
			tepRepo.saveMetadata(
					request.getNguoiTaoId(),
					f,
					"application/vnd.ms-excel");

			danhSachId = danhSachRepo.save(
					request.getTenDanhSach(),
					request.getNguoiTaoId(),
					request.isLaCongKhai(),
					request.getHocKyId());

			for(ImportedLopAggregate agg : map.values()) {

				Long hocPhanId = resolveHocPhan(agg);
				Long giangVienId = resolveGiangVien(agg);
				Long lopId = resolveLopHocPhan(agg, hocPhanId, giangVienId);

				List<LichHoc> lichHocList = convertAndDedupeBuoi(lopId, agg.getBuoiList());

				lichRepo.saveAll(lopId, lichHocList);
				danhSachRepo.addChiTiet(danhSachId, lopId);
			}

		}
		catch(Exception ex) {
			throw new PersistenceException("Import failed", ex);
		}

		return danhSachRepo.findByIdWithDetails(danhSachId)
				.map(this::toDto)
				.orElseThrow(() -> new PersistenceException("Cannot load created list"));
	}

	private Long resolveHocPhan(ImportedLopAggregate agg)
	{
		if(agg.maHocPhan != null) {
			Optional<Long> idOpt = hocPhanRepo.findIdByMaHocPhan(agg.maHocPhan);
			if(idOpt.isPresent()) return idOpt.get();
		}

		HocPhan hp = new HocPhan();
		hp.setMaHocPhan(agg.maHocPhan);
		hp.setTenHocPhan(
				agg.tenHocPhan != null ? agg.tenHocPhan : "");
		hp.setSoTinChi(agg.soTinChi);

		return hocPhanRepo.save(hp);
	}

	private Long resolveGiangVien(ImportedLopAggregate agg)
	{
		if(agg.tenGiangVien != null &&
				!agg.tenGiangVien.isEmpty()) {

			Optional<Long> idOpt = giangVienRepo.findIdByTen(agg.tenGiangVien);

			if(idOpt.isPresent())
				return idOpt.get();

			GiangVien gv = new GiangVien();
			gv.setTenGiangVien(agg.tenGiangVien);

			return giangVienRepo.save(gv);
		}
		return null;
	}

	private Long resolveLopHocPhan(
			ImportedLopAggregate agg,
			Long hocPhanId,
			Long giangVienId)
	{
		Optional<Long> idOpt = lopRepo.findIdByMaAndHocPhanId(
				agg.maLop,
				hocPhanId);

		if(idOpt.isPresent())
			return idOpt.get();

		LopHocPhan lop = new LopHocPhan();
		lop.setMaLop(agg.maLop);

		HocPhan hocPhan = new HocPhan();
		hocPhan.setId(hocPhanId);
		lop.setHocPhan(hocPhan);

		GiangVien giangVien = new GiangVien();
		giangVien.setId(giangVienId);
		lop.setGiangVien(giangVien);

		lop.setHinhThucDay(parseHinhThuc(agg.hinhThucDay));
		lop.setDiaDiem(agg.diaDiem);

		return lopRepo.save(lop);
	}

	private List<LichHoc> convertAndDedupeBuoi(
			Long lopId,
			List<ImportedLopRow.Buoi> src)
	{
		Set<String> seen = new HashSet<>();
		List<LichHoc> result = new ArrayList<>();

		for(ImportedLopRow.Buoi b : src) {

			String key = b.thu + "-" +
					b.tietBatDau + "-" +
					b.tietKetThuc;

			if(seen.contains(key)) continue;

			seen.add(key);

			LichHoc lich = new LichHoc(
					null,
					lopId,
					ThuTrongTuan.fromGiaTri(b.thu),
					b.tietBatDau,
					b.tietKetThuc);

			result.add(lich);
		}
		return result;
	}

	private HinhThucDay parseHinhThuc(String raw)
	{
		if(raw == null) return HinhThucDay.KHONG_XAC_DINH;

		String value = raw.trim().toUpperCase();

		switch(value) {
			case "ONLINE":
				return HinhThucDay.ONLINE;
			case "TRUC_TIEP":
			case "TRỰC TIẾP":
				return HinhThucDay.TRUC_TIEP;
			default:
				return HinhThucDay.KHONG_XAC_DINH;
		}
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

			if(r.buoiList != null)
				this.buoiList.addAll(r.buoiList);
		}

		public void merge(ImportedLopRow r)
		{
			if(this.maHocPhan == null &&
					r.maHocPhan != null)
				this.maHocPhan = r.maHocPhan;

			if((this.tenHocPhan == null ||
					this.tenHocPhan.isEmpty()) &&
					r.tenHocPhan != null)
				this.tenHocPhan = r.tenHocPhan;

			if(this.soTinChi == null &&
					r.soTinChi != null)
				this.soTinChi = r.soTinChi;

			if((this.tenGiangVien == null ||
					this.tenGiangVien.isEmpty()) &&
					r.tenGiangVien != null)
				this.tenGiangVien = r.tenGiangVien;

			if((this.hinhThucDay == null ||
					this.hinhThucDay.isEmpty()) &&
					r.hinhThucDay != null)
				this.hinhThucDay = r.hinhThucDay;

			if((this.diaDiem == null ||
					this.diaDiem.isEmpty()) &&
					r.diaDiem != null)
				this.diaDiem = r.diaDiem;

			if(r.buoiList != null)
				this.buoiList.addAll(r.buoiList);
		}

		public List<ImportedLopRow.Buoi> getBuoiList()
		{
			return buoiList;
		}
	}

	private DanhSachLopDto toDto(DanhSachLop model)
	{
		DanhSachLopDto dto = new DanhSachLopDto();
		dto.setId(model.getId());
		dto.setTenDanhSach(model.getTenDanhSach());
		dto.setNguoiTaoId(model.getNguoiTaoId());
		dto.setHocKyId(model.getHocKyId());
		dto.setLaCongKhai(model.getLaCongKhai());
		dto.setNgayTao(model.getNgayTao());

		return dto;
	}
}
