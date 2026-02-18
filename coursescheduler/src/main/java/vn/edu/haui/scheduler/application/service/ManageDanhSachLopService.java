package vn.edu.haui.scheduler.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.exception.DataAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;

public class ManageDanhSachLopService implements ManageDanhSachLopUseCase
{

	private final DanhSachLopRepository danhSachRepo;

	public ManageDanhSachLopService(DanhSachLopRepository danhSachRepo)
	{
		this.danhSachRepo = danhSachRepo;
	}

	@Override
	public List<DanhSachLopDto> listDanhSachChoNguoiDung(Long nguoiDungId)
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
	public DanhSachLopDto getChiTietDanhSach(Long nguoiDungId, Long danhSachId)
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
	public DanhSachLopDto updateDanhSach(Long nguoiDungId, UpdateDanhSachLopRequestDto request)
	{
		if(nguoiDungId == null || request == null || request.getId() == null) {
			throw new ValidationException("Invalid request");
		}

		try {
			boolean isCreator = danhSachRepo.isCreator(request.getId(), nguoiDungId);

			if(!isCreator) {
				throw new ValidationException("Only creator can edit the list");
			}

			String ten = request.getTenDanhSach();

			if(ten == null || ten.trim().isEmpty()) {
				throw new ValidationException("Ten danh sach required");
			}

			danhSachRepo.updateHeader(
					request.getId(),
					ten.trim(),
					request.getHocKyId());

			if(request.getLopHocPhanIds() != null) {

				danhSachRepo.deleteAllChiTiet(request.getId());

				for(Long lopId : request.getLopHocPhanIds()) {
					if(lopId != null) {
						danhSachRepo.addChiTiet(request.getId(), lopId);
					}
				}
			}

			Optional<DanhSachLop> updated = danhSachRepo.findByIdWithDetails(request.getId());

			DanhSachLop danhSach = updated.orElseThrow(() -> new DataAccessException("Updated but cannot fetch"));

			return toDto(danhSach);
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot update list", ex);
		}
	}

	@Override
	public void deleteDanhSach(Long nguoiDungId, Long danhSachId)
	{
		if(nguoiDungId == null || danhSachId == null) {
			throw new ValidationException("Invalid ids");
		}

		try {
			boolean isCreator = danhSachRepo.isCreator(danhSachId, nguoiDungId);

			if(!isCreator) {
				throw new ValidationException("Only creator can delete the list");
			}

			danhSachRepo.deleteDanhSach(danhSachId);
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new DataAccessException("Cannot delete list", ex);
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
