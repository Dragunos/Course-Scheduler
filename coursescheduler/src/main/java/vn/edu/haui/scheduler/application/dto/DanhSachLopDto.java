package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DanhSachLopDto
{
	private Long id;

	private String tenDanhSach;

	private Long nguoiTaoId;

	private Integer laCongKhai;

	private Long hocKyId;

	private LocalDateTime ngayTao;

	private List<DanhSachLopChiTietDto> chiTiet;

	public DanhSachLopDto()
	{
		this.chiTiet = new ArrayList<>();
	}

	public DanhSachLopDto(Long id, String tenDanhSach, Long nguoiTaoId, Integer laCongKhai,
			Long hocKyId, LocalDateTime ngayTao, List<DanhSachLopChiTietDto> chiTiet)
	{
		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTaoId = nguoiTaoId;
		this.laCongKhai = laCongKhai;
		this.hocKyId = hocKyId;
		this.ngayTao = ngayTao;
		this.chiTiet = (chiTiet == null) ? new ArrayList<>() : new ArrayList<>(chiTiet);
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

	public List<DanhSachLopChiTietDto> getChiTiet()
	{
		return new ArrayList<>(chiTiet);
	}

	public void setChiTiet(List<DanhSachLopChiTietDto> chiTiet)
	{
		this.chiTiet = (chiTiet == null) ? new ArrayList<>() : new ArrayList<>(chiTiet);
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		DanhSachLopDto that = (DanhSachLopDto) o;
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
		return "DanhSachLopDto{" +
				"id=" + id +
				", tenDanhSach='" + tenDanhSach + '\'' +
				", nguoiTaoId=" + nguoiTaoId +
				", laCongKhai=" + laCongKhai +
				", hocKyId=" + hocKyId +
				", ngayTao=" + ngayTao +
				", chiTietSize=" + (chiTiet == null ? 0 : chiTiet.size()) +
				'}';
	}
}