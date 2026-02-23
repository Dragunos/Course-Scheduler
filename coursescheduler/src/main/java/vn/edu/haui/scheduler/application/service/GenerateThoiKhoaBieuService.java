package vn.edu.haui.scheduler.application.service;

import java.util.*;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.GenerateThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepository;
import vn.edu.haui.scheduler.application.port.out.YeuCauRepository;
import vn.edu.haui.scheduler.application.service.mapper.ThoiKhoaBieuMapper;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.domain.optimizer.Optimizer;
import vn.edu.haui.scheduler.domain.optimizer.PhuongAnThoiKhoaBieu;

public class GenerateThoiKhoaBieuService implements GenerateThoiKhoaBieuUseCase
{
	private static final long DEFAULT_TIME_LIMIT = 5000L;

	private final NguoiDungRepository nguoiDungRepository;

	private final DanhSachLopRepository danhSachLopRepository;

	private final ThoiKhoaBieuRepository thoiKhoaBieuRepository;

	private final YeuCauRepository yeuCauRepository;

	public GenerateThoiKhoaBieuService(
			NguoiDungRepository nguoiDungRepository,
			DanhSachLopRepository danhSachLopRepository,
			ThoiKhoaBieuRepository thoiKhoaBieuRepository,
			YeuCauRepository yeuCauRepository)
	{
		this.nguoiDungRepository = nguoiDungRepository;
		this.danhSachLopRepository = danhSachLopRepository;
		this.thoiKhoaBieuRepository = thoiKhoaBieuRepository;
		this.yeuCauRepository = yeuCauRepository;
	}

	@Override
	public List<ThoiKhoaBieuDto> generate(
			Long nguoiDungId,
			Long yeuCauId,
			int topK)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		if(yeuCauId == null)
			throw new ValidationException("YeuCauId must not be null");

		if(topK <= 0)
			throw new ValidationException("topK must be positive");

		NguoiDung nguoiDung = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		YeuCau yeuCau = yeuCauRepository
				.findById(yeuCauId)
				.orElseThrow(() -> new EntityNotFoundException("YeuCau", yeuCauId));

		DanhSachLop danhSach = yeuCau.getDanhSachLop();

		List<LopHocPhan> allLop = danhSach
				.getChiTietList()
				.stream()
				.map(DanhSachLopChiTiet::getLopHocPhan)
				.toList();

		Set<String> requiredHocPhanCodes = new HashSet<>();
		Set<String> preferredLopIds = new HashSet<>();
		Set<String> avoidHinhThuc = new HashSet<>();
		Set<Integer> avoidTiet = new HashSet<>();
		Set<Integer> avoidThu = new HashSet<>();

		for(RangBuocToiUu rb : yeuCau.getRangBuoc()) {
			switch(rb.getLoaiRangBuoc()) {
				case "FIX_SECTION" -> preferredLopIds.add(rb.getTargetValue());

				case "REQUIRE_COURSE" -> requiredHocPhanCodes.add(rb.getTargetValue());

				case "AVOID_MODE" -> avoidHinhThuc.add(rb.getValue());

				case "AVOID_THU" -> avoidThu.add(Integer.parseInt(rb.getValue()));

				case "AVOID_TIET" -> avoidTiet.add(Integer.parseInt(rb.getValue()));
			}
		}

		Optimizer optimizer = new Optimizer(
				allLop,
				requiredHocPhanCodes,
				preferredLopIds,
				avoidHinhThuc,
				avoidTiet,
				avoidThu,
				topK,
				DEFAULT_TIME_LIMIT);

		List<PhuongAnThoiKhoaBieu> results = optimizer.optimize();

		if(results.isEmpty())
			throw new ValidationException("Khong tim duoc thoi khoa bieu hop le");

		return results.stream()
				.map(pa -> {
					ThoiKhoaBieu tkb = ThoiKhoaBieu.create(
							nguoiDung,
							danhSach,
							"GENERATED");

					tkb.chamDiem(pa.getDiemDanhGia());
					pa.getLopHocPhans().forEach(tkb::themLop);

					return ThoiKhoaBieuMapper.toDto(tkb);
				})
				.toList();
	}

	@Override
	public ThoiKhoaBieuDto regenerate(
			Long nguoiDungId,
			Long thoiKhoaBieuId,
			Long yeuCauId,
			int topK)
	{
		if(thoiKhoaBieuId == null)
			throw new ValidationException("ThoiKhoaBieuId must not be null");

		thoiKhoaBieuRepository
				.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new EntityNotFoundException(
						"ThoiKhoaBieu",
						thoiKhoaBieuId));

		List<ThoiKhoaBieuDto> list = generate(
				nguoiDungId,
				yeuCauId,
				topK);

		return list.get(0);
	}

	@Override
	public void saveAll(
			Long nguoiDungId,
			List<ThoiKhoaBieuDto> selectedDtos,
			boolean overwrite)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		if(selectedDtos == null || selectedDtos.isEmpty())
			throw new ValidationException("No ThoiKhoaBieu selected");

		NguoiDung nguoiDung = nguoiDungRepository
				.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		for(ThoiKhoaBieuDto dto : selectedDtos) {

			DanhSachLop danhSach = danhSachLopRepository
					.findById(dto.getDanhSachLopId())
					.orElseThrow(() -> new EntityNotFoundException(
							"DanhSachLop",
							dto.getDanhSachLopId()));

			Set<Long> selectedIds = new HashSet<>(dto.getLopHocPhanIdList());
			List<LopHocPhan> cacLop = danhSach
					.getChiTietList()
					.stream()
					.map(DanhSachLopChiTiet::getLopHocPhan)
					.filter(lop -> selectedIds.contains(lop.getId()))
					.toList();

			ThoiKhoaBieu domain = ThoiKhoaBieuMapper.toDomain(
					dto,
					nguoiDung,
					danhSach,
					cacLop);

			if(overwrite && domain.getId() != null) {
				thoiKhoaBieuRepository.deleteById(domain.getId());
			}

			thoiKhoaBieuRepository.save(domain);
		}
	}
}