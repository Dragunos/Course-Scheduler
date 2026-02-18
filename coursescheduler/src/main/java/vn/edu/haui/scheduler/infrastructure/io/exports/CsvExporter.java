package vn.edu.haui.scheduler.infrastructure.io.exports;

import vn.edu.haui.scheduler.application.port.out.FileExporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvExporter implements FileExporter
{

	private final String separator = ",";

	@Override
	public void exportCsv(Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows) throws IOException
	{

		if(outputPath.getParent() != null) {
			Files.createDirectories(outputPath.getParent());
		}

		try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

			writer.write("\uFEFF");

			writer.write(String.join(separator, headers));
			writer.newLine();

			for(Map<String, String> row : rows) {

				String line = headers.stream()
						.map(h -> escapeCsv(row.getOrDefault(h, "")))
						.collect(Collectors.joining(separator));

				writer.write(line);
				writer.newLine();
			}

			writer.flush();
		}
	}

	@Override
	public void exportPdf(Path outputPath,
			String title,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		throw new UnsupportedOperationException(
				"Use PdfExporter adapter instead");
	}

	@Override
	public void exportExcel(Path outputPath,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		throw new UnsupportedOperationException("Use ExcelExporter adapter instead");
	}

	private String escapeCsv(String value)
	{
		if(value == null) return "";

		boolean needQuote = value.contains(",") ||
				value.contains("\"") ||
				value.contains("\n") ||
				value.contains("\r");

		String v = value.replace("\"", "\"\"");

		return needQuote ? "\"" + v + "\"" : v;
	}
}
