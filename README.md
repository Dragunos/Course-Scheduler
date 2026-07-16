# 📅 Course Scheduler

> Ứng dụng desktop JavaFX hỗ trợ sinh, so sánh và quản lý phương án đăng ký học phần theo ràng buộc và ưu tiên cá nhân, được xây dựng theo Clean / Hexagonal Architecture

---

## 📌 Giới thiệu dự án

**Course Scheduler** giải quyết bài toán đăng ký học phần đại học - một quy trình thường đòi hỏi sinh viên tự chọn các lớp thỏa đồng thời nhiều điều kiện: không trùng lịch, phù hợp giảng viên mong muốn, khung giờ hợp lý và ưu tiên cá nhân. Cách làm thủ công dễ dẫn đến xung đột thời khóa biểu, tốn thời gian thử-sai và khó tối ưu.

Ứng dụng cung cấp một giải pháp tự động hóa hoàn chỉnh trên máy tính cá nhân (offline), lưu trữ dữ liệu cục bộ bằng SQLite, không phụ thuộc vào bất kỳ dịch vụ ngoài nào.

Dự án được xây dựng với mục tiêu thực hành và thể hiện:
- **Clean / Hexagonal Architecture** trong ứng dụng desktop thực tế.
- Thuật toán **Backtracking có ràng buộc** (hard constraint + soft constraint + time-limit) cho bài toán sinh tổ hợp.
- Tổ chức code theo chiều sâu: Domain invariant, Port/adapter pattern, Mapper layer, Exception hierarchy.
- Khả năng **xuất đa định dạng** (CSV, Excel, PDF, iCalendar) qua Composite pattern.

---

## 🗂️ Mục lục

