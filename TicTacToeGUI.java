import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeGUI extends JFrame {
    private char[][] board;
    private char currentPlayer;
    private JButton[][] buttons;
    private JLabel statusLabel, scoreLabel;
    private String player1Name, player2Name;
    private int player1Score, player2Score, totalMatches;
    private JComboBox<String> levelComboBox;
    private String difficultyLevel;
    private boolean isPlayerVsComputer;
    private String currentPlayerName;

    public TicTacToeGUI() {
        setTitle("Tic-Tac-Toe Game");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel for Player Names and Difficulty
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        // Player 1 name input
        inputPanel.add(new JLabel("Enter Player 1 Name:"));
        JTextField player1Field = new JTextField();
        inputPanel.add(player1Field);

        // Player 2 name input (only shown if Player vs Player is selected)
        JLabel player2Label = new JLabel("Enter Player 2 Name:");
        JTextField player2Field = new JTextField();
        inputPanel.add(player2Label);
        inputPanel.add(player2Field);

        // Difficulty Level dropdown
        inputPanel.add(new JLabel("Choose Difficulty:"));
        levelComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Difficult"});
        inputPanel.add(levelComboBox);

        // Mode selection for PvP or PvC
        inputPanel.add(new JLabel("Choose Game Mode:"));
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"Player vs Player", "Player vs Computer"});
        inputPanel.add(modeComboBox);

        // Start Button to begin the game
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> {
            player1Name = player1Field.getText();
            difficultyLevel = (String) levelComboBox.getSelectedItem();
            String mode = (String) modeComboBox.getSelectedItem();
            isPlayerVsComputer = mode.equals("Player vs Computer");

            if (isPlayerVsComputer) {
                player2Name = "Computer";  // Automatically set player 2 as "Computer"
                player2Label.setVisible(false);  // Hide the second player label and input field
                player2Field.setVisible(false);
            } else {
                player2Name = player2Field.getText();  // Player 2 name input if Player vs Player mode
                player2Label.setVisible(true);
                player2Field.setVisible(true);
            }

            // Validate if Player 1 name is entered
            if (player1Name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Player 1's name.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                initializeGame();
            }
        });

        inputPanel.add(startButton);

        // Add input panel at the top of the screen
        add(inputPanel, BorderLayout.NORTH);

        // Game Panel with buttons for the Tic-Tac-Toe grid
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));

        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton(" ");
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setEnabled(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                gamePanel.add(buttons[i][j]);
            }
        }

        // Status label at the center of the window
        statusLabel = new JLabel("Player 1's turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(statusLabel, BorderLayout.CENTER);

        // Score label below the game grid to show score and match count
        scoreLabel = new JLabel("Score: " + player1Name + " 0 - 0 " + player2Name, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(scoreLabel, BorderLayout.SOUTH);

        // Add the game panel at the bottom
        add(gamePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void initializeGame() {
        board = new char[3][3];
        currentPlayer = 'X';
        totalMatches++;

        // Reset all buttons and board for the new game
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                buttons[i][j].setText(" ");
                buttons[i][j].setEnabled(true);
            }
        }

        currentPlayerName = player1Name; // Player 1 starts
        statusLabel.setText(player1Name + "'s turn");

        if (isPlayerVsComputer) {
            // If Player vs Computer, disable player2 input
            player2Name = "Computer";
            statusLabel.setText(player1Name + "'s turn");
            if (currentPlayer == 'O') {
                // Make computer play after player 1 starts
                computerMove();
            }
        }
    }

    private void displayWinner(char winner) {
        String winnerName = (winner == 'X') ? player1Name : player2Name;
        String message = winnerName + " wins!";
        if (winner == ' ') {
            message = "It's a tie!";
        }

        // Display winner message in dialog
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        // Update the score and total matches
        if (winner == 'X') {
            player1Score++;
        } else if (winner == 'O') {
            player2Score++;
        }

        // Update the score label with the new scores and match count
        scoreLabel.setText("Score: " + player1Name + " " + player1Score + " - " + player2Score + " " + player2Name);
        statusLabel.setText("Match " + totalMatches + " Results: " + winnerName + " won!");

        // Reset the game after showing the result
        initializeGame();
    }

    private boolean checkWin() {
        // Check rows and columns for a win
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) ||
                (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer)) {
                return true;
            }
        }
        // Check diagonals for a win
        if ((board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) ||
            (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer)) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void computerMove() {
        // Make a random move for the computer
        Random rand = new Random();
        List<int[]> availableMoves = getAvailableMoves();
        if (availableMoves.isEmpty()) return;

        int[] move = availableMoves.get(rand.nextInt(availableMoves.size()));
        int row = move[0];
        int col = move[1];
        board[row][col] = 'O';
        buttons[row][col].setText("O");

        if (checkWin()) {
            displayWinner('O');
            return;
        }

        if (isBoardFull()) {
            displayWinner(' ');
            return;
        }

        currentPlayer = 'X';
        currentPlayerName = player1Name;
        statusLabel.setText(player1Name + "'s turn");
    }

    private List<int[]> getAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (board[row][col] == ' ' && !isBoardFull()) {
                board[row][col] = currentPlayer;
                buttons[row][col].setText(String.valueOf(currentPlayer));

                if (checkWin()) {
                    displayWinner(currentPlayer);
                    return;
                }

                if (isBoardFull()) {
                    displayWinner(' ');
                    return;
                }

                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                currentPlayerName = (currentPlayer == 'X') ? player1Name : player2Name;
                statusLabel.setText(currentPlayerName + "'s turn");

                if (isPlayerVsComputer && currentPlayer == 'O') {
                    computerMove();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
