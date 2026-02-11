package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.dto.ImportDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.domain.model.HocPhan;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelCourseImporter;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopRow;
import vn.edu.haui.scheduler.application.port.out.*;
import vn.edu.haui.scheduler.infrastructure.persistence.config.DataSourceProvider;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

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

		try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
			boolean previousAuto = conn.getAutoCommit();
			conn.setAutoCommit(false);
			try {
				int tepId = tepRepo.saveMetadata(conn, request.getNguoiTaoId(), f, "application/vnd.ms-excel");
				int danhSachId = danhSachRepo.save(conn, request.getTenDanhSach(), request.getNguoiTaoId(),
						request.isLaCongKhai(), request.getHocKyId());

				for(ImportedLopRow row : rows) {
					int hocPhanId = resolveHocPhan(conn, row);
					int giangVienId = resolveGiangVien(conn, row);
					int lopId = resolveLopHocPhan(conn, row, hocPhanId, giangVienId);
					lichRepo.saveAll(conn, lopId, row.buoiList);
					danhSachRepo.addChiTiet(conn, danhSachId, lopId);
				}

				conn.commit();
			}
			catch(Exception ex) {
				conn.rollback();
				throw new PersistenceException("Import failed: " + ex.getMessage(), ex);
			}
			finally {
				conn.setAutoCommit(previousAuto);
			}
		}
	}

	private int resolveHocPhan(Connection conn, ImportedLopRow row) throws Exception
	{
		if(row.maHocPhan != null) {
			Optional<Integer> idOpt = hocPhanRepo.findIdByMaHocPhan(conn, row.maHocPhan);
			if(idOpt.isPresent()) return idOpt.get();
		}
		HocPhan hp = new HocPhan();
		hp.setMaHocPhan(row.maHocPhan);
		hp.setTenHocPhan(row.tenHocPhan != null ? row.tenHocPhan : "");
		hp.setSoTinChi(row.soTinChi);
		return hocPhanRepo.save(conn, hp);
	}

	private int resolveGiangVien(Connection conn, ImportedLopRow row) throws Exception
	{
		if(row.tenGiangVien != null && !row.tenGiangVien.isEmpty()) {
			Optional<Integer> idOpt = giangVienRepo.findIdByTen(conn, row.tenGiangVien);
			if(idOpt.isPresent()) return idOpt.get();
			return giangVienRepo.save(conn, row.tenGiangVien);
		}
		return -1;
	}

	private int resolveLopHocPhan(Connection conn, ImportedLopRow row, int hocPhanId, int giangVienId) throws Exception
	{
		Optional<Integer> idOpt = lopRepo.findIdByMaAndHocPhanId(conn, row.maLop, hocPhanId);
		if(idOpt.isPresent()) return idOpt.get();
		LopHocPhan lop = new LopHocPhan();
		lop.setMaLop(row.maLop);
		lop.setHocPhanId(hocPhanId);
		if(giangVienId > 0) lop.setGiangVienId(giangVienId);
		else lop.setGiangVienId(null);
		lop.setHinhThucDay(row.hinhThucDay);
		lop.setDiaDiem(row.diaDiem);
		return lopRepo.save(conn, lop);
	}
}