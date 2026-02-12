package vn.edu.haui.scheduler.application.port.out;

import java.util.List;
import vn.edu.haui.scheduler.infrastructure.io.imports.ImportedLopRow;

public interface LichHocRepositoryPort
{
	void saveAll(int lopHocPhanId, List<ImportedLopRow.Buoi> buois) throws Exception;
}