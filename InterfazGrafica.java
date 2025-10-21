
import Modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


 InterfazGrafica (Swing)
public class InterfazGrafica extends JFrame {

    // === Modelo de dominio ===
    private final Administrador admin = new Administrador("Admin", "admin@demo", "123");

    // === Componentes comunes ===
    private final JTabbedPane tabs = new JTabbedPane();

    // === Usuarios ===
    private final JTextField uNombre = new JTextField();
    private final JTextField uCorreo = new JTextField();
    private final JTextField uCarne  = new JTextField();
    private final DefaultTableModel uModel = new DefaultTableModel(
            new String[]{"Nombre", "Correo", "Carné"}, 0);
    private final JTable uTable = new JTable(uModel);

    // === Retos ===
    private final JTextField rNombre = new JTextField();
    private final JTextArea  rDesc   = new JTextArea(3, 20);
    private final JSpinner   rPuntos = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
    private final JCheckBox  rEstado = new JCheckBox("Activo");
    private final DefaultTableModel rModel = new DefaultTableModel(
            new String[]{"Nombre", "Puntos", "Activo"}, 0);
    private final JTable rTable = new JTable(rModel);

    // === Grupos ===
    private final JTextField gNombre = new JTextField();
    private final DefaultTableModel gModel = new DefaultTableModel(
            new String[]{"Nombre", "Miembros"}, 0);
    private final JTable gTable = new JTable(gModel);

    public InterfazGrafica() {
        super("PROYECTO-COMPETENCIA — Interfaz Gráfica");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabs.addTab("Usuarios", buildUsuariosPanel());
        tabs.addTab("Retos",    buildRetosPanel());
        tabs.addTab("Grupos",   buildGruposPanel());

        add(tabs, BorderLayout.CENTER);

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

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Nombre"), c);
        c.gridx = 1; c.gridy = row; form.add(uNombre, c); row++;

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Correo"), c);
        c.gridx = 1; c.gridy = row; form.add(uCorreo, c); row++;

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Carné"), c);
        c.gridx = 1; c.gridy = row; form.add(uCarne, c); row++;

        JButton add = new JButton("Registrar usuario");
        add.addActionListener(this::onAgregarUsuario);
        c.gridx = 1; c.gridy = row; form.add(add, c);

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
        c.gridx = 0; c.gridy = row; form.add(new JLabel("Nombre"), c);
        c.gridx = 1; c.gridy = row; form.add(rNombre, c); row++;

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Descripción"), c);
        c.gridx = 1; c.gridy = row; c.weightx = 1.0;
        form.add(new JScrollPane(rDesc), c); row++; c.weightx = 0;

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Puntos"), c);
        c.gridx = 1; c.gridy = row; form.add(rPuntos, c); row++;

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Estado"), c);
        c.gridx = 1; c.gridy = row; form.add(rEstado, c); row++;

        JButton add = new JButton("Agregar reto");
        add.addActionListener(this::onAgregarReto);
        c.gridx = 1; c.gridy = row; form.add(add, c);

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
        c.gridx = 0; c.gridy = row; form.add(new JLabel("Nombre del grupo"), c);
        c.gridx = 1; c.gridy = row; form.add(gNombre, c); row++;

        JButton add = new JButton("Crear grupo");
        add.addActionListener(this::onCrearGrupo);
        c.gridx = 1; c.gridy = row; form.add(add, c);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(gTable), BorderLayout.CENTER);
        return panel;
    }

    // === Acciones ===

    private void onAgregarUsuario(ActionEvent e) {
        String nombre = uNombre.getText().trim();
        String correo = uCorreo.getText().trim();
        String carne  = uCarne.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || carne.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = admin.registrarUsuario(nombre, correo, carne);
        if (ok) {
            uNombre.setText("");
            uCorreo.setText("");
            uCarne.setText("");
            refrescarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "El usuario no pudo registrarse.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAgregarReto(ActionEvent e) {
        String nombre = rNombre.getText().trim();
        String descripcion = rDesc.getText().trim();
        int puntos = ((Number) rPuntos.getValue()).intValue();
        boolean estado = rEstado.isSelected();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y descripción son obligatorios.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = admin.registrarReto(nombre, descripcion, puntos, estado);
        if (ok) {
            rNombre.setText("");
            rDesc.setText("");
            rPuntos.setValue(10);
            rEstado.setSelected(false);
            refrescarRetos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el reto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCrearGrupo(ActionEvent e) {
        String nombre = gNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe un nombre para el grupo.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Grupo creado = admin.crearGrupo(nombre); 
        if (creado != null) {
            gNombre.setText("");
            refrescarGrupos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear el grupo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === Refrescos ===

    private void refrescarUsuarios() {
        // Vaciar
        while (uModel.getRowCount() > 0) uModel.removeRow(0);
        List<Usuario> lista = admin.getUsuarios();
        for (Usuario u : lista) {
            String nombre = safeGet(u, "nombre");
            String correo = safeGet(u, "correo");
            String carne  = safeGet(u, "carne");
            uModel.addRow(new Object[]{nombre, correo, carne});
        }
    }

    private void refrescarRetos() {
        while (rModel.getRowCount() > 0) rModel.removeRow(0);
        List<Retos> lista = admin.getRetos();
        for (Retos r : lista) {
            String nombre = r.getNombreReto(); // existe en tu clase
            int puntos    = r.getPuntos();
            boolean act   = r.isEstado();
            rModel.addRow(new Object[]{nombre, puntos, act});
        }
    }

    @SuppressWarnings("unchecked")
    private void refrescarGrupos() {
        while (gModel.getRowCount() > 0) gModel.removeRow(0);
        List<Grupo> lista = admin.getGrupos();
        for (Grupo g : lista) {
            String nombre = safeGet(g, "grupoNombre");
            int miembros = 0;
            try {
                Field f = g.getClass().getDeclaredField("participantes");
                f.setAccessible(true);
                Object val = f.get(g);
                if (val instanceof List) miembros = ((List<?>)val).size();
            } catch (Exception ignored) {}
            gModel.addRow(new Object[]{nombre, miembros});
        }
    }

    // === Utilidad: leer campo privado como String ===
    private String safeGet(Object obj, String field) {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            Object v = f.get(obj);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazGrafica::new);
    }
}
