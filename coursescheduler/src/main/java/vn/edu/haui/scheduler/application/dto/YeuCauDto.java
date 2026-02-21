package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;

public class YeuCauDto
{
	private Long id;

	private Long nguoiTaoId;

	private Long danhSachLopId;

	private String tenYeuCau;

	private LocalDateTime ngayTao;

	public YeuCauDto()
	{
	}

	public Long getId()
	{
		return id;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public String getTenYeuCau()
	{
		return tenYeuCau;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public void setTenYeuCau(String tenYeuCau)
	{
		this.tenYeuCau = tenYeuCau;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}
}