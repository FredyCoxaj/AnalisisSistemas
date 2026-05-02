package miumg.edu.gt.controlinventariopeps.vista;

import miumg.edu.gt.controlinventariopeps.dao.LoteDAO;
import miumg.edu.gt.controlinventariopeps.dao.ProductoDAO;
import miumg.edu.gt.controlinventariopeps.modelo.Lote;
import miumg.edu.gt.controlinventariopeps.modelo.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PanelProductos extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private ProductoDAO productoDAO;
    private LoteDAO loteDAO;

    private JTextField txtNombre, txtDescripcion, txtPrecio, txtStock;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar;

    public PanelProductos() {
        productoDAO = new ProductoDAO();
        loteDAO = new LoteDAO();
        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(txtDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Precio Unitario:"), gbc);
        txtPrecio = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(txtPrecio, gbc);

        // Campo Stock editable (solo para nuevo producto)
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Stock Inicial:"), gbc);
        txtStock = new JTextField("0", 20);
        gbc.gridx = 1;
        panelFormulario.add(txtStock, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarProductos());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        // Tabla
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Descripción");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Stock");

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarProducto();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));

        add(panelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoDAO.listarProductos();
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{
                p.getIdProducto(),
                p.getNombreProducto(),
                p.getDescripcion(),
                String.format("$%.2f", p.getPrecioUnitario()),
                p.getStockTotal()
            });
        }
    }

    private void agregarProducto() {
        if (!validarCampos()) return;
        
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stockInicial = Integer.parseInt(txtStock.getText().trim());
            if (stockInicial < 0) {
                JOptionPane.showMessageDialog(this, "El stock inicial no puede ser negativo");
                return;
            }

            Producto producto = new Producto();
            producto.setNombreProducto(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecioUnitario(precio);

            if (productoDAO.insertarProducto(producto)) {
                int idProducto = producto.getIdProducto();
                // Si el usuario puso stock inicial > 0, crear un lote automático
                if (stockInicial > 0) {
                    Lote lote = new Lote();
                    lote.setIdProducto(idProducto);
                    lote.setCodigoLote("AUTO-" + idProducto);  // código automático
                    lote.setFechaIngreso(Date.valueOf(LocalDate.now()));
                    lote.setFechaVencimiento(null);  // sin vencimiento
                    lote.setCantidadInicial(stockInicial);
                    lote.setCostoUnitario(precio);
                    
                    if (loteDAO.insertarLote(lote)) {
                        JOptionPane.showMessageDialog(this, "Producto agregado con stock inicial de " + stockInicial + " unidades.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Producto creado pero no se pudo crear el lote automático. Deberá agregar lotes manualmente.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Producto agregado sin stock inicial.");
                }
                limpiarCampos();
                cargarProductos();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos para precio y stock.");
        }
    }

    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar");
            return;
        }
        if (!validarCampos()) return;
        
        int idProducto = (int) modeloTabla.getValueAt(fila, 0);
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setNombreProducto(txtNombre.getText().trim());
        producto.setDescripcion(txtDescripcion.getText().trim());
        producto.setPrecioUnitario(Double.parseDouble(txtPrecio.getText()));
        // No se modifica el stock total aquí (se mantiene el existente)
        
        if (productoDAO.actualizarProducto(producto)) {
            JOptionPane.showMessageDialog(this, "Producto actualizado. El stock no se modifica desde aquí.");
            limpiarCampos();
            cargarProductos();
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
            return;
        }
        int idProducto = (int) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar producto '" + nombre + "'? Se eliminarán también sus lotes (si no tienen movimientos).",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productoDAO.eliminarProducto(idProducto)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado.");
                limpiarCampos();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar. El producto podría tener ventas asociadas.");
            }
        }
    }

    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
            txtDescripcion.setText((String) modeloTabla.getValueAt(fila, 2));
            String precio = ((String) modeloTabla.getValueAt(fila, 3)).replace("$", "");
            txtPrecio.setText(precio);
            int stock = (int) modeloTabla.getValueAt(fila, 4);
            txtStock.setText(String.valueOf(stock));  // Mostrar stock actual (solo lectura visual)
            txtStock.setEditable(false);  // No permitir editar stock en productos existentes
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del producto");
            return false;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            if (precio < 0) {
                JOptionPane.showMessageDialog(this, "El precio no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio válido");
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("0");
        txtStock.setEditable(true);  // Para nuevos productos se puede escribir
        tablaProductos.clearSelection();
    }
}