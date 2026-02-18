package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class DanhSachLop
{
	private Long id;

	private final String tenDanhSach;

	private final NguoiDung nguoiTao;

	private final boolean congKhai;

	private final HocKy hocKy;

	private final LocalDateTime ngayTao;

	private final List<DanhSachLopChiTiet> chiTietList;

	public DanhSachLop(String tenDanhSach,
			NguoiDung nguoiTao,
			boolean congKhai,
			HocKy hocKy)
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

	public DanhSachLop(Long id,
			String tenDanhSach,
			NguoiDung nguoiTao,
			boolean congKhai,
			HocKy hocKy,
			LocalDateTime ngayTao,
			List<DanhSachLopChiTiet> chiTietList)
	{
		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTao = nguoiTao;
		this.congKhai = congKhai;
		this.hocKy = hocKy;
		this.ngayTao = ngayTao;
		this.chiTietList = chiTietList == null
				? new ArrayList<>()
				: new ArrayList<>(chiTietList);
	}

	public void themLop(LopHocPhan lop, boolean batBuoc)
	{
		Objects.requireNonNull(lop);

		boolean daTonTai = chiTietList.stream()
				.anyMatch(ct -> ct.getLopHocPhan().getId().equals(lop.getId()));

		if(daTonTai)
			throw new IllegalStateException("Lop da ton tai");

		chiTietList.add(new DanhSachLopChiTiet(lop, batBuoc));
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

	public boolean isCongKhai()
	{
		return congKhai;
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
