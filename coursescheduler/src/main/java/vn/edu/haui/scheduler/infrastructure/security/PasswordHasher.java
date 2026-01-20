package vn.edu.haui.scheduler.infrastructure.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher
{

	public String hash(String rawPassword)
	{
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
	}

	public boolean verify(String rawPassword, String hashed)
	{
		return BCrypt.checkpw(rawPassword, hashed);
	}
}