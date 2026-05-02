package miumg.edu.gt.controlinventariopeps.vista;

import miumg.edu.gt.controlinventariopeps.dao.UsuarioDAO;
import miumg.edu.gt.controlinventariopeps.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private final UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Inventarios PEPS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título
        JLabel lblTitulo = new JLabel("Sistema de Inventarios PEPS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 102, 204));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Método PEPS (Primeras Entradas, Primeras Salidas)");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(lblTitulo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(lblSubtitulo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Panel formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Correo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 13));
        panelFormulario.add(lblCorreo, gbc);

        txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCorreo.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.weightx = 1;
        panelFormulario.add(txtCorreo, gbc);

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 13));
        panelFormulario.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.weightx = 1;
        panelFormulario.add(txtPassword, gbc);

        mainPanel.add(panelFormulario);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(new Color(240, 248, 255));

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(140, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> login());

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(108, 117, 125));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setPreferredSize(new Dimension(140, 40));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> salirAplicacion());

        panelBotones.add(btnLogin);
        panelBotones.add(btnSalir);
        
        mainPanel.add(panelBotones);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel información (opcional)
        JPanel panelInfo = new JPanel();
        panelInfo.setBackground(new Color(255, 245, 200));
        panelInfo.setBorder(BorderFactory.createLineBorder(new Color(200, 180, 100)));
        
        JLabel lblInfo = new JLabel("Credenciales de prueba: admin@email.com / admin123");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(100, 80, 0));
        panelInfo.add(lblInfo);
        
        mainPanel.add(panelInfo);

        // Ajustar tamaño
        mainPanel.setPreferredSize(new Dimension(420, 420));
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        // Login con Enter
        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtPassword.getPassword());

        if (correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su correo electrónico", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtCorreo.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su contraseña", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() {
                return usuarioDAO.login(correo, contrasena);
            }
            
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "¡Bienvenido " + usuario.getNombreCompleto() + "!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
                        new MainFrame(usuario).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Credenciales incorrectas", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Error al conectar con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void salirAplicacion() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea salir?", "Confirmar Salida", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}