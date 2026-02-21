package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class HocPhan
{
	private final Long id;

	private final String maHocPhan;

	private String tenHocPhan;

	private int soTinChi;

	private HocPhan(
			Long id,
			String maHocPhan,
			String tenHocPhan,
			int soTinChi)
	{
		validateInvariant(maHocPhan, tenHocPhan, soTinChi);

		this.id = id;
		this.maHocPhan = maHocPhan;
		this.tenHocPhan = tenHocPhan;
		this.soTinChi = soTinChi;
	}

	public static HocPhan create(
			String maHocPhan,
			String tenHocPhan,
			int soTinChi)
	{
		return new HocPhan(
				null,
				maHocPhan,
				tenHocPhan,
				soTinChi);
	}

	public static HocPhan reconstruct(
			Long id,
			String maHocPhan,
			String tenHocPhan,
			int soTinChi)
	{
		if(id == null) {
			throw new IllegalStateException("Persisted HocPhan must have id");
		}

		return new HocPhan(
				id,
				maHocPhan,
				tenHocPhan,
				soTinChi);
	}

	private static void validateInvariant(
			String maHocPhan,
			String tenHocPhan,
			int soTinChi)
	{
		if(maHocPhan == null || maHocPhan.isBlank()) {
			throw new IllegalArgumentException("Ma hoc phan khong hop le");
		}

		if(tenHocPhan == null || tenHocPhan.isBlank()) {
			throw new IllegalArgumentException("Ten hoc phan khong hop le");
		}

		if(soTinChi < 0) {
			throw new IllegalArgumentException("So tin chi khong hop le");
		}
	}

	public void doiTen(String tenMoi)
	{
		if(tenMoi == null || tenMoi.isBlank()) {
			throw new IllegalArgumentException("Ten hoc phan moi khong hop le");
		}

		this.tenHocPhan = tenMoi;
	}

	public void capNhatSoTinChi(int soTinChiMoi)
	{
		if(soTinChiMoi < 0) {
			throw new IllegalArgumentException("So tin chi moi khong hop le");
		}

		this.soTinChi = soTinChiMoi;
	}

	public Long getId()
	{
		return id;
	}

	public String getMaHocPhan()
	{
		return maHocPhan;
	}

	public String getTenHocPhan()
	{
		return tenHocPhan;
	}

	public int getSoTinChi()
	{
		return soTinChi;
	}

	public boolean isPersisted()
	{
		return id != null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof HocPhan)) return false;
		HocPhan that = (HocPhan) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}