/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;
import Dao.ProductoDAO;
import Modelo.Producto;
import dao.*;
import modelo.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author Fredy
 */
public class PanelProductos extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private ProductoDAO productoDAO;
    
    private JTextField txtNombre, txtDescripcion, txtPrecio;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar;
    
    public PanelProductos() {
        productoDAO = new ProductoDAO();
        initComponents();
        cargarProductos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Descripción:"), gbc);
        
        txtDescripcion = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(txtDescripcion, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Precio Unitario:"), gbc);
        
        txtPrecio = new JTextField(20);
        gbc.gridx = 1;
        panelFormulario.add(txtPrecio, gbc);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.addActionListener(e -> agregarProducto());
        
        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 193, 7));
        btnEditar.addActionListener(e -> editarProducto());
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarProducto());
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarProductos());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);
        
        // Tabla de productos
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        
        // Panel principal
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
        if (validarCampos()) {
            Producto producto = new Producto();
            producto.setNombreProducto(txtNombre.getText());
            producto.setDescripcion(txtDescripcion.getText());
            producto.setPrecioUnitario(Double.parseDouble(txtPrecio.getText()));
            
            if (productoDAO.insertarProducto(producto)) {
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente");
                limpiarCampos();
                cargarProductos();
            }
        }
    }
    
    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int idProducto = (int) modeloTabla.getValueAt(fila, 0);
            
            if (validarCampos()) {
                Producto producto = new Producto();
                producto.setIdProducto(idProducto);
                producto.setNombreProducto(txtNombre.getText());
                producto.setDescripcion(txtDescripcion.getText());
                producto.setPrecioUnitario(Double.parseDouble(txtPrecio.getText()));
                
                if (productoDAO.actualizarProducto(producto)) {
                    JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente");
                    limpiarCampos();
                    cargarProductos();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar");
        }
    }
    
    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            int idProducto = (int) modeloTabla.getValueAt(fila, 0);
            String nombre = (String) modeloTabla.getValueAt(fila, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar el producto: " + nombre + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (productoDAO.eliminarProducto(idProducto)) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente");
                    limpiarCampos();
                    cargarProductos();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
        }
    }
    
    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
            txtDescripcion.setText((String) modeloTabla.getValueAt(fila, 2));
            String precio = ((String) modeloTabla.getValueAt(fila, 3)).replace("$", "");
            txtPrecio.setText(precio);
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
        tablaProductos.clearSelection();
    }
}
