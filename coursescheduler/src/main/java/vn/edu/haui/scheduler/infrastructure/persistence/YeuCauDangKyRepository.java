package vn.edu.haui.scheduler.infrastructure.persistence;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;
import vn.edu.haui.scheduler.domain.model.YeuCauDangKy;
import vn.edu.haui.scheduler.domain.constraint.RangBuocToiUu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class YeuCauDangKyRepository
{
	private final LopHocPhanRepository lopRepo = new LopHocPhanRepository();

	private final RangBuocToiUuRepository rangBuocRepo = new RangBuocToiUuRepository();

	public YeuCauDangKy taiYeuCauChoOptimizer(
			Connection conn,
			long yeuCauId) throws SQLException
	{
		List<LopHocPhan> lopHocPhan = lopRepo.layTatCaLopHocPhan(conn);

		List<RangBuocToiUu> rangBuoc = rangBuocRepo.layRangBuocTheoYeuCau(conn, yeuCauId);

		return new YeuCauDangKy(
				yeuCauId,
				lopHocPhan,
				rangBuoc);
	}
}
