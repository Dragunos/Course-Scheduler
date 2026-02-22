package vn.edu.haui.scheduler.application.service;

import java.util.List;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ImportDanhSachLopException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.*;
import vn.edu.haui.scheduler.application.service.mapper.DanhSachLopMapper;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelDanhSachLopImporter;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopHocPhanRaw;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManager;

public class ImportDanhSachLopService implements ImportDanhSachLopUseCase
{
	private final ExcelDanhSachLopImporter importer;

	private final DanhSachLopRepository danhSachLopRepository;

	private final HocPhanRepository hocPhanRepository;

	private final GiangVienRepository giangVienRepository;

	private final LopHocPhanRepository lopHocPhanRepository;

	private final NguoiDungRepository nguoiDungRepository;

	private final HocKyRepository hocKyRepository;

	private final TepTaiLenRepository tepTaiLenRepository;

	private final TransactionManager transactionManager;

	public ImportDanhSachLopService(
			ExcelDanhSachLopImporter importer,
			DanhSachLopRepository danhSachLopRepository,
			HocPhanRepository hocPhanRepository,
			GiangVienRepository giangVienRepository,
			LopHocPhanRepository lopHocPhanRepository,
			NguoiDungRepository nguoiDungRepository,
			HocKyRepository hocKyRepository,
			TepTaiLenRepository tepTaiLenRepository,
			TransactionManager transactionManager)
	{
		this.importer = importer;
		this.danhSachLopRepository = danhSachLopRepository;
		this.hocPhanRepository = hocPhanRepository;
		this.giangVienRepository = giangVienRepository;
		this.lopHocPhanRepository = lopHocPhanRepository;
		this.nguoiDungRepository = nguoiDungRepository;
		this.hocKyRepository = hocKyRepository;
		this.tepTaiLenRepository = tepTaiLenRepository;
		this.transactionManager = transactionManager;
	}

	@Override
	public DanhSachLopDto importFromExcel(
			Long nguoiTaoId,
			String tenDanhSach,
			Long hocKyId,
			TepTaiLenDto tepTaiLenDto)
	{
		if(nguoiTaoId == null)
			throw new ValidationException("NguoiTaoId must not be null");

		if(tenDanhSach == null || tenDanhSach.isBlank())
			throw new ValidationException("TenDanhSach must not be blank");

		if(tepTaiLenDto == null)
			throw new ValidationException("TepTaiLen must not be null");

		NguoiDung nguoiTao = nguoiDungRepository
				.findById(nguoiTaoId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiTaoId));

		HocKy hocKy = null;
		if(hocKyId != null) {
			hocKy = hocKyRepository
					.findById(hocKyId)
					.orElseThrow(() -> new EntityNotFoundException("HocKy", hocKyId));
		}

		transactionManager.begin();
		try {
			TepTaiLen tepTaiLen = TepTaiLen.create(
					nguoiTao,
					tepTaiLenDto.getTenTepGoc(),
					tepTaiLenDto.getLoaiTep(),
					tepTaiLenDto.getDuongDan(),
					tepTaiLenDto.getStorageType(),
					tepTaiLenDto.getFileBlob(),
					tepTaiLenDto.getChecksum(),
					tepTaiLenDto.getKichThuoc());

			tepTaiLenRepository.save(tepTaiLen);

			ExcelDanhSachLopImporter.ImportFileResult result = importer.read(tepTaiLen);
			List<ImportedLopHocPhanRaw> rawList = result.rows;

			DanhSachLop danhSach = DanhSachLop.create(
					tenDanhSach.trim(),
					nguoiTao,
					false,
					hocKy);

			for(ImportedLopHocPhanRaw raw : rawList) {

				HocPhan hocPhan = hocPhanRepository
						.findByMaHocPhan(raw.maHocPhan())
						.orElseGet(() -> {
							HocPhan newHp = HocPhan.create(
									raw.maHocPhan(),
									raw.tenHocPhan(),
									raw.soTinChi());
							return hocPhanRepository.save(newHp);
						});

				GiangVien giangVien = null;
				if(raw.tenGiangVien() != null && !raw.tenGiangVien().isBlank()) {
					giangVien = giangVienRepository
							.findByTen(raw.tenGiangVien())
							.orElseGet(() -> {
								GiangVien gv = GiangVien.create(raw.tenGiangVien());
								return giangVienRepository.save(gv);
							});
				}

				LopHocPhan lop = LopHocPhan.create(
						raw.maLop(),
						hocPhan,
						giangVien,
						raw.hinhThucDay(),
						raw.diaDiem());

				raw.lichHocList().forEach(lh -> {
					LichHoc lichHoc = LichHoc.create(
							null,
							lh.thu(),
							lh.tietBatDau(),
							lh.tietKetThuc());

					lop.themLichHoc(lichHoc);
				});

				LopHocPhan savedLop = lopHocPhanRepository.save(lop);

				danhSach.themLop(savedLop, false);
			}

			DanhSachLop saved = danhSachLopRepository.save(danhSach);

			transactionManager.commit();

			return DanhSachLopMapper.toDto(saved);
		}
		catch(Exception e) {
			transactionManager.rollback();
			throw new ImportDanhSachLopException(
					"Error importing DanhSachLop",
					e);
		}
	}
}