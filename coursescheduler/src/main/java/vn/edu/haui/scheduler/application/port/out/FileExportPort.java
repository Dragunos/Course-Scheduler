package vn.edu.haui.scheduler.application.port.out;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FileExportPort
{

	void exportCsv(
			Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException;

	void exportPdf(
			Path outputPath,
			String title,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException;

	void exportExcel(
			Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException;
}
