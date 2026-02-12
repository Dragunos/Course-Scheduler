package vn.edu.haui.scheduler.domain.model;

import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;

import java.util.Objects;

public class LichHoc
{
	private Long id;

	private Long lopHocPhanId;

	private ThuTrongTuan thu;

	private Integer tietBatDau;

	private Integer tietKetThuc;

	public LichHoc()
	{
	}

	public LichHoc(Long id, Long lopHocPhanId, ThuTrongTuan thu, Integer tietBatDau, Integer tietKetThuc)
	{
		this.id = id;
		this.lopHocPhanId = lopHocPhanId;
		this.thu = thu;
		setTietBatDau(tietBatDau);
		setTietKetThuc(tietKetThuc);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}

	public ThuTrongTuan getThu()
	{
		return thu;
	}

	public void setThu(ThuTrongTuan thu)
	{
		this.thu = thu;
	}

	public Integer getTietBatDau()
	{
		return tietBatDau;
	}

	public void setTietBatDau(Integer tietBatDau)
	{
		this.tietBatDau = tietBatDau;
		if(this.tietKetThuc != null && this.tietBatDau != null && this.tietBatDau > this.tietKetThuc) {
			throw new IllegalArgumentException("tietBatDau must be <= tietKetThuc");
		}
	}

	public Integer getTietKetThuc()
	{
		return tietKetThuc;
	}

	public void setTietKetThuc(Integer tietKetThuc)
	{
		this.tietKetThuc = tietKetThuc;
		if(this.tietBatDau != null && this.tietKetThuc != null && this.tietBatDau > this.tietKetThuc) {
			throw new IllegalArgumentException("tietBatDau must be <= tietKetThuc");
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		LichHoc lichHoc = (LichHoc) o;
		return Objects.equals(id, lichHoc.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "LichHoc{" +
				"id=" + id +
				", lopHocPhanId=" + lopHocPhanId +
				", thu=" + thu +
				", tietBatDau=" + tietBatDau +
				", tietKetThuc=" + tietKetThuc +
				'}';
	}
}