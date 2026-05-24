package com.inventario.service;

import com.inventario.dto.ReporteVentasDTO;
import com.inventario.repository.DetalleVentaRepository;
import com.inventario.repository.VentaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    
    public ReporteVentasDTO generarReporteVentas(LocalDateTime inicio, LocalDateTime fin) {
        ReporteVentasDTO reporte = new ReporteVentasDTO();
        reporte.setFechaInicio(inicio);
        reporte.setFechaFin(fin);
        
        BigDecimal montoTotal = ventaRepository.sumVentasPorPeriodo(inicio, fin);
        Long totalTransacciones = ventaRepository.countVentasPorPeriodo(inicio, fin);
        
        reporte.setMontoTotal(montoTotal != null ? montoTotal : BigDecimal.ZERO);
        reporte.setTotalTransacciones(totalTransacciones != null ? totalTransacciones : 0L);
        
        if (totalTransacciones != null && totalTransacciones > 0) {
            BigDecimal promedio = reporte.getMontoTotal()
                .divide(new BigDecimal(totalTransacciones), 2, RoundingMode.HALF_UP);
            reporte.setPromedioPorVenta(promedio);
        } else {
            reporte.setPromedioPorVenta(BigDecimal.ZERO);
        }
        
        // Obtener producto más vendido
        List<Object[]> productosMasVendidos = detalleVentaRepository.findProductosMasVendidos();
        if (!productosMasVendidos.isEmpty()) {
            Object[] top = productosMasVendidos.get(0);
            reporte.setProductoMasVendido((String) top[0]);
            reporte.setCantidadProductoMasVendido(((Number) top[1]).intValue());
        }
        
        return reporte;
    }
    
    public byte[] exportarUsuariosExcel(List<com.inventario.model.Usuario> usuarios) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Usuarios");
            
            // Crear estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Encabezados
            Row header = sheet.createRow(0);
            String[] columnas = {"ID", "Nombre Completo", "Email", "Rol", "Carnet", "Teléfono", "Dirección", "Fecha Registro"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // Datos
            int rowNum = 1;
            for (com.inventario.model.Usuario u : usuarios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getIdUsuario());
                row.createCell(1).setCellValue(u.getNombreCompleto());
                row.createCell(2).setCellValue(u.getEmail());
                row.createCell(3).setCellValue(u.getRol());
                row.createCell(4).setCellValue(u.getCarnetIdentidad());
                row.createCell(5).setCellValue(u.getTelefono() != null ? u.getTelefono() : "");
                row.createCell(6).setCellValue(u.getDireccion() != null ? u.getDireccion() : "");
                row.createCell(7).setCellValue(u.getFechaRegistro() != null ? u.getFechaRegistro().toString() : "");
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }
}