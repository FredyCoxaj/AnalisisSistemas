package miumg.edu.gt.controlinventariopeps.vista;

import miumg.edu.gt.controlinventariopeps.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final Usuario usuarioActual;
    private JTabbedPane tabbedPane;

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Inventarios PEPS - Usuario: " + usuarioActual.getNombreCompleto());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Paneles comunes
        tabbedPane.addTab("Productos", new PanelProductos());
        tabbedPane.addTab("Lotes", new PanelLotes());

        if (usuarioActual.getRol().equals("Vendedor") || usuarioActual.getRol().equals("Admin")) {
            tabbedPane.addTab("Ventas", new PanelVentas(usuarioActual));
        }

        // Menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuCerrarSesion = new JMenuItem("Cerrar Sesión");
        menuCerrarSesion.addActionListener(e -> cerrarSesion());
        JMenuItem menuSalir = new JMenuItem("Salir");
        menuSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(menuCerrarSesion);
        menuArchivo.add(menuSalir);
        menuBar.add(menuArchivo);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem menuAcerca = new JMenuItem("Acerca de");
        menuAcerca.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(menuAcerca);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
        add(tabbedPane);

        // Barra de estado
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(new JLabel("Usuario: " + usuarioActual.getNombreCompleto() + " | Rol: " + usuarioActual.getRol()));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Desea cerrar sesión?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Inventarios PEPS\nVersión 1.0\nDesarrollado con Java y SQL Server\nMétodo de valuación: PEPS (Primeras Entradas, Primeras Salidas)",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
}