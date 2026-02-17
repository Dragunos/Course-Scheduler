package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.port.in.SinhThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.*;
import vn.edu.haui.scheduler.domain.constraint.RangBuocToiUu;
import vn.edu.haui.scheduler.domain.enums.*;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.domain.optimizer.BacktrackingOptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SinhThoiKhoaBieuAppService implements SinhThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepositoryPort thoiKhoaBieuRepo;

	private final YeuCauRepositoryPort yeuCauRepo;

	private final YeuCauChiTietRepositoryPort yeuCauChiTietRepo;

	private final DanhSachLopRepositoryPort danhSachRepo;

	private final LopHocPhanRepositoryPort lopRepo;

	private final LichHocRepositoryPort lichRepo;

	private final RangBuocToiUuRepositoryPort rangBuocRepo;

	public SinhThoiKhoaBieuAppService(
			ThoiKhoaBieuRepositoryPort thoiKhoaBieuRepo,
			YeuCauRepositoryPort yeuCauRepo,
			YeuCauChiTietRepositoryPort yeuCauChiTietRepo,
			DanhSachLopRepositoryPort danhSachRepo,
			LopHocPhanRepositoryPort lopRepo,
			LichHocRepositoryPort lichRepo,
			RangBuocToiUuRepositoryPort rangBuocRepo)
	{
		this.thoiKhoaBieuRepo = thoiKhoaBieuRepo;
		this.yeuCauRepo = yeuCauRepo;
		this.yeuCauChiTietRepo = yeuCauChiTietRepo;
		this.danhSachRepo = danhSachRepo;
		this.lopRepo = lopRepo;
		this.lichRepo = lichRepo;
		this.rangBuocRepo = rangBuocRepo;
	}

	@Override
	public long taoYeuCau(long nguoiDungId,
			long danhSachLopId,
			String tenYeuCau) throws Exception
	{
		return yeuCauRepo.save(nguoiDungId, danhSachLopId, tenYeuCau);
	}

	@Override
	public List<PhuongAnThoiKhoaBieuDto> chayToiUu(
			long yeuCauId,
			int topK,
			long timeLimitMillis) throws Exception
	{
		YeuCau yeuCau = yeuCauRepo.findById(yeuCauId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

		DanhSachLop danhSach = danhSachRepo
				.findByIdWithDetails(yeuCau.getDanhSachLopId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy danh sách lớp"));

		List<LopHocPhan> allLops = loadFullLopHocPhan(danhSach);

		Map<Long, LopHocPhan> lopMap = allLops.stream()
				.collect(Collectors.toMap(LopHocPhan::getId, l -> l));

		List<List<Long>> groups = groupByHocPhan(allLops);

		List<RangBuocToiUu> constraints = new ArrayList<>();

		List<YeuCauChiTiet> chiTietList = yeuCauChiTietRepo.findByYeuCauId(yeuCauId);

		constraints.addAll(convertChiTietToConstraint(chiTietList));

		List<RangBuocToiUuDto> dtoList = rangBuocRepo.findByYeuCauId(yeuCauId);

		for(RangBuocToiUuDto dto : dtoList)
			constraints.add(mapToDomain(dto));

		BacktrackingOptimizer optimizer = new BacktrackingOptimizer(topK, timeLimitMillis);

		List<BacktrackingOptimizer.PhuongAn> result = optimizer.solve(groups, constraints);

		return mapToDto(result, lopMap);
	}

	private List<LopHocPhan> loadFullLopHocPhan(DanhSachLop danhSach) throws Exception
	{
		List<Long> ids = danhSach.getChiTietList()
				.stream()
				.map(DanhSachLopChiTiet::getLopHocPhanId)
				.collect(Collectors.toList());

		List<LopHocPhan> lops = lopRepo.findByIds(ids);

		Map<Long, List<LichHoc>> lichMap = lichRepo.findByLopHocPhanIds(ids);

		for(LopHocPhan lop : lops)
			lop.setDanhSachLichHoc(lichMap.get(lop.getId()));

		return lops;
	}

	private List<List<Long>> groupByHocPhan(List<LopHocPhan> list)
	{
		Map<Long, List<Long>> map = list.stream()
				.collect(Collectors.groupingBy(
						l -> l.getHocPhan().getId(),
						Collectors.mapping(LopHocPhan::getId, Collectors.toList())));

		return new ArrayList<>(map.values());
	}

	private List<RangBuocToiUu> convertChiTietToConstraint(
			List<YeuCauChiTiet> chiTietList)
	{
		List<RangBuocToiUu> result = new ArrayList<>();

		for(YeuCauChiTiet ct : chiTietList) {
			if(ct.getLoaiChiDinh() == LoaiChiDinh.REQUIRE) {
				result.add(RangBuocToiUu.requireSection(
						ct.getLopHocPhanId()));
			}
			else if(ct.getLoaiChiDinh() == LoaiChiDinh.EXCLUDE) {
				result.add(RangBuocToiUu.excludeSection(
						ct.getLopHocPhanId()));
			}
		}

		return result;
	}

	@Override
	public long luuPhuongAn(
			long nguoiDungId,
			long danhSachLopId,
			String ten,
			double diem,
			List<Long> lopIds) throws Exception
	{
		long id = thoiKhoaBieuRepo.save(
				nguoiDungId,
				danhSachLopId,
				ten,
				diem);

		thoiKhoaBieuRepo.saveChiTiet(id, lopIds);

		return id;
	}

	@Override
	public List<PhuongAnThoiKhoaBieuDto> toiUuLai(
			long thoiKhoaBieuId,
			int topK,
			long timeLimitMillis) throws Exception
	{
		long yeuCauId = yeuCauRepo.createFromThoiKhoaBieu(thoiKhoaBieuId);

		return chayToiUu(yeuCauId, topK, timeLimitMillis);
	}

	private RangBuocToiUu mapToDomain(RangBuocToiUuDto dto)
	{
		LoaiRangBuoc loai = LoaiRangBuoc.valueOf(dto.getLoaiRangBuoc());

		TargetType targetType = dto.getTargetType() != null
				? TargetType.valueOf(dto.getTargetType())
				: TargetType.NONE;

		ToanTuSoSanh operator = dto.getOperator() != null
				? ToanTuSoSanh.valueOf(dto.getOperator())
				: null;

		return new RangBuocToiUu(
				loai,
				dto.isLaCung(),
				dto.getTrongSo(),
				targetType,
				dto.getTargetValue(),
				dto.getAttribute(),
				operator,
				dto.getValue());
	}

	private List<PhuongAnThoiKhoaBieuDto> mapToDto(
			List<BacktrackingOptimizer.PhuongAn> list,
			Map<Long, LopHocPhan> lopMap)
	{
		List<PhuongAnThoiKhoaBieuDto> result = new ArrayList<>();

		for(BacktrackingOptimizer.PhuongAn pa : list) {

			PhuongAnThoiKhoaBieuDto dto = new PhuongAnThoiKhoaBieuDto();
			dto.setDiemDanhGia(pa.diem);

			List<LopHocPhanDto> lopDtos = new ArrayList<>();

			for(Long id : pa.lopIds) {

				LopHocPhan lop = lopMap.get(id);

				LopHocPhanDto d = new LopHocPhanDto();
				d.setId(lop.getId());
				d.setMaLop(lop.getMaLop());

				lopDtos.add(d);
			}

			dto.setDanhSachLopHocPhan(lopDtos);
			result.add(dto);
		}

		return result;
	}
}
