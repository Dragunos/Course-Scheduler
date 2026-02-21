package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;

public class NguoiDungDto
{
	private Long id;

	private String tenDangNhap;

	private String matKhauHash;

	private Long roleId;

	private LocalDateTime ngayTao;

	public NguoiDungDto()
	{
	}

	public NguoiDungDto(Long id,
			String tenDangNhap,
			String matKhauHash,
			Long roleId,
			LocalDateTime ngayTao)
	{
		this.id = id;
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.roleId = roleId;
		this.ngayTao = ngayTao;
	}

	public Long getId()
	{
		return id;
	}

	public String getTenDangNhap()
	{
		return tenDangNhap;
	}

	public String getMatKhauHash()
	{
		return matKhauHash;
	}

	public Long getRoleId()
	{
		return roleId;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setTenDangNhap(String tenDangNhap)
	{
		this.tenDangNhap = tenDangNhap;
	}

	public void setMatKhauHash(String matKhauHash)
	{
		this.matKhauHash = matKhauHash;
	}

	public void setRoleId(Long roleId)
	{
		this.roleId = roleId;
	}

	public void setNgayTao(LocalDateTime ngayTao)
	{
		this.ngayTao = ngayTao;
	}
}