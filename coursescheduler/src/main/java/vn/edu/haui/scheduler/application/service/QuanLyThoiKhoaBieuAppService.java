package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.port.in.QuanLyThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.*;

import vn.edu.haui.scheduler.domain.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class QuanLyThoiKhoaBieuAppService implements QuanLyThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepositoryPort thoiKhoaBieuRepo;

	private final LopHocPhanRepositoryPort lopHocPhanRepo;

	private final LichHocRepositoryPort lichHocRepo;

	private final GiangVienRepositoryPort giangVienRepo;

	public QuanLyThoiKhoaBieuAppService(
			ThoiKhoaBieuRepositoryPort thoiKhoaBieuRepo,
			LopHocPhanRepositoryPort lopHocPhanRepo,
			LichHocRepositoryPort lichHocRepo,
			GiangVienRepositoryPort giangVienRepo)
	{
		this.thoiKhoaBieuRepo = thoiKhoaBieuRepo;
		this.lopHocPhanRepo = lopHocPhanRepo;
		this.lichHocRepo = lichHocRepo;
		this.giangVienRepo = giangVienRepo;
	}

	@Override
	public List<PhuongAnThoiKhoaBieuDto> layDanhSachTheoNguoiDung(long nguoiDungId) throws Exception
	{
		List<PhuongAnThoiKhoaBieu> danhSach = thoiKhoaBieuRepo.findByNguoiDungId(nguoiDungId);

		return danhSach.stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}

	@Override
	public PhuongAnThoiKhoaBieuDto xemChiTiet(long thoiKhoaBieuId, long nguoiDungId) throws Exception
	{
		PhuongAnThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thời khóa biểu"));

		if(pa.getNguoiDungId() != nguoiDungId)
			throw new RuntimeException("Không có quyền truy cập");

		List<Long> lopIds = thoiKhoaBieuRepo.findChiTietByThoiKhoaBieuId(thoiKhoaBieuId);

		List<LopHocPhan> lops = lopHocPhanRepo.findByIds(lopIds);

		Map<Long, List<LichHoc>> lichMap = lichHocRepo.findByLopHocPhanIds(lopIds);

		Set<Long> gvIds = lops.stream().map(LopHocPhan::getGiangVien).filter(Objects::nonNull).map(GiangVien::getId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		Map<Long, GiangVien> gvMap = new HashMap<>();

		if(!gvIds.isEmpty()) {
			List<GiangVien> gvs = giangVienRepo.findByIds(new ArrayList<>(gvIds));

			gvMap = gvs.stream()
					.collect(Collectors.toMap(GiangVien::getId, g -> g));
		}

		PhuongAnThoiKhoaBieuDto paDto = mapToDto(pa);

		List<LopHocPhanDto> lopDtos = new ArrayList<>();

		for(LopHocPhan lop : lops) {

			LopHocPhanDto lopDto = mapToDto(lop);

			List<LichHoc> lichCuaLop = lichMap.getOrDefault(lop.getId(), Collections.emptyList());

			lopDto.setLichHocDanhSach(
					lichCuaLop.stream()
							.map(this::mapToDto)
							.collect(Collectors.toList()));

			GiangVien gvEntity = lop.getGiangVien();

			if(gvEntity != null && gvEntity.getId() != null) {
				GiangVien gv = gvMap.get(gvEntity.getId());
				if(gv != null)
					lopDto.setGiangVien(mapToDto(gv));
			}

			lopDtos.add(lopDto);
		}

		paDto.setDanhSachLopHocPhan(lopDtos);

		return paDto;
	}

	@Override
	public PhuongAnThoiKhoaBieuDto doiTen(long thoiKhoaBieuId, long nguoiDungId, String tenMoi) throws Exception
	{
		if(tenMoi == null || tenMoi.trim().isEmpty())
			throw new RuntimeException("Tên không hợp lệ");

		PhuongAnThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thời khóa biểu"));

		if(pa.getNguoiDungId() != nguoiDungId)
			throw new RuntimeException("Không có quyền sửa");

		thoiKhoaBieuRepo.updateTenPhuongAn(thoiKhoaBieuId, tenMoi.trim());
		pa.setTenPhuongAn(tenMoi.trim());

		return mapToDto(pa);
	}

	@Override
	public void xoa(long thoiKhoaBieuId, long nguoiDungId) throws Exception
	{
		PhuongAnThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thời khóa biểu"));

		if(pa.getNguoiDungId() != nguoiDungId)
			throw new RuntimeException("Không có quyền xóa");

		thoiKhoaBieuRepo.deleteById(thoiKhoaBieuId);
	}

	private PhuongAnThoiKhoaBieuDto mapToDto(PhuongAnThoiKhoaBieu pa)
	{
		PhuongAnThoiKhoaBieuDto dto = new PhuongAnThoiKhoaBieuDto();
		dto.setId(pa.getId());
		dto.setTenPhuongAn(pa.getTenPhuongAn());
		dto.setNguoiDungId(pa.getNguoiDungId());
		return dto;
	}

	private LopHocPhanDto mapToDto(LopHocPhan lop)
	{
		LopHocPhanDto dto = new LopHocPhanDto();
		dto.setId(lop.getId());
		dto.setMaLop(lop.getMaLop());

		if(lop.getGiangVien() != null) dto.setGiangVienId(lop.getGiangVien().getId());

		return dto;
	}

	private LichHocDto mapToDto(LichHoc lich)
	{
		LichHocDto dto = new LichHocDto();
		dto.setThu(lich.getThu());
		dto.setTietBatDau(lich.getTietBatDau());
		dto.setTietKetThuc(lich.getTietKetThuc());
		return dto;
	}

	private GiangVienDto mapToDto(GiangVien gv)
	{
		GiangVienDto dto = new GiangVienDto();
		dto.setId(gv.getId());
		dto.setTenGiangVien(gv.getTenGiangVien());
		return dto;
	}
}
