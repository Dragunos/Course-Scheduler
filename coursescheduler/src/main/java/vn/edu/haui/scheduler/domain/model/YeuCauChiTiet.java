package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class YeuCauChiTiet
{
	private final YeuCau yeuCau;

	private final LopHocPhan lopHocPhan;

	private final boolean batBuoc;

	private final String loaiChiDinh;

	private final Double trongSo;

	private YeuCauChiTiet(
			YeuCau yeuCau,
			LopHocPhan lopHocPhan,
			boolean batBuoc,
			String loaiChiDinh,
			Double trongSo)
	{
		validateInvariant(yeuCau, lopHocPhan);

		this.yeuCau = yeuCau;
		this.lopHocPhan = lopHocPhan;
		this.batBuoc = batBuoc;
		this.loaiChiDinh = normalizeLoaiChiDinh(loaiChiDinh);
		this.trongSo = trongSo;
	}

	// ---------- Factory Methods ----------

	public static YeuCauChiTiet create(
			YeuCau yeuCau,
			LopHocPhan lopHocPhan,
			boolean batBuoc,
			String loaiChiDinh,
			Double trongSo)
	{
		return new YeuCauChiTiet(
				yeuCau,
				lopHocPhan,
				batBuoc,
				loaiChiDinh,
				trongSo);
	}

	public static YeuCauChiTiet reconstruct(
			YeuCau yeuCau,
			LopHocPhan lopHocPhan,
			boolean batBuoc,
			String loaiChiDinh,
			Double trongSo)
	{
		if(yeuCau == null)
			throw new IllegalStateException("Reconstruct YeuCauChiTiet requires YeuCau");

		if(lopHocPhan == null)
			throw new IllegalStateException("Reconstruct YeuCauChiTiet requires LopHocPhan");

		return new YeuCauChiTiet(
				yeuCau,
				lopHocPhan,
				batBuoc,
				loaiChiDinh,
				trongSo);
	}

	private static void validateInvariant(
			YeuCau yeuCau,
			LopHocPhan lopHocPhan)
	{
		if(yeuCau == null)
			throw new IllegalArgumentException("Yeu cau khong duoc null");

		if(lopHocPhan == null)
			throw new IllegalArgumentException("Lop hoc phan khong duoc null");
	}

	private static String normalizeLoaiChiDinh(String value)
	{
		if(value == null || value.isBlank())
			return "NONE";

		return value.toUpperCase();
	}

	public boolean isOptional()
	{
		return !batBuoc;
	}

	public boolean hasTrongSo()
	{
		return trongSo != null;
	}

	// ---------- Identity ----------

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;

		if(!(o instanceof YeuCauChiTiet))
			return false;

		YeuCauChiTiet that = (YeuCauChiTiet) o;

		return Objects.equals(
				lopHocPhan.getId(),
				that.lopHocPhan.getId());
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(lopHocPhan.getId());
	}

	// ---------- Getters ----------

	public YeuCau getYeuCau()
	{
		return yeuCau;
	}

	public LopHocPhan getLopHocPhan()
	{
		return lopHocPhan;
	}

	public boolean isBatBuoc()
	{
		return batBuoc;
	}

	public String getLoaiChiDinh()
	{
		return loaiChiDinh;
	}

	public Double getTrongSo()
	{
		return trongSo;
	}
}