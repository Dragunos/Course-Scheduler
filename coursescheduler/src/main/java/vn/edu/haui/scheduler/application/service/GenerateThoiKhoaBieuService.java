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
import vn.edu.haui.scheduler.domain.optimizer.ConstraintMapper;
import vn.edu.haui.scheduler.domain.optimizer.HardConstraint;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationInput;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationResult;
import vn.edu.haui.scheduler.domain.optimizer.Optimizer;
import vn.edu.haui.scheduler.domain.optimizer.SoftConstraint;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
		
		System.out.println("requiredCourseIds = " + requiredCourseIds);

		if(requiredCourseIds.isEmpty()) {
			throw new IllegalArgumentException("Không có Lớp Học Phần nào thuộc Học phần mong muốn được mở");
		}

		List<HardConstraint> hardConstraints = ConstraintMapper.mapHard(rangBuocDtos);

		List<SoftConstraint> softConstraints = ConstraintMapper.mapSoft(rangBuocDtos);

		System.out.println("hardConstraints size = " + hardConstraints.size());
		System.out.println("softConstraints size = " + softConstraints.size());

		OptimizationInput input = new OptimizationInput(
				availableSections,
				requiredCourseIds,
				hardConstraints,
				softConstraints,
				topK,
				Duration.ofMillis(timeLimitMillis));
		
		System.out.println("availableSections size = " + availableSections.size());

		List<OptimizationResult> results = optimizer.optimize(input);

		System.out.println(results);

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

					System.out.println("selected size = " + r.selectedSections().size());
					System.out.println("score = " + r.score());

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
			throw new IllegalArgumentException("Không có lớp học nào được tìm thấy");
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

		System.out.println("availableSections size = " + availableSections.size());
		
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
	    if (selectedResults == null || selectedResults.isEmpty()) return;

	    for (ThoiKhoaBieuDto dto : selectedResults) {

	        NguoiDung nguoiDung = nguoiDungRepository.findById(dto.getNguoiDungId())
	                .orElseThrow(() -> new EntityNotFoundException("NguoiDung", dto.getNguoiDungId()));

	        // Use danhSachLopId param if provided, otherwise fallback to dto.getDanhSachLopId()
	        Long usedDanhSachId = danhSachLopId != null ? danhSachLopId : dto.getDanhSachLopId();

	        DanhSachLop danhSach = danhSachLopRepository.findById(usedDanhSachId)
	                .orElseThrow(() -> new EntityNotFoundException("DanhSachLop", usedDanhSachId));

	        // Build a map of available sections for quick lookup
	        Map<Long, LopHocPhan> availableMap = danhSach.getLopHocPhanList()
	                .stream()
	                .collect(Collectors.toMap(LopHocPhan::getId, s -> s));

	        // Prepare cacLop from DTO's id list
	        List<LopHocPhan> cacLop = new ArrayList<>();

	        List<Long> lopIdList = dto.getLopHocPhanIdList();
	        if (lopIdList != null && !lopIdList.isEmpty()) {
	            for (Long lopId : lopIdList) {
	                LopHocPhan found = availableMap.get(lopId);
	                if (found == null) {
	                    // If you prefer to tolerate missing sections, change this to a warning + continue.
	                    throw new EntityNotFoundException("LopHocPhan", lopId);
	                }
	                cacLop.add(found);
	            }
	        } else {
	            // Fallback: try to build from danhSachLopHocPhan DTOs if id list not present
	            if (dto.getDanhSachLopHocPhan() != null && !dto.getDanhSachLopHocPhan().isEmpty()) {
	                for (var lDto : dto.getDanhSachLopHocPhan()) {
	                    Long lopId = lDto.getId();
	                    LopHocPhan found = availableMap.get(lopId);
	                    if (found == null) {
	                        throw new EntityNotFoundException("LopHocPhan", lopId);
	                    }
	                    cacLop.add(found);
	                }
	            }
	        }

	        // Convert DTO -> domain with real LopHocPhan list
	        ThoiKhoaBieu domain = ThoiKhoaBieuMapper.toDomain(dto, nguoiDung, danhSach, cacLop);

	        if (overwrite) {
	            thoiKhoaBieuRepository.update(domain);
	        } else {
	            thoiKhoaBieuRepository.save(domain);
	        }
	    }
	}
}