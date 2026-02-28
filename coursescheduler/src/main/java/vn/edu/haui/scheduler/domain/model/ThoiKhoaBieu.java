package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class ThoiKhoaBieu
{
	private final Long id;

	private final NguoiDung nguoiDung;

	private final DanhSachLop danhSachLop;

	private String tenPhuongAn;

	private Double diemDanhGia;

	private final LocalDateTime ngayTao;

	private final Set<LopHocPhan> cacLop = new HashSet<>();

	private ThoiKhoaBieu(
			Long id,
			NguoiDung nguoiDung,
			DanhSachLop danhSachLop,
			String tenPhuongAn,
			Double diemDanhGia,
			LocalDateTime ngayTao)
	{
		validateInvariant(nguoiDung, danhSachLop, tenPhuongAn, ngayTao);

		this.id = id;
		this.nguoiDung = nguoiDung;
		this.danhSachLop = danhSachLop;
		this.tenPhuongAn = tenPhuongAn;
		this.diemDanhGia = diemDanhGia;
		this.ngayTao = ngayTao;
	}

	public static ThoiKhoaBieu create(
			NguoiDung nguoiDung,
			DanhSachLop danhSachLop,
			String tenPhuongAn)
	{
		return new ThoiKhoaBieu(
				null,
				nguoiDung,
				danhSachLop,
				tenPhuongAn,
				null,
				LocalDateTime.now());
	}

	public static ThoiKhoaBieu reconstruct(
			Long id,
			NguoiDung nguoiDung,
			DanhSachLop danhSachLop,
			String tenPhuongAn,
			Double diemDanhGia,
			LocalDateTime ngayTao,
			Set<LopHocPhan> cacLop)
	{
		if(id == null)
			throw new IllegalStateException(
					"Persisted ThoiKhoaBieu must have id");

		ThoiKhoaBieu tkb = new ThoiKhoaBieu(
				id,
				nguoiDung,
				danhSachLop,
				tenPhuongAn,
				diemDanhGia,
				ngayTao);

		if(cacLop != null)
			tkb.cacLop.addAll(cacLop);

		return tkb;
	}

	private static void validateInvariant(
			NguoiDung nguoiDung,
			DanhSachLop danhSachLop,
			String tenPhuongAn,
			LocalDateTime ngayTao)
	{
		if(nguoiDung == null)
			throw new IllegalArgumentException("Nguoi dung null");

		if(danhSachLop == null)
			throw new IllegalArgumentException("Danh sach lop null");

		if(tenPhuongAn == null || tenPhuongAn.isBlank())
			throw new IllegalArgumentException("Ten phuong an khong hop le");

		if(ngayTao == null)
			throw new IllegalArgumentException("Ngay tao null");
	}

	public void themLop(LopHocPhan lop)
	{
		if(!danhSachLop.chuaLop(lop))
			throw new IllegalStateException("Lop khong thuoc danh sach");

		cacLop.add(lop);
	}

	public void chamDiem(double diem)
	{
		// if(diem < 0) throw new IllegalArgumentException("Diem khong hop le");

		this.diemDanhGia = diem;
	}

	public void doiTenPhuongAn(String tenMoi)
	{
		if(tenMoi == null || tenMoi.isBlank())
			throw new IllegalArgumentException("Ten phuong an khong hop le");

		String normalized = tenMoi.trim();

		this.tenPhuongAn = normalized;
	}

	public Long getId()
	{
		return id;
	}

	public NguoiDung getNguoiDung()
	{
		return nguoiDung;
	}

	public DanhSachLop getDanhSachLop()
	{
		return danhSachLop;
	}

	public String getTenPhuongAn()
	{
		return tenPhuongAn;
	}

	public Double getDiemDanhGia()
	{
		return diemDanhGia;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public Set<LopHocPhan> getCacLop()
	{
		return cacLop;
	}

	public boolean isPersisted()
	{
		return id != null;
	}
}