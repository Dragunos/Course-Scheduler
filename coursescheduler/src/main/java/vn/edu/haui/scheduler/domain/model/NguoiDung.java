package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;

public class NguoiDung
{
	private Integer id;

	private String tenDangNhap;

	private String matKhauHash;

	private Integer vaiTroId;

	private LocalDateTime ngayTao;

	public NguoiDung(String tenDangNhap, String matKhauHash, Integer vaiTroId)
	{
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.vaiTroId = vaiTroId;
	}

	public NguoiDung(Integer id, String tenDangNhap, String matKhauHash, Integer vaiTroId, LocalDateTime ngayTao)
	{
		this.id = id;
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.vaiTroId = vaiTroId;
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

	public String getMatKhauHash()
	{
		return matKhauHash;
	}

	public Integer getVaiTroId()
	{
		return vaiTroId;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}
}
