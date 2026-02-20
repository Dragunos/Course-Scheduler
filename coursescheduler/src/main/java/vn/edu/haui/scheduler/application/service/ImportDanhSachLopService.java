package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopResultDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.GiangVien;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.enums.HinhThucDay;
import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelDanhSachLopImporter;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopHocPhanRow;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManager;
import vn.edu.haui.scheduler.application.port.out.*;

import java.io.File;
import java.util.*;

public class ImportDanhSachLopService implements ImportDanhSachLopUseCase
{
	private final ExcelDanhSachLopImporter importer;

	private final HocPhanRepository hocPhanRepo;

	private final GiangVienRepository giangVienRepo;

	private final LopHocPhanRepository lopRepo;

	private final LichHocRepository lichRepo;

	private final DanhSachLopRepository danhSachRepo;

	private final TepTaiLenRepository tepRepo;

	private final TransactionManager txManager;

	public ImportDanhSachLopService(
			ExcelDanhSachLopImporter importer,
			HocPhanRepository hocPhanRepo,
			GiangVienRepository giangVienRepo,
			LopHocPhanRepository lopRepo,
			LichHocRepository lichRepo,
			DanhSachLopRepository danhSachRepo,
			TepTaiLenRepository tepRepo,
			TransactionManager txManager)
	{
		this.importer = importer;
		this.hocPhanRepo = hocPhanRepo;
		this.giangVienRepo = giangVienRepo;
		this.lopRepo = lopRepo;
		this.lichRepo = lichRepo;
		this.danhSachRepo = danhSachRepo;
		this.tepRepo = tepRepo;
		this.txManager = txManager;
	}

	@Override
	public ImportDanhSachLopResultDto importDanhSachLop(ImportDanhSachLopRequestDto request)
	{
		File f = new File(request.getFilePath());
		if(!f.exists()) throw new IllegalArgumentException("File not found: " + request.getFilePath());

		ExcelDanhSachLopImporter.ImportFileResult importResult;
		try {
			importResult = importer.importFrom(f);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			throw new DataAccessException("Failed to read file: " + ex.getMessage(), ex);
		}

		Map<String, ImportedLopAggregate> map = new LinkedHashMap<>();
		for(ImportedLopHocPhanRow r : importResult.rows) {
			ImportedLopAggregate agg = map.computeIfAbsent(r.maLop, k -> new ImportedLopAggregate(r));
			agg.merge(r);
		}

		Long danhSachId = null;
		try {
			txManager.begin();

			tepRepo.saveMetadata(
					request.getNguoiTaoId(),
					f,
					request.getMimeType() == null ? "application/octet-stream" : request.getMimeType());

			danhSachId = danhSachRepo.save(
					request.getTenDanhSach(),
					request.getNguoiTaoId(),
					request.isLaCongKhai(),
					request.getHocKyId());

			// Cache để tránh lookup nhiều lần
			Map<String, Long> cacheHocPhan = new HashMap<>();
			Map<String, Long> cacheGiangVien = new HashMap<>();

			for(ImportedLopAggregate agg : map.values()) {
				Long hocPhanId = resolveHocPhanCached(agg, cacheHocPhan);
				Long giangVienId = resolveGiangVienCached(agg, cacheGiangVien);
				Long lopId = resolveLopHocPhan(agg, hocPhanId, giangVienId);
				List<LichHoc> lichHocList = convertAndDedupeBuoi(lopId, agg.getBuoiList());

				lichRepo.replaceAllByLopHocPhanId(
						lopId,
						lichHocList);

				danhSachRepo.addChiTiet(danhSachId, lopId);
			}

			txManager.commit();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			try {
				txManager.rollback();
			}
			catch(Exception ignore) {
				ignore.printStackTrace();
			}
			throw new DataAccessException("Import failed: " + ex.getMessage(), ex);
		}

		DanhSachLopDto dto = danhSachRepo.findByIdWithDetails(danhSachId)
				.map(this::toDto)
				.orElseThrow(() -> new DataAccessException("Cannot load created list"));

		ImportDanhSachLopResultDto result = new ImportDanhSachLopResultDto();
		result.setDanhSach(dto);
		result.setWarnings(importResult.warnings);
		return result;
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

		if(idOpt.isPresent()) {

			Long id = idOpt.get();

			lopRepo.updateBasicInfo(
					id,
					giangVienId,
					parseHinhThuc(
							agg.hinhThucDay,
							agg.diaDiem),
					agg.diaDiem);

			return id;
		}

		LopHocPhan lop = new LopHocPhan();
		lop.setMaLop(agg.maLop);

		HocPhan hocPhan = new HocPhan();
		hocPhan.setId(hocPhanId);
		lop.setHocPhan(hocPhan);

		GiangVien giangVien = new GiangVien();
		giangVien.setId(giangVienId);
		lop.setGiangVien(giangVien);

		lop.setHinhThucDay(
				parseHinhThuc(
						agg.hinhThucDay,
						agg.diaDiem));

		lop.setDiaDiem(agg.diaDiem);

		return lopRepo.save(lop);
	}

