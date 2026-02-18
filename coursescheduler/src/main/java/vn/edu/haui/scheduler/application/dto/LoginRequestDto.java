package vn.edu.haui.scheduler.application.dto;

public class LoginRequestDto
{
	private String tenDangNhap;

	private String matKhau;

	public LoginRequestDto()
	{
	}

	public LoginRequestDto(String tenDangNhap, String matKhau)
	{
		this.tenDangNhap = tenDangNhap;
		this.matKhau = matKhau;
	}

	public String getTenDangNhap()
	{
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap)
	{
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhau()
	{
		return matKhau;
	}

	public void setMatKhau(String matKhau)
	{
		this.matKhau = matKhau;
	}
}
