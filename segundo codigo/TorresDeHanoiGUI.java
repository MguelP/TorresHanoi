import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class TorresDeHanoiGUI extends JFrame {
    private ArrayList<Integer>[] torres;
    private JButton moveButton;
    private JButton exitButton;
    private JButton updateDiscosButton;
    private JButton showHistoryButton;
    private JButton saveButton;
    private JButton loadButton;
    private JTextField numDiscosField;
    private JComboBox<Integer> fromTorreCombo;
    private JComboBox<Integer> toTorreCombo;
    private JTextArea movimientosArea;
    private TorresPanel torresPanel;
    private ArrayList<String> movimientos;

    private static final int NUM_TORRES = 3;
    private static final String SAVE_FILE = "torresdehanoi_save.txt";

    public TorresDeHanoiGUI() {
        setTitle("Torres de Hanoi");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        torres = new ArrayList[NUM_TORRES];
        for (int i = 0; i < NUM_TORRES; i++) {
            torres[i] = new ArrayList<>();
        }

        movimientos = new ArrayList<>();
        torresPanel = new TorresPanel(torres);
        setLayout(new BorderLayout());

        moveButton = new JButton("Mover Disco");
        moveButton.setPreferredSize(new Dimension(150, 40));

        updateDiscosButton = new JButton("Actualizar Discos");
        updateDiscosButton.setPreferredSize(new Dimension(150, 40));

        exitButton = new JButton("Salir");
        exitButton.setPreferredSize(new Dimension(150, 40));

        showHistoryButton = new JButton("Mostrar Historial");
        showHistoryButton.setPreferredSize(new Dimension(150, 40));

        saveButton = new JButton("Guardar Juego");
        saveButton.setPreferredSize(new Dimension(150, 40));

        loadButton = new JButton("Cargar Juego");
        loadButton.setPreferredSize(new Dimension(150, 40));

        numDiscosField = new JTextField(5);
        fromTorreCombo = new JComboBox<>(new Integer[]{1, 2, 3});
        toTorreCombo = new JComboBox<>(new Integer[]{1, 2, 3});
        movimientosArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(movimientosArea);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Número de Discos:"), gbc);
        inputPanel.add(numDiscosField, gbc);
        inputPanel.add(new JLabel("Mover desde Torre:"), gbc);
        inputPanel.add(fromTorreCombo, gbc);
        inputPanel.add(new JLabel("Mover a Torre:"), gbc);
        inputPanel.add(toTorreCombo, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 7, 10, 10));
        buttonPanel.add(moveButton);
        buttonPanel.add(updateDiscosButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(showHistoryButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(inputPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.add(new JLabel("Movimientos:"), BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        add(torresPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(logPanel, BorderLayout.EAST);

        moveButton.addActionListener(e -> moverDisco());
        exitButton.addActionListener(e -> salirJuego());
        updateDiscosButton.addActionListener(e -> actualizarDiscos());
        showHistoryButton.addActionListener(e -> mostrarHistorial());
        saveButton.addActionListener(e -> {
            try {
                guardarJuego();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Hubo un error al guardar el juego.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> cargarJuego());

        String instrucciones = "Instrucciones:\n" +
            "1. El objetivo del juego es mover todos los discos de la torre de origen a la torre de destino.\n" +
            "2. Solo puedes mover un disco a la vez.\n" +
            "3. No puedes colocar un disco más grande sobre uno más pequeño.\n" +
            "4. Usa los botones para realizar movimientos, guardar o cargar el juego.\n" +
            "¡Buena suerte!";

        JOptionPane.showMessageDialog(this, instrucciones, "Bienvenida al juego de Torres de Hanoi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarHistorial() {
        if (movimientos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay movimientos registrados aún.", "Historial vacío", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder historial = new StringBuilder("Historial de Movimientos:\n");
            for (String movimiento : movimientos) {
                historial.append(movimiento).append("\n");
            }
            // Actualizar el JTextArea con el historial
            movimientosArea.setText(historial.toString());
        }
    }

    private void moverDisco() {
        try {
            int fromTorre = fromTorreCombo.getSelectedIndex();
            int toTorre = toTorreCombo.getSelectedIndex();

            if (fromTorre == toTorre) {
                JOptionPane.showMessageDialog(this, "¡No puedes mover el disco a la misma torre!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (torres[fromTorre].isEmpty()) {
                JOptionPane.showMessageDialog(this, "La torre de origen está vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer disco = torres[fromTorre].remove(torres[fromTorre].size() - 1);

            if (!torres[toTorre].isEmpty() && torres[toTorre].get(torres[toTorre].size() - 1) < disco) {
                JOptionPane.showMessageDialog(this, "No puedes colocar un disco más grande sobre uno más pequeño.", "Error", JOptionPane.ERROR_MESSAGE);
                torres[fromTorre].add(disco);
                return;
            }

            torres[toTorre].add(disco);
            String movimiento = "Mover disco de Torre " + (fromTorre + 1) + " a Torre " + (toTorre + 1);
            movimientos.add(movimiento);
            
            // Actualizar el área de texto con el historial de movimientos
            movimientosArea.append(movimiento + "\n");
            
            torresPanel.repaint();

            if (torres[2].size() == Integer.parseInt(numDiscosField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "¡Felicidades! ¡Has ganado el juego!", "Juego Terminado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hubo un error al mover el disco.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarDiscos() {
        try {
            int numDiscos = Integer.parseInt(numDiscosField.getText().trim());

            if (numDiscos <= 0) {
                JOptionPane.showMessageDialog(this, "El número de discos debe ser mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < NUM_TORRES; i++) {
                torres[i].clear();
            }

            for (int i = numDiscos; i > 0; i--) {
                torres[0].add(i);
            }

            torresPanel.repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido de discos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salirJuego() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres salir?", "Salir", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void guardarJuego() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            String numDiscosText = numDiscosField.getText().trim();
            if (numDiscosText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa un número de discos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int numDiscos = Integer.parseInt(numDiscosText);
            writer.write(numDiscos + "\n");

            for (int i = 0; i < NUM_TORRES; i++) {
                for (Integer disco : torres[i]) {
                    writer.write(disco + " ");
                }
                writer.write("\n");
            }

            for (String movimiento : movimientos) {
                writer.write(movimiento + "\n");
            }

            JOptionPane.showMessageDialog(this, "El juego ha sido guardado correctamente.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cargarJuego() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line = reader.readLine();
            if (line == null) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar el juego, archivo vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int numDiscos = Integer.parseInt(line);
            numDiscosField.setText(String.valueOf(numDiscos));

            for (int i = 0; i < NUM_TORRES; i++) {
                torres[i].clear();
                line = reader.readLine();
                if (line != null) {
                    String[] discos = line.split(" ");
                    for (String disco : discos) {
                        if (!disco.isEmpty()) {
                            torres[i].add(Integer.parseInt(disco));
                        }
                    }
                }
            }

            movimientos.clear();
            while ((line = reader.readLine()) != null) {
                movimientos.add(line);
            }

            torresPanel.repaint();
            mostrarHistorial();
            JOptionPane.showMessageDialog(this, "El juego ha sido cargado correctamente.", "Cargar", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Hubo un error al cargar el juego.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class TorresPanel extends JPanel {
        private ArrayList<Integer>[] torres;

        public TorresPanel(ArrayList<Integer>[] torres) {
            this.torres = torres;
            setPreferredSize(new Dimension(600, 400));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Dibujar las torres y los discos
            int baseY = getHeight() - 30;
            int espacioX = 200;

            for (int i = 0; i < NUM_TORRES; i++) {
                int torreX = 100 + i * espacioX;
                g.setColor(Color.BLACK);
                g.fillRect(torreX - 10, baseY - 150, 20, 150); // Dibujar la torre

                for (int j = 0; j < torres[i].size(); j++) {
                    int disco = torres[i].get(j);
                    g.setColor(new Color(255 - disco * 10, 255 - disco * 20, 255)); // Diferentes colores para los discos
                    g.fillRect(torreX - disco * 10, baseY - 20 * (j + 1), disco * 20, 20);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TorresDeHanoiGUI gui = new TorresDeHanoiGUI();
            gui.setVisible(true);
        });
    }
}
