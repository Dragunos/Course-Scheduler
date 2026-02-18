package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class HocKy
{

	private Long id;

	private String tenHocKy;

	private String namHoc;

	public HocKy(String tenHocKy, String namHoc)
	{
		this.tenHocKy = validateTenHocKy(tenHocKy);
		this.namHoc = validateNamHoc(namHoc);
	}

	public HocKy(Long id, String tenHocKy, String namHoc)
	{
		this.id = id;
		this.tenHocKy = validateTenHocKy(tenHocKy);
		this.namHoc = validateNamHoc(namHoc);
	}

	private String validateTenHocKy(String tenHocKy)
	{
		if(tenHocKy == null || tenHocKy.isBlank()) {
			throw new IllegalArgumentException("Ten hoc ky khong duoc de trong");
		}
		return tenHocKy.trim();
	}

	private String validateNamHoc(String namHoc)
	{
		if(namHoc == null || namHoc.isBlank()) {
			throw new IllegalArgumentException("Nam hoc khong duoc de trong");
		}
		return namHoc.trim();
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

	public void doiTenHocKy(String tenHocKy)
	{
		this.tenHocKy = validateTenHocKy(tenHocKy);
	}

	public void doiNamHoc(String namHoc)
	{
		this.namHoc = validateNamHoc(namHoc);
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof HocKy)) return false;
		HocKy that = (HocKy) o;

		if(id != null && that.id != null) {
			return id.equals(that.id);
		}

		return tenHocKy.equals(that.tenHocKy)
				&& namHoc.equals(that.namHoc);
	}

	@Override
	public int hashCode()
	{
		if(id != null) {
			return id.hashCode();
		}
		return Objects.hash(tenHocKy, namHoc);
	}

	@Override
	public String toString()
	{
		return tenHocKy + " - " + namHoc;
	}
}