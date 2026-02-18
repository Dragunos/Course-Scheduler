package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.dto.*;
import vn.edu.haui.scheduler.application.port.in.ManageThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.*;

import vn.edu.haui.scheduler.domain.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ManageThoiKhoaBieuService implements ManageThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepository thoiKhoaBieuRepo;

	private final LopHocPhanRepository lopHocPhanRepo;

	private final LichHocRepository lichHocRepo;

	private final GiangVienRepository giangVienRepo;

	public ManageThoiKhoaBieuService(
			ThoiKhoaBieuRepository thoiKhoaBieuRepo,
			LopHocPhanRepository lopHocPhanRepo,
			LichHocRepository lichHocRepo,
			GiangVienRepository giangVienRepo)
	{
		this.thoiKhoaBieuRepo = thoiKhoaBieuRepo;
		this.lopHocPhanRepo = lopHocPhanRepo;
		this.lichHocRepo = lichHocRepo;
		this.giangVienRepo = giangVienRepo;
	}

	@Override
	public List<ThoiKhoaBieuDto> layDanhSachTheoNguoiDung(long nguoiDungId) throws Exception
	{
		List<ThoiKhoaBieu> danhSach = thoiKhoaBieuRepo.findByNguoiDungId(nguoiDungId);

		return danhSach.stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}

	@Override
	public ThoiKhoaBieuDto xemChiTiet(long thoiKhoaBieuId, long nguoiDungId) throws Exception
	{
		ThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
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

		ThoiKhoaBieuDto paDto = mapToDto(pa);

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
	public ThoiKhoaBieuDto doiTen(long thoiKhoaBieuId, long nguoiDungId, String tenMoi) throws Exception
	{
		if(tenMoi == null || tenMoi.trim().isEmpty())
			throw new RuntimeException("Tên không hợp lệ");

		ThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
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
		ThoiKhoaBieu pa = thoiKhoaBieuRepo.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thời khóa biểu"));

		if(pa.getNguoiDungId() != nguoiDungId)
			throw new RuntimeException("Không có quyền xóa");

		thoiKhoaBieuRepo.deleteById(thoiKhoaBieuId);
	}

	private ThoiKhoaBieuDto mapToDto(ThoiKhoaBieu pa)
	{
		ThoiKhoaBieuDto dto = new ThoiKhoaBieuDto();
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
