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
			throw new IllegalArgumentException("Tên Học Kỳ không hợp lệ");

		if(namHoc == null || namHoc.isBlank())
			throw new IllegalArgumentException("Năm học không hợp lệ");

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
		if(id == null) throw new IllegalStateException("Thông tin về Học Kỳ (ID) bị thiếu");
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