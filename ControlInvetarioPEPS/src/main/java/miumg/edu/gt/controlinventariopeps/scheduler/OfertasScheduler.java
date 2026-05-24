package com.inventario.scheduler;

import com.inventario.model.Lote;
import com.inventario.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OfertaScheduler {
    
    @Autowired
    private LoteRepository loteRepository;
    
    /**
     * Tarea programada que se ejecuta todos los días a las 2:00 AM
     * Busca lotes que caducan en menos de 3 meses (90 días) y los pone en oferta con 5% de descuento
     */
    @Scheduled(cron = "0 0 2 * * ?") // Cada día a las 2 AM
    @Transactional
    public void actualizarOfertasPorCaducidad() {
        System.out.println("=== INICIANDO ACTUALIZACIÓN AUTOMÁTICA DE OFERTAS ===");
        System.out.println("Fecha y hora: " + LocalDateTime.now());
        
        LocalDate fechaLimite = LocalDate.now().plusDays(90);
        List<Lote> lotesPorCaducar = loteRepository.findByFechaCaducidadBeforeAndEnOfertaFalse(fechaLimite);
        
        int contador = 0;
        for (Lote lote : lotesPorCaducar) {
            lote.setEnOferta(true);
            lote.setPorcentajeDescuentoOferta(new BigDecimal("5.00"));
            loteRepository.save(lote);
            contador++;
            
            System.out.println("✓ Lote ID: " + lote.getIdLote() + 
                             " | Producto: " + lote.getProducto().getNombre() +
                             " | Caducidad: " + lote.getFechaCaducidad() +
                             " | Descuento aplicado: 5%");
        }
        
        System.out.println("=== ACTUALIZACIÓN FINALIZADA ===");
        System.out.println("Total de lotes puestos en oferta: " + contador);
    }
    
    /**
     * Tarea programada que se ejecuta todos los lunes a las 1:00 AM
     * Elimina ofertas de lotes que ya caducaron
     */
    @Scheduled(cron = "0 0 1 * * MON") // Todos los lunes a la 1 AM
    @Transactional
    public void removerOfertasCaducadas() {
        System.out.println("=== INICIANDO LIMPIEZA DE OFERTAS CADUCADAS ===");
        System.out.println("Fecha y hora: " + LocalDateTime.now());
        
        LocalDate fechaActual = LocalDate.now();
        List<Lote> lotesCaducados = loteRepository.findByFechaCaducidadBeforeAndEnOfertaTrue(fechaActual);
        
        int contador = 0;
        for (Lote lote : lotesCaducados) {
            lote.setEnOferta(false);
            lote.setPorcentajeDescuentoOferta(BigDecimal.ZERO);
            loteRepository.save(lote);
            contador++;
            
            System.out.println("✓ Oferta removida - Lote ID: " + lote.getIdLote() + 
                             " | Producto: " + lote.getProducto().getNombre() +
                             " | Caducó el: " + lote.getFechaCaducidad());
        }
        
        System.out.println("=== LIMPIEZA FINALIZADA ===");
        System.out.println("Total de ofertas removidas: " + contador);
    }
    
    /**
     * Tarea programada que se ejecuta cada 6 horas
     * Registra un log del estado actual de los lotes en oferta
     */
    @Scheduled(cron = "0 0 */6 * * *") // Cada 6 horas
    public void logEstadoOfertas() {
        List<Lote> lotesEnOferta = loteRepository.findByEnOfertaTrueAndActivoTrue();
        
        System.out.println("=== ESTADO ACTUAL DE OFERTAS ===");
        System.out.println("Fecha y hora: " + LocalDateTime.now());
        System.out.println("Total de lotes en oferta: " + lotesEnOferta.size());
        
        for (Lote lote : lotesEnOferta) {
            System.out.println("  - Lote " + lote.getIdLote() + ": " + 
                             lote.getProducto().getNombre() + 
                             " | Stock: " + lote.getCantidadDisponible() +
                             " | Descuento: " + lote.getPorcentajeDescuentoOferta() + "%");
        }
        System.out.println("=================================");
    }
}