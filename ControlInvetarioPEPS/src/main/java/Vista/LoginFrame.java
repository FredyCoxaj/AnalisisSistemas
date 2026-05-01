/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;
import Dao.UsuarioDAO;
import Modelo.Usuario;
import dao.*;
import modelo.*;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Fredy
 */
public class LoginFrame extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private UsuarioDAO usuarioDAO;
    
    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Sistema de Inventarios PEPS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Título
        JLabel lblTitulo = new JLabel("Inventarios PEPS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        // Correo
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Correo:"), gbc);
        
        txtCorreo = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);
        
        // Contraseña
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        // Botón Login
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(e -> login());
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);
        
        add(panel);
    }
    
    private void login() {
        String correo = txtCorreo.getText();
        String contrasena = new String(txtPassword.getPassword());
        
        // En una implementación real, deberías aplicar hash a la contraseña
        Usuario usuario = usuarioDAO.login(correo, contrasena);
        
        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + usuario.getNombreCompleto());
            new MainFrame(usuario).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
