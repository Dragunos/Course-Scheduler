# CourseScheduler

Ứng dụng desktop JavaFX hỗ trợ **sinh, so sánh và quản lý phương án đăng ký học phần** theo ràng buộc và ưu tiên của người dùng.  
Dự án được xây dựng theo **Clean / Hexagonal Architecture**, với mục tiêu thể hiện rõ **cách tổ chức code, tách tầng trách nhiệm, và tư duy thiết kế hệ thống**.

> **Trạng thái hiện tại:** Đã hoàn thiện các tính năng cơ bản   
> **Cách chạy hiện tại:** Chạy trực tiếp từ mã nguồn trên IDE

---

## Project Overview

Quá trình đăng ký học phần ở bậc đại học thường đòi hỏi sinh viên phải tự chọn các lớp thỏa nhiều điều kiện cùng lúc: không trùng lịch, phù hợp với giảng viên mong muốn, khung giờ hợp lý, và ưu tiên cá nhân. Cách làm thủ công dễ dẫn đến xung đột thời khóa biểu, tốn thời gian thử-sai, và khó tối ưu theo nhu cầu thực tế.

**CourseScheduler** được xây dựng để giải quyết bài toán đó bằng cách:
- Tự động sinh các tổ hợp lớp khả thi,
- Đánh giá các phương án theo ràng buộc cứng và ưu tiên mềm,
- Cho phép người dùng so sánh, lưu và xuất kết quả,
- Cung cấp công cụ quản trị dữ liệu học phần ở mức cục bộ trên máy.

Dự án hiện được định hướng không chỉ như một bài tập học thuật, mà như một **phần mềm desktop có cấu trúc rõ ràng**, có thể đọc hiểu, kiểm tra và mở rộng.

### Điểm nhấn kiến trúc

- **Tầng Application**: Chứa use case, DTO, exception xử lý nghiệp vụ.
- **Tầng Domain**: Chứa mô hình nghiệp vụ cốt lõi và module optimizer.
- **Tầng Infrastructure=**: Xử lý database, import/export file, bảo mật.
- **Tầng UI**: JavaFX + FXML + controller + viewmodel.

Mục tiêu là làm nổi bật:
- Cách tách module hợp lý,
- Khả năng mở rộng,
- Và tư duy tổ chức code theo chiều sâu, không chỉ ăn điểm bằng giao diện.

---

## Minh họa

> WIP

---

## Tính năng cốt lõi

- Sinh tổ hợp lớp học phần không trùng lịch.
- Hỗ trợ thiết lập ưu tiên cá nhân như:
  - Giờ học mong muốn,
  - Giảng viên ưu tiên,
  - Khóa các lớp bắt buộc.
- Cho phép duyệt, chỉnh sửa, lưu và xuất phương án.
- Có giao diện quản trị để quản lý dữ liệu:
  - Môn học,
  - Lớp học phần,
  - Giảng viên,
  - Học kỳ,
  - Ràng buộc và dữ liệu liên quan.
- Hỗ trợ nhập dữ liệu từ Excel.
- Hỗ trợ xuất kết quả ra CSV, PDF và iCalendar (.ics).
- Lưu trữ cục bộ bằng SQLite.
- Tách riêng module tối ưu để có thể kiểm thử và phát triển độc lập.
- Tổ chức theo Clean Architecture / Hexagonal Architecture để dễ bảo trì.

---

## Công nghệ áp dụng

### Ngôn ngữ và nền tảng
- Java 21
- JavaFX / OpenJFX
- Maven

### Cơ sở dữ liệu
- SQLite
- JDBC
- HikariCP

### Nhập / xuất dữ liệu
- Apache POI (`.xls`, `.xlsx`)
- Apache Commons CSV
- Apache PDFBox
- ical4j

### Bảo mật
- jBCrypt

### Kiểm thử
- JUnit 5

### Đóng gói
- jpackage

### Công cụ phát triển
- Eclipse IDE for Java Developers 2023-12
- Scene Builder
- Git / GitHub

---

## Cấu trúc dự án

```text
coursescheduler
├─ pom.xml
└─ src
   └─ main
      ├─ java
      │  └─ vn.edu.haui.scheduler
      │     ├─ MainApp.java
      │     ├─ application
      │     │  ├─ dto
      │     │  ├─ exception
      │     │  ├─ port
      │     │  │  ├─ in
      │     │  │  └─ out
      │     │  └─ service
      │     │     └─ mapper
      │     ├─ domain
      │     │  ├─ model
      │     │  └─ optimizer
      │     ├─ infrastructure
      │     │  ├─ io
      │     │  │  ├─ imports
      │     │  │  └─ exports
      │     │  ├─ persistence
      │     │  │  ├─ config
      │     │  │  └─ jdbc
      │     │  │     └─ mapper
      │     │  └─ security
      │     └─ ui
      │        ├─ controller
      │        │  └─ handler
      │        ├─ fx
      │        │  └─ config
      │        ├─ util
      │        └─ viewmodel
      └─ resources
         ├─ db
         │  └─ migration
         ├─ css
         ├─ font
         └─ fxml