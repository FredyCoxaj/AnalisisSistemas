package miumg.edu.gt.controlinventariopeps.vista;

import miumg.edu.gt.controlinventariopeps.dao.ProductoDAO;
import miumg.edu.gt.controlinventariopeps.dao.VentaDAO;
import miumg.edu.gt.controlinventariopeps.modelo.ItemVenta;
import miumg.edu.gt.controlinventariopeps.modelo.Producto;
import miumg.edu.gt.controlinventariopeps.modelo.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelVentas extends JPanel {
    private final Usuario usuarioActual;
    private final ProductoDAO productoDAO;
    private final VentaDAO ventaDAO;

    private JComboBox<Producto> cmbProductos;
    private JSpinner spnCantidad;
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JTextField txtDescuento;

    private final List<ItemVenta> carrito;

    public PanelVentas(Usuario usuario) {
        this.usuarioActual = usuario;
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
        this.carrito = new ArrayList<>();
        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel agregar producto
        JPanel panelAgregar = new JPanel(new GridBagLayout());
        panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panelAgregar.add(new JLabel("Producto:"), gbc);
        cmbProductos = new JComboBox<>();
        cmbProductos.setPreferredSize(new Dimension(300, 25));
        gbc.gridx = 1;
        panelAgregar.add(cmbProductos, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelAgregar.add(new JLabel("Cantidad:"), gbc);
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        gbc.gridx = 1;
        panelAgregar.add(spnCantidad, gbc);

        JButton btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.setBackground(new Color(0, 123, 255));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelAgregar.add(btnAgregar, gbc);

        // Tabla carrito
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        modeloTabla.addColumn("ID Producto");
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Cantidad");
        modeloTabla.addColumn("Precio Unitario");
        modeloTabla.addColumn("Subtotal");

        tablaCarrito = new JTable(modeloTabla);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton btnEliminar = new JButton("Eliminar del Carrito");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarDelCarrito());

        JPanel panelCarrito = new JPanel(new BorderLayout());
        panelCarrito.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));
        panelCarrito.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
        panelCarrito.add(btnEliminar, BorderLayout.SOUTH);

        // Panel total
        JPanel panelTotal = new JPanel(new GridBagLayout());
        panelTotal.setBorder(BorderFactory.createTitledBorder("Resumen de Venta"));
        GridBagConstraints gbcTotal = new GridBagConstraints();
        gbcTotal.insets = new Insets(5, 5, 5, 5);

        gbcTotal.gridx = 0; gbcTotal.gridy = 0;
        panelTotal.add(new JLabel("Descuento:"), gbcTotal);
        txtDescuento = new JTextField("0", 10);
        gbcTotal.gridx = 1;
        panelTotal.add(txtDescuento, gbcTotal);

        gbcTotal.gridx = 0; gbcTotal.gridy = 1;
        panelTotal.add(new JLabel("Total:"), gbcTotal);
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(40, 167, 69));
        gbcTotal.gridx = 1;
        panelTotal.add(lblTotal, gbcTotal);

        JButton btnFinalizar = new JButton("Finalizar Venta");
        btnFinalizar.setBackground(new Color(40, 167, 69));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFinalizar.addActionListener(e -> finalizarVenta());
        gbcTotal.gridx = 0; gbcTotal.gridy = 2;
        gbcTotal.gridwidth = 2;
        panelTotal.add(btnFinalizar, gbcTotal);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(panelAgregar, BorderLayout.NORTH);
        panelDerecho.add(panelTotal, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCarrito, panelDerecho);
        splitPane.setDividerLocation(600);

        add(splitPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnLimpiar = new JButton("Limpiar Carrito");
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        panelBotones.add(btnLimpiar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarProductos() {
        cmbProductos.removeAllItems();
        List<Producto> productos = productoDAO.listarProductos();
        for (Producto p : productos) {
            if (p.getStockTotal() > 0) {
                cmbProductos.addItem(p);
            }
        }
    }

    private void agregarAlCarrito() {
        Producto producto = (Producto) cmbProductos.getSelectedItem();
        int cantidad = (int) spnCantidad.getValue();
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }
        if (cantidad > producto.getStockTotal()) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente. Stock actual: " + producto.getStockTotal());
            return;
        }
        // Buscar si ya existe
        for (ItemVenta item : carrito) {
            if (item.getIdProducto() == producto.getIdProducto()) {
                if (item.getCantidad() + cantidad > producto.getStockTotal()) {
                    JOptionPane.showMessageDialog(this, "La cantidad total excede el stock disponible");
                    return;
                }
                item.setCantidad(item.getCantidad() + cantidad);
                actualizarTablaCarrito();
                actualizarTotal();
                return;
            }
        }
        ItemVenta nuevoItem = new ItemVenta();
        nuevoItem.setIdProducto(producto.getIdProducto());
        nuevoItem.setNombreProducto(producto.getNombreProducto());
        nuevoItem.setCantidad(cantidad);
        nuevoItem.setPrecioUnitario(producto.getPrecioUnitario());
        carrito.add(nuevoItem);
        actualizarTablaCarrito();
        actualizarTotal();
    }

    private void eliminarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            carrito.remove(fila);
            actualizarTablaCarrito();
            actualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito");
        }
    }

    private void limpiarCarrito() {
        carrito.clear();
        actualizarTablaCarrito();
        actualizarTotal();
        txtDescuento.setText("0");
    }

    private void actualizarTablaCarrito() {
        modeloTabla.setRowCount(0);
        for (ItemVenta item : carrito) {
            modeloTabla.addRow(new Object[]{
                item.getIdProducto(),
                item.getNombreProducto(),
                item.getCantidad(),
                String.format("$%.2f", item.getPrecioUnitario()),
                String.format("$%.2f", item.getCantidad() * item.getPrecioUnitario())
            });
        }
    }

    private void actualizarTotal() {
        double total = 0;
        for (ItemVenta item : carrito) {
            total += item.getCantidad() * item.getPrecioUnitario();
        }
        try {
            double descuento = Double.parseDouble(txtDescuento.getText());
            total -= descuento;
            if (total < 0) total = 0;
        } catch (NumberFormatException e) { }
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void finalizarVenta() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }
        double descuento = 0;
        try {
            descuento = Double.parseDouble(txtDescuento.getText());
            if (descuento < 0) {
                JOptionPane.showMessageDialog(this, "El descuento no puede ser negativo");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un descuento válido");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmar venta por total de " + lblTotal.getText() + "?",
                "Confirmar Venta", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (ventaDAO.realizarVenta(usuarioActual.getIdUsuario(), carrito, descuento)) {
                limpiarCarrito();
                cargarProductos();  // actualizar stock
                JOptionPane.showMessageDialog(this, "Venta realizada exitosamente");
            }
        }
    }
}