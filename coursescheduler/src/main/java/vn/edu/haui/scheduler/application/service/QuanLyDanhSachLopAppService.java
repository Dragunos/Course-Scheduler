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
			return danhSachRepo.findByNguoiTaoOrShared(nguoiDungId.intValue());
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
			boolean allowed = danhSachRepo.isCreator(danhSachId.intValue(), nguoiDungId.intValue())
					|| danhSachRepo.isShared(danhSachId.intValue(), nguoiDungId.intValue());
			if(!allowed) {
				throw new ValidationException("Access denied");
			}
			Optional<DanhSachLopDto> opt = danhSachRepo.findByIdWithDetails(danhSachId.intValue());
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
		if(nguoiDungId == null || request == null || request.id == null) {
			throw new ValidationException("Invalid request");
		}
		try {
			boolean isCreator = danhSachRepo.isCreator(request.id.intValue(), nguoiDungId.intValue());
			if(!isCreator) {
				throw new ValidationException("Only creator can edit the list");
			}
			if(request.tenDanhSach == null || request.tenDanhSach.trim().isEmpty()) {
				throw new ValidationException("Ten danh sach required");
			}
			Integer hocKyInt = request.hocKyId != null ? request.hocKyId.intValue() : null;
			danhSachRepo.updateHeader(request.id.intValue(), request.tenDanhSach.trim(), hocKyInt);

			if(request.lopHocPhanIds != null) {
				danhSachRepo.deleteAllChiTiet(request.id.intValue());
				for(Long lopIdLong : request.lopHocPhanIds) {
					if(lopIdLong != null) {
						danhSachRepo.addChiTiet(request.id.intValue(), lopIdLong.intValue());
					}
				}
			}
			Optional<DanhSachLopDto> updated = danhSachRepo.findByIdWithDetails(request.id.intValue());
			return updated.orElseThrow(() -> new PersistenceException("Updated but cannot fetch"));
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
			boolean isCreator = danhSachRepo.isCreator(danhSachId.intValue(), nguoiDungId.intValue());
			if(!isCreator) {
				throw new ValidationException("Only creator can delete the list");
			}
			danhSachRepo.deleteDanhSach(danhSachId.intValue());
		}
		catch(ValidationException ve) {
			throw ve;
		}
		catch(Exception ex) {
			throw new PersistenceException("Cannot delete list", ex);
		}
	}
}
