package vn.edu.haui.scheduler.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ImportDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.in.AdminDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;

public class AdminDanhSachLopService
		implements AdminDanhSachLopUseCase
{
	private final DanhSachLopRepository repo;

	private final ImportDanhSachLopUseCase importUseCase;

	public AdminDanhSachLopService(
			DanhSachLopRepository repo,
			ImportDanhSachLopUseCase importUseCase)
	{
		this.repo = repo;
		this.importUseCase = importUseCase;
	}

	@Override
	public List<DanhSachLopDto> listDanhSachCongKhai()
	{
		try {
			return repo.findAllPublic()
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot load public lists", ex);
		}
	}

	@Override
	public DanhSachLopDto getChiTiet(Long danhSachId)
	{
		try {
			Optional<DanhSachLop> opt = repo.findByIdWithDetails(danhSachId);

			DanhSachLop ds = opt.orElseThrow(
					() -> new ValidationException("Not found"));

			if(!ds.isCongKhai()) {
				throw new ValidationException("Not public list");
			}

			return toDto(ds);
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot load detail", ex);
		}
	}

	@Override
	public DanhSachLopDto importDanhSach(
			ImportDanhSachLopRequestDto request)
	{
		if(request == null) {
			throw new ValidationException("Invalid request");
		}

		DanhSachLopDto dto = importUseCase.importDanhSach(request);

		return dto;
	}

	@Override
	public DanhSachLopDto updateDanhSach(
			UpdateDanhSachLopRequestDto request)
	{
		if(request == null || request.getId() == null) {
			throw new ValidationException("Invalid request");
		}

		try {
			repo.updateHeader(
					request.getId(),
					request.getTenDanhSach(),
					request.getHocKyId());

			repo.deleteAllChiTiet(request.getId());

			if(request.getLopHocPhanIds() != null) {
				for(Long id : request.getLopHocPhanIds()) {
					repo.addChiTiet(request.getId(), id);
				}
			}

			return getChiTiet(request.getId());
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot update", ex);
		}
	}

	@Override
	public void deleteDanhSach(Long danhSachId)
	{
		try {
			if(!repo.isPublic(danhSachId)) {
				throw new ValidationException("Not public list");
			}

			repo.deleteDanhSach(danhSachId);
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot delete", ex);
		}
	}

	private DanhSachLopDto toDto(DanhSachLop model)
	{
		DanhSachLopDto dto = new DanhSachLopDto();

		dto.setId(model.getId());
		dto.setTenDanhSach(model.getTenDanhSach());

		dto.setHocKyId(
				model.getHocKy() != null
						? model.getHocKy().getId()
						: null);

		dto.setNguoiTaoId(
				model.getNguoiTao() != null
						? model.getNguoiTao().getId()
						: null);

		dto.setLaCongKhai(model.isCongKhai() ? 1 : 0);
		dto.setNgayTao(model.getNgayTao());

		return dto;
	}
}
