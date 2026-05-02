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
    private ProductoDAO productoDAO;
    private LoteDAO loteDAO;

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

        // Panel superior: selección de producto y formulario
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));

        // ---- Selección de producto ----
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar Producto"));
        panelSeleccion.add(new JLabel("Producto:"));
        cmbProductos = new JComboBox<>();
        cmbProductos.addActionListener(e -> cargarLotes());
        panelSeleccion.add(cmbProductos);
        panelNorte.add(panelSeleccion, BorderLayout.NORTH);

        // ---- Formulario de registro de lote ----
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Lote"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Código Lote
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Código Lote:"), gbc);
        txtCodigoLote = new JTextField(15);
        gbc.gridx = 1;
        panelFormulario.add(txtCodigoLote, gbc);

        // Fecha Ingreso
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Fecha Ingreso:"), gbc);
        spnFechaIngreso = new JSpinner(new SpinnerDateModel());
        spnFechaIngreso.setEditor(new JSpinner.DateEditor(spnFechaIngreso, "yyyy-MM-dd"));
        gbc.gridx = 1;
        panelFormulario.add(spnFechaIngreso, gbc);

        // Fecha Vencimiento
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Fecha Vencimiento:"), gbc);
        spnFechaVencimiento = new JSpinner(new SpinnerDateModel());
        spnFechaVencimiento.setEditor(new JSpinner.DateEditor(spnFechaVencimiento, "yyyy-MM-dd"));
        gbc.gridx = 1;
        panelFormulario.add(spnFechaVencimiento, gbc);

        // Cantidad Inicial
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Cantidad Inicial:"), gbc);
        txtCantidad = new JTextField(15);
        gbc.gridx = 1;
        panelFormulario.add(txtCantidad, gbc);

        // Costo Unitario (en Quetzales)
        gbc.gridx = 0; gbc.gridy = 4;
        panelFormulario.add(new JLabel("Costo Unitario (Q):"), gbc);
        txtCosto = new JTextField(15);
        gbc.gridx = 1;
        panelFormulario.add(txtCosto, gbc);

        // Botón Registrar (sin personalizaciones para que sea visible)
        btnAgregar = new JButton("Registrar Lote");
        btnAgregar.addActionListener(e -> agregarLote());
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelFormulario.add(btnAgregar, gbc);

        panelNorte.add(panelFormulario, BorderLayout.CENTER);
        add(panelNorte, BorderLayout.NORTH);

        // ---- Tabla de lotes ----
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
        modeloTabla.addColumn("Costo Unitario (Q)");

        tablaLotes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaLotes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lotes del Producto"));
        add(scrollPane, BorderLayout.CENTER);

        // ---- Botón Actualizar (abajo) ----
        JPanel panelBotonActualizar = new JPanel(new FlowLayout());
        btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.addActionListener(e -> cargarLotes());
        panelBotonActualizar.add(btnActualizar);
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
                    String.format("Q %.2f", l.getCostoUnitario())  // Cambiado de $ a Q
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
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
                return;
            }
            if (costo <= 0) {
                JOptionPane.showMessageDialog(this, "El costo debe ser mayor a 0");
                return;
            }

            Lote lote = new Lote();
            lote.setIdProducto(productoSeleccionado.getIdProducto());
            lote.setCodigoLote(txtCodigoLote.getText().trim());
            lote.setFechaIngreso(new Date(((java.util.Date) spnFechaIngreso.getValue()).getTime()));
            lote.setFechaVencimiento(new Date(((java.util.Date) spnFechaVencimiento.getValue()).getTime()));
            lote.setCantidadInicial(cantidad);
            lote.setCostoUnitario(costo);

            if (loteDAO.insertarLote(lote)) {
                JOptionPane.showMessageDialog(this, "Lote registrado exitosamente");
                limpiarCampos();
                cargarLotes();
                cargarProductos();  // Para refrescar el stock del producto en el combo
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el lote. Verifique que el código no esté duplicado.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos para cantidad y costo.");
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