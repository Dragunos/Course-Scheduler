package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.dto.PhuongAnThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.port.in.SinhThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepositoryPort;
import vn.edu.haui.scheduler.domain.optimizer.BacktrackingOptimizer;

import java.util.ArrayList;
import java.util.List;

public class SinhThoiKhoaBieuAppService implements SinhThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepositoryPort repository;

	public SinhThoiKhoaBieuAppService(ThoiKhoaBieuRepositoryPort repository)
	{
		this.repository = repository;
	}

	@Override
	public long taoYeuCau(long nguoiDungId,
			long danhSachLopId,
			String tenYeuCau)
	{
		return 0;
	}

	@Override
	public List<PhuongAnThoiKhoaBieuDto> chayToiUu(long yeuCauId,
			int topK,
			long timeLimitMillis)
	{
		BacktrackingOptimizer optimizer = new BacktrackingOptimizer(topK, timeLimitMillis);

		List<List<Long>> groups = new ArrayList<>();
		List result = optimizer.solve(groups, List.of());

		List<PhuongAnThoiKhoaBieuDto> dtos = new ArrayList<>();

		for(Object o : result) {
			BacktrackingOptimizer.PhuongAn pa = (BacktrackingOptimizer.PhuongAn) o;

			dtos.add(new PhuongAnThoiKhoaBieuDto(
					null,
					pa.lopIds,
					pa.diem));
		}

		return dtos;
	}

	@Override
	public long luuPhuongAn(long nguoiDungId,
			long danhSachLopId,
			String ten,
			double diem,
			List<Long> lopIds) throws Exception
	{
		long id = repository.save(nguoiDungId,
				danhSachLopId,
				ten,
				diem);

		repository.saveChiTiet(id, lopIds);

		return id;
	}

	@Override
	public List<PhuongAnThoiKhoaBieuDto> toiUuLai(long thoiKhoaBieuId,
			int topK,
			long timeLimitMillis)
			throws Exception
	{
		long danhSachLopId = repository.findDanhSachLopId(thoiKhoaBieuId);

		return chayToiUu(danhSachLopId,
				topK,
				timeLimitMillis);
	}
}
