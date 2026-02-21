package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.*;

public class YeuCau
{
	private final Long id;

	private final NguoiDung nguoiTao;

	private final DanhSachLop danhSachLop;

	private String tenYeuCau;

	private final LocalDateTime ngayTao;

	private final Map<Long, YeuCauChiTiet> chiTietMap = new HashMap<>();

	private final List<RangBuocToiUu> rangBuocList = new ArrayList<>();

	private YeuCau(
			Long id,
			NguoiDung nguoiTao,
			DanhSachLop danhSachLop,
			String tenYeuCau,
			LocalDateTime ngayTao)
	{
		validateInvariant(nguoiTao, danhSachLop, ngayTao);

		this.id = id;
		this.nguoiTao = nguoiTao;
		this.danhSachLop = danhSachLop;
		this.tenYeuCau = tenYeuCau;
		this.ngayTao = ngayTao;
	}

	public static YeuCau create(
			NguoiDung nguoiTao,
			DanhSachLop danhSachLop,
			String tenYeuCau)
	{
		return new YeuCau(
				null,
				nguoiTao,
				danhSachLop,
				tenYeuCau,
				LocalDateTime.now());
	}

	public static YeuCau reconstruct(
			Long id,
			NguoiDung nguoiTao,
			DanhSachLop danhSachLop,
			String tenYeuCau,
			LocalDateTime ngayTao,
			Collection<YeuCauChiTiet> chiTiet,
			Collection<RangBuocToiUu> rangBuoc)
	{
		if(id == null) {
			throw new IllegalStateException("Persisted YeuCau must have id");
		}

		YeuCau yc = new YeuCau(id, nguoiTao, danhSachLop, tenYeuCau, ngayTao);

		if(chiTiet != null) {
			for(YeuCauChiTiet ct : chiTiet) {
				yc.chiTietMap.put(ct.getLopHocPhan().getId(), ct);
			}
		}

		if(rangBuoc != null) {
			yc.rangBuocList.addAll(rangBuoc);
		}

		return yc;
	}

	private static void validateInvariant(
			NguoiDung nguoiTao,
			DanhSachLop danhSachLop,
			LocalDateTime ngayTao)
	{
		if(nguoiTao == null)
			throw new IllegalArgumentException("Nguoi tao khong duoc null");

		if(danhSachLop == null)
			throw new IllegalArgumentException("Danh sach lop khong duoc null");

		if(ngayTao == null)
			throw new IllegalArgumentException("Ngay tao khong duoc null");
	}

	public void themChiTiet(LopHocPhan lop, boolean batBuoc, String loaiChiDinh, Double trongSo)
	{
		if(lop == null)
			throw new IllegalArgumentException("Lop khong duoc null");

		if(!danhSachLop.chuaLop(lop))
			throw new IllegalStateException("Lop khong thuoc danh sach lop");

		if(chiTietMap.containsKey(lop.getId()))
			throw new IllegalStateException("Chi tiet da ton tai");

		YeuCauChiTiet ct = YeuCauChiTiet.create(this, lop, batBuoc, loaiChiDinh, trongSo);
		chiTietMap.put(lop.getId(), ct);
	}

	public void themRangBuoc(RangBuocToiUu rangBuoc)
	{
		if(rangBuoc == null)
			throw new IllegalArgumentException("Rang buoc khong duoc null");

		if(!rangBuoc.apDungChoYeuCau(this))
			throw new IllegalStateException("Rang buoc khong thuoc YeuCau nay");

		rangBuocList.add(rangBuoc);
	}

	public Long getId()
	{
		return id;
	}

	public NguoiDung getNguoiTao()
	{
		return nguoiTao;
	}

	public DanhSachLop getDanhSachLop()
	{
		return danhSachLop;
	}

	public String getTenYeuCau()
	{
		return tenYeuCau;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public Collection<YeuCauChiTiet> getChiTiet()
	{
		return Collections.unmodifiableCollection(chiTietMap.values());
	}

	public List<RangBuocToiUu> getRangBuoc()
	{
		return Collections.unmodifiableList(rangBuocList);
	}

	public boolean isPersisted()
	{
		return id != null;
	}
}