package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DanhSachLop
{
	private Long id;

	private String tenDanhSach;

	private Long nguoiTaoId;

	private Integer laCongKhai;

	private Long hocKyId;

	private LocalDateTime ngayTao;

	private List<DanhSachLopChiTiet> chiTietList;

	public DanhSachLop()
	{
		this.chiTietList = new ArrayList<>();
	}

	public DanhSachLop(Long id,
			String tenDanhSach,
			Long nguoiTaoId,
			Integer laCongKhai,
			Long hocKyId,
			LocalDateTime ngayTao,
			List<DanhSachLopChiTiet> chiTietList)
	{

		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTaoId = nguoiTaoId;
		this.laCongKhai = laCongKhai;
		this.hocKyId = hocKyId;
		this.ngayTao = ngayTao;
		this.chiTietList = (chiTietList == null)
				? new ArrayList<>()
				: new ArrayList<>(chiTietList);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTenDanhSach()
	{
		return tenDanhSach;
	}

	public void setTenDanhSach(String tenDanhSach)
	{
		this.tenDanhSach = tenDanhSach;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}

	public Integer getLaCongKhai()
	{
		return laCongKhai;
	}

	public void setLaCongKhai(Integer laCongKhai)
	{
		this.laCongKhai = laCongKhai;
	}

	public Long getHocKyId()
	{
		return hocKyId;
	}

	public void setHocKyId(Long hocKyId)
	{
		this.hocKyId = hocKyId;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}

	public List<DanhSachLopChiTiet> getChiTietList()
	{
		return new ArrayList<>(chiTietList);
	}

	public void setChiTietList(List<DanhSachLopChiTiet> chiTietList)
	{
		this.chiTietList = (chiTietList == null)
				? new ArrayList<>()
				: new ArrayList<>(chiTietList);
	}

	public List<Long> getLopHocPhanIds()
	{
		List<Long> ids = new ArrayList<>();
		for(DanhSachLopChiTiet ct : chiTietList) {
			ids.add(ct.getLopHocPhanId());
		}
		return ids;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof DanhSachLop)) return false;
		DanhSachLop that = (DanhSachLop) o;
		return Objects.equals(id, that.id);
	}

	public boolean isCongKhai()
	{
		return Integer.valueOf(1).equals(this.laCongKhai);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public String toString()
	{
		return "DanhSachLop{" +
				"id=" + id +
				", tenDanhSach='" + tenDanhSach + '\'' +
				", nguoiTaoId=" + nguoiTaoId +
				", laCongKhai=" + laCongKhai +
				", hocKyId=" + hocKyId +
				", ngayTao=" + ngayTao +
				", chiTietList=" + chiTietList +
				'}';
	}
}