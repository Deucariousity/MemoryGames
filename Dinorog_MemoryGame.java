package MemoryGames;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Dinorog_MemoryGame extends JFrame {
    private final int SIZE = 4;
    private JButton[][] buttons = new JButton[SIZE][SIZE];
    private int[][] numbers = new int[SIZE][SIZE];
    private boolean[][] matched = new boolean[SIZE][SIZE];
    private JButton firstButton = null, secondButton = null;
    private int firstRow = -1, firstCol = -1;
    private int tries = 0;
    private JLabel triesLabel;
    private ImageIcon[] imageIcons = new ImageIcon[8];

    public Dinorog_MemoryGame() {
        setTitle("BINI Memory Matching Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top label
        triesLabel = new JLabel("Tries: 0");
        triesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        triesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(triesLabel, BorderLayout.NORTH);

        // Grid panel
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        add(gridPanel, BorderLayout.CENTER);

        loadImages();
        initializeBoard();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(100, 100));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                btn.setBorder(UIManager.getBorder("Button.border"));
                buttons[i][j] = btn;

                final int row = i, col = j;
                btn.addActionListener(_ -> handleClick(row, col));

                gridPanel.add(btn);
            }
        }

        setSize(440, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadImages() {
        for (int i = 0; i < 8; i++) {
            String path = "MemoryGames/images/img" + (i + 1) + ".jpg";
            ImageIcon original = new ImageIcon(path);
            Image scaled = original.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageIcons[i] = new ImageIcon(scaled);
        }
    }

    private void initializeBoard() {
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            nums.add(i); // image index 0-7
            nums.add(i);
        }
        Collections.shuffle(nums);
        Iterator<Integer> it = nums.iterator();
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                numbers[i][j] = it.next();
    }

    private void handleClick(int row, int col) {
        if (matched[row][col] || buttons[row][col].getIcon() != null || firstButton == buttons[row][col]) return;

        buttons[row][col].setIcon(imageIcons[numbers[row][col]]);

        if (firstButton == null) {
            firstButton = buttons[row][col];
            firstRow = row;
            firstCol = col;
        } else {
            secondButton = buttons[row][col];
            disableAllButtons();

            Timer timer = new Timer(800, _ -> {
                checkMatch(row, col);
                enableUnmatchedButtons();

                if (isGameOver()) {
                    int option = JOptionPane.showConfirmDialog(
                        this,
                        "🎉 You matched all cards in " + tries + " tries!\nDo you want to try again?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        resetGame();
                    } else {
                        System.exit(0);
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void checkMatch(int row2, int col2) {
        tries++;
        triesLabel.setText("Tries: " + tries);

        if (numbers[firstRow][firstCol] == numbers[row2][col2]) {
            matched[firstRow][firstCol] = true;
            matched[row2][col2] = true;

            ImageIcon icon1 = imageIcons[numbers[firstRow][firstCol]];
            ImageIcon icon2 = imageIcons[numbers[row2][col2]];

            firstButton.setDisabledIcon(icon1);
            secondButton.setDisabledIcon(icon2);

            firstButton.setEnabled(false);
            secondButton.setEnabled(false);
        } else {
            firstButton.setIcon(null);
            secondButton.setIcon(null);
        }

        firstButton = null;
        secondButton = null;
        firstRow = -1;
        firstCol = -1;
    }

    private void disableAllButtons() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                buttons[i][j].setEnabled(false);
    }

    private void enableUnmatchedButtons() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (!matched[i][j])
                    buttons[i][j].setEnabled(true);
    }

    private boolean isGameOver() {
        for (boolean[] row : matched)
            for (boolean val : row)
                if (!val) return false;
        return true;
    }

    private void resetGame() {
        tries = 0;
        triesLabel.setText("Tries: 0");
        firstButton = null;
        secondButton = null;
        firstRow = -1;
        firstCol = -1;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matched[i][j] = false;
                buttons[i][j].setEnabled(true);
                buttons[i][j].setIcon(null);
                buttons[i][j].setDisabledIcon(null);
            }
        }

        initializeBoard(); // Reshuffle the cards
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dinorog_MemoryGame::new);
    }
}