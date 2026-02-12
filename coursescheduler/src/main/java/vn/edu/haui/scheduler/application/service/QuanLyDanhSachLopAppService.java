package vn.edu.haui.scheduler.application.service;

import java.util.List;
import java.util.Optional;

import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.dto.UpdateDanhSachLopRequestDto;
import vn.edu.haui.scheduler.application.port.in.QuanLyDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepositoryPort;
import vn.edu.haui.scheduler.application.exception.PersistenceException;
import vn.edu.haui.scheduler.application.exception.ValidationException;

public class QuanLyDanhSachLopAppService implements QuanLyDanhSachLopUseCase
{

	private final DanhSachLopRepositoryPort danhSachRepo;

	public QuanLyDanhSachLopAppService(DanhSachLopRepositoryPort danhSachRepo)
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
			return danhSachRepo.findByNguoiTaoOrShared(nguoiDungId);
		}
		catch(Exception ex) {
			throw new PersistenceException("Cannot load lists", ex);
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
			Optional<DanhSachLopDto> opt = danhSachRepo.findByIdWithDetails(danhSachId);
			return opt.orElseThrow(() -> new ValidationException("Danh sach not found"));
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new PersistenceException("Cannot load detail", ex);
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

			Optional<DanhSachLopDto> updated = danhSachRepo.findByIdWithDetails(request.getId());

			return updated.orElseThrow(
					() -> new PersistenceException("Updated but cannot fetch"));
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new PersistenceException("Cannot update list", ex);
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
			throw new PersistenceException("Cannot delete list", ex);
		}
	}
}
