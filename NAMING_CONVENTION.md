# NAMING CONVENTION – CourseScheduler

Tài liệu này là quy ước bắt buộc cho toàn bộ project.

1. NGUYÊN TẮC NỀN TẢNG

1.1 Phân tách ngôn ngữ theo vai trò

- Domain model, DTO field, Database → Tiếng Việt (camelCase ở Java, snake_case ở DB)
- Hành vi kỹ thuật (UseCase, Service, Exporter, Importer, Controller) → Tiếng Anh
- Enum nghiệp vụ → Tiếng Việt
- Tên bảng DB → snake_case tiếng Việt

Không trộn Anh – Việt trong cùng một vai trò.

Ví dụ sai:
createNguoiDung()
updateUser()

Ví dụ đúng:
createNguoiDung()
updateNguoiDung()

2. DATABASE (SQLite)

2.1 Tên bảng

- snake_case
- tiếng Việt
- số ít

Ví dụ:
nguoi_dung
vai_tro
hoc_phan
lop_hoc_phan
danh_sach_lop
thoi_khoa_bieu
rang_buoc_toi_uu

2.2 Tên cột

- snake_case
- tiếng Việt
- không viết tắt tùy tiện

Ví dụ:
ten_dang_nhap
mat_khau_hash
ngay_tao
la_cong_khai
tiet_bat_dau
tiet_ket_thuc

3. DOMAIN LAYER

3.1 Model Class

- PascalCase
- tiếng Việt
- số ít

Ví dụ:
NguoiDung
DanhSachLop
ThoiKhoaBieu
RangBuocToiUu
YeuCau

3.2 Field trong Model

- camelCase
- giống DB nhưng chuyển sang camelCase

Ví dụ mapping:

ten_dang_nhap  → tenDangNhap
mat_khau_hash  → matKhauHash
ngay_tao       → ngayTao
la_cong_khai   → laCongKhai

Không được dùng:
username
password
createdAt

3.3 Enum

- PascalCase
- tiếng Việt nếu thuộc nghiệp vụ

Ví dụ:
HinhThucDay
LoaiRangBuoc
ThuTrongTuan
LoaiChiDinh

Giá trị enum có thể viết HOA nếu map với DB:
ONLINE
TRUC_TIEP
KHONG_XAC_DINH

4. APPLICATION LAYER

4.1 DTO

Tên class:
- PascalCase
- tiếng Việt
- hậu tố Dto

Ví dụ:
NguoiDungDto
DanhSachLopDto
ThoiKhoaBieuDto
YeuCauDto
RangBuocToiUuDto

Request DTO:
LoginRequestDto
RegisterRequestDto
ImportDanhSachLopRequestDto
UpdateDanhSachLopRequestDto

Field DTO:
- BẮT BUỘC giống Domain
- camelCase
- không dịch sang English

Sai:
username
password
createdAt

Đúng:
tenDangNhap
matKhau
ngayTao

4.2 UseCase Interface

Tên class:
- PascalCase
- tiếng Anh cho hành vi
- hậu tố UseCase

Ví dụ:
AuthUseCase
ImportDanhSachLopUseCase
GenerateThoiKhoaBieuUseCase
ManageDanhSachLopUseCase
ManageThoiKhoaBieuUseCase
ExportDanhSachLopUseCase
ExportThoiKhoaBieuUseCase
AdminDanhSachLopUseCase

Method trong UseCase:

Quy tắc:
verb + VietnameseDomainName

Nhóm CRUD:
createDanhSachLop()
updateDanhSachLop()
deleteDanhSachLop()
getDanhSachLopById()
getAllDanhSachLop()

Nhóm xác thực:
login(LoginRequestDto request)
register(RegisterRequestDto request)

Nhóm import/export:
importDanhSachLop()
exportDanhSachLop()
exportThoiKhoaBieu()

Nhóm xử lý nghiệp vụ:
generateThoiKhoaBieu()
adminDanhSachLop()

Không dùng:
process()
handle()
execute()
doSomething()

4.3 Service Implementation

Tên class:
<Auth>Service implements AuthUseCase

Ví dụ:
AuthService
GenerateThoiKhoaBieuService
ManageDanhSachLopAppService
ManageThoiKhoaBieuAppService

Tên method trong Service phải giống UseCase 100%.

5. PORT OUT (Repository)

5.1 Tên Interface

- PascalCase
- tiếng Việt
- hậu tố Repository

Ví dụ:
NguoiDungRepository
DanhSachLopRepository
ThoiKhoaBieuRepository
YeuCauRepository

5.2 Method Repository

Chuẩn:
save()
update()
deleteById()
findById()
findAll()

Đặc thù:
findByTenDangNhap()
findByDanhSachLopId()
findByNguoiDungId()

Không đặt:
getUser()
getData()
getList()

6. INFRASTRUCTURE

6.1 JDBC Implementation

Tên class:
Jdbc + EntityName + Repository

Ví dụ:
JdbcNguoiDungRepository
JdbcDanhSachLopRepository
JdbcThoiKhoaBieuRepository

6.2 Exporter

Tên class:
Format + Exporter

Ví dụ:
CsvExporter
ExcelExporter
PdfExporter
IcsExporter
CompositeFileExporter

Không đặt:
ExportHelper
FileExportService

7. UI LAYER

7.1 Controller

<Auth>Controller
<Home>Controller

7.2 Pane Handler

<Auth>PaneHandler
<ManageDanhSachLop>PaneHandler
<GenerateThoiKhoaBieu>PaneHandler

Không viết tắt.

8. ĐỒNG BỘ GIỮA CÁC TẦNG

Chuỗi tên phải khớp:

DB → Domain → DTO → Repository → UseCase → Service

Ví dụ:

ten_dang_nhap
tenDangNhap
tenDangNhap
findByTenDangNhap()

Nếu đổi tên ở một tầng, phải đổi ở tất cả tầng.

9. QUY TẮC BẮT BUỘC

1. Không dùng tiếng Anh cho field nghiệp vụ.
2. Không dùng tiếng Việt cho hành vi kỹ thuật.
3. Không viết tắt tùy tiện.
4. Không dùng từ mơ hồ.
5. Mỗi khái niệm chỉ có một tên duy nhất trong toàn hệ thống.
6. Tên giữa các tầng phải đồng bộ tuyệt đối.