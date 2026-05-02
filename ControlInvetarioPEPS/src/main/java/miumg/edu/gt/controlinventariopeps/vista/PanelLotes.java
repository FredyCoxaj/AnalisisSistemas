package miumg.edu.gt.controlinventariopeps.vista;

import miumg.edu.gt.controlinventariopeps.dao.LoteDAO;
import miumg.edu.gt.controlinventariopeps.dao.ProductoDAO;
import miumg.edu.gt.controlinventariopeps.modelo.Lote;
import miumg.edu.gt.controlinventariopeps.modelo.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class PanelLotes extends JPanel {
    private JComboBox<Producto> cmbProductos;
    private JTable tablaLotes;
    private DefaultTableModel modeloTabla;
    private final ProductoDAO productoDAO;
    private final LoteDAO loteDAO;

    private JTextField txtCodigoLote, txtCantidad, txtCosto;
    private JSpinner spnFechaIngreso, spnFechaVencimiento;
    private JButton btnAgregar, btnActualizar;

    public PanelLotes() {
        productoDAO = new ProductoDAO();
        loteDAO = new LoteDAO();
        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel selección producto
        JPanel panelSeleccion = new JPanel(new FlowLayout());
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar Producto"));
        panelSeleccion.add(new JLabel("Producto:"));
        cmbProductos = new JComboBox<>();
        cmbProductos.addActionListener(e -> cargarLotes());
        panelSeleccion.add(cmbProductos);

        // Panel formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Lote"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Código Lote:"), gbc);
        txtCodigoLote = new JTextField(15);
        gbc.gridx = 1;
        panelFormulario.add(txtCodigoLote, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Fecha Ingreso:"), gbc);
        spnFechaIngreso = new JSpinner(new SpinnerDateModel());
        spnFechaIngreso.setEditor(new JSpinner.DateEditor(spnFechaIngreso, "yyyy-MM-dd"));
        gbc.gridx = 1;
        panelFormulario.add(spnFechaIngreso, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Fecha Vencimiento:"), gbc);
        spnFechaVencimiento = new JSpinner(new SpinnerDateModel());
        spnFechaVencimiento.setEditor(new JSpinner.DateEditor(spnFechaVencimiento, "yyyy-MM-dd"));
        gbc.gridx = 1;
        panelFormulario.add(spnFechaVencimiento, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Cantidad Inicial:"), gbc);
        txtCantidad = new JTextField(15);
        gbc.gridx = 1;
        panelFormulario.add(txtCantidad, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Costo Unitario:"), gbc);
        txtCosto = new JTextField(15);
        gbc.gridx = 1;
        panelFormulario.add(txtCosto, gbc);

        btnAgregar = new JButton("Registrar Lote");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> agregarLote());
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelFormulario.add(btnAgregar, gbc);

        // Tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("ID Lote");
        modeloTabla.addColumn("Código");
        modeloTabla.addColumn("F. Ingreso");
        modeloTabla.addColumn("F. Vencimiento");
        modeloTabla.addColumn("Cant. Inicial");
        modeloTabla.addColumn("Stock Actual");
        modeloTabla.addColumn("Costo Unitario");

        tablaLotes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaLotes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lotes del Producto"));

        JPanel panelBotonActualizar = new JPanel(new FlowLayout());
        btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarLotes());
        panelBotonActualizar.add(btnActualizar);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelSeleccion, BorderLayout.NORTH);
        panelNorte.add(panelFormulario, BorderLayout.CENTER);

        add(panelNorte, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotonActualizar, BorderLayout.SOUTH);
    }

    private void cargarProductos() {
        cmbProductos.removeAllItems();
        List<Producto> productos = productoDAO.listarProductos();
        for (Producto p : productos) {
            cmbProductos.addItem(p);
        }
    }

    private void cargarLotes() {
        modeloTabla.setRowCount(0);
        Producto productoSeleccionado = (Producto) cmbProductos.getSelectedItem();
        if (productoSeleccionado != null) {
            List<Lote> lotes = loteDAO.listarLotesPorProducto(productoSeleccionado.getIdProducto());
            for (Lote l : lotes) {
                modeloTabla.addRow(new Object[]{
                    l.getIdLote(),
                    l.getCodigoLote(),
                    l.getFechaIngreso(),
                    l.getFechaVencimiento(),
                    l.getCantidadInicial(),
                    l.getStockActual(),
                    String.format("$%.2f", l.getCostoUnitario())
                });
            }
        }
    }

    private void agregarLote() {
        Producto productoSeleccionado = (Producto) cmbProductos.getSelectedItem();
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }
        if (txtCodigoLote.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del lote");
            return;
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            double costo = Double.parseDouble(txtCosto.getText());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
                return;
            }
            if (costo < 0) {
                JOptionPane.showMessageDialog(this, "El costo no puede ser negativo");
                return;
            }

            Lote lote = new Lote();
            lote.setIdProducto(productoSeleccionado.getIdProducto());
            lote.setCodigoLote(txtCodigoLote.getText());
            lote.setFechaIngreso(new Date(((java.util.Date) spnFechaIngreso.getValue()).getTime()));
            lote.setFechaVencimiento(new Date(((java.util.Date) spnFechaVencimiento.getValue()).getTime()));
            lote.setCantidadInicial(cantidad);
            lote.setCostoUnitario(costo);

            if (loteDAO.insertarLote(lote)) {
                JOptionPane.showMessageDialog(this, "Lote registrado exitosamente");
                limpiarCampos();
                cargarLotes();
                cargarProductos();  // para refrescar stock en combo
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
        }
    }

    private void limpiarCampos() {
        txtCodigoLote.setText("");
        txtCantidad.setText("");
        txtCosto.setText("");
        spnFechaIngreso.setValue(new java.util.Date());
        spnFechaVencimiento.setValue(new java.util.Date());
    }
}