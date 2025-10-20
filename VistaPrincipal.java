
import Modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class VistaPrincipal extends JFrame {
    
    private Controlador controlador = new Controlador();
    
    private JTextField txtNombre, txtCorreo, txtCarne;
    private JButton btnAgregarUsuario;
    private JList<String> listaUsuarios;
    private DefaultListModel<String> modeloListaUsuarios;
    
    private JTextField txtNombreReto, txtDescripcion, txtPuntos;
    private JCheckBox chkEstado;
    private JButton btnAgregarReto;
    private JTextArea areaRetos;
    
    
    
    public VistaPrincipal() {
        
        setTitle("Sistema de Retos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 2));
        
        crearPanelUsuarios();
        crearPanelRetos();
        
        setVisible(true);
    }
    
    private void crearPanelUsuarios() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Usuarios"));
        panel.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(4, 2));
        form.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        form.add(txtNombre);
        
        form.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        form.add(txtCorreo);
        
        form.add(new JLabel("Carné:"));
        txtCarne = new JTextField();
        form.add(txtCarne);
        
        btnAgregarUsuario = new JButton("Agregar");
        form.add(btnAgregarUsuario);
        
        modeloListaUsuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloListaUsuarios);
        
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(listaUsuarios), BorderLayout.CENTER);
        
        btnAgregarUsuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarUsuario();
            }
        });
        
        add(panel);
    }
    
    private void crearPanelRetos() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Retos"));
        panel.setLayout(new BorderLayout());
        
        JPanel form = new JPanel(new GridLayout(5, 2));
        form.add(new JLabel("Nombre:"));
        txtNombreReto = new JTextField();
        form.add(txtNombreReto);
        
        form.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        form.add(txtDescripcion);
        
        form.add(new JLabel("Puntos:"));
        txtPuntos = new JTextField();
        form.add(txtPuntos);
        
        form.add(new JLabel("Activo:"));
        chkEstado = new JCheckBox();
        form.add(chkEstado);
        
        btnAgregarReto = new JButton("Agregar");
        form.add(btnAgregarReto);
        
        areaRetos = new JTextArea();
        areaRetos.setEditable(false);
        
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaRetos), BorderLayout.CENTER);
        
        btnAgregarReto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarReto();
            }
        });
        
        add(panel);
    }
    
    private void agregarUsuario() {
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String carne = txtCarne.getText();
        
        if (controlador.agregarUsuario(nombre,correo,carne)) {
            modeloListaUsuarios.addElement(nombre + " - " + carne);
            
            txtNombre.setText("");
            txtCorreo.setText("");
            txtCarne.setText("");
            
            JOptionPane.showMessageDialog(this, "Usuario agregado");
        } else {
            JOptionPane.showMessageDialog(this, "Llene todos los campos");
        }
    }
    
    private void agregarReto() {
        String nombre = txtNombreReto.getText();
        String descripcion = txtDescripcion.getText();
        String puntosTexto = txtPuntos.getText();
        try {
            int puntos = Integer.parseInt(puntosTexto);
            boolean estado = chkEstado.isSelected();
            if(controlador.agregarReto(nombre, descripcion, puntos, estado)){
                String texto = areaRetos.getText();
                texto += nombre + " - " + puntos + " pts - " + (estado ? "Activo" : "Inactivo") + "\n";
                areaRetos.setText(texto);
                
                txtNombreReto.setText("");
                txtDescripcion.setText("");
                txtPuntos.setText("");
                chkEstado.setSelected(false);
                
                JOptionPane.showMessageDialog(this, "Reto agregado");
            }else{
                JOptionPane.showMessageDialog(this, "Llene todos los campos");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en puntos");
        }
    }
    
    public static void main(String[] args) {
        new VistaPrincipal();
    }
}