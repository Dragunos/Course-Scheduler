package vn.edu.haui.scheduler.domain.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class PhuongAnThoiKhoaBieu
{
	private final Set<LopHocPhan> cacLopDuocChon;

	private final double diemDanhGia;

	public PhuongAnThoiKhoaBieu(
			Set<LopHocPhan> cacLopDuocChon,
			double diemDanhGia)
	{
		this.cacLopDuocChon = Collections.unmodifiableSet(
				Set.copyOf(
						Objects.requireNonNull(cacLopDuocChon)));

		this.diemDanhGia = diemDanhGia;
	}

	public Set<LopHocPhan> getCacLopDuocChon()
	{
		return cacLopDuocChon;
	}

	public double getDiemDanhGia()
	{
		return diemDanhGia;
	}
}
