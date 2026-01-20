package vn.edu.haui.scheduler.application.auth;

import vn.edu.haui.scheduler.domain.model.NguoiDung;

public class AuthSession
{
	private NguoiDung currentUser;

	public void login(NguoiDung user)
	{
		this.currentUser = user;
	}

	public void logout()
	{
		this.currentUser = null;
	}

	public boolean isAuthenticated()
	{
		return currentUser != null;
	}

	public NguoiDung getCurrentUser()
	{
		return currentUser;
	}
}
