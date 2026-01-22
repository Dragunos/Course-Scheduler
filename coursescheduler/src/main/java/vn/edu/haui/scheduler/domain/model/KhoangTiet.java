package vn.edu.haui.scheduler.domain.model;

public class KhoangTiet
{
	private final int tietBatDau;

	private final int tietKetThuc;

	public KhoangTiet(int tietBatDau, int tietKetThuc)
	{
		if(tietBatDau > tietKetThuc) {
			throw new IllegalArgumentException();
		}
		this.tietBatDau = tietBatDau;
		this.tietKetThuc = tietKetThuc;
	}

	public int getTietBatDau()
	{
		return tietBatDau;
	}

	public int getTietKetThuc()
	{
		return tietKetThuc;
	}
}
