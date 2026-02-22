package vn.edu.haui.scheduler.application.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import vn.edu.haui.scheduler.application.exception.EntityNotFoundException;
import vn.edu.haui.scheduler.application.exception.ExportThoiKhoaBieuException;
import vn.edu.haui.scheduler.application.exception.UnauthorizedAccessException;
import vn.edu.haui.scheduler.application.exception.ValidationException;
import vn.edu.haui.scheduler.application.port.in.ExportThoiKhoaBieuUseCase;
import vn.edu.haui.scheduler.application.port.out.ThoiKhoaBieuRepository;
import vn.edu.haui.scheduler.domain.model.*;
import vn.edu.haui.scheduler.infrastructure.io.exports.FileExporter;
import vn.edu.haui.scheduler.infrastructure.io.exports.IcsExporter;

public class ExportThoiKhoaBieuService implements ExportThoiKhoaBieuUseCase
{
	private final ThoiKhoaBieuRepository thoiKhoaBieuRepository;

	private final FileExporter fileExporter;

	private final IcsExporter icsExporter;

	public ExportThoiKhoaBieuService(
			ThoiKhoaBieuRepository thoiKhoaBieuRepository,
			FileExporter fileExporter,
			IcsExporter icsExporter)
	{
		this.thoiKhoaBieuRepository = thoiKhoaBieuRepository;
		this.fileExporter = fileExporter;
		this.icsExporter = icsExporter;
	}

	@Override
	public void export(
			Long nguoiDungId,
			Long thoiKhoaBieuId,
			String format,
			String outputPath)
	{
		if(nguoiDungId == null)
			throw new ValidationException("NguoiDungId must not be null");

		if(thoiKhoaBieuId == null)
			throw new ValidationException("ThoiKhoaBieuId must not be null");

		if(format == null || format.isBlank())
			throw new ValidationException("Format must not be blank");

		if(outputPath == null || outputPath.isBlank())
			throw new ValidationException("OutputPath must not be blank");

		ThoiKhoaBieu tkb = thoiKhoaBieuRepository
				.findById(thoiKhoaBieuId)
				.orElseThrow(() -> new EntityNotFoundException("ThoiKhoaBieu", thoiKhoaBieuId));

		if(!tkb.getNguoiDung().getId().equals(nguoiDungId))
			throw new UnauthorizedAccessException();

		List<String> headers = buildHeaders();

		List<Map<String, String>> rows = buildRows(tkb);

		Path path = Path.of(outputPath);

		performExport(format, path, headers, rows);
	}

	private List<String> buildHeaders()
	{
		return List.of(
				"MaLop",
				"MaHocPhan",
				"TenHocPhan",
				"SoTinChi",
				"GiangVien",
				"HinhThuc",
				"DiaDiem",
				"Thu",
				"TietBatDau",
				"TietKetThuc");
	}

	private List<Map<String, String>> buildRows(ThoiKhoaBieu tkb)
	{
		List<Map<String, String>> rows = new ArrayList<>();

		for(LopHocPhan lop : tkb.getCacLop()) {

			HocPhan hp = lop.getHocPhan();
			GiangVien gv = lop.getGiangVien();

			for(LichHoc lich : lop.getLichHocList()) {

				Map<String, String> row = new LinkedHashMap<>();

				row.put("ma_lop", safe(lop.getMaLop()));
				row.put("ma_hoc_phan", safe(hp.getMaHocPhan()));
				row.put("ten_hoc_phan", safe(hp.getTenHocPhan()));
				row.put("so_tin_chi", String.valueOf(hp.getSoTinChi()));
				row.put("ten_giang_vien", gv != null ? gv.getTenGiangVien() : "");
				row.put("hinh_thuc", String.valueOf(lop.getHinhThucDay()));
				row.put("dia_diem", safe(lop.getDiaDiem()));
				row.put("thu", String.valueOf(lich.getThu()));
				row.put("tiet_bat_dau", String.valueOf(lich.getTietBatDau()));
				row.put("tiet_ket_thuc", String.valueOf(lich.getTietKetThuc()));

				rows.add(row);
			}
		}

		return rows;
	}

	private void performExport(
			String format,
			Path path,
			List<String> headers,
			List<Map<String, String>> rows)
	{
		try {
			switch(format.trim().toUpperCase()) {
				case "CSV" -> fileExporter.exportCsv(path, headers, rows);
				case "PDF" -> fileExporter.exportPdf(path, "Thời khóa biểu", headers, rows);
				case "ICS" -> icsExporter.export(path, "Thời khóa biểu", rows);
				default -> throw new ValidationException("Unsupported export format");
			}
		}
		catch(IOException ex) {
			throw new ExportThoiKhoaBieuException("Failed to export timetable", ex);
		}
	}

	private String safe(String value)
	{
		return value == null ? "" : value;
	}
}