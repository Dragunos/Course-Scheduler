package vn.edu.haui.scheduler.application.service;

import vn.edu.haui.scheduler.application.dto.ThoiKhoaBieuDto;
import vn.edu.haui.scheduler.application.port.in.GenerateThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.DanhSachLopRepository;
import vn.edu.haui.scheduler.application.port.out.LopHocPhanRepository;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.optimizer.Optimizer;
import vn.edu.haui.scheduler.domain.optimizer.PhuongAnThoiKhoaBieu;
import vn.edu.haui.scheduler.application.service.mapper.ThoiKhoaBieuMapper;

import java.util.*;
import java.util.stream.Collectors;

public class GenerateThoiKhoaBieuService implements GenerateThoiKhoaBieuUseCase {

    private final DanhSachLopRepository danhSachLopRepository;
    private final LopHocPhanRepository lopHocPhanRepository;

    public GenerateThoiKhoaBieuService(DanhSachLopRepository danhSachLopRepository,
                                       LopHocPhanRepository lopHocPhanRepository) {
        this.danhSachLopRepository = danhSachLopRepository;
        this.lopHocPhanRepository = lopHocPhanRepository;
    }

    @Override
    public long createYeuCau(long nguoiDungId, long danhSachLopId) throws Exception {
        // Trong scope này, tạo một "YeuCau" mới từ danh sách lớp
        // Giả sử repository có phương thức insertYeuCau trả về ID mới
        return danhSachLopRepository.createYeuCau(nguoiDungId, danhSachLopId);
    }

    @Override
    public List<ThoiKhoaBieuDto> generateThoiKhoaBieu(long yeuCauId, int topK, long timeLimitMillis) throws Exception {
        // 1. Lấy danh sách lớp học phần từ danh sách lớp
        List<LopHocPhan> allLopHocPhan = lopHocPhanRepository.findByYeuCauId(yeuCauId);

        // 2. Xác định các ràng buộc cứng / mềm
        Set<String> requiredHocPhanCodes = lopHocPhanRepository.findRequiredHocPhanCodesByYeuCau(yeuCauId);
        Set<String> preferredLopIds = lopHocPhanRepository.findPreferredLopIdsByYeuCau(yeuCauId);
        Set<Integer> avoidThu = lopHocPhanRepository.findAvoidThuByYeuCau(yeuCauId);
        Set<Integer> avoidTiet = lopHocPhanRepository.findAvoidTietByYeuCau(yeuCauId);
        Set<?> avoidHinhThuc = lopHocPhanRepository.findAvoidHinhThucByYeuCau(yeuCauId);

        // 3. Chạy Optimizer
        Optimizer optimizer = new Optimizer(allLopHocPhan,
                requiredHocPhanCodes,
                preferredLopIds,
                (Set) avoidHinhThuc,
                avoidTiet,
                avoidThu,
                topK,
                timeLimitMillis);

        List<PhuongAnThoiKhoaBieu> phuongAns = optimizer.optimize();

        // 4. Map sang DTO
        return phuongAns.stream()
                .map(ThoiKhoaBieuMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public long saveThoiKhoaBieu(long nguoiDungId, long danhSachLopId, String tenPhuongAn,
                                  double diemDanhGia, List<Long> lopHocPhanIds) throws Exception {
        // Lưu ThoiKhoaBieu vào DB (repository)
        return lopHocPhanRepository.saveThoiKhoaBieu(nguoiDungId, danhSachLopId, tenPhuongAn, diemDanhGia, lopHocPhanIds);
    }

    @Override
    public List<ThoiKhoaBieuDto> regenerateThoiKhoaBieu(long thoiKhoaBieuId, int topK, long timeLimitMillis) throws Exception {
        // Lấy danh sách lớp từ phương án đã lưu
        List<LopHocPhan> allLopHocPhan = lopHocPhanRepository.findByThoiKhoaBieuId(thoiKhoaBieuId);

        Set<String> requiredHocPhanCodes = lopHocPhanRepository.findRequiredHocPhanCodesByThoiKhoaBieu(thoiKhoaBieuId);
        Set<String> preferredLopIds = lopHocPhanRepository.findPreferredLopIdsByThoiKhoaBieu(thoiKhoaBieuId);
        Set<Integer> avoidThu = lopHocPhanRepository.findAvoidThuByThoiKhoaBieu(thoiKhoaBieuId);
        Set<Integer> avoidTiet = lopHocPhanRepository.findAvoidTietByThoiKhoaBieu(thoiKhoaBieuId);
        Set<?> avoidHinhThuc = lopHocPhanRepository.findAvoidHinhThucByThoiKhoaBieu(thoiKhoaBieuId);

        Optimizer optimizer = new Optimizer(allLopHocPhan,
                requiredHocPhanCodes,
                preferredLopIds,
                (Set) avoidHinhThuc,
                avoidTiet,
                avoidThu,
                topK,
                timeLimitMillis);

        List<PhuongAnThoiKhoaBieu> phuongAns = optimizer.optimize();

        return phuongAns.stream()
                .map(ThoiKhoaBieuMapper::toDto)
                .collect(Collectors.toList());
    }
}