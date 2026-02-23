package vn.edu.haui.scheduler.application.service;

import java.util.List;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.TepTaiLenDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ImportDanhSachLopException;
import vn.edu.haui.scheduler.application.exception.UnauthorizedAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.AdminDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.HocKyRepository;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.service.mapper.DanhSachLopMapper;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManager;

public class AdminDanhSachLopService implements AdminDanhSachLopUseCase
{
	private static final String ADMIN_ROLE_NAME = "ADMIN";

	private final DanhSachLopRepository danhSachLopRepository;

	private final NguoiDungRepository nguoiDungRepository;

	private final HocKyRepository hocKyRepository;

	private final ImportDanhSachLopUseCase importDanhSachLopService;

	private final TransactionManager transactionManager;

	public AdminDanhSachLopService(
			DanhSachLopRepository danhSachLopRepository,
			NguoiDungRepository nguoiDungRepository,
			HocKyRepository hocKyRepository,
			ImportDanhSachLopUseCase importDanhSachLopService,
			TransactionManager transactionManager)
	{
		this.danhSachLopRepository = danhSachLopRepository;
		this.nguoiDungRepository = nguoiDungRepository;
		this.hocKyRepository = hocKyRepository;
		this.importDanhSachLopService = importDanhSachLopService;
		this.transactionManager = transactionManager;
	}

	@Override
	public List<DanhSachLopDto> findAllPublic()
	{
		return danhSachLopRepository.findPublicLists()
				.stream()
				.map(DanhSachLopMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public DanhSachLopDto importPublic(
			Long adminId,
			String tenDanhSach,
			Long hocKyId,
			TepTaiLenDto tepTaiLenDto)
	{
		validateAdmin(adminId);

		HocKy hocKy = validateHocKy(hocKyId);

		DanhSachLopDto imported = importDanhSachLopService.importFromExcel(
				adminId,
				tenDanhSach,
				hocKy.getId(),
				tepTaiLenDto);

		transactionManager.begin();
		try {
			DanhSachLop domain = danhSachLopRepository
					.findById(imported.getId())
					.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", imported.getId()));

			domain.congKhai();

			DanhSachLop saved = danhSachLopRepository.save(domain);

			transactionManager.commit();

			return DanhSachLopMapper.toDto(saved);
		}
		catch(Exception e) {
			transactionManager.rollback();
			throw new ImportDanhSachLopException("Cannot import public DanhSachLop", e);
		}
	}

	@Override
	public void deletePublic(Long adminId, Long danhSachLopId)
	{
		if(adminId == null || danhSachLopId == null)
			throw new ValidationException("Id must not be null");

		validateAdmin(adminId);

		DanhSachLop danhSach = danhSachLopRepository
				.findById(danhSachLopId)
				.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", danhSachLopId));

		if(!danhSach.isLaCongKhai()) {
			throw new ValidationException("DanhSachLop is not public");
		}

		transactionManager.begin();
		try {
			danhSachLopRepository.deleteById(danhSachLopId);
			transactionManager.commit();
		}
		catch(Exception e) {
			transactionManager.rollback();
			throw e;
		}
	}

	private void validateAdmin(Long adminId)
	{
		if(adminId == null)
			throw new ValidationException("AdminId must not be null");

		NguoiDung admin = nguoiDungRepository
				.findById(adminId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", adminId));

		if(admin.getVaiTro() == null ||
				!ADMIN_ROLE_NAME.equalsIgnoreCase(admin.getVaiTro().getTenVaiTro())) {
			throw new UnauthorizedAccessException("User is not admin");
		}
	}

	private HocKy validateHocKy(Long hocKyId)
	{
		if(hocKyId == null)
			throw new ValidationException("HocKyId must not be null");

		return hocKyRepository.findById(hocKyId)
				.orElseThrow(() -> new EntityNotFoundException("HocKy", hocKyId));
	}
}