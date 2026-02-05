package vn.edu.haui.scheduler.application.dto;

import java.time.LocalDateTime;

public class NguoiDungDto
{
	private final Integer id;

	private final String tenDangNhap;

	private final String vaiTro;

	private final LocalDateTime ngayTao;

	public NguoiDungDto(Integer id, String tenDangNhap, String vaiTro, LocalDateTime ngayTao)
	{
		this.id = id;
		this.tenDangNhap = tenDangNhap;
		this.vaiTro = vaiTro;
		this.ngayTao = ngayTao;
	}

	public Integer getId()
	{
		return id;
	}

	public String getTenDangNhap()
	{
		return tenDangNhap;
	}

	public String getVaiTro()
	{
		return vaiTro;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}
}
