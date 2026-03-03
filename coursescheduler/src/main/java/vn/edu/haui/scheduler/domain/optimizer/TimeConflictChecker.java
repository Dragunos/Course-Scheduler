package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LichHoc;
import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.List;

public final class TimeConflictChecker
{

	public boolean hasConflict(List<LopHocPhan> sections)
	{
		for(int i = 0; i < sections.size(); i++) {
			for(int j = i + 1; j < sections.size(); j++) {
				if(conflict(sections.get(i), sections.get(j))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean conflict(LopHocPhan a, LopHocPhan b)
	{
		for(LichHoc la : a.getLichHocList()) {
			for(LichHoc lb : b.getLichHocList()) {
				if(la.getThu() == lb.getThu()) {
					boolean overlap = la.getTietBatDau() <= lb.getTietKetThuc() &&
							lb.getTietBatDau() <= la.getTietKetThuc();

					if(overlap) return true;
				}
			}
		}

		return false;
	}
}