	private Long resolveHocPhanCached(ImportedLopAggregate agg, Map<String, Long> cache)
	{
		String ma = agg.maHocPhan;
		if(ma != null && cache.containsKey(ma)) return cache.get(ma);
		Long id = resolveHocPhan(agg);
		if(ma != null && id != null) cache.put(ma, id);
		return id;
	}

	private Long resolveGiangVienCached(ImportedLopAggregate agg, Map<String, Long> cache)
	{
		String ten = agg.tenGiangVien;
		if(ten != null && cache.containsKey(ten)) return cache.get(ten);
		Long id = resolveGiangVien(agg);
		if(ten != null && id != null) cache.put(ten, id);
		return id;
	}

	private List<LichHoc> convertAndDedupeBuoi(
			Long lopId,
			List<ImportedLopHocPhanRow.Buoi> src)
	{
		Set<String> seen = new HashSet<>();
		List<LichHoc> result = new ArrayList<>();

		for(ImportedLopHocPhanRow.Buoi b : src) {

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

	private String normalizeText(String input)
	{
		if(input == null) return null;

		String v = input.trim().toUpperCase();

		v = java.text.Normalizer.normalize(
				v,
				java.text.Normalizer.Form.NFKD);

		v = v.replaceAll("\\p{M}", "");
		v = v.replaceAll("[^A-Z0-9 ]", "");
		v = v.replaceAll("\\s+", " ").trim();

		return v.isEmpty() ? null : v;
	}

	private HinhThucDay parseHinhThuc(String raw, String diaDiem)
	{
		String hinhThucNorm = normalizeText(raw);
		String diaDiemNorm = normalizeText(diaDiem);

		// --------- RULE 1 ----------
		if(hinhThucNorm != null &&
				hinhThucNorm.contains("ONLINE")) {
			return HinhThucDay.ONLINE;
		}

		// --------- RULE 2 ----------
		if(diaDiemNorm != null &&
				diaDiemNorm.equals("PH ONLINE")) {
			return HinhThucDay.ONLINE;
		}

		// --------- RULE 3 ----------
		return HinhThucDay.TRUC_TIEP;
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

		private final List<ImportedLopHocPhanRow.Buoi> buoiList = new ArrayList<>();

		public ImportedLopAggregate(ImportedLopHocPhanRow r)
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

		public void merge(ImportedLopHocPhanRow r)
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

		public List<ImportedLopHocPhanRow.Buoi> getBuoiList()
		{
			return buoiList;
		}
	}

	private DanhSachLopDto toDto(DanhSachLop model)
	{
		DanhSachLopDto dto = new DanhSachLopDto();

		dto.setId(model.getId());
		dto.setTenDanhSach(model.getTenDanhSach());

		dto.setNguoiTaoId(
				model.getNguoiTao() != null
						? model.getNguoiTao().getId()
						: null);

		dto.setHocKyId(
				model.getHocKy() != null
						? model.getHocKy().getId()
						: null);

		dto.setLaCongKhai(model.isCongKhai() ? 1 : 0);

		dto.setNgayTao(model.getNgayTao());

		return dto;
	}
}
