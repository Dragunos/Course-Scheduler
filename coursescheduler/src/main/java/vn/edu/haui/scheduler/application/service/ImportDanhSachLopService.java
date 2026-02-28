package vn.edu.haui.scheduler.application.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.*;
import vn.edu.haui.scheduler.application.service.mapper.DanhSachLopMapper;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.io.imports.ExcelDanhSachLopImporter;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopHocPhanRaw;

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

	public ImportDanhSachLopService(
			ExcelDanhSachLopImporter importer,
			DanhSachLopRepository danhSachLopRepository,
			HocPhanRepository hocPhanRepository,
			GiangVienRepository giangVienRepository,
			LopHocPhanRepository lopHocPhanRepository,
			NguoiDungRepository nguoiDungRepository,
			HocKyRepository hocKyRepository,
			TepTaiLenRepository tepTaiLenRepository)
	{
		this.importer = importer;
		this.danhSachLopRepository = danhSachLopRepository;
		this.hocPhanRepository = hocPhanRepository;
		this.giangVienRepository = giangVienRepository;
		this.lopHocPhanRepository = lopHocPhanRepository;
		this.nguoiDungRepository = nguoiDungRepository;
		this.hocKyRepository = hocKyRepository;
		this.tepTaiLenRepository = tepTaiLenRepository;
	}

	@Override
	public DanhSachLopDto importFromExcel(
			Long nguoiTaoId,
			String tenDanhSach,
			Long hocKyId,
			TepTaiLenDto tepTaiLenDto)
	{
		if(nguoiTaoId == null)
			throw new ValidationException("Người Tạo ID không được phép NULL");

		if(tenDanhSach == null || tenDanhSach.isBlank())
			throw new ValidationException("Tên Danh Sách không được phép để trống");

		if(tepTaiLenDto == null)
			throw new ValidationException("Chưa có Tệp");

		NguoiDung nguoiTao = nguoiDungRepository
				.findById(nguoiTaoId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiTaoId));

		HocKy hocKy = null;
		if(hocKyId != null) {
			hocKy = hocKyRepository
					.findById(hocKyId)
					.orElseThrow(() -> new EntityNotFoundException("HocKy", hocKyId));
		}

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

		DanhSachLop danhSach = DanhSachLop.create(
				tenDanhSach.trim(),
				nguoiTao,
				false,
				hocKy);

		List<ImportedLopHocPhanRaw> rawList = result.rows;

		Map<String, List<ImportedLopHocPhanRaw>> grouped =
		        rawList.stream()
		               .collect(Collectors.groupingBy(ImportedLopHocPhanRaw::maLop));

		for (Map.Entry<String, List<ImportedLopHocPhanRaw>> entry : grouped.entrySet()) {

		    List<ImportedLopHocPhanRaw> rows = entry.getValue();
		    ImportedLopHocPhanRaw first = rows.get(0);

		    // ===== Học phần =====
		    HocPhan hocPhan = hocPhanRepository
		            .findByMaHocPhan(first.maHocPhan())
		            .orElseGet(() -> {
		                HocPhan newHp = HocPhan.create(
		                        first.maHocPhan(),
		                        first.tenHocPhan(),
		                        first.soTinChi());
		                return hocPhanRepository.save(newHp);
		            });

		    // ===== Giảng viên =====
		    GiangVien giangVien = null;
		    if(first.tenGiangVien() != null && !first.tenGiangVien().isBlank()) {
		        giangVien = giangVienRepository
		                .findByTen(first.tenGiangVien())
		                .orElseGet(() -> {
		                    GiangVien gv = GiangVien.create(first.tenGiangVien());
		                    return giangVienRepository.save(gv);
		                });
		    }

		    // ===== Tạo lớp =====
		    LopHocPhan lop = LopHocPhan.create(
		            first.maLop(),
		            hocPhan,
		            giangVien,
		            first.hinhThucDay(),
		            first.diaDiem());

		    // ===== Gộp tất cả lịch học của cùng mã lớp =====
		    for (ImportedLopHocPhanRaw raw : rows) {
		        raw.lichHocList().forEach(lh -> {
		            LichHoc lichHoc = LichHoc.create(
		                    null,
		                    lh.thu(),
		                    lh.tietBatDau(),
		                    lh.tietKetThuc());
		            lop.themLichHoc(lichHoc);
		        });
		    }

		    LopHocPhan savedLop = lopHocPhanRepository.save(lop);
		    danhSach.themLop(savedLop, false);
		}

		DanhSachLop saved = danhSachLopRepository.save(danhSach);

		return DanhSachLopMapper.toDto(saved);
	}
}