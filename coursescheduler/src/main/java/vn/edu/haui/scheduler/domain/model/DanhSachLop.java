package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class DanhSachLop
{
	private final Long id;

	private String tenDanhSach;

	private final NguoiDung nguoiTao;

	private boolean laCongKhai;

	private HocKy hocKy;

	private final LocalDateTime ngayTao;

	private final List<DanhSachLopChiTiet> chiTietList = new ArrayList<>();

	private final Set<Long> sharedUserIds = new HashSet<>();

	private DanhSachLop(
			Long id,
			String tenDanhSach,
			NguoiDung nguoiTao,
			boolean laCongKhai,
			HocKy hocKy,
			LocalDateTime ngayTao,
			List<DanhSachLopChiTiet> chiTiet,
			Set<Long> sharedUserIds)
	{
		if(tenDanhSach == null || tenDanhSach.isBlank())
			throw new IllegalArgumentException("Tên Danh Sách không hợp lệ");

		this.id = id;
		this.tenDanhSach = tenDanhSach.trim();
		this.nguoiTao = Objects.requireNonNull(nguoiTao);
		this.laCongKhai = laCongKhai;
		this.hocKy = hocKy;
		this.ngayTao = Objects.requireNonNull(ngayTao);

		if(chiTiet != null)
			this.chiTietList.addAll(chiTiet);

		if(sharedUserIds != null)
			this.sharedUserIds.addAll(sharedUserIds);
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
				Collections.emptyList(),
				Collections.emptySet());
	}

	public static DanhSachLop reconstruct(
			Long id,
			String tenDanhSach,
			NguoiDung nguoiTao,
			boolean laCongKhai,
			HocKy hocKy,
			LocalDateTime ngayTao,
			List<DanhSachLopChiTiet> chiTiet,
			Set<Long> sharedUserIds)
	{
		if(id == null)
			throw new IllegalStateException("Danh Sách Lớp mục tiêu không xác định (ID không tồn tại)");

		return new DanhSachLop(
				id,
				tenDanhSach,
				nguoiTao,
				laCongKhai,
				hocKy,
				ngayTao,
				chiTiet,
				sharedUserIds);
	}

	public void themLop(LopHocPhan lop, boolean batBuoc)
	{
		if(lop == null)
			throw new IllegalArgumentException("LopHocPhan null");

		Long lopId = lop.getId();

		for(DanhSachLopChiTiet c : chiTietList) {
			if(c.getLopHocPhan().getId().equals(lopId))
				throw new IllegalStateException("Lớp đã tồn tại");
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

	public void doiTen(String tenMoi)
	{
		if(tenMoi == null || tenMoi.isBlank())
			throw new IllegalArgumentException("Tên Danh Sách không hợp lệ");

		this.tenDanhSach = tenMoi.trim();
	}

	public void doiHocKy(HocKy hocKyMoi)
	{
		this.hocKy = hocKyMoi;
	}

	public void xoaTatCaLop()
	{
		this.chiTietList.clear();
	}

	public boolean duocChiaSeCho(NguoiDung user)
	{
		if(user == null || user.getId() == null)
			return false;

		return laCongKhai
				|| nguoiTao.getId().equals(user.getId())
				|| sharedUserIds.contains(user.getId());
	}

	public void chiaSeCho(NguoiDung user)
	{
		if(user == null || user.getId() == null)
			throw new IllegalArgumentException();

		sharedUserIds.add(user.getId());
	}

	public void huyChiaSeCho(NguoiDung user)
	{
		if(user == null || user.getId() == null)
			return;

		sharedUserIds.remove(user.getId());
	}

	public void congKhai()
	{
		if(this.laCongKhai)
			throw new IllegalStateException("Danh Sách đã được công khai");

		this.laCongKhai = true;
	}

	public void anDanhSach()
	{
		if(!this.laCongKhai)
			throw new IllegalStateException("Trạng thái Công Khai đã thay đổi");

		this.laCongKhai = false;
	}
	
	public List<LopHocPhan> getLopHocPhanList()
	{
	    List<LopHocPhan> result = new ArrayList<>();

	    for (DanhSachLopChiTiet ct : chiTietList) {
	        result.add(ct.getLopHocPhan());
	    }

	    return Collections.unmodifiableList(result);
	}

	public List<DanhSachLopChiTiet> getChiTietList()
	{
		return Collections.unmodifiableList(chiTietList);
	}

	public Set<Long> getSharedUserIds()
	{
		return Collections.unmodifiableSet(sharedUserIds);
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