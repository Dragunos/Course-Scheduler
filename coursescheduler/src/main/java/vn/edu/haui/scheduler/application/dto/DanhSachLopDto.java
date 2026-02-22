package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DanhSachLopDto
{
	private Long id;

	private String tenDanhSach;

	private Long nguoiTaoId;

	private Boolean laCongKhai;

	private Long hocKyId;

	private LocalDateTime ngayTao;
	
	private List<DanhSachLopChiTietDto> chiTiet;

	public DanhSachLopDto()
	{
	}

	public DanhSachLopDto(Long id,
			String tenDanhSach,
			Long nguoiTaoId,
			Boolean laCongKhai,
			Long hocKyId,
			LocalDateTime ngayTao)
	{
		this.id = id;
		this.tenDanhSach = tenDanhSach;
		this.nguoiTaoId = nguoiTaoId;
		this.laCongKhai = laCongKhai;
		this.hocKyId = hocKyId;
		this.ngayTao = ngayTao;
	}

	public Long getId()
	{
		return id;
	}

	public String getTenDanhSach()
	{
		return tenDanhSach;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public Boolean getLaCongKhai()
	{
		return laCongKhai;
	}

	public Long getHocKyId()
	{
		return hocKyId;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setTenDanhSach(String tenDanhSach)
	{
		this.tenDanhSach = tenDanhSach;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}

	public void setLaCongKhai(Boolean laCongKhai)
	{
		this.laCongKhai = laCongKhai;
	}

	public void setHocKyId(Long hocKyId)
	{
		this.hocKyId = hocKyId;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}
	
	public List<DanhSachLopChiTietDto> getChiTiet()
	{
	    return chiTiet;
	}

	public void setChiTiet(List<DanhSachLopChiTietDto> chiTiet)
	{
	    this.chiTiet = chiTiet;
	}
}