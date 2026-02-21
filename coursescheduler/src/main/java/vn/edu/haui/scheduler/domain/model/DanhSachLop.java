package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class DanhSachLop
{
	private final Long id;

	private final String tenDanhSach;

	private final NguoiDung nguoiTao;

	private final boolean laCongKhai;

	private final HocKy hocKy;

	private final LocalDateTime ngayTao;

	private final List<DanhSachLopChiTiet> chiTietList = new ArrayList<>();

	private DanhSachLop(
			Long id,
			String tenDanhSach,
			NguoiDung nguoiTao,
			boolean laCongKhai,
			HocKy hocKy,
			LocalDateTime ngayTao,
			List<DanhSachLopChiTiet> chiTiet)
	{
		if(tenDanhSach == null || tenDanhSach.isBlank())
			throw new IllegalArgumentException("Ten danh sach khong hop le");

		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTao = Objects.requireNonNull(nguoiTao);
		this.laCongKhai = laCongKhai;
		this.hocKy = hocKy;
		this.ngayTao = Objects.requireNonNull(ngayTao);

		if(chiTiet != null)
			this.chiTietList.addAll(chiTiet);
	}

	public static DanhSachLop create(
			String tenDanhSach,
			NguoiDung nguoiTao,
			boolean laCongKhai,
			HocKy hocKy)
	{
		return new DanhSachLop(
				null,
				tenDanhSach,
				nguoiTao,
				laCongKhai,
				hocKy,
				LocalDateTime.now(),
				Collections.emptyList());
	}

	public static DanhSachLop reconstruct(
			Long id,
			String tenDanhSach,
			NguoiDung nguoiTao,
			boolean laCongKhai,
			HocKy hocKy,
			LocalDateTime ngayTao,
			List<DanhSachLopChiTiet> chiTiet)
	{
		if(id == null)
			throw new IllegalStateException("Persisted DanhSachLop must have id");

		return new DanhSachLop(
				id,
				tenDanhSach,
				nguoiTao,
				laCongKhai,
				hocKy,
				ngayTao,
				chiTiet);
	}

	public void themLop(LopHocPhan lop, boolean batBuoc)
	{
		if(lop == null)
			throw new IllegalArgumentException("LopHocPhan null");

		Long lopId = lop.getId();

		for(DanhSachLopChiTiet c : chiTietList) {
			if(c.getLopHocPhan().getId().equals(lopId))
				throw new IllegalStateException("Lop da ton tai");
		}

		chiTietList.add(DanhSachLopChiTiet.create(lop, batBuoc));
	}

	public boolean chuaLop(LopHocPhan lop)
	{
		if(lop == null)
			return false;

		Long lopId = lop.getId();

		for(DanhSachLopChiTiet ct : chiTietList) {
			if(ct.getLopHocPhan().getId().equals(lopId)) {
				return true;
			}
		}

		return false;
	}

	public List<DanhSachLopChiTiet> getChiTietList()
	{
		return Collections.unmodifiableList(chiTietList);
	}

	public Long getId()
	{
		return id;
	}

	public String getTenDanhSach()
	{
		return tenDanhSach;
	}

	public NguoiDung getNguoiTao()
	{
		return nguoiTao;
	}

	public boolean isLaCongKhai()
	{
		return laCongKhai;
	}

	public HocKy getHocKy()
	{
		return hocKy;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}
}