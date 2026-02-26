package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.LopHocPhan;

import java.util.List;

public interface SoftConstraint
{
	double evaluate(List<LopHocPhan> solution);
}