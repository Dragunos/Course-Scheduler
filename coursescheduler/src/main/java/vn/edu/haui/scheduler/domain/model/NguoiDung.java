package vn.edu.haui.scheduler.domain.model;

import java.time.LocalDateTime;

public class NguoiDung
{
	private Long id;

	private String tenDangNhap;

	private String matKhauHash;

	private Long vaiTroId;

	private LocalDateTime ngayTao;

	public NguoiDung(String tenDangNhap, String matKhauHash, Long vaiTroId)
	{
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.vaiTroId = vaiTroId;
	}

	public NguoiDung(Long id, String tenDangNhap, String matKhauHash, Long vaiTroId, LocalDateTime ngayTao)
	{
		this.id = id;
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.vaiTroId = vaiTroId;
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

	public Long getVaiTroId()
	{
		return vaiTroId;
	}

	public LocalDateTime getNgayTao()
	{
		return ngayTao;
	}
}
