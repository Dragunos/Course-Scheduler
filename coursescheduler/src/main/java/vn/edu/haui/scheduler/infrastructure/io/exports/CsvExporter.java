package vn.edu.haui.scheduler.infrastructure.io.exports;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvExporter
{

	private final String separator = ",";

	public void export(Path outputPath, List<String> headers, List<Map<String, String>> rows) throws IOException
	{
		if(outputPath.getParent() != null) {
			Files.createDirectories(outputPath.getParent());
		}
		try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
			writer.write(headers.stream().collect(Collectors.joining(separator)));
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

	private String escapeCsv(String value)
	{
		if(value == null) return "";
		boolean needQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
		String v = value.replace("\"", "\"\"");
		if(needQuote) {
			return "\"" + v + "\"";
		}
		else {
			return v;
		}
	}
}
