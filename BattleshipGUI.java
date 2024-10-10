import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleshipGUI {
    private static final int SIZE = 6;
    private static final int NUMBER_OF_2CELL_SHIPS = 4; // Number of 2-cell ships
    private static final int NUMBER_OF_3CELL_SHIPS = 1; // Number of 3-cell ships
    private static final int MAX_ATTEMPTS = 15; // Maximum number of attempts
    private JButton[][] buttons = new JButton[SIZE][SIZE];
    private JButton[][] shipButtons = new JButton[SIZE][SIZE]; // For ship display
    private boolean[][] ships = new boolean[SIZE][SIZE];
    private List<List<Point>> shipLocations = new ArrayList<>();
    private int attempts = 0;
    private boolean gameOver = false;
    private JLabel attemptsLabel;
    private static final Dimension BUTTON_SIZE = new Dimension(60, 60);
    private static final Dimension SHOW_SHIPS_BUTTON_SIZE = new Dimension(120, 40);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // Initialize the ocean grid and place the ships
        initializeOcean();
        placeShips();

        // Create and set up the main window
        JFrame frame = new JFrame("Battleship Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a panel for the grid
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(SIZE, SIZE));

        // Create buttons for the grid and add them to the panel
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton button = new JButton("~");
                button.setPreferredSize(BUTTON_SIZE);
                button.setBackground(Color.CYAN); // Ocean blue
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border
                button.addActionListener(new ButtonClickListener(row, col));
                buttons[row][col] = button;
                gridPanel.add(button);
            }
        }

        // Create and add the Show Ship Locations button
        JButton showShipsButton = new JButton("Show Ship Locations");
        showShipsButton.setPreferredSize(SHOW_SHIPS_BUTTON_SIZE);
        showShipsButton.addActionListener(e -> createShipDisplayGUI());

        // Create a panel for the status
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout());

        // Create and add the Attempts Label
        attemptsLabel = new JLabel("Attempts Left: " + (MAX_ATTEMPTS - attempts));
        statusPanel.add(attemptsLabel);

        // Add grid panel, Show Ships button, and status panel to the frame
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(showShipsButton, BorderLayout.SOUTH);
        frame.add(statusPanel, BorderLayout.NORTH);

        // Display the main window
        frame.pack();
        frame.setVisible(true);
    }

    private void initializeOcean() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                ships[row][col] = false; // No ships initially
            }
        }
    }

    private void placeShips() {
        Random random = new Random();

        // Place 2-cell ships
        for (int i = 0; i < NUMBER_OF_2CELL_SHIPS; i++) {
            placeShipOfLength(random, 2);
        }

        // Place 3-cell ship
        placeShipOfLength(random, 3);
    }

    private void placeShipOfLength(Random random, int length) {
        boolean placed = false;
        while (!placed) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            boolean horizontal = random.nextBoolean();

            if (canPlaceShip(row, col, horizontal, length)) {
                List<Point> shipCells = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    if (horizontal) {
                        ships[row][col + i] = true;
                        shipCells.add(new Point(row, col + i));
                    } else {
                        ships[row + i][col] = true;
                        shipCells.add(new Point(row + i, col));
                    }
                }
                shipLocations.add(shipCells); // Add ship's locations to the list
                placed = true;
            }
        }
    }

    private boolean canPlaceShip(int row, int col, boolean horizontal, int length) {
        if (horizontal) {
            if (col + length > SIZE) return false;

            for (int i = 0; i < length; i++) {
                if (ships[row][col + i]) return false;
            }

            // Check surrounding area for gaps
            if (col - 1 >= 0 && (ships[row][col - 1] || (col + length < SIZE && ships[row][col + length]))) return false;
            if (row - 1 >= 0 && (ships[row - 1][col] || (row - 1 >= 0 && col + length < SIZE && ships[row - 1][col + length]))) return false;
            if (row + 1 < SIZE && (ships[row + 1][col] || (row + 1 < SIZE && col + length < SIZE && ships[row + 1][col + length]))) return false;
        } else {
            if (row + length > SIZE) return false;

            for (int i = 0; i < length; i++) {
                if (ships[row + i][col]) return false;
            }

            // Check surrounding area for gaps
            if (row - 1 >= 0 && (ships[row - 1][col] || (row + length < SIZE && ships[row + length][col]))) return false;
            if (col - 1 >= 0 && (ships[row][col - 1] || (row + length < SIZE && ships[row + length][col - 1]))) return false;
            if (col + 1 < SIZE && (ships[row][col + 1] || (row + length < SIZE && ships[row + length][col + 1]))) return false;
        }
        return true;
    }

    private void createShipDisplayGUI() {
        // Create and set up the ship display window
        JFrame shipFrame = new JFrame("Ship Locations");
        shipFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        shipFrame.setLayout(new GridLayout(SIZE, SIZE));

        // Create buttons for the ship display grid and add them to the frame
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(BUTTON_SIZE);
                if (ships[row][col]) {
                    button.setBackground(Color.RED); // Show ship locations in red
                } else {
                    button.setBackground(Color.CYAN); // Ocean blue for empty cells
                }
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border
                shipButtons[row][col] = button;
                shipFrame.add(button);
            }
        }

        // Display the ship display window
        shipFrame.pack();
        shipFrame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameOver || attempts >= MAX_ATTEMPTS) return; // No more actions if the game is over or attempts are maxed out

            JButton button = buttons[row][col];
            String buttonText = button.getText();

            // Check if the cell has already been clicked
            if (!buttonText.equals("~")) {
                return; // Do nothing if the cell is already hit
            }

            attempts++;

            if (ships[row][col]) {
                button.setText("BOOM!");
                button.setBackground(Color.RED); // Change to red on hit
                checkAndUpdateShipStatus(row, col);
            } else {
                button.setText("Miss");
                button.setBackground(Color.BLUE); // Change to blue on miss
            }

            updateAttemptsLabel();

            if (gameOver) {
                JOptionPane.showMessageDialog(null, "Congratulations! You won the game in " + attempts + " attempts!");
                disableAllButtons();
            } else if (attempts >= MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(null, "Game Over! You have used all your attempts.");
                disableAllButtons();
            }
        }
    }

    private void checkAndUpdateShipStatus(int row, int col) {
        for (List<Point> ship : shipLocations) {
            boolean isDestroyed = true;
            for (Point p : ship) {
                if (buttons[p.x][p.y].getText().equals("~")) {
                    isDestroyed = false;
                    break;
                }
            }
            if (isDestroyed) {
                for (Point p : ship) {
                    buttons[p.x][p.y].setBackground(Color.GRAY); // Change to grey for destroyed ship
                }
                shipLocations.remove(ship);
                break;
            }
        }
        if (shipLocations.isEmpty()) {
            gameOver = true;
        }
    }

    private void updateAttemptsLabel() {
        attemptsLabel.setText("Attempts Left: " + (MAX_ATTEMPTS - attempts));
    }

    private void disableAllButtons() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                buttons[row][col].setEnabled(false);
            }
        }
    }
}