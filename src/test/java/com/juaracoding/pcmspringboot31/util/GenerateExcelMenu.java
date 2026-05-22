package com.juaracoding.pcmspringboot31.util;

import com.juaracoding.pcmspringboot31.dto.validation.ValMenuDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GenerateExcelMenu {
    /**
     * Fungsi untuk meng-generate file Excel dari data RepKategoriDTO.
     * @param data       List data RepKategoriDTO
     * @param kolom      Array string untuk header kolom
     * @return String path lengkap file Excel yang berhasil dibuat
     */
    public static String generateDataExcel(List<ValMenuDTO> data, String[] kolom) {
        String fileName = "menu.xlsx";
        Path outputDir = Paths.get(System.getProperty("user.dir"),"src","test","resources", "data-test");
        System.out.println("Output dir: " + outputDir);
        Path filePath = outputDir.resolve(fileName);

        try {
            // Buat folder /excel jika belum ada, agar FileOutputStream tidak melempar FileNotFoundException
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat direktori output: " + e.getMessage(), e);
        }

        // 2. Inisialisasi Workbook dan Sheet
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // 3. Setup CellStyle untuk Header (Opsional, agar lebih rapi)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 4. Setup CellStyle untuk LocalDate
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

            // 5. Buat Row Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < kolom.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(kolom[i]);
                cell.setCellStyle(headerStyle);
            }

            // 6. Isi Data ke dalam Grid
            // Memastikan tidak terjadi IndexOutOfBounds jika jumlahData > data.size()
            int limit = data.size();

            for (int i = 0; i < limit; i++) {
                ValMenuDTO valMenuDTO = data.get(i);
                Row row = sheet.createRow(i + 1); // +1 karena index 0 dipakai header

                row.createCell(0).setCellValue(valMenuDTO.getNama() != null ? valMenuDTO.getNama() : "");
                row.createCell(1).setCellValue(valMenuDTO.getPath() != null ? valMenuDTO.getPath() : "");
                row.createCell(2).setCellValue(valMenuDTO.getDeskripsi() != null ? valMenuDTO.getDeskripsi() : "");
                row.createCell(3).setCellValue(valMenuDTO.getKodeMenu() != null ? valMenuDTO.getKodeMenu() : "");

                // Handling LocalDate
//                Cell dateCell = row.createCell(4);
//                if (repKategoriDTO.getTanggalLahir() != null) {
//                    dateCell.setCellValue(repKategoriDTO.getTanggalLahir());
//                    dateCell.setCellStyle(dateStyle);
//                } else {
//                    dateCell.setCellValue("");
//                }
            }

            // 7. Auto-size kolom agar lebar kolom menyesuaikan isi (Opsional)
            for (int i = 0; i < kolom.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 8. Tulis file ke storage
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                workbook.write(fileOut);
            }
            return filePath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Terjadi kesalahan saat meng-generate file Excel: " + e.getMessage(), e);
        }
    }
}