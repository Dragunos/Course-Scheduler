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

	private List<Long> lopHocPhanIds;

	public DanhSachLop()
	{
		this.lopHocPhanIds = new ArrayList<>();
	}

	public DanhSachLop(Long id, String tenDanhSach, Long nguoiTaoId, Integer laCongKhai,
			Long hocKyId, LocalDateTime ngayTao, List<Long> lopHocPhanIds)
	{
		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTaoId = nguoiTaoId;
		this.laCongKhai = laCongKhai;
		this.hocKyId = hocKyId;
		this.ngayTao = ngayTao;
		this.lopHocPhanIds = (lopHocPhanIds == null) ? new ArrayList<>() : new ArrayList<>(lopHocPhanIds);
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

	public List<Long> getLopHocPhanIds()
	{
		return new ArrayList<>(lopHocPhanIds);
	}

	public void setLopHocPhanIds(List<Long> lopHocPhanIds)
	{
		this.lopHocPhanIds = (lopHocPhanIds == null) ? new ArrayList<>() : new ArrayList<>(lopHocPhanIds);
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		DanhSachLop that = (DanhSachLop) o;
		return Objects.equals(id, that.id);
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
				", lopHocPhanIds=" + lopHocPhanIds +
				'}';
	}
}