- [Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
- [Kiến trúc hệ thống](#️-kiến-trúc-hệ-thống)
- [Tính năng nổi bật](#-tính-năng-nổi-bật)
- [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [Domain Model (Aggregate Boundaries)](#-domain-model)
- [Luồng nghiệp vụ chính](#-luồng-nghiệp-vụ-chính)
- [Module Use Cases](#-module-use-cases)
- [Cơ sở dữ liệu](#️-cơ-sở-dữ-liệu)
- [Hướng dẫn chạy dự án](#-hướng-dẫn-chạy-dự-án)
- [Thiết kế & Quyết định kỹ thuật](#️-thiết-kế--quyết-định-kỹ-thuật)
- [Trạng thái phát triển](#-trạng-thái-phát-triển)

---

## 🛠️ Công nghệ sử dụng

### Ngôn ngữ & Nền tảng

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **Java** | 21 | Ngôn ngữ lập trình chính |
| **JavaFX / OpenJFX** | 21.0.9 | UI framework cho ứng dụng desktop |
| **Maven** | 3.x | Build tool & quản lý dependency |

### Cơ sở dữ liệu

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **SQLite** | 3.43.2 | Database quan hệ nhúng, lưu trữ cục bộ |
| **SQLite JDBC** | 3.46.1.3 | Kết nối JDBC tới SQLite |
| **HikariCP** | 5.1.0 | Connection pool (max 10, min-idle 1) |

### Import / Export dữ liệu

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **Apache POI (poi-ooxml)** | 5.3.0 | Đọc file Excel `.xls`/`.xlsx` (import), ghi `.xlsx` (export) |
| **Apache Commons CSV** | 1.11.0 | Xuất file CSV |
| **Apache PDFBox** | 3.0.2 | Xuất file PDF |
| **ical4j** | 4.0.3 | Xuất file iCalendar `.ics` |

### Bảo mật & Tiện ích

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **jBCrypt** | 0.4 | Hash mật khẩu người dùng |
| **Apache Commons Lang3** | 3.14.0 | Tiện ích xử lý chuỗi & dữ liệu |

### Kiểm thử

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| **JUnit 5 (Jupiter)** | 5.11.0 | Unit test |
| **maven-surefire-plugin** | 3.3.0 | Chạy test trong Maven |

### Đóng gói & Công cụ phát triển

| Công cụ | Mục đích |
|---|---|
| **javafx-maven-plugin** | Chạy ứng dụng trong development |
| **jpackage** | Đóng gói thành native installer |
| **Eclipse IDE / Scene Builder** | IDE phát triển & thiết kế FXML |

---

## 🏗️ Kiến trúc hệ thống

Dự án áp dụng **Clean / Hexagonal Architecture (Ports & Adapters)**:

```
User Action (JavaFX UI)
        │
        ▼
┌───────────────────────────────────────────────┐
│  ui/controller  (FXML Controllers + Handlers) │  ← Xử lý sự kiện UI, gọi Use Case
│  ui/viewmodel   (AuthViewModel)               │  ← Trạng thái màn hình
│  ui/fx          (ScreenManager, FxConfig)      │  ← Điều hướng màn hình
└─────────────────────┬─────────────────────────┘
                      │  gọi qua Port interface
                      ▼
┌───────────────────────────────────────────────┐
│  application/port/in  (Use Case interfaces)   │  ← Input Ports
│  application/service  (*Service)              │  ← Business logic
│  application/dto      (Data Transfer Objects) │
│  application/exception (Exception hierarchy)  │
└────────────┬──────────────────────┬───────────┘
             │                      │ gọi qua Port interface
             ▼                      ▼
┌────────────────────┐  ┌──────────────────────────────────┐
│  domain/model      │  │  application/port/out            │
│  (Entities + Rules)│  │  (Repository interfaces)         │
│  domain/optimizer  │  └──────────────┬───────────────────┘
│  (Backtracking)    │                 │ implements
└────────────────────┘                 ▼
                       ┌──────────────────────────────────┐
                       │  infrastructure/persistence/jdbc  │  ← JDBC Repositories
                       │  infrastructure/io/imports        │  ← Excel Importer
                       │  infrastructure/io/exports        │  ← CSV/Excel/PDF/ICS
                       │  infrastructure/security          │  ← PasswordHasher
                       └──────────────────────────────────┘
                                        │
                                        ▼
                               SQLite (local .db file)
                           (~/.coursescheduler/coursescheduler.db)
```

**Điểm nổi bật về kiến trúc:**
- `MainApp.java` là **composition root** duy nhất - khởi tạo toàn bộ dependency tree thủ công (manual DI), không dùng DI framework.
- Domain model hoàn toàn **framework-free**: Không annotation, không JPA, không Spring.
- Các entity domain sử dụng factory method `create()` / `reconstruct()` để kiểm soát invariant ngay tại thời điểm khởi tạo.
- `ScreenManager` đóng vai trò router UI - nắm giữ tham chiếu đến tất cả Use Case và `NguoiDungDto` của session hiện tại.

---

## ✨ Tính năng nổi bật

### 🔐 Xác thực người dùng
- Đăng ký / Đăng nhập với mật khẩu được hash bằng BCrypt.
- Phân quyền theo vai trò: **Admin** (roleId = 1) và **User thường**.
- Session được lưu in-memory trong `ScreenManager.currentUser`.

### 📋 Quản lý Danh sách Lớp
- Tạo, xem, sửa, xóa danh sách lớp học phần (theo học kỳ).
- Hỗ trợ **công khai / riêng tư** và **chia sẻ** danh sách cho người dùng khác.
- Import danh sách lớp từ file **Excel** (`.xls`, `.xlsx`) - tự động chuẩn hóa hình thức dạy (online/trực tiếp) từ dữ liệu thô.
- Export danh sách lớp ra **CSV**, **Excel** hoặc **PDF**.

### 🤖 Sinh Thời Khóa Biểu tự động
- Người dùng chọn danh sách lớp, chỉ định các môn học cần đăng ký và thiết lập ràng buộc.
- Hệ thống chạy thuật toán **Backtracking** sinh các tổ hợp lớp hợp lệ (không trùng lịch).
- Trả về top-K phương án tốt nhất theo điểm số từ soft constraint.
- Hỗ trợ **Re-generate**: tái sinh dựa trên thời khóa biểu đã có với ràng buộc mới.

### ⚙️ Hệ thống Ràng buộc
- **Hard Constraint** (bắt buộc thỏa mãn, prune nhánh nếu vi phạm):
  - `FIX_SECTION` - bắt buộc chọn lớp cụ thể
  - `AVOID_SECTION` - loại bỏ lớp cụ thể
  - `AVOID_DAY` - tránh ngày trong tuần
  - `AVOID_TIME` - tránh khung tiết cụ thể
  - `AVOID_MODE` - tránh hình thức dạy (online/trực tiếp)
- **Soft Constraint** (tính điểm để xếp hạng phương án):
  - `PREFER_SECTION` - ưu tiên lớp cụ thể
  - `AVOID_MODE`, `AVOID_DAY`, `AVOID_TIME` - trừ điểm theo tần suất vi phạm × trọng số

### 📅 Quản lý Thời Khóa Biểu đã lưu
- Xem, đổi tên, xóa các phương án thời khóa biểu đã lưu.
- Export thời khóa biểu ra **CSV**, **Excel**, **PDF** hoặc **iCalendar (`.ics`)**.

### 🛡️ Quản trị (Admin)
- Xem toàn bộ danh sách lớp công khai.
- Import và quản lý danh sách lớp cho toàn hệ thống.
- Xóa danh sách lớp công khai.

---

## 📁 Cấu trúc thư mục

```
coursescheduler/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/vn/edu/haui/scheduler/
    │   │   ├── MainApp.java                    # Composition root, khởi động JavaFX Application
    │   │   ├── application/
    │   │   │   ├── dto/                        # Data Transfer Objects (trao đổi giữa các tầng)
    │   │   │   ├── exception/                  # Exception hierarchy: ApplicationException,
    │   │   │   │                               #   BusinessException, ValidationException,
    │   │   │   │                               #   AuthenticationException, UnauthorizedAccessException,
    │   │   │   │                               #   EntityNotFoundException, DataAccessException, ...
    │   │   │   ├── port/
    │   │   │   │   ├── in/                     # Input Ports (Use Case interfaces)
    │   │   │   │   └── out/                    # Output Ports (Repository interfaces)
    │   │   │   └── service/
    │   │   │       ├── AuthService.java
    │   │   │       ├── ImportDanhSachLopService.java
    │   │   │       ├── ManageDanhSachLopService.java
    │   │   │       ├── ExportDanhSachLopService.java
    │   │   │       ├── GenerateThoiKhoaBieuService.java
    │   │   │       ├── ManageThoiKhoaBieuService.java
    │   │   │       ├── ExportThoiKhoaBieuService.java
    │   │   │       ├── AdminDanhSachLopService.java
    │   │   │       └── mapper/                 # Domain <-> DTO mappers
    │   │   ├── domain/
    │   │   │   ├── model/                      # Domain entities (invariant-enforced)
    │   │   │   │   ├── NguoiDung.java          # Người dùng
    │   │   │   │   ├── VaiTro.java             # Vai trò (Admin/User)
    │   │   │   │   ├── HocKy.java              # Học kỳ
    │   │   │   │   ├── HocPhan.java            # Học phần (môn học)
    │   │   │   │   ├── GiangVien.java          # Giảng viên
    │   │   │   │   ├── LichHoc.java            # Lịch học (thu, tiết bắt đầu/kết thúc)
    │   │   │   │   ├── LopHocPhan.java         # Lớp học phần (section)
    │   │   │   │   ├── DanhSachLop.java        # Danh sách lớp (container)
    │   │   │   │   ├── DanhSachLopChiTiet.java # Chi tiết từng lớp trong danh sách
    │   │   │   │   ├── ThoiKhoaBieu.java       # Thời khóa biểu (kết quả được lưu)
    │   │   │   │   ├── YeuCau.java             # Yêu cầu sinh TKB
    │   │   │   │   ├── YeuCauChiTiet.java      # Chi tiết lớp trong yêu cầu
    │   │   │   │   ├── RangBuocToiUu.java      # Ràng buộc tối ưu (hard/soft)
    │   │   │   │   └── TepTaiLen.java          # Metadata file đã upload
    │   │   │   └── optimizer/
    │   │   │       ├── Optimizer.java          # Facade công khai
    │   │   │       ├── BacktrackingEngine.java # Thuật toán backtracking (package-private)
    │   │   │       ├── TimeConflictChecker.java
    │   │   │       ├── ConstraintMapper.java   # DTO -> HardConstraint/SoftConstraint
    │   │   │       ├── HardConstraint.java     # Interface: isSatisfied()
    │   │   │       ├── SoftConstraint.java     # Interface: evaluate()
    │   │   │       ├── OptimizationInput.java
    │   │   │       └── OptimizationResult.java
    │   │   ├── infrastructure/
    │   │   │   ├── io/
    │   │   │   │   ├── imports/
    │   │   │   │   │   ├── ExcelDanhSachLopImporter.java
    │   │   │   │   │   └── ImportedLopHocPhanRaw.java
    │   │   │   │   └── exports/
    │   │   │   │       ├── FileExporter.java           # Interface
    │   │   │   │       ├── CompositeFileExporter.java  # Composite pattern
    │   │   │   │       ├── CsvExporter.java
    │   │   │   │       ├── ExcelExporter.java
    │   │   │   │       ├── PdfExporter.java
    │   │   │   │       └── IcsExporter.java
    │   │   │   ├── persistence/
    │   │   │   │   ├── config/
    │   │   │   │   │   ├── DatabaseConfig.java         # Path & pool config
    │   │   │   │   │   └── DataSourceProvider.java     # HikariCP singleton
    │   │   │   │   └── jdbc/
    │   │   │   │       ├── JdbcNguoiDungRepository.java
    │   │   │   │       ├── JdbcVaiTroRepository.java
    │   │   │   │       ├── JdbcHocPhanRepository.java
    │   │   │   │       ├── JdbcGiangVienRepository.java
    │   │   │   │       ├── JdbcHocKyRepository.java
    │   │   │   │       ├── JdbcLopHocPhanRepository.java
    │   │   │   │       ├── JdbcDanhSachLopRepository.java
    │   │   │   │       ├── JdbcTepTaiLenRepository.java
    │   │   │   │       ├── JdbcThoiKhoaBieuRepository.java
    │   │   │   │       ├── JdbcYeuCauRepository.java
    │   │   │   │       └── mapper/                     # ResultSet -> Domain mappers
    │   │   │   └── security/
    │   │   │       └── PasswordHasher.java             # BCrypt wrapper
    │   │   └── ui/
    │   │       ├── controller/
    │   │       │   ├── AuthController.java
    │   │       │   ├── HomeController.java
    │   │       │   └── handler/                        # Pane-level event handlers
    │   │       │       ├── AuthPaneHandler.java
    │   │       │       ├── ImportDanhSachLopPaneHandler.java
    │   │       │       ├── ManageDanhSachLopPaneHandler.java
    │   │       │       ├── AdminDanhSachLopPaneHandler.java
    │   │       │       ├── ExportDanhSachLopPaneHandler.java
    │   │       │       ├── GenerateThoiKhoaBieuPaneHandler.java
    │   │       │       ├── ManageThoiKhoaBieuPaneHandler.java
    │   │       │       └── ExportThoiKhoaBieuPaneHandler.java
    │   │       ├── fx/
    │   │       │   ├── AppLauncher.java                # main() entry point
    │   │       │   ├── ScreenManager.java              # UI router + session holder
    │   │       │   └── config/FxConfig.java
    │   │       ├── viewmodel/
    │   │       │   └── AuthViewModel.java
    │   │       └── util/
    │   │           └── UiUtils.java
    │   └── resources/
    │       ├── fxml/
    │       │   ├── auth.fxml                           # Màn hình đăng nhập / đăng ký
    │       │   └── home.fxml                           # Màn hình chính (multi-pane)
    │       ├── css/                                    # Stylesheet JavaFX
    │       └── fonts/                                  # Font nhúng (SEGOEUI.TTF cho PDF)
    └── test/
        └── java/
            ├── domain/model/                           # Unit test từng entity (14 classes)
            └── domain/optimizer/                       # Test optimizer & constraint (3 classes)
                ├── OptimizerAndBacktrackingTest.java
                ├── TimeConflictCheckerTest.java
                └── ConstraintMapperTest.java
```

---

## 🧩 Domain Model (Aggregate Boundaries)

```
NguoiDung (id, tenDangNhap, matKhauHash, VaiTro, ngayTao)
    │
    ├── owns ──> DanhSachLop (id, tenDanhSach, laCongKhai, HocKy, sharedUserIds)
    │               │
    │               └── contains --> DanhSachLopChiTiet (LopHocPhan, batBuoc)
    │                                       │
    │                                       └── LopHocPhan (maLop, HocPhan, GiangVien, hinhThucDay, diaDiem)
    │                                                │
    │                                                └── List<LichHoc> (thu, tietBatDau, tietKetThuc)
    │
    ├── creates --> YeuCau (tenYeuCau, DanhSachLop, ngayTao)
    │                 │
    │                 ├── Map<LopId, YeuCauChiTiet> (batBuoc, loaiChiDinh, trongSo)
    │                 └── List<RangBuocToiUu> (loaiRangBuoc, laCung, trongSo, ...)
    │
    └── saves --> ThoiKhoaBieu (tenPhuongAn, DanhSachLop, diemDanhGia, ngayTao)
                     │
                     └── Set<LopHocPhan> (các lớp được chọn)
```

| Entity | Trách nhiệm chính |
|---|---|
| `NguoiDung` | Thông tin tài khoản, vai trò, đổi mật khẩu |
| `DanhSachLop` | Container lớp học phần, hỗ trợ public/share, kiểm tra ownership |
| `LopHocPhan` | Một lớp cụ thể: mã lớp, môn, giảng viên, hình thức, lịch học |
| `LichHoc` | Một slot thời gian (thứ + tiết bắt đầu/kết thúc) |
| `ThoiKhoaBieu` | Phương án thời khóa biểu đã được đánh điểm và lưu |
| `YeuCau` | Phiên sinh TKB: danh sách lớp, ràng buộc, người tạo |
| `RangBuocToiUu` | Một ràng buộc cụ thể (hard hoặc soft) với loại, target, value, trọng số |

---

## 🔄 Luồng nghiệp vụ chính

### Luồng sinh Thời Khóa Biểu

```
1. User đăng nhập (BCrypt verify)
        │
2. Chọn / Import Danh sách Lớp (từ Excel)
        │
3. Thiết lập ràng buộc:
   ├── Hard: FIX_SECTION, AVOID_SECTION, AVOID_DAY, AVOID_TIME, AVOID_MODE
   └── Soft: PREFER_SECTION, AVOID_MODE, AVOID_DAY, AVOID_TIME (có trọng số)
        │
4. Chọn danh sách Học Phần muốn đăng ký
        │
5. Gọi GenerateThoiKhoaBieuService.generate()
   ├── Tạo YeuCau, lưu vào DB
   ├── Lọc available sections theo required course IDs
   ├── ConstraintMapper -> List<HardConstraint> + List<SoftConstraint>
   └── Optimizer.optimize(input)
            │
            └── BacktrackingEngine.solve()
                ├── Group sections by HocPhan ID
                ├── For each combination:
                │   ├── Check TimeConflict (always-on hard check)
                │   ├── Check HardConstraints (prune nếu vi phạm)
                │   └── Evaluate SoftConstraints -> score
                └── Maintain Top-K PriorityQueue, deadline-aware
        │
6. Hiển thị Top-K phương án -> User chọn & lưu (ThoiKhoaBieu)
        │
7. Export: CSV / Excel / PDF / .ics
```

### Luồng Import Danh sách Lớp từ Excel

```
User chọn file Excel (.xls/.xlsx)
        │
ExcelDanhSachLopImporter -> List<ImportedLopHocPhanRaw>
        │
ImportDanhSachLopService:
  ├── Upsert HocPhan (theo maHocPhan)
  ├── Upsert GiangVien (theo maGiangVien)
  ├── Upsert LopHocPhan (theo maLop) + chuẩn hóa hinhThucDay
  ├── Upsert LichHoc
  └── Tạo DanhSachLop -> lưu TepTaiLen (metadata file)
```

---

## 📦 Module Use Cases

| Use Case Interface | Service Implementation | Chức năng |
|---|---|---|
| `AuthUseCase` | `AuthService` | Đăng ký (`register`) và đăng nhập (`login`) |
| `ImportDanhSachLopUseCase` | `ImportDanhSachLopService` | Import danh sách lớp từ Excel |
| `ManageDanhSachLopUseCase` | `ManageDanhSachLopService` | CRUD danh sách lớp của user |
| `ExportDanhSachLopUseCase` | `ExportDanhSachLopService` | Xuất danh sách lớp (CSV/Excel/PDF) |
| `GenerateThoiKhoaBieuUseCase` | `GenerateThoiKhoaBieuService` | Sinh, tái sinh và lưu thời khóa biểu |
| `ManageThoiKhoaBieuUseCase` | `ManageThoiKhoaBieuService` | Xem, đổi tên, xóa thời khóa biểu |
| `ExportThoiKhoaBieuUseCase` | `ExportThoiKhoaBieuService` | Xuất thời khóa biểu (CSV/Excel/PDF/ICS) |
| `AdminDanhSachLopUseCase` | `AdminDanhSachLopService` | Quản trị danh sách lớp công khai (Admin) |

---

## 🗄️ Cơ sở dữ liệu

Database SQLite được lưu tại: `~/.coursescheduler/coursescheduler.db`

Toàn bộ schema được khởi tạo thủ công qua JDBC — không dùng ORM hay migration tool.

### Các bảng chính

| Bảng | Mô tả |
|---|---|
| `nguoi_dung` | Tài khoản người dùng (id, ten_dang_nhap, mat_khau_hash, vai_tro_id, ngay_tao) |
| `vai_tro` | Vai trò (1=Admin, n=User thường) |
| `hoc_ky` | Học kỳ (ma_hoc_ky, ten_hoc_ky, nam_hoc) |
| `hoc_phan` | Học phần/môn học (ma_hoc_phan, ten_hoc_phan, so_tin_chi) |
| `giang_vien` | Giảng viên (ma_giang_vien, ten_giang_vien) |
| `lop_hoc_phan` | Lớp học phần (ma_lop, hoc_phan_id, giang_vien_id, hinh_thuc_day, dia_diem) |
| `lich_hoc` | Lịch học của từng lớp (lop_hoc_phan_id, thu, tiet_bat_dau, tiet_ket_thuc) |
| `danh_sach_lop` | Danh sách lớp (ten_danh_sach, nguoi_tao_id, la_cong_khai, hoc_ky_id) |
| `danh_sach_lop_chi_tiet` | Liên kết N-N: danh_sach_lop <-> lop_hoc_phan |
| `danh_sach_lop_chia_se` | Chia sẻ danh sách: danh_sach_lop_id -> nguoi_dung_id |
| `tep_tai_len` | Metadata file đã upload (ten_tep, duong_dan, kich_thuoc) |
| `yeu_cau` | Phiên sinh TKB (nguoi_tao_id, danh_sach_lop_id, ten_yeu_cau) |
| `yeu_cau_chi_tiet` | Chi tiết lớp trong yêu cầu (bat_buoc, loai_chi_dinh, trong_so) |
| `rang_buoc_toi_uu` | Ràng buộc (loai, la_cung, target_type, target_value, attribute, operator, value, trong_so) |
| `thoi_khoa_bieu` | Phương án lưu (ten_phuong_an, nguoi_dung_id, danh_sach_lop_id, diem_danh_gia) |
| `thoi_khoa_bieu_lop` | Liên kết N-N: thoi_khoa_bieu <-> lop_hoc_phan |

### Connection Pool (HikariCP)

| Tham số | Giá trị |
|---|---|
| `maximumPoolSize` | 10 |
| `minimumIdle` | 1 |
| `connectionTimeout` | 5,000 ms |
| `idleTimeout` | 30,000 ms |
| `maxLifetime` | 300,000 ms |

---

## 🚀 Hướng dẫn chạy dự án

### Yêu cầu

- **Java 21+** (JDK, không phải JRE)
- **Maven 3.6+**
- Không cần cài đặt database - SQLite nhúng sẵn qua JDBC driver

### 1. Clone repository

```bash
git clone https://github.com/<your-username>/Course-Scheduler.git
cd Course-Scheduler/coursescheduler
```

### 2. Chạy ứng dụng (Development)

```bash
mvn javafx:run
```

> Ứng dụng sẽ tự tạo thư mục `~/.coursescheduler/` và file database `coursescheduler.db` khi khởi động lần đầu.

### 3. Build JAR

```bash
mvn clean package
```

> File JAR được tạo tại `target/coursescheduler-1.0-SNAPSHOT.jar`.
> **Lưu ý:** JavaFX không được đóng gói trong fat-JAR mặc định - cần dùng `javafx-maven-plugin` hoặc `jpackage` để phân phối.

### 4. Chạy Tests

```bash
mvn clean test
```

### 5. Đóng gói native (jpackage)

```bash
mvn clean package
jpackage --input target/ ^
         --main-jar coursescheduler-1.0-SNAPSHOT.jar ^
         --main-class vn.edu.haui.scheduler.ui.fx.AppLauncher ^
         --name CourseScheduler ^
         --type app-image
```

### Tài khoản mặc định

Không có tài khoản seed sẵn. Người dùng tự đăng ký qua giao diện khi chạy lần đầu.

> **Lưu ý về Admin:** Vai trò Admin được xác định bằng `roleId = 1` trong `ScreenManager.isAdminUser()`. Cần seed dữ liệu vào bảng `vai_tro` thủ công (hoặc qua DB browser) trước khi sử dụng tính năng quản trị.

---

## ⚙️ Thiết kế & Quyết định kỹ thuật

### 1. Clean / Hexagonal Architecture với Manual DI
`MainApp.java` đóng vai trò **composition root** - toàn bộ dependency được khởi tạo và wire thủ công. Lựa chọn này giữ domain hoàn toàn framework-free và làm rõ dependency graph mà không cần annotation hay IoC container.

### 2. Domain Invariant tại thời điểm khởi tạo
Mỗi entity có hai factory method: `create()` (tạo mới) và `reconstruct()` (tái tạo từ DB). Constructor là private, `validateInvariant()` chạy trước khi gán field - đảm bảo không bao giờ tồn tại entity ở trạng thái không hợp lệ trong bộ nhớ.

### 3. Backtracking với Time-Limit và Top-K Priority Queue
`BacktrackingEngine` sử dụng `PriorityQueue` để duy trì top-K kết quả trong khi backtrack. Deadline được tính từ `System.nanoTime()` - khi hết giờ, flag `stopped = true` cắt nhánh còn lại. Trade-off có chủ đích: Ưu tiên responsiveness hơn completeness khi không gian tìm kiếm lớn.

### 4. Constraint System tách biệt khỏi Domain Model
`HardConstraint` và `SoftConstraint` là functional interface. `ConstraintMapper` chuyển đổi `RangBuocToiUuDto` thành lambda expressions cụ thể tại runtime - optimizer không phụ thuộc vào cấu trúc DTO và dễ mở rộng loại ràng buộc mới.

### 5. Composite Pattern cho Export
`CompositeFileExporter` triển khai `FileExporter` và delegate đến `CsvExporter`, `ExcelExporter`, `PdfExporter`. `IcsExporter` được xử lý riêng vì API ical4j khác biệt so với ba exporter còn lại.

### 6. Chuẩn hóa hình thức dạy trong Domain
`LopHocPhan` tự chuẩn hóa `hinhThucDay` trong constructor: Loại dấu tiếng Việt (Unicode NFKD), uppercase, nhận diện từ khóa "ONLINE" trong cả trường hình thức lẫn địa điểm. Trade-off: Logic domain phụ thuộc quy ước dữ liệu nhập vào - đổi lại là consistency tuyệt đối sau khi entity được tạo.

### 7. SQLite với HikariCP
SQLite phù hợp với ứng dụng desktop single-user. HikariCP tái sử dụng connection và tránh overhead mở/đóng liên tục. `DataSourceProvider` là singleton, được shutdown sạch trong `MainApp.stop()`.

### 8. ScreenManager là UI Router kiêm Session Store
`ScreenManager` giữ tham chiếu đến tất cả Use Case và `NguoiDungDto` của session hiện tại - là điểm kết nối duy nhất giữa tầng UI và tầng Application. Controller nhận `ScreenManager` qua method `init()` thay vì field injection.

---

## 📋 Trạng thái phát triển

| Tính năng | Trạng thái |
|---|---|
| Đăng ký / Đăng nhập (BCrypt) | ✅ Hoàn thành |
| Phân quyền Admin / User | ✅ Hoàn thành |
| Import danh sách lớp từ Excel | ✅ Hoàn thành |
| Quản lý danh sách lớp (CRUD, public/share) | ✅ Hoàn thành |
| Export danh sách lớp (CSV, Excel, PDF) | ✅ Hoàn thành |
| Sinh TKB tự động (Backtracking + Hard/Soft Constraint) | ✅ Hoàn thành |
| Re-generate TKB với ràng buộc mới | ✅ Hoàn thành |
| Lưu & quản lý TKB (đổi tên, xóa) | ✅ Hoàn thành |
| Export TKB (CSV, Excel, PDF, iCalendar .ics) | ✅ Hoàn thành |
| Quản trị Admin (danh sách lớp công khai) | ✅ Hoàn thành |
| Unit test domain model (14 test classes) | ✅ Hoàn thành |
| Unit test optimizer & constraint system (3 test classes) | ✅ Hoàn thành |
| Native installer (jpackage) | 🔧 Cấu hình sẵn trong pom.xml, chưa build |
| Integration test (UI / JDBC) | 📋 Chưa có |
| Schema migration tool (Flyway/Liquibase) | 📋 Chưa áp dụng |

---
