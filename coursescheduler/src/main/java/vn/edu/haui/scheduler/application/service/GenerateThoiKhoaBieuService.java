package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.dto.RangBuocToiUuDto;
import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.port.in.GenerateThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.NguoiDungRepository;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepository;
import vn.edu.haui.scheduler.application.port.out.YeuCauRepository;
import vn.edu.haui.scheduler.application.service.mapper.RangBuocToiUuMapper;
import vn.edu.haui.scheduler.application.service.mapper.ThoiKhoaBieuMapper;
import vn.edu.haui.scheduler.domain.model.DanhSachLop;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.NguoiDung;
import vn.edu.haui.scheduler.domain.model.RangBuocToiUu;
import vn.edu.haui.scheduler.domain.model.ThoiKhoaBieu;
import vn.edu.haui.scheduler.domain.model.YeuCau;
import vn.edu.haui.scheduler.domain.optimizer.HardConstraint;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationInput;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationResult;
import vn.edu.haui.scheduler.domain.optimizer.Optimizer;
import vn.edu.haui.scheduler.domain.optimizer.SoftConstraint;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenerateThoiKhoaBieuService implements GenerateThoiKhoaBieuUseCase
{
	private final NguoiDungRepository nguoiDungRepository;

	private final DanhSachLopRepository danhSachLopRepository;

	private final YeuCauRepository yeuCauRepository;

	private final ThoiKhoaBieuRepository thoiKhoaBieuRepository;

	private final Optimizer optimizer;

	public GenerateThoiKhoaBieuService(
			NguoiDungRepository nguoiDungRepository,
			DanhSachLopRepository danhSachLopRepository,
			YeuCauRepository yeuCauRepository,
			ThoiKhoaBieuRepository thoiKhoaBieuRepository,
			Optimizer optimizer)
	{
		this.nguoiDungRepository = nguoiDungRepository;
		this.danhSachLopRepository = danhSachLopRepository;
		this.yeuCauRepository = yeuCauRepository;
		this.thoiKhoaBieuRepository = thoiKhoaBieuRepository;
		this.optimizer = optimizer;
	}

	@Override
	public List<ThoiKhoaBieuDto> generate(
			Long nguoiDungId,
			Long danhSachLopId,
			List<String> maHocPhanDangKy,
			List<RangBuocToiUuDto> rangBuocDtos,
			int topK,
			long timeLimitMillis)
	{

		DanhSachLop danhSach = danhSachLopRepository.findById(danhSachLopId)
				.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", danhSachLopId));

		NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
				.orElseThrow(() -> new EntityNotFoundException("NguoiDung", nguoiDungId));

		// tạo YeuCau mới (aggregate root)
		YeuCau yeuCau = YeuCau.create(
				nguoiDung,
				danhSach,
				"Auto Generated Request");

		List<RangBuocToiUu> rangBuocs = rangBuocDtos.stream()
				.map(dto -> RangBuocToiUuMapper.toDomain(dto, yeuCau, null, null))
				.collect(Collectors.toList());

		for(RangBuocToiUu rb : rangBuocs) {
			yeuCau.themRangBuoc(rb);
		}

		yeuCauRepository.save(yeuCau);

		List<LopHocPhan> availableSections = danhSach.getLopHocPhanList();

		Set<Long> requiredCourseIds = availableSections.stream()
				.filter(s -> maHocPhanDangKy.contains(s.getHocPhan().getMaHocPhan()))
				.map(s -> s.getHocPhan().getId())
				.collect(Collectors.toSet());

		if(requiredCourseIds.isEmpty()) {
			throw new IllegalArgumentException("No required courses found for given maHocPhanDangKy");
		}

		List<HardConstraint> hardConstraints = List.of();
		List<SoftConstraint> softConstraints = List.of();

		OptimizationInput input = new OptimizationInput(
				availableSections,
				requiredCourseIds,
				hardConstraints,
				softConstraints,
				topK,
				Duration.ofMillis(timeLimitMillis));

		List<OptimizationResult> results = optimizer.optimize(input);

		return results.stream()
				.map(r -> {
					ThoiKhoaBieu tkb = ThoiKhoaBieu.create(
							yeuCau.getNguoiTao(),
							yeuCau.getDanhSachLop(),
							"Generated Plan");

					for(LopHocPhan lop : r.selectedSections()) {
						tkb.themLop(lop);
					}

					tkb.chamDiem(r.score());

					return ThoiKhoaBieuMapper.toDto(tkb);
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<ThoiKhoaBieuDto> reGenerate(
			Long nguoiDungId,
			Long thoiKhoaBieuId,
			List<RangBuocToiUuDto> updatedConstraints,
			int topK,
			long timeLimitMillis)
	{

		ThoiKhoaBieu existing = thoiKhoaBieuRepository.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new EntityNotFoundException("ThoiKhoaBieu", thoiKhoaBieuId));

		YeuCau yeuCau = YeuCau.create(
				existing.getNguoiDung(),
				existing.getDanhSachLop(),
				"Regenerate from TKB " + thoiKhoaBieuId);

		List<RangBuocToiUu> rangBuocs = updatedConstraints.stream()
				.map(dto -> RangBuocToiUuMapper.toDomain(dto, yeuCau, null, null))
				.collect(Collectors.toList());

		for(RangBuocToiUu rb : rangBuocs) {
			yeuCau.themRangBuoc(rb);
		}

		yeuCauRepository.save(yeuCau);

		List<LopHocPhan> availableSections = existing.getDanhSachLop().getLopHocPhanList();

		Set<Long> requiredCourseIds = existing.getCacLop()
				.stream()
				.map(l -> l.getHocPhan().getId())
				.collect(Collectors.toSet());

		if(requiredCourseIds.isEmpty()) {
			throw new IllegalArgumentException("No required courses found in existing schedule");
		}

		List<HardConstraint> hardConstraints = List.of();
		List<SoftConstraint> softConstraints = List.of();

		OptimizationInput input = new OptimizationInput(
				availableSections,
				requiredCourseIds,
				hardConstraints,
				softConstraints,
				topK,
				Duration.ofMillis(timeLimitMillis));

		List<OptimizationResult> results = optimizer.optimize(input);

		return results.stream()
				.map(r -> {
					ThoiKhoaBieu tkb = ThoiKhoaBieu.create(
							yeuCau.getNguoiTao(),
							yeuCau.getDanhSachLop(),
							"Generated Plan");

					for(LopHocPhan lop : r.selectedSections()) {
						tkb.themLop(lop);
					}

					tkb.chamDiem(r.score());

					return ThoiKhoaBieuMapper.toDto(tkb);
				})
				.collect(Collectors.toList());
	}

	@Override
	public void save(
			Long nguoiDungId,
			Long danhSachLopId,
			List<ThoiKhoaBieuDto> selectedResults,
			boolean overwrite)
	{

		for(ThoiKhoaBieuDto dto : selectedResults) {

			NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getNguoiDungId())
					.orElseThrow(() -> new EntityNotFoundException("NguoiDung", dto.getNguoiDungId()));

			DanhSachLop danhSach = danhSachLopRepository.findById(dto.getDanhSachLopId())
					.orElseThrow(() -> new EntityNotFoundException("DanhSachLop", dto.getDanhSachLopId()));

			List<LopHocPhan> cacLop = List.of();

			ThoiKhoaBieu domain = ThoiKhoaBieuMapper.toDomain(dto, nguoiDung, danhSach, cacLop);

			if(overwrite) {
				thoiKhoaBieuRepository.update(domain);
			}
			else {
				thoiKhoaBieuRepository.save(domain);
			}
		}
	}
}