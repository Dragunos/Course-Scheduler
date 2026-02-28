package vn.edu.haui.scheduler.application.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import vn.edu.haui.scheduler.application.dto.DanhSachLopChiTietDto;
import vn.edu.haui.scheduler.application.dto.DanhSachLopDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.UnauthorizedAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ManageDanhSachLopUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.HocKyRepository;
import vn.edu.haui.scheduler.application.port.out.LopHocPhanRepository;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.service.mapper.DanhSachLopMapper;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.HocKy;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;

public class ManageDanhSachLopService implements ManageDanhSachLopUseCase
{
	private final DanhSachLopRepository danhSachLopRepository;

	private final NguoiDungRepository nguoiDungRepository;

	private final HocKyRepository hocKyRepository;

	private final LopHocPhanRepository lopHocPhanRepository;

	public ManageDanhSachLopService(
			DanhSachLopRepository danhSachLopRepository,
			NguoiDungRepository nguoiDungRepository,
			HocKyRepository hocKyRepository,
			LopHocPhanRepository lopHocPhanRepository)
	{
		this.danhSachLopRepository = danhSachLopRepository;
		this.nguoiDungRepository = nguoiDungRepository;
		this.hocKyRepository = hocKyRepository;
		this.lopHocPhanRepository = lopHocPhanRepository;
	}

	@Override
	public List<DanhSachLopDto> findAllByUser(Long nguoiDungId)
	{
		if(nguoiDungId == null)
			throw new ValidationException("Người Dùng ID không được phép NULL");

		NguoiDung user = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		List<DanhSachLop> owned = danhSachLopRepository.findByNguoiTaoId(user.getId());

		List<DanhSachLop> publicLists = danhSachLopRepository.findPublicLists();

		Set<Long> seenIds = new HashSet<>();
		List<DanhSachLop> accessible = new ArrayList<>();

		for(DanhSachLop d : owned) {
			accessible.add(d);
			seenIds.add(d.getId());
		}

		for(DanhSachLop d : publicLists) {
			if(!seenIds.contains(d.getId())) {
				accessible.add(d);
			}
		}

		return accessible.stream()
				.map(DanhSachLopMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public DanhSachLopDto findDetail(Long nguoiDungId, Long danhSachLopId)
	{
		if(nguoiDungId == null || danhSachLopId == null)
			throw new ValidationException("IDs Không được phép NULL");

		NguoiDung user = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		DanhSachLop danhSach = danhSachLopRepository
				.findById(danhSachLopId)
				.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", danhSachLopId));

		checkAccess(user, danhSach);

		List<DanhSachLopChiTietDto> chiTietDtos = danhSach.getChiTietList()
				.stream()
				.map(DanhSachLopMapper::toDetailDto)
				.collect(Collectors.toList());

		return DanhSachLopMapper.toDetailDto(danhSach, chiTietDtos);
	}

	@Override
	public DanhSachLopDto updateDanhSach(
			Long nguoiDungId,
			Long danhSachLopId,
			String tenDanhSach,
			Long hocKyId,
			List<Long> lopHocPhanIds)
	{
		if(nguoiDungId == null || danhSachLopId == null)
			throw new ValidationException("IDs Không được phép NULL");

		if(tenDanhSach == null || tenDanhSach.isBlank())
			throw new ValidationException("Tên Danh Sách không được để trống");

		NguoiDung user = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		DanhSachLop danhSach = danhSachLopRepository
				.findById(danhSachLopId)
				.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", danhSachLopId));

		checkAccess(user, danhSach);

		HocKy hocKy = null;
		if(hocKyId != null) {
			hocKy = hocKyRepository
					.findById(hocKyId)
					.orElseThrow(() -> new EntityNotFoundException("HocKy", hocKyId));
		}

		danhSach.doiTen(tenDanhSach.trim());
		danhSach.doiHocKy(hocKy);

		danhSach.xoaTatCaLop();

		if(lopHocPhanIds != null) {
			for(Long lopId : lopHocPhanIds) {

				LopHocPhan lop = lopHocPhanRepository
						.findById(lopId)
						.orElseThrow(() -> new EntityNotFoundException("LopHocPhan", lopId));

				danhSach.themLop(lop, false);
			}
		}

		DanhSachLop saved = danhSachLopRepository.save(danhSach);

		List<DanhSachLopChiTietDto> chiTietDtos = saved.getChiTietList()
				.stream()
				.map(DanhSachLopMapper::toDetailDto)
				.collect(Collectors.toList());

		return DanhSachLopMapper.toDetailDto(saved, chiTietDtos);
	}

	@Override
	public void deleteDanhSach(Long nguoiDungId, Long danhSachLopId)
	{
		if(nguoiDungId == null || danhSachLopId == null)
			throw new ValidationException("IDs Không được phép NULL");

		NguoiDung user = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		DanhSachLop danhSach = danhSachLopRepository
				.findById(danhSachLopId)
				.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", danhSachLopId));

		checkAccess(user, danhSach);

		danhSachLopRepository.deleteById(danhSach.getId());
	}

	private void checkAccess(NguoiDung user, DanhSachLop danhSach)
	{
		boolean isOwner = Objects.equals(
				danhSach.getNguoiTao().getId(),
				user.getId());

		boolean isShared = danhSach.duocChiaSeCho(user);

		if(!isOwner && !isShared)
			throw new UnauthorizedAccessException();
	}
}