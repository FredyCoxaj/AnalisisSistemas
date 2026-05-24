package com.inventario.service;

import com.inventario.dto.LoginRequestDTO;
import com.inventario.dto.LoginResponseDTO;
import com.inventario.exception.BusinessException;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.exception.UnauthorizedException;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // LOGIN - Comparación directa texto plano
    public LoginResponseDTO login(LoginRequestDTO request) {
        System.out.println("=== LOGIN ===");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password enviado: " + request.getPassword());
        
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        System.out.println("Usuario encontrado: " + usuario.getEmail());
        System.out.println("Password en BD: " + usuario.getContrasenaHash());
        
        // COMPARACIÓN DIRECTA - TEXTO PLANO
        if (!usuario.getContrasenaHash().equals(request.getPassword())) {
            System.out.println("❌ Las contraseñas NO coinciden");
            throw new UnauthorizedException("Credenciales inválidas");
        }
        
        System.out.println("✅ Contraseñas coinciden!");
        
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol(), usuario.getIdUsuario());
        
        return new LoginResponseDTO(token, usuario.getRol(), usuario.getNombreCompleto(), 
                                   usuario.getIdUsuario(), usuario.getEmail());
    }
    
    // LISTAR TODOS
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    // BUSCAR POR ID
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }
    
    // BUSCAR POR EMAIL
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
    }
    
    // BUSCAR POR ROL
    public List<Usuario> buscarPorRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    // CREAR USUARIO
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        // Guardar la contraseña en texto plano (para pruebas)
        usuario.setContrasenaHash(usuario.getContrasenaHash());
        return usuarioRepository.save(usuario);
    }
    
    // ACTUALIZAR USUARIO
    public Usuario actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        Usuario usuario = buscarPorId(id);
        usuario.setNombreCompleto(usuarioActualizado.getNombreCompleto());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setDireccion(usuarioActualizado.getDireccion());
        if (usuarioActualizado.getContrasenaHash() != null && !usuarioActualizado.getContrasenaHash().isEmpty()) {
            usuario.setContrasenaHash(usuarioActualizado.getContrasenaHash());
        }
        return usuarioRepository.save(usuario);
    }
    
    // ELIMINAR USUARIO
    public void eliminarUsuario(Integer id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}