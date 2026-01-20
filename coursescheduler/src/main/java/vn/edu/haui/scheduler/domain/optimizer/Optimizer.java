package vn.edu.haui.scheduler.domain.optimizer;

import vn.edu.haui.scheduler.domain.model.YeuCauDangKy;
import vn.edu.haui.scheduler.domain.optimizer.OptimizationResult;
import java.time.Duration;

public interface Optimizer
{
	OptimizationResult optimize(YeuCauDangKy request, OptimizationConfig config);
}
