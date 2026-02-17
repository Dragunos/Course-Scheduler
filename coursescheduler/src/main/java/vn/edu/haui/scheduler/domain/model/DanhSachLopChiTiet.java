package vn.edu.haui.scheduler.domain.model;

import java.util.Objects;

public class DanhSachLopChiTiet
{

	private Long danhSachLopId;

	private Long lopHocPhanId;

	private Integer batBuoc;

	private LopHocPhan lopHocPhan;

	public DanhSachLopChiTiet()
	{
	}

	public DanhSachLopChiTiet(
			Long danhSachLopId,
			Long lopHocPhanId,
			Integer batBuoc)
	{
		this.danhSachLopId = danhSachLopId;
		this.lopHocPhanId = lopHocPhanId;
		this.batBuoc = (batBuoc == null) ? 0 : batBuoc;
	}

	public DanhSachLopChiTiet(
			Long danhSachLopId,
			LopHocPhan lopHocPhan,
			Integer batBuoc)
	{
		this.danhSachLopId = danhSachLopId;
		this.lopHocPhan = Objects.requireNonNull(lopHocPhan);
		this.lopHocPhanId = lopHocPhan.getId();
		this.batBuoc = (batBuoc == null) ? 0 : batBuoc;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public Long getLopHocPhanId()
	{
		return lopHocPhanId;
	}

	public void setLopHocPhanId(Long lopHocPhanId)
	{
		this.lopHocPhanId = lopHocPhanId;
	}

	public LopHocPhan getLopHocPhan()
	{
		return lopHocPhan;
	}

	public void setLopHocPhan(LopHocPhan lopHocPhan)
	{
		this.lopHocPhan = lopHocPhan;
	}

	public Integer getBatBuoc()
	{
		return batBuoc;
	}

	public void setBatBuoc(Integer batBuoc)
	{
		this.batBuoc = (batBuoc == null) ? 0 : batBuoc;
	}
	
	

	public boolean isBatBuoc()
	{
		return batBuoc != null && batBuoc == 1;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof DanhSachLopChiTiet)) return false;
		DanhSachLopChiTiet that = (DanhSachLopChiTiet) o;
		return Objects.equals(danhSachLopId, that.danhSachLopId)
				&& Objects.equals(lopHocPhanId, that.lopHocPhanId);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(danhSachLopId, lopHocPhanId);
	}
}
