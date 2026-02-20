package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class DanhSachLop
{
	private Long id;

	private String tenDanhSach;

	private final NguoiDung nguoiTao;

	private final boolean congKhai;

	private HocKy hocKy;

	private final LocalDateTime ngayTao;

	private final List<DanhSachLopChiTiet> chiTietList;

	public DanhSachLop(String tenDanhSach, NguoiDung nguoiTao, boolean congKhai, HocKy hocKy)
	{
		if(tenDanhSach == null || tenDanhSach.isBlank())
			throw new IllegalArgumentException("Ten danh sach khong duoc rong");

		this.tenDanhSach = tenDanhSach.trim();
		this.nguoiTao = Objects.requireNonNull(nguoiTao);
		this.congKhai = congKhai;
		this.hocKy = Objects.requireNonNull(hocKy);
		this.ngayTao = LocalDateTime.now();
		this.chiTietList = new ArrayList<>();
	}

	public DanhSachLop(Long id, String tenDanhSach, NguoiDung nguoiTao, boolean congKhai, HocKy hocKy,
			LocalDateTime ngayTao, List<DanhSachLopChiTiet> chiTietList)
	{
		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTao = nguoiTao;
		this.congKhai = congKhai;
		this.hocKy = hocKy;
		this.ngayTao = ngayTao;
		this.chiTietList = chiTietList == null ? new ArrayList<>() : new ArrayList<>(chiTietList);
	}

	public Long getId()
	{
		return id;
	}

	public String getTenDanhSach()
	{
		return tenDanhSach;
	}
	
	public void doiTenDanhSach(String tenMoi)
	{
		if(tenMoi == null || tenMoi.isBlank())
			throw new IllegalArgumentException();

		this.tenDanhSach = tenMoi.trim();
	}

	public NguoiDung getNguoiTao()
	{
		return nguoiTao;
	}

	public boolean isCongKhai()
	{
		return congKhai;
	}

	public HocKy getHocKy()
	{
		return hocKy;
	}
	
	public void doiHocKy(Long hocKyId)
	{
		if(hocKyId == null) {
			this.hocKy = null;
			return;
		}

		this.hocKy = new HocKy(hocKyId, "UNKNOWN", "UNKNOWN");
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void themLopHocPhan(Long lopHocPhanId)
	{
		Objects.requireNonNull(lopHocPhanId);

		boolean daTonTai = chiTietList.stream()
				.anyMatch(ct -> ct.getLopHocPhan().getId().equals(lopHocPhanId));

		if(daTonTai)
			return;

		LopHocPhan lopStub = new LopHocPhan(lopHocPhanId);

		chiTietList.add(new DanhSachLopChiTiet(lopStub, false));
	}

	public void themLop(LopHocPhan lop, boolean batBuoc)
	{
		Objects.requireNonNull(lop);

		boolean daTonTai = chiTietList.stream().anyMatch(ct -> ct.getLopHocPhan().getId().equals(lop.getId()));

		if(daTonTai)
			throw new IllegalStateException("Lop da ton tai");

		chiTietList.add(new DanhSachLopChiTiet(lop, batBuoc));
	}

	public void xoaLopHocPhan(Long lopHocPhanId)
	{
		Objects.requireNonNull(lopHocPhanId);

		chiTietList.removeIf(ct -> ct.getLopHocPhan().getId().equals(lopHocPhanId));
	}

	public List<DanhSachLopChiTiet> getChiTietList()
	{
		return Collections.unmodifiableList(chiTietList);
	}
}
