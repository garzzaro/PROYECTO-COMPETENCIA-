import Modelo.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class InterfazGrafica extends JFrame {

    private final JTabbedPane tabs = new JTabbedPane();

    // === Usuarios (form) ===
    private final JTextField uNombre = new JTextField();
    private final JTextField uCorreo = new JTextField();
    private final JTextField uCarne  = new JTextField();
    private final JPasswordField uContra = new JPasswordField();
    private JButton btnLogin;
    private JButton btnLogout;

    private final DefaultTableModel uModel = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Correo", "Carné", "Creado"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable uTable = new JTable(uModel);

    // === Retos === (UI controls nuevos)
    private final JTextField rNombre = new JTextField();
    private final JTextArea  rDesc   = new JTextArea(3, 20);
    private final JSpinner   rPuntos = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
    private final JCheckBox  rEstado = new JCheckBox("Activo");
    private final DefaultTableModel rModel = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Puntos", "Activo", "Creado"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable rTable = new JTable(rModel);

    // Controles adicionales en pestaña Retos (asignar / calificar)
    private final JComboBox<String> cbAdminGrupos = new JComboBox<>(); // "id - nombre"
    private final JLabel lblRetoAsignado = new JLabel("Reto asignado: (ninguno)");
    private final JLabel lblAdminMensaje = new JLabel(""); // mensaje cuando no es admin
    private final JComboBox<String> cbRetosDisponibles = new JComboBox<>(); // "id - nombre"
    private final JButton btnAsignarReto = new JButton("Asignar reto al grupo");
    private final JButton btnQuitarReto  = new JButton("Quitar reto asignado");

    // Calificar usuarios en grupo
    private final JComboBox<String> cbUsuariosGrupo = new JComboBox<>(); // "id - nombre"
    private final JSpinner spPuntuacion = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
    private final JButton btnAgregarCalificacion = new JButton("Agregar calificación");

    // === Grupos ===
    private final JTextField gNombre = new JTextField();
    private final DefaultTableModel gModel = new DefaultTableModel(
            new String[]{"ID", "Nombre", "# Miembros", "Reto Asignado", "Finalizado", "Creado"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable gTable = new JTable(gModel);

    // === Puntos (nueva pestaña) ===
    private final JComboBox<String> cbPuntosGrupos = new JComboBox<>(); // grupos del usuario (id - nombre)
    private final DefaultTableModel puntosModel = new DefaultTableModel(
            new String[]{"Usuario ID", "Usuario", "Puntos"}, 0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable puntosTable = new JTable(puntosModel);

    // === Sesión actual (objetos) ===
    private Usuario usuarioLogueado = null; // objeto Modelo.Usuario en sesión
    private Grupo grupoActual = null;       // objeto Modelo.Grupo actual (solo para manejo en la UI)
    private final JLabel lblSesionUsuarios = new JLabel("Usuario actual: (no hay sesión)");
    private final JLabel lblSesionRetos    = new JLabel("Usuario actual: (no hay sesión)");
    private final JLabel lblSesionGrupos   = new JLabel("Usuario actual: (no hay sesión)");
    private final JLabel lblGrupoActual    = new JLabel("Grupo actual: (ninguno)");

    // Controlador opcional (puedes usarlo o ignorarlo); lo agrego para separar lógica
    private final Controlador controlador = new Controlador();

    public InterfazGrafica() {
        super("PROYECTO-COMPETENCIA — Interfaz (MySQL)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Orden de pestañas requerido: Usuarios, Grupos, Retos, Puntos
        tabs.addTab("Usuarios", buildUsuariosPanel());
        tabs.addTab("Grupos",   buildGruposPanel());
        tabs.addTab("Retos",    buildRetosPanel());
        tabs.addTab("Puntos",   buildPuntosPanel());
        add(tabs, BorderLayout.CENTER);

        // Cargar datos desde MySQL
        refrescarUsuarios();
        refrescarRetos();
        refrescarGrupos();
        cargarAdminGrupos();   // para la pestaña Retos
        cargarRetosDisponibles();
        cargarPuntosGrupos();

        // actualizar etiquetas (color)
        actualizarEtiquetasSesion();

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

        c.gridx=0;c.gridy=row; form.add(new JLabel("Contraseña"), c);
        c.gridx=1;c.gridy=row; form.add(uContra, c); row++;

        btnLogin = new JButton("Iniciar sesión / Registrar");
        btnLogin.addActionListener(this::onLoginUsuario);
        c.gridx=1;c.gridy=row; form.add(btnLogin, c);

        // logout
        btnLogout = new JButton("Cerrar sesión");
        btnLogout.addActionListener(e -> cerrarSesion());
        c.gridx=1; c.gridy = ++row;
        form.add(btnLogout, c);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(uTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(lblSesionUsuarios, BorderLayout.NORTH);
        south.add(lblGrupoActual, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildRetosPanel() {
        JPanel container = new JPanel(new BorderLayout());

        // Form para crear retos (igual que antes)
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

        // Panel de administración de asignación de retos (solo admins)
        JPanel adminPanel = new JPanel(new GridBagLayout());
        adminPanel.setBorder(BorderFactory.createTitledBorder("Administrar retos por grupo (solo admins)"));
        GridBagConstraints a = new GridBagConstraints();
        a.insets = new Insets(4,4,4,4);
        a.fill = GridBagConstraints.HORIZONTAL;
        int ar = 0;

        a.gridx=0; a.gridy=ar; adminPanel.add(new JLabel("Tus grupos (admin)"), a);
        a.gridx=1; a.gridy=ar++; adminPanel.add(cbAdminGrupos, a);

        a.gridx=0; a.gridy=ar; adminPanel.add(new JLabel("Reto asignado"), a);
        a.gridx=1; a.gridy=ar++; adminPanel.add(lblRetoAsignado, a);

        // mensaje de admin (rojo) si no es admin de ningun grupo
        lblAdminMensaje.setForeground(Color.RED);
        a.gridx=0; a.gridy=ar; adminPanel.add(new JLabel(""), a);
        a.gridx=1; a.gridy=ar++; adminPanel.add(lblAdminMensaje, a);

        a.gridx=0; a.gridy=ar; adminPanel.add(new JLabel("Retos disponibles"), a);
        a.gridx=1; a.gridy=ar++; adminPanel.add(cbRetosDisponibles, a);

        a.gridx=1; a.gridy=ar++; adminPanel.add(btnAsignarReto, a);
        a.gridx=1; a.gridy=ar++; adminPanel.add(btnQuitarReto, a);

        btnAsignarReto.addActionListener(e -> asignarRetoAGrupo());
        btnQuitarReto.addActionListener(e -> quitarRetoDeGrupo());
        cbAdminGrupos.addActionListener(e -> actualizarRetoAsignadoYUsuarios());

        // Panel para calificar usuarios del grupo
        JPanel califPanel = new JPanel(new GridBagLayout());
        califPanel.setBorder(BorderFactory.createTitledBorder("Calificar usuario del grupo (admin)"));
        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(4,4,4,4);
        b.fill = GridBagConstraints.HORIZONTAL;
        int br = 0;

        b.gridx=0; b.gridy=br; califPanel.add(new JLabel("Usuario (del grupo)"), b);
        b.gridx=1; b.gridy=br++; califPanel.add(cbUsuariosGrupo, b);

        b.gridx=0; b.gridy=br; califPanel.add(new JLabel("Puntuación"), b);
        b.gridx=1; b.gridy=br++; califPanel.add(spPuntuacion, b);

        b.gridx=1; b.gridy=br++; califPanel.add(btnAgregarCalificacion, b);

        btnAgregarCalificacion.addActionListener(e -> agregarCalificacionAUsuario());

        // Arrange top: creation form + admin panels
        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.WEST);

        JPanel eastStack = new JPanel(new GridLayout(2,1));
        eastStack.add(adminPanel);
        eastStack.add(califPanel);
        top.add(eastStack, BorderLayout.CENTER);

        container.add(top, BorderLayout.NORTH);

        // Table retos
        container.add(new JScrollPane(rTable), BorderLayout.CENTER);

        // bottom: session label
        container.add(lblSesionRetos, BorderLayout.SOUTH);

        return container;
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

        // boton unirse
        JButton join = new JButton("Unirse a grupo seleccionado");
        join.addActionListener(e -> onUnirseGrupo());
        c.gridx=1; c.gridy = ++row;
        form.add(join, c);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(gTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(2,1));
        south.add(lblSesionGrupos);
        south.add(lblGrupoActual);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildPuntosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Seleccionar grupo"));
        top.add(cbPuntosGrupos);
        JButton btnRefresh = new JButton("Actualizar puntos");
        btnRefresh.addActionListener(e -> refrescarPuntos());
        top.add(btnRefresh);
        panel.add(top, BorderLayout.NORTH);

        panel.add(new JScrollPane(puntosTable), BorderLayout.CENTER);
        return panel;
    }

    /* ===================== LOGIN / REGISTRO ===================== */

    private void onLoginUsuario(ActionEvent e) {
        String nombre = uNombre.getText().trim();
        String correo = uCorreo.getText().trim();
        String carne  = uCarne.getText().trim();
        String contra = new String(uContra.getPassword()).trim();

        if (nombre.isEmpty() || correo.isEmpty() || carne.isEmpty() || contra.isEmpty()) {
            warn("Complete todos los campos.");
            return;
        }

        // Usar controlador o lógica in-place (similar a tu código)
        try (Connection cn = BD.conectar()) {
            final String checkSQL = "SELECT * FROM usuarios WHERE correo=? OR carne=?";
            try (PreparedStatement check = cn.prepareStatement(checkSQL)) {
                check.setString(1, correo);
                check.setString(2, carne);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    // Usuario existe → verificar password (campo en BD: password)
                    String passBD = rs.getString("password");
                    if (passBD.equals(contra) || new Usuario(rs.getString("nombre"), rs.getString("correo"), contra, rs.getString("carne")).verificarContra(contra)) {
                        // crear objeto Usuario en memoria (usando constructor que encripta internamente)
                        usuarioLogueado = new Usuario(
                                rs.getString("nombre"),
                                rs.getString("correo"),
                                contra,
                                rs.getString("carne")
                        );
                        info("Bienvenido, " + rs.getString("nombre"));
                    } else {
                        error("Contraseña incorrecta.");
                        return;
                    }
                } else {
                    // Usuario no existe → registrar en BD
                    final String insertSQL = "INSERT INTO usuarios(nombre, correo, carne, password) VALUES(?,?,?,?)";
                    try (PreparedStatement ps = cn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, nombre);
                        ps.setString(2, correo);
                        ps.setString(3, carne);
                        ps.setString(4, contra);
                        ps.executeUpdate();

                        usuarioLogueado = new Usuario(nombre, correo, contra, carne);

                        info("Usuario registrado e inició sesión correctamente.");
                    }
                }
            }

            if (btnLogin != null) btnLogin.setEnabled(false);

            // Refrescar vistas y cargas relacionadas con permisos
            refrescarUsuarios();
            refrescarRetos();
            refrescarGrupos();
            limpiarCamposLogin();
            actualizarEtiquetasSesion();

            // recargar combos dependientes de la sesión
            cargarAdminGrupos();
            cargarPuntosGrupos();
            cargarRetosDisponibles();

        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    private void cerrarSesion() {
        usuarioLogueado = null;
        grupoActual = null;
        info("Sesión cerrada.");

        if (btnLogin != null) btnLogin.setEnabled(true);

        actualizarEtiquetasSesion();

        // limpiar combos dependientes
        cbAdminGrupos.removeAllItems();
        cbUsuariosGrupo.removeAllItems();
        cbPuntosGrupos.removeAllItems();

        // limpiar mensajes y habilitaciones
        lblAdminMensaje.setText("");
        cbAdminGrupos.setEnabled(true);
        cbRetosDisponibles.setEnabled(true);
        btnAsignarReto.setEnabled(true);
        btnQuitarReto.setEnabled(true);
    }

    /* ===================== ACCIONES (INSERT / UPDATE en MySQL) ===================== */

    private void onAgregarReto(ActionEvent e) {
        String nombre = rNombre.getText().trim();
        String descripcion = rDesc.getText().trim();
        int puntos = ((Number) rPuntos.getValue()).intValue();
        boolean estado = rEstado.isSelected();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            warn("Nombre y descripción son obligatorios.");
            return;
        }

        final String sql = "INSERT INTO retos(nombre, descripcion, puntos, estado) VALUES(?,?,?,?)";
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
            cargarRetosDisponibles();
        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    private void onCrearGrupo(ActionEvent e) {
        if (usuarioLogueado == null) {
            error("Debe iniciar sesión para crear un grupo.");
            return;
        }

        String nombre = gNombre.getText().trim();
        if (nombre.isEmpty()) { warn("Escribe un nombre."); return; }

        try (Connection cn = BD.conectar()) {

            // 1) Insertar grupo y obtener id generado
            String sqlGrupo = "INSERT INTO grupos(nombre) VALUES(?)";
            PreparedStatement psGrupo = cn.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS);
            psGrupo.setString(1, nombre);
            psGrupo.executeUpdate();

            ResultSet gen = psGrupo.getGeneratedKeys();
            if (!gen.next()) {
                throw new SQLException("No se obtuvo id de grupo.");
            }
            int grupoID = gen.getInt(1);

            // 2) Obtener id del usuario actual
            int usuarioID = obtenerIDUsuario(usuarioLogueado.getCorreo(), cn);

            // 3) Insertar en grupo_participantes (creador es participante)
            String sqlP = "INSERT INTO grupo_participantes(grupo_id, usuario_id) VALUES(?,?)";
            try (PreparedStatement psP = cn.prepareStatement(sqlP)) {
                psP.setInt(1, grupoID);
                psP.setInt(2, usuarioID);
                psP.executeUpdate();
            }

            // 4) Insertar en grupo_admin (creador es admin)
            String sqlA = "INSERT INTO grupo_admin(grupo_id, usuario_id) VALUES(?,?)";
            try (PreparedStatement psA = cn.prepareStatement(sqlA)) {
                psA.setInt(1, grupoID);
                psA.setInt(2, usuarioID);
                psA.executeUpdate();
            }

            // 5) Guardar grupo actual en memoria (objeto simple para UI)
            grupoActual = new Grupo(nombre);
            lblGrupoActual.setText("Grupo actual: " + grupoActual.getGrupoNombre());

            info("Grupo creado correctamente.");

            gNombre.setText("");
            refrescarGrupos();

            // recargar combos relevantes
            cargarAdminGrupos();
            cargarPuntosGrupos();

        } catch (SQLIntegrityConstraintViolationException d) {
            error("El nombre del grupo ya existe o viola una restricción.");
        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    private void onUnirseGrupo() {
        if (usuarioLogueado == null) {
            error("Debe iniciar sesión.");
            return;
        }

        int row = gTable.getSelectedRow();
        if (row == -1) {
            warn("Seleccione un grupo de la tabla.");
            return;
        }

        int grupoID = (int) gModel.getValueAt(row, 0);
        String nombreGrupo = (String) gModel.getValueAt(row, 1);

        try (Connection cn = BD.conectar()) {
            int userID = obtenerIDUsuario(usuarioLogueado.getCorreo(), cn);

            // evitar duplicados
            PreparedStatement check = cn.prepareStatement(
                "SELECT * FROM grupo_participantes WHERE grupo_id=? AND usuario_id=?"
            );
            check.setInt(1, grupoID);
            check.setInt(2, userID);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                warn("Ya eres miembro de este grupo.");
                return;
            }

            // Insertar participante
            PreparedStatement join = cn.prepareStatement(
                "INSERT INTO grupo_participantes(grupo_id, usuario_id) VALUES(?,?)"
            );
            join.setInt(1, grupoID);
            join.setInt(2, userID);
            join.executeUpdate();

            // actualizar grupo actual en UI
            grupoActual = new Grupo(nombreGrupo);
            lblGrupoActual.setText("Grupo actual: " + grupoActual.getGrupoNombre());

            info("Te uniste al grupo correctamente.");
            refrescarGrupos();

            // recargar combos
            cargarPuntosGrupos();

        } catch (SQLException ex) {
            error("Error BD: " + ex.getMessage());
        }
    }

    // ===================== NUEVAS FUNCIONES: asignar/quitar retos y calificaciones =====================

    private void cargarAdminGrupos() {
        cbAdminGrupos.removeAllItems();
        lblAdminMensaje.setText("");
        cbUsuariosGrupo.removeAllItems();
        // Deshabilitar controles hasta saber si hay admins
        cbRetosDisponibles.setEnabled(true);
        btnAsignarReto.setEnabled(true);
        btnQuitarReto.setEnabled(true);
        try (Connection cn = BD.conectar()) {
            if (usuarioLogueado == null) {
                lblAdminMensaje.setText("Debe iniciar sesión para ver grupos de administración.");
                // deshabilitar controles admin
                cbAdminGrupos.setEnabled(false);
                cbRetosDisponibles.setEnabled(false);
                btnAsignarReto.setEnabled(false);
                btnQuitarReto.setEnabled(false);
                return;
            }
            int userID = obtenerIDUsuario(usuarioLogueado.getCorreo(), cn);
            String sql = "SELECT g.id, g.nombre FROM grupos g JOIN grupo_admin ga ON ga.grupo_id = g.id WHERE ga.usuario_id = ?";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, userID);
                ResultSet rs = ps.executeQuery();
                boolean any = false;
                while (rs.next()) {
                    cbAdminGrupos.addItem(rs.getInt("id") + " - " + rs.getString("nombre"));
                    any = true;
                }
                if (!any) {
                    // no es admin de ningún grupo
                    lblAdminMensaje.setText("No eres administrador de ningún grupo.");
                    cbAdminGrupos.setEnabled(false);
                    cbRetosDisponibles.setEnabled(false);
                    btnAsignarReto.setEnabled(false);
                    btnQuitarReto.setEnabled(false);
                    cbUsuariosGrupo.removeAllItems();
                } else {
                    lblAdminMensaje.setText("");
                    cbAdminGrupos.setEnabled(true);
                    cbRetosDisponibles.setEnabled(true);
                    btnAsignarReto.setEnabled(true);
                    btnQuitarReto.setEnabled(true);
                }
            }
        } catch (SQLException ex) {
            error("Error cargando grupos admin: " + ex.getMessage());
        }
        // actualizar info del grupo seleccionado (si hay)
        actualizarRetoAsignadoYUsuarios();
    }

    private void cargarRetosDisponibles() {
        cbRetosDisponibles.removeAllItems();
        try (Connection cn = BD.conectar()) {
            String sql = "SELECT id, nombre FROM retos WHERE estado = 1 ORDER BY id DESC";
            try (PreparedStatement ps = cn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cbRetosDisponibles.addItem(rs.getInt("id") + " - " + rs.getString("nombre"));
                }
            }
        } catch (SQLException ex) {
            error("Error cargando retos disponibles: " + ex.getMessage());
        }
    }

    private void actualizarRetoAsignadoYUsuarios() {
        lblRetoAsignado.setText("Reto asignado: (ninguno)");
        cbUsuariosGrupo.removeAllItems();

        // si combo deshabilitado o vacío, salir
        if (!cbAdminGrupos.isEnabled() || cbAdminGrupos.getItemCount() == 0) {
            // aseguramos spinner con tope amplio por defecto
            spPuntuacion.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10000), Integer.valueOf(1)));
            return;
        }

        String sel = (String) cbAdminGrupos.getSelectedItem();
        if (sel == null) return;
        int grupoId = Integer.parseInt(sel.split(" - ")[0]);
        try (Connection cn = BD.conectar()) {
            // obtener reto asignado
            PreparedStatement ps = cn.prepareStatement("SELECT r.id, r.nombre, r.puntos FROM grupos g LEFT JOIN retos r ON r.id = g.reto_asignado WHERE g.id = ?");
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            Integer retoPuntos = null;
            if (rs.next()) {
                int idR = rs.getInt(1);
                String nombre = rs.getString(2);
                if (nombre != null) {
                    lblRetoAsignado.setText("Reto asignado: " + idR + " - " + nombre);
                    retoPuntos = rs.getInt("puntos");
                } else {
                    lblRetoAsignado.setText("Reto asignado: (ninguno)");
                }
            }

            // cargar usuarios del grupo para calificar
            PreparedStatement ps2 = cn.prepareStatement(
                "SELECT u.id, u.nombre FROM usuarios u JOIN grupo_participantes gp ON gp.usuario_id = u.id WHERE gp.grupo_id = ?"
            );
            ps2.setInt(1, grupoId);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                cbUsuariosGrupo.addItem(rs2.getInt("id") + " - " + rs2.getString("nombre"));
            }

            // Establecer límite máximo del spinner según puntos del reto asignado (si existe)
            if (retoPuntos != null) {
                // Evitar ambigüedad en constructor: utilizar Integer.valueOf(...)
                spPuntuacion.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(retoPuntos), Integer.valueOf(1)));
            } else {
                // sin reto asignado: dejamos un tope grande para no bloquear (pero validamos antes de insertar)
                spPuntuacion.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(10000), Integer.valueOf(1)));
            }

        } catch (SQLException ex) {
            error("Error actualizando info de grupo: " + ex.getMessage());
        }
    }

    private void asignarRetoAGrupo() {
        if (usuarioLogueado == null) { error("Debe iniciar sesión."); return; }
        String selG = (String) cbAdminGrupos.getSelectedItem();
        String selR = (String) cbRetosDisponibles.getSelectedItem();
        if (selG == null || selR == null) { warn("Seleccione grupo y reto."); return; }
        int grupoId = Integer.parseInt(selG.split(" - ")[0]);
        int retoId  = Integer.parseInt(selR.split(" - ")[0]);

        try (Connection cn = BD.conectar()) {
            // verificar admin
            if (!esAdminDelGrupo(grupoId, cn)) {
                error("No eres administrador de ese grupo.");
                return;
            }
            // asignar
            PreparedStatement ps = cn.prepareStatement("UPDATE grupos SET reto_asignado = ? WHERE id = ?");
            ps.setInt(1, retoId);
            ps.setInt(2, grupoId);
            ps.executeUpdate();
            info("Reto asignado al grupo.");
            actualizarRetoAsignadoYUsuarios();
            refrescarGrupos();
        } catch (SQLException ex) {
            error("Error asignando reto: " + ex.getMessage());
        }
    }

    private void quitarRetoDeGrupo() {
        if (usuarioLogueado == null) { error("Debe iniciar sesión."); return; }
        String selG = (String) cbAdminGrupos.getSelectedItem();
        if (selG == null) { warn("Seleccione grupo."); return; }
        int grupoId = Integer.parseInt(selG.split(" - ")[0]);

        try (Connection cn = BD.conectar()) {
            if (!esAdminDelGrupo(grupoId, cn)) {
                error("No eres administrador de ese grupo.");
                return;
            }
            PreparedStatement ps = cn.prepareStatement("UPDATE grupos SET reto_asignado = NULL WHERE id = ?");
            ps.setInt(1, grupoId);
            ps.executeUpdate();
            info("Reto quitado del grupo.");
            actualizarRetoAsignadoYUsuarios();
            refrescarGrupos();
        } catch (SQLException ex) {
            error("Error quitando reto: " + ex.getMessage());
        }
    }

    private void agregarCalificacionAUsuario() {
        if (usuarioLogueado == null) { error("Debe iniciar sesión."); return; }
        String selG = (String) cbAdminGrupos.getSelectedItem();
        String selU = (String) cbUsuariosGrupo.getSelectedItem();
        if (selG == null || selU == null) { warn("Seleccione grupo y usuario."); return; }
        int grupoId = Integer.parseInt(selG.split(" - ")[0]);
        int usuarioIdEvaluado = Integer.parseInt(selU.split(" - ")[0]);
        int puntuacion = ((Number) spPuntuacion.getValue()).intValue();

        try (Connection cn = BD.conectar()) {
            if (!esAdminDelGrupo(grupoId, cn)) {
                error("No eres administrador de ese grupo.");
                return;
            }

            // Obtener reto asignado al grupo (para relacionar calificación y validar puntos máximos)
            PreparedStatement psR = cn.prepareStatement("SELECT reto_asignado FROM grupos WHERE id = ?");
            psR.setInt(1, grupoId);
            ResultSet rsR = psR.executeQuery();
            if (!rsR.next() || rsR.getObject(1) == null) {
                warn("El grupo no tiene un reto asignado. Asigne un reto antes de calificar.");
                return;
            }
            int retoId = rsR.getInt(1);

            // obtener puntos máximos del reto
            PreparedStatement psP = cn.prepareStatement("SELECT puntos FROM retos WHERE id = ?");
            psP.setInt(1, retoId);
            ResultSet rsP = psP.executeQuery();
            int maxPuntos = 10000; // fallback
            if (rsP.next()) maxPuntos = rsP.getInt(1);

            // Validar que la puntuación no supere el máximo
            if (puntuacion > maxPuntos) {
                warn("La puntuación no puede ser mayor al máximo del reto (" + maxPuntos + ").");
                return;
            }

            // Insertar en calificaciones
            PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO calificaciones(usuario_id, reto_id, puntuacion_final) VALUES(?,?,?)"
            );
            ps.setInt(1, usuarioIdEvaluado);
            ps.setInt(2, retoId);
            ps.setInt(3, puntuacion);
            ps.executeUpdate();
            info("Calificación registrada para el usuario.");
            // refrescar datos de puntos si están viendo la pestaña Puntos
            refrescarPuntos();
        } catch (SQLException ex) {
            error("Error agregando calificación: " + ex.getMessage());
        }
    }

    // ===================== PUNTOS - nueva pestaña =====================

    private void cargarPuntosGrupos() {
        cbPuntosGrupos.removeAllItems();
        if (usuarioLogueado == null) return;
        try (Connection cn = BD.conectar()) {
            int userID = obtenerIDUsuario(usuarioLogueado.getCorreo(), cn);
            String sql = "SELECT g.id, g.nombre FROM grupos g JOIN grupo_participantes gp ON gp.grupo_id = g.id WHERE gp.usuario_id = ? GROUP BY g.id, g.nombre";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, userID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    cbPuntosGrupos.addItem(rs.getInt("id") + " - " + rs.getString("nombre"));
                }
            }
        } catch (SQLException ex) {
            error("Error cargando grupos para puntos: " + ex.getMessage());
        }
    }

    private void refrescarPuntos() {
        puntosModel.setRowCount(0);
        String sel = (String) cbPuntosGrupos.getSelectedItem();
        if (sel == null) return;
        int grupoId = Integer.parseInt(sel.split(" - ")[0]);

        try (Connection cn = BD.conectar()) {
            String sql =
                "SELECT u.id AS uid, u.nombre, COALESCE(SUM(c.puntuacion_final),0) AS puntos " +
                "FROM usuarios u " +
                "JOIN grupo_participantes gp ON gp.usuario_id = u.id " +
                "LEFT JOIN calificaciones c ON c.usuario_id = u.id " +
                "WHERE gp.grupo_id = ? " +
                "GROUP BY u.id, u.nombre " +
                "ORDER BY puntos DESC";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, grupoId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    puntosModel.addRow(new Object[]{
                        rs.getInt("uid"),
                        rs.getString("nombre"),
                        rs.getInt("puntos")
                    });
                }
            }
        } catch (SQLException ex) {
            error("Error cargando puntos: " + ex.getMessage());
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
        final String sql = "SELECT id, nombre, puntos, estado, creado_en FROM retos ORDER BY id DESC";
        try (Connection cn = BD.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
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
            "SELECT g.id, g.nombre AS grupo_nombre, " +
            "       COALESCE(COUNT(gp.usuario_id),0) AS miembros, " +
            "       (SELECT r.nombre FROM retos r WHERE r.id=g.reto_asignado) AS reto, " +
            "       g.reto_finalizado, g.creado_en " +
            "FROM grupos g " +
            "LEFT JOIN grupo_participantes gp ON gp.grupo_id = g.id " +
            "GROUP BY g.id, g.nombre, g.reto_asignado, g.reto_finalizado, g.creado_en " +
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

    // sobrecarga: usa conexión ya abierta
    private int obtenerIDUsuario(String correo, Connection cn) throws SQLException {
        PreparedStatement ps = cn.prepareStatement("SELECT id FROM usuarios WHERE correo=?");
        ps.setString(1, correo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        throw new SQLException("Usuario no encontrado para correo: " + correo);
    }

    // versión que abre conexión
    private int obtenerIDUsuario(String correo) throws SQLException {
        try (Connection cn = BD.conectar()) {
            return obtenerIDUsuario(correo, cn);
        }
    }

    private boolean esAdminDelGrupo(int grupoId, Connection cn) throws SQLException {
        if (usuarioLogueado == null) return false;
        int uid = obtenerIDUsuario(usuarioLogueado.getCorreo(), cn);
        PreparedStatement ps = cn.prepareStatement("SELECT * FROM grupo_admin WHERE grupo_id = ? AND usuario_id = ?");
        ps.setInt(1, grupoId);
        ps.setInt(2, uid);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    private void actualizarEtiquetasSesion() {
        String texto = "Usuario actual: " + (usuarioLogueado != null ? usuarioLogueado.getNombre() : "(no hay sesión)");
        lblSesionUsuarios.setText(texto);
        lblSesionRetos.setText(texto);
        lblSesionGrupos.setText(texto);

        Color color = (usuarioLogueado == null) ? Color.RED : Color.GREEN;
        lblSesionUsuarios.setForeground(color);
        lblSesionRetos.setForeground(color);
        lblSesionGrupos.setForeground(color);

        // actualizar label de grupo actual
        lblGrupoActual.setText("Grupo actual: " + (grupoActual != null ? grupoActual.getGrupoNombre() : "(ninguno)"));
    }

    private void limpiarCamposLogin() {
        uNombre.setText("");
        uCorreo.setText("");
        uCarne.setText("");
        uContra.setText("");
    }

    private void info(String msg){ JOptionPane.showMessageDialog(this, msg, "OK", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String msg){ JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg){ JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazGrafica::new);
    }
}
