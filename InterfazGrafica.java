import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;          // <<< JBDC

public class InterfazGrafica extends JFrame {

    private final JTabbedPane tabs = new JTabbedPane();

    // === Usuarios ===
    private final JTextField uNombre = new JTextField();
    private final JTextField uCorreo = new JTextField();
    private final JTextField uCarne  = new JTextField();
    private final DefaultTableModel uModel = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Correo", "Carné", "Creado"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable uTable = new JTable(uModel);

    // === Retos ===
    private final JTextField rNombre = new JTextField();
    private final JTextArea  rDesc   = new JTextArea(3, 20);
    private final JSpinner   rPuntos = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
    private final JCheckBox  rEstado = new JCheckBox("Activo");
    private final DefaultTableModel rModel = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Puntos", "Activo", "Creado"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable rTable = new JTable(rModel);

    // === Grupos ===
    private final JTextField gNombre = new JTextField();
    private final DefaultTableModel gModel = new DefaultTableModel(
            new String[]{"ID", "Nombre", "# Miembros", "Reto Asignado", "Finalizado", "Creado"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable gTable = new JTable(gModel);

    public InterfazGrafica() {
        super("PROYECTO-COMPETENCIA — Interfaz (MySQL)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabs.addTab("Usuarios", buildUsuariosPanel());
        tabs.addTab("Retos",    buildRetosPanel());
        tabs.addTab("Grupos",   buildGruposPanel());
        add(tabs, BorderLayout.CENTER);

        // Cargar datos desde MySQL
        refrescarUsuarios();
        refrescarRetos();
        refrescarGrupos();

        setVisible(true);
    }

    private JPanel buildUsuariosPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx=0;c.gridy=row; form.add(new JLabel("Nombre"), c);
        c.gridx=1;c.gridy=row; form.add(uNombre, c); row++;

        c.gridx=0;c.gridy=row; form.add(new JLabel("Correo"), c);
        c.gridx=1;c.gridy=row; form.add(uCorreo, c); row++;

        c.gridx=0;c.gridy=row; form.add(new JLabel("Carné"), c);
        c.gridx=1;c.gridy=row; form.add(uCarne, c); row++;

        JButton add = new JButton("Registrar usuario");
        add.addActionListener(this::onAgregarUsuario);
        c.gridx=1;c.gridy=row; form.add(add, c);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(uTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRetosPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx=0;c.gridy=row; form.add(new JLabel("Nombre"), c);
        c.gridx=1;c.gridy=row; form.add(rNombre, c); row++;

        c.gridx=0;c.gridy=row; form.add(new JLabel("Descripción"), c);
        c.gridx=1;c.gridy=row; form.add(new JScrollPane(rDesc), c); row++;

        c.gridx=0;c.gridy=row; form.add(new JLabel("Puntos"), c);
        c.gridx=1;c.gridy=row; form.add(rPuntos, c); row++;

        c.gridx=0;c.gridy=row; form.add(new JLabel("Estado"), c);
        c.gridx=1;c.gridy=row; form.add(rEstado, c); row++;

        JButton add = new JButton("Agregar reto");
        add.addActionListener(this::onAgregarReto);
        c.gridx=1;c.gridy=row; form.add(add, c);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(rTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildGruposPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx=0;c.gridy=row; form.add(new JLabel("Nombre del grupo"), c);
        c.gridx=1;c.gridy=row; form.add(gNombre, c); row++;

        JButton add = new JButton("Crear grupo");
        add.addActionListener(this::onCrearGrupo);
        c.gridx=1;c.gridy=row; form.add(add, c);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(gTable), BorderLayout.CENTER);
        return panel;
    }

    /* ===================== ACCIONES (INSERT en MySQL) ===================== */

    private void onAgregarUsuario(ActionEvent e) {
        String nombre = uNombre.getText().trim();
        String correo = uCorreo.getText().trim();
        String carne  = uCarne.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || carne.isEmpty()) {
            warn("Complete todos los campos.");
            return;
        }

        final String sql = "INSERT INTO usuarios(nombre, correo, contra, carne) VALUES(?,?,?,?)";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, "123");     // coloca aquí tu política de contraseña
            ps.setString(4, carne);
            ps.executeUpdate();
            info("Usuario registrado.");
            uNombre.setText(""); uCorreo.setText(""); uCarne.setText("");
            refrescarUsuarios();
        } catch (SQLIntegrityConstraintViolationException d) {
            error("Correo o carné ya existen.");
        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    private void onAgregarReto(ActionEvent e) {
        String nombre = rNombre.getText().trim();
        String descripcion = rDesc.getText().trim();
        int puntos = ((Number) rPuntos.getValue()).intValue();
        boolean estado = rEstado.isSelected();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            warn("Nombre y descripción son obligatorios.");
            return;
        }

        final String sql = "INSERT INTO retos(nombre_reto, descripcion, puntos, estado) VALUES(?,?,?,?)";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, puntos);
            ps.setBoolean(4, estado);
            ps.executeUpdate();
            info("Reto agregado.");
            rNombre.setText(""); rDesc.setText(""); rPuntos.setValue(10); rEstado.setSelected(false);
            refrescarRetos();
        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    private void onCrearGrupo(ActionEvent e) {
        String nombre = gNombre.getText().trim();
        if (nombre.isEmpty()) { warn("Escribe un nombre."); return; }

        final String sql = "INSERT INTO grupos(grupo_nombre) VALUES(?)";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            info("Grupo creado.");
            gNombre.setText("");
            refrescarGrupos();
        } catch (SQLIntegrityConstraintViolationException d) {
            error("El nombre del grupo ya existe.");
        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    /* ===================== CARGA DE TABLAS (SELECT) ===================== */

    private void refrescarUsuarios() {
        clearModel(uModel);
        final String sql = "SELECT id, nombre, correo, carne, creado_en FROM usuarios ORDER BY id DESC";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                uModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("carne"),
                        rs.getTimestamp("creado_en")
                });
            }
        } catch (SQLException ex) {
            error("Error cargando usuarios: " + ex.getMessage());
        }
    }

    private void refrescarRetos() {
        clearModel(rModel);
        final String sql = "SELECT id, nombre_reto, puntos, estado, creado_en FROM retos ORDER BY id DESC";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre_reto"),
                        rs.getInt("puntos"),
                        rs.getBoolean("estado"),
                        rs.getTimestamp("creado_en")
                });
            }
        } catch (SQLException ex) {
            error("Error cargando retos: " + ex.getMessage());
        }
    }

    private void refrescarGrupos() {
        clearModel(gModel);
        final String sql =
            "SELECT g.id, g.grupo_nombre, " +
            "       COALESCE(COUNT(gp.usuario_id),0) AS miembros, " +
            "       (SELECT r.nombre_reto FROM retos r WHERE r.id=g.reto_id) AS reto, " +
            "       g.reto_finalizado, g.creado_en " +
            "FROM grupos g " +
            "LEFT JOIN grupo_participantes gp ON gp.grupo_id = g.id " +
            "GROUP BY g.id, g.grupo_nombre, g.reto_id, g.reto_finalizado, g.creado_en " +
            "ORDER BY g.id DESC";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                gModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("grupo_nombre"),
                        rs.getInt("miembros"),
                        rs.getString("reto"),
                        rs.getBoolean("reto_finalizado"),
                        rs.getTimestamp("creado_en")
                });
            }
        } catch (SQLException ex) {
            error("Error cargando grupos: " + ex.getMessage());
        }
    }

    /* ===================== UTILIDADES ===================== */

    private void clearModel(DefaultTableModel m){
        while(m.getRowCount()>0) m.removeRow(0);
    }
    private void info(String msg){ JOptionPane.showMessageDialog(this, msg, "OK", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String msg){ JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg){ JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazGrafica::new);
    }
}
