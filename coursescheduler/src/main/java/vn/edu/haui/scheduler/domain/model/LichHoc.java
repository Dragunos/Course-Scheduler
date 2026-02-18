package vn.edu.haui.scheduler.domain.model;

import vn.edu.haui.scheduler.domain.enums.ThuTrongTuan;

public class LichHoc
{
	private Long id;

	private Long lopHocPhanId;

	private ThuTrongTuan thu;

	private KhoangTiet khoangTiet;

	public LichHoc()
	{
	}

	public LichHoc(Long id, Long lopHocPhanId, ThuTrongTuan thu, KhoangTiet khoangTiet)
	{
		this.id = id;
		this.lopHocPhanId = lopHocPhanId;
		this.thu = thu;
		this.khoangTiet = khoangTiet;
	}

	public LichHoc(Long id, Long lopHocPhanId, ThuTrongTuan thu, int tietBatDau, int tietKetThuc)
	{
		this.id = id;
		this.lopHocPhanId = lopHocPhanId;
		this.thu = thu;
		this.khoangTiet = new KhoangTiet(tietBatDau, tietKetThuc);
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

	public KhoangTiet getKhoangTiet()
	{
		return khoangTiet;
	}

	public Integer getTietBatDau()
	{
		return khoangTiet != null ? khoangTiet.getTietBatDau() : null;
	}

	public Integer getTietKetThuc()
	{
		return khoangTiet != null ? khoangTiet.getTietKetThuc() : null;
	}

	public void setTietBatDau(int tietBatDau)
	{
		if(this.khoangTiet == null) {
			this.khoangTiet = new KhoangTiet(tietBatDau, tietBatDau);
		}
		else {
			this.khoangTiet = new KhoangTiet(tietBatDau, this.khoangTiet.getTietKetThuc());
		}
	}

	public void setTietKetThuc(int tietKetThuc)
	{
		if(this.khoangTiet == null) {
			this.khoangTiet = new KhoangTiet(tietKetThuc, tietKetThuc);
		}
		else {
			this.khoangTiet = new KhoangTiet(this.khoangTiet.getTietBatDau(), tietKetThuc);
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof LichHoc)) return false;
		LichHoc other = (LichHoc) o;
		return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode()
	{
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return "LichHoc{" +
				"id=" + id +
				", lopHocPhanId=" + lopHocPhanId +
				", thu=" + thu +
				", khoangTiet=" + khoangTiet +
				'}';
	}
}
