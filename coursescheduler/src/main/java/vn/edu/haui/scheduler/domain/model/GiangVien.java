package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class GiangVien
{
	private final Long id;

	private final String tenGiangVien;

	private GiangVien(Long id, String ten)
	{
		if(ten == null || ten.isBlank())
			throw new IllegalArgumentException("Tên Giảng Viên không hợp lệ");

		this.id = id;
		this.tenGiangVien = ten;
	}

	public static GiangVien create(String ten)
	{
		return new GiangVien(null, ten);
	}

	public static GiangVien reconstruct(Long id, String ten)
	{
		if(id == null) throw new IllegalStateException("Thông tin về Giảng Viên (ID) bị thiếu");
		return new GiangVien(id, ten);
	}

	public Long getId()
	{
		return id;
	}

	public String getTenGiangVien()
	{
		return tenGiangVien;
	}

	public boolean isPersisted()
	{
		return id != null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof GiangVien)) return false;
		GiangVien that = (GiangVien) o;
		return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(id);
	}
}