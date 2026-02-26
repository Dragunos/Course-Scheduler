package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.List;

public interface HardConstraint
{
	boolean isSatisfied(List<LopHocPhan> partialSolution);
}