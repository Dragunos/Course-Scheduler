package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class HocKy
{
	private final Long id;

	private final String tenHocKy;

	private final String namHoc;

	private HocKy(Long id, String tenHocKy, String namHoc)
	{
		if(tenHocKy == null || tenHocKy.isBlank())
			throw new IllegalArgumentException("Ten hoc ky khong hop le");

		if(namHoc == null || namHoc.isBlank())
			throw new IllegalArgumentException("Nam hoc khong hop le");

		this.id = id;
		this.tenHocKy = tenHocKy;
		this.namHoc = namHoc;
	}

	public static HocKy create(String ten, String nam)
	{
		return new HocKy(null, ten, nam);
	}

	public static HocKy reconstruct(Long id, String ten, String nam)
	{
		if(id == null) throw new IllegalStateException("Persisted HocKy must have id");
		return new HocKy(id, ten, nam);
	}

	public Long getId()
	{
		return id;
	}

	public String getTenHocKy()
	{
		return tenHocKy;
	}

	public String getNamHoc()
	{
		return namHoc;
	}

	public boolean isPersisted()
	{
		return id != null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof HocKy)) return false;
		HocKy that = (HocKy) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}