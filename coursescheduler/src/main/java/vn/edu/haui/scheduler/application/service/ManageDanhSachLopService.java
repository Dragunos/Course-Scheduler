package vn.edu.haui.scheduler.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.LichHocDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.DanhSachLopChiTiet;
import vn.edu.haui.scheduler.infrastructure.persistence.config.TransactionManager;

public class ManageDanhSachLopService implements ManageDanhSachLopUseCase
{

	private final DanhSachLopRepository danhSachRepo;

	private final TransactionManager txManager;

	public ManageDanhSachLopService(
			DanhSachLopRepository danhSachRepo,
			TransactionManager txManager)
	{
		this.danhSachRepo = danhSachRepo;
		this.txManager = txManager;
	}

	@Override
	public List<DanhSachLopDto> getAllDanhSachLopByNguoiDungId(Long nguoiDungId)
	{
		if(nguoiDungId == null) {
			throw new ValidationException("User id required");
		}

		try {
			List<DanhSachLop> danhSachList = danhSachRepo.findByNguoiTaoOrShared(nguoiDungId);

			return danhSachList.stream()
					.map(this::toDto)
					.collect(Collectors.toList());
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot load lists", ex);
		}
	}

	@Override
	public DanhSachLopDto getDanhSachLopById(Long nguoiDungId, Long danhSachId)
	{
		if(nguoiDungId == null || danhSachId == null) {
			throw new ValidationException("Missing ids");
		}

		try {
			boolean allowed = danhSachRepo.isCreator(danhSachId, nguoiDungId)
					|| danhSachRepo.isShared(danhSachId, nguoiDungId);

			if(!allowed) {
				throw new ValidationException("Access denied");
			}

			Optional<DanhSachLop> opt = danhSachRepo.findByIdWithDetails(danhSachId);

			DanhSachLop danhSach = opt.orElseThrow(() -> new ValidationException("Danh sach not found"));

			return toDto(danhSach);
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot load detail", ex);
		}
	}

	@Override
	public DanhSachLopDto updateDanhSachLop(
			Long nguoiDungId,
			UpdateDanhSachLopRequestDto request)
	{
		if(nguoiDungId == null || request == null || request.getId() == null)
			throw new ValidationException("Invalid request");

		return txManager.executeInTransaction(() -> {

			DanhSachLop danhSach = danhSachRepo.findByIdWithDetails(request.getId())
					.orElseThrow(() -> new ValidationException("Danh sach not found"));

			if(!danhSach.getNguoiTao().getId().equals(nguoiDungId))
				throw new ValidationException("Access denied");

			String ten = request.getTenDanhSach();

			if(ten == null || ten.trim().isEmpty())
				throw new ValidationException("Ten danh sach required");

			danhSach.doiTenDanhSach(ten.trim());
			danhSach.doiHocKy(request.getHocKyId());

			if(request.getLopHocPhanIds() != null)
				syncChiTiet(danhSach, request.getLopHocPhanIds());

			danhSachRepo.save(danhSach);

			return toDto(danhSach);
		});
	}

	@Override
	public void deleteDanhSachLop(Long nguoiDungId, Long danhSachId)
	{
		if(nguoiDungId == null || danhSachId == null)
			throw new ValidationException("Invalid ids");

		txManager.executeInTransaction(() -> {

			DanhSachLop danhSach = danhSachRepo.findByIdWithDetails(danhSachId)
					.orElseThrow(() -> new ValidationException("Danh sach not found"));

			if(!danhSach.getNguoiTao().getId().equals(nguoiDungId))
				throw new ValidationException("Access denied");

			danhSachRepo.deleteDanhSach(danhSachId);

			return null;
		});
	}

	private void syncChiTiet(
			DanhSachLop danhSach,
			List<Long> newIds)
	{
		Set<Long> currentIds = danhSach.getChiTietList()
				.stream()
				.map(ct -> ct.getLopHocPhan().getId())
				.collect(Collectors.toSet());

		Set<Long> targetIds = newIds.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Set<Long> toAdd = new HashSet<>(targetIds);
		toAdd.removeAll(currentIds);

		Set<Long> toRemove = new HashSet<>(currentIds);
		toRemove.removeAll(targetIds);

		for(Long id : toAdd)
			danhSach.themLopHocPhan(id);

		for(Long id : toRemove)
			danhSach.xoaLopHocPhan(id);
	}

	private DanhSachLopChiTietDto toChiTietDto(DanhSachLopChiTiet model)
	{
		DanhSachLopChiTietDto dto = new DanhSachLopChiTietDto();

		LopHocPhan lop = model.getLopHocPhan();

		if(lop != null) {

			dto.setMaLop(lop.getMaLop());
			dto.setDiaDiem(lop.getDiaDiem());

			if(lop.getHinhThucDay() != null) {
				dto.setHinhThucDay(lop.getHinhThucDay().name());
			}

			if(lop.getHocPhan() != null) {
				dto.setTenHocPhan(lop.getHocPhan().getTenHocPhan());
				dto.setMaHocPhan(lop.getHocPhan().getMaHocPhan());
			}

			if(lop.getGiangVien() != null) {
				dto.setTenGiangVien(lop.getGiangVien().getTenGiangVien());
			}

			if(lop.getDanhSachLichHoc() != null) {
				dto.setLichHoc(
						lop.getDanhSachLichHoc()
								.stream()
								.map(this::toLichHocDto)
								.collect(Collectors.toList()));
			}
		}

		return dto;
	}

	private LichHocDto toLichHocDto(LichHoc lich)
	{
		LichHocDto dto = new LichHocDto();

		dto.setThu(lich.getThu());
		dto.setTietBatDau(lich.getTietBatDau());
		dto.setTietKetThuc(lich.getTietKetThuc());

		return dto;
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

		if(model.getChiTietList() != null) {
			dto.setChiTiet(
					model.getChiTietList()
							.stream()
							.map(this::toChiTietDto)
							.collect(Collectors.toList()));
		}

		return dto;
	}
}
