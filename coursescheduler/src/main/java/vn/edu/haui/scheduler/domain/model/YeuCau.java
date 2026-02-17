package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;

public class YeuCau
{
	private Long id;

	private Long nguoiTaoId;

	private Long danhSachLopId;

	private String tenYeuCau;

	private LocalDateTime ngayTao;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getNguoiTaoId()
	{
		return nguoiTaoId;
	}

	public void setNguoiTaoId(Long nguoiTaoId)
	{
		this.nguoiTaoId = nguoiTaoId;
	}

	public Long getDanhSachLopId()
	{
		return danhSachLopId;
	}

	public void setDanhSachLopId(Long danhSachLopId)
	{
		this.danhSachLopId = danhSachLopId;
	}

	public String getTenYeuCau()
	{
		return tenYeuCau;
	}

	public void setTenYeuCau(String tenYeuCau)
	{
		this.tenYeuCau = tenYeuCau;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}
}
