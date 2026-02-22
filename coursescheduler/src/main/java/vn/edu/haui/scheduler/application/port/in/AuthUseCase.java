package vn.edu.haui.scheduler.application.port.in;

import vn.edu.haui.scheduler.application.dto.NguoiDungDto;

public interface AuthUseCase
{
	NguoiDungDto register(String tenDangNhap, String matKhau);

	NguoiDungDto login(String tenDangNhap, String matKhau);
}