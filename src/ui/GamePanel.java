package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.Move;
import game.Player;
import main.Main;
import model.Colors;
import model.Piece;
import model.Position;
import observers.CheckObserver;
import observers.LoggerObserver;
import pieces.PieceFactory;
import strategies.EndGameCondition;
import ui.ResultPanel.ResultType;

public class GamePanel extends JPanel {
    private final Main mainApp;
    private final MainFrame frame;
    private final MenuPanel menuPanel;

    // Am nevoie de flag-urile check si checkmate
    private final CheckObserver checkObserver;

    private final Game game;

    private JLabel turnLabel;
    private JLabel turnDot;
    private JLabel scoreLabel;
    
    private DefaultListModel<String> moveListModel;

    private JPanel userCaptureIcons;
    private JPanel computerCaptureIcons;

    // Pentru Back to Menu
    private final boolean discardOnBackMenu;
    private List<Piece> savedBoardPieces;
    private List<Move> savedMoves;
    private Colors savedCurrentColor;
    private LoggerObserver loggerObserver;

    // Tabla de joc
    private JButton[][] boardSquares;
    private Position selectedPosition;
    private List<Position> selectedPossibleMoves;
    private List<Position> highlightedMoves;

    // Final de joc
    private boolean gameOverPending;
    private EndGameCondition pendingCondition;
    private ResultPanel.ResultType pendingResultType;
    private int pendingPoints;
    private int pendingTotalPoints;
    private JButton continueButton;

    private final Color lightSquare = new Color(236, 220, 187);
    private final Color darkSquare = new Color(175, 128, 86);
    private final Color highlightSquare = new Color(180, 205, 90);
    private final Color selectedSquare = new Color(140, 180, 70);

    public GamePanel(Main mainApp, MainFrame frame, Game game, MenuPanel menuPanel, CheckObserver checkObserver, boolean discardOnBackMenu) {
        this.mainApp = mainApp;
        this.frame = frame;
        this.game = game;
        this.menuPanel = menuPanel;
        this.checkObserver = checkObserver;
        this.discardOnBackMenu = discardOnBackMenu;

        setLayout(new BorderLayout());
        
        BackgroundPanel background = new BackgroundPanel("assets/photos/background.png");
        background.setLayout(new BorderLayout());

        JPanel overlay = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(18, 23, 33, 210));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.add(buildTopBar(), BorderLayout.NORTH);
        overlay.add(buildMainArea(), BorderLayout.CENTER);

        background.add(overlay, BorderLayout.CENTER);
        add(background, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(true);
        top.setBackground(new Color(28, 34, 46));
        top.setBorder(BorderFactory.createEmptyBorder(20, 32, 12, 32));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        ImageIcon crowIcon = new ImageIcon("assets/pieces/king.png");
        Image scaled = crowIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(scaled));

        JLabel title = new JLabel("Chess Master");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        left.add(icon);
        left.add(Box.createHorizontalStrut(10));
        left.add(title);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);

        turnDot = new JLabel("\u25CF");
        turnDot.setFont(new Font("SansSerif", Font.BOLD, 12));
        turnDot.setForeground(Color.GREEN);

        turnLabel = new JLabel("Your turn");
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        turnLabel.setForeground(Color.WHITE);

        center.add(turnDot);
        center.add(Box.createHorizontalStrut(6));
        center.add(turnLabel);

        scoreLabel = new JLabel("\u2605 0 pts", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(230, 200, 90));

        top.add(left, BorderLayout.WEST);
        top.add(center, BorderLayout.CENTER);
        top.add(scoreLabel, BorderLayout.EAST);

        return top;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(10, 24, 20, 24));

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(buildMoveHistoryPanel(), BorderLayout.CENTER);
        // Inaltimea este flexibila (height == 0)
        left.setPreferredSize(new Dimension(220, 0));
        left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JPanel center = buildBoardArea();

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(220, 0));
        right.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JPanel captured = new JPanel(new BorderLayout());
        captured.setOpaque(false);
        captured.add(buildCapturedPiecesPanel());

        right.add(captured, BorderLayout.CENTER);
        right.add(buildButtons(), BorderLayout.SOUTH);

        main.add(left, BorderLayout.WEST);
        main.add(center, BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);

        return main;
    }

    private JPanel buildMoveHistoryPanel() {
        JPanel moveHistoryPanel = new RoundedPanel(16, new Color(28, 34, 46));
        moveHistoryPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        moveHistoryPanel.setLayout(new BorderLayout());
        
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

        JLabel menuIcon = new JLabel("\u2630");
        menuIcon.setFont(new Font("SansSerif", Font.BOLD, 18));
        menuIcon.setForeground(new Color(200, 210, 225));

        JLabel title = new JLabel("Move History");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        header.add(menuIcon);
        header.add(Box.createHorizontalStrut(12));
        header.add(title);

        moveListModel = new DefaultListModel<>();
        JList<String> moveList = new JList<>(moveListModel);
        moveList.setOpaque(false);
        moveList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        moveList.setForeground(new Color(200, 210, 225));
        moveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moveList.setFocusable(false);
        moveList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, false, false);
                label.setOpaque(false);
                label.setForeground(new Color(200, 210, 225));
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(moveList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        moveHistoryPanel.add(header, BorderLayout.NORTH);
        moveHistoryPanel.add(scroll, BorderLayout.CENTER);

        return moveHistoryPanel;
    }

    private JPanel buildBoardArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel board = buildBoardPanel();
        container.add(board, BorderLayout.CENTER);

        JPanel continueRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        continueRow.setOpaque(false);
        continueRow.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        continueButton = new RoundedButton("Continue", new Color(80, 160, 80), 16);
        continueButton.setPreferredSize(new Dimension(220, 32));
        continueButton.setVisible(false);
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleContinueToResult();
            }
        });

        continueRow.add(continueButton);
        container.add(continueRow, BorderLayout.SOUTH);

        return container;
    }


    private JPanel buildBoardPanel() {
        boardSquares = new JButton[8][8];
        highlightedMoves = new ArrayList<>();

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel boardFrame = new JPanel(new BorderLayout());
        boardFrame.setOpaque(true);
        boardFrame.setBackground(new Color(120, 90, 60));
        boardFrame.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        boardFrame.setPreferredSize(new Dimension(520, 520));

        JPanel grid = new JPanel(new GridLayout(8,8));
        grid.setOpaque(true);
        grid.setBackground(new Color(90, 70, 50));

        int row, col;
        for (row = 0; row < 8; row++) {
            for (col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);
                button.setOpaque(true);
                
                // ActionListener ma obliga sa le fac final
                final int r = row;
                final int c = col;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleSquareClick(r, c);
                    }
                });

                boardSquares[row][col] = button;
                grid.add(button);
            }
        }

        boardFrame.add(grid, BorderLayout.CENTER);
        wrapper.add(boardFrame);

        refreshBoard();
        return wrapper;
    }

    private void handleSquareClick(int row, int col) {
        if (gameOverPending) {
            showContinueOnlyPopup();
            return;
        }

        if (game == null) {
            return;
        }

        Player human = game.getHumanPlayer();
        Player current = game.getCurrentPlayer();
        if (human == null || current == null || !current.equals(human)) {
            return;
        }

        Position toPosition = uiToPosition(row, col);
        if (selectedPosition == null) {
            Piece piece = game.getBoard().getPieceAt(toPosition);
            if (piece == null || piece.getColor() != human.getColor()) {
                return;
            }
            selectedPosition = toPosition;
            selectedPossibleMoves = piece.getPossibleMoves(game.getBoard());
            highlightedMoves = getValidMoves(toPosition, selectedPossibleMoves);
            
            // Pop
            if (checkObserver != null && checkObserver.isCheck() && human.equals(checkObserver.getCheckedPlayer()) && (highlightedMoves == null || highlightedMoves.isEmpty())) {
                showInfo("Esti in sah! Aceasta piesa nu poate proteja regele.");
                clearSelection();
                return;
            }

            refreshBoard();
            return;
        }

        // User-ul da click din nou pe piesa
        if (selectedPosition.equals(toPosition)) {
            clearSelection();
            return;
        }

        // Nu respecta regulile sahului
        if (selectedPossibleMoves == null || !selectedPossibleMoves.contains(toPosition)) {
            showError("Mutare invalida: piesa nu se poate muta acolo!");
            clearSelection();
            return;
        }
        // User-ul da click pe o mutare invalida
        if (highlightedMoves == null || !highlightedMoves.contains(toPosition)) {
            showError("Mutare ilegala: regele ramane in sah!");
            clearSelection();
            return;
        }

        humanMove(selectedPosition, toPosition);
    }

    // Helpere pentru pop-uri
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Mutare invalida", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sah", JOptionPane.INFORMATION_MESSAGE);
    }

    private void humanMove(Position from, Position to) {
        Player human = game.getHumanPlayer();
        Piece captured = game.getBoard().getPieceAt(to);
        // Pentru promotion
        Piece pieceToMove = game.getBoard().getPieceAt(from);

        char promotionPawnChar = 'Q';
        if (pieceToMove != null && pieceToMove.type() == 'P') {
            int targetRank = to.getY();
            if ((pieceToMove.getColor() == Colors.WHITE && targetRank == 8)
                    || (pieceToMove.getColor() == Colors.BLACK && targetRank == 1)) {
                promotionPawnChar = askPromotionPawnChoice(pieceToMove.getColor());
            }
        }
        
        try {
            human.makeMove(from, to, game.getBoard(), promotionPawnChar);
            game.addMove(human, from, to, captured);
            game.switchPlayer();
            
            if ((checkObserver != null && checkObserver.isCheckMate()) || game.checkForStaleMate() || game.checkDrawByRepetition()) {
                handleGameOver(null);
                return;
            }
            clearSelection();
            refreshBoard();
            computerMove();
        } catch (InvalidMoveException | InvalidCommandException e1) {
            showError(e1.getMessage());
            clearSelection();
        }
    }

    private char askPromotionPawnChoice(Colors color) {
        char[] types = {'Q', 'R', 'B', 'N'};
        Object[] options = new Object[types.length];

        int i;
        for (i = 0; i < types.length; i++) {
            try {
                Piece auxPiece = PieceFactory.createPiece(types[i] , color, new Position('A', 1));
                options[i] = getPieceIcon(auxPiece, 32);
            } catch (InvalidCommandException e) {
            }
        }
        
        int choice = -1;
        while (choice < 0) {
            choice = JOptionPane.showOptionDialog(this, "Choose promotion piece:", "Pawn Promotion",
                         JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice < 0) {
                JOptionPane.showMessageDialog(this, "You must choose a piece!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return types[choice];
    }

    private void computerMove() {
        if (gameOverPending) {
            return;
        }

        Player human = game.getHumanPlayer();
        Player current = game.getCurrentPlayer();

        if (human == null || current == null || current.equals(human)) {
            return;
        }

        Timer timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOverPending) {
                    return;
                }

                try {
                    Move move = current.makeRandomMove(game.getBoard());
                    if (move == null) {
                        handleGameOver(null);
                        return;
                    }
                    game.addMove(move);
                    game.switchPlayer();
                    refreshBoard();
                } catch (InvalidMoveException | InvalidCommandException e2) {
                    showError("Computer move failed: " + e2.getMessage());
                    // Ca sa nu ramana blocat
                    game.switchPlayer();
                    refreshBoard();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private List<Position> getValidMoves(Position from, List<Position> candidates) {
        List<Position> validMoves = new ArrayList<>();
        if (candidates == null) {
            return validMoves;
        }

        for (Position to : candidates) {
            if (game.getBoard().isValidMove(from, to)) {
                validMoves.add(to);
            }
        }
        return validMoves;
    }

    private void clearSelection() {
        selectedPosition = null;
        selectedPossibleMoves = null;
        if (highlightedMoves != null) {
            highlightedMoves.clear();
        }
        refreshBoard();
    }

    private Position uiToPosition(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return new Position(file, rank);
    }

    private int[] positionToUi(Position position) {
        int col = position.getX() - 'A';
        int row = 8 - position.getY();
        return new int[] {row, col};
    }

    private void refreshBoard() {
        if (boardSquares == null) {
            return;
        }

        int row, col;
        for (row = 0; row < 8; row++) {
            for (col = 0; col < 8; col++) {
                JButton button = boardSquares[row][col];
                
                if ((row + col) % 2 != 0) {
                    button.setBackground(darkSquare);
                }
                else {
                    button.setBackground(lightSquare);
                }
                
                Position position = uiToPosition(row, col);
                Piece piece = game.getBoard().getPieceAt(position);
                if (piece != null) {
                    button.setIcon(getPieceIcon(piece, 48));
                }
                else {
                    button.setIcon(null);
                }
            }
        }
        int[] rowCol;
        if (selectedPosition != null) {
            rowCol = positionToUi(selectedPosition);
            boardSquares[rowCol[0]][rowCol[1]].setBackground(selectedSquare);
        }

        if (highlightedMoves != null) {
            for (Position position : highlightedMoves) {
                rowCol = positionToUi(position);
                boardSquares[rowCol[0]][rowCol[1]].setBackground(highlightSquare);
            }
        }
    }

    private JPanel buildCapturedPiecesPanel() {
        JPanel panel = new RoundedPanel(16, new Color(28, 34, 46));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Captured Pieces");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        userCaptureIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        userCaptureIcons.setOpaque(false);
        userCaptureIcons.setPreferredSize(new Dimension(180, 40));

        computerCaptureIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        computerCaptureIcons.setOpaque(false);
        computerCaptureIcons.setPreferredSize(new Dimension(180, 40));

        JPanel capturesArea = new JPanel(new GridLayout(2, 1, 0, 16));
        capturesArea.setOpaque(false);

        capturesArea.add(buildCaptureBlock("Your captures:", userCaptureIcons));
        capturesArea.add(buildCaptureBlock("Opponent's captures:", computerCaptureIcons));

        panel.add(header, BorderLayout.NORTH);
        panel.add(capturesArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildCaptureBlock(String labelText, JPanel iconsPanel) {
        JPanel block = new JPanel(new BorderLayout());
        block.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(new Color(150, 160, 175));

        block.add(label, BorderLayout.NORTH);
        block.add(iconsPanel, BorderLayout.CENTER);

        return block;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        Dimension buttonSize = new Dimension(220, 32);

        JButton resign = new RoundedButton("Resign", new Color(220, 70, 60), 16);
        resign.setPreferredSize(buttonSize);

        JButton saveExit = new RoundedButton("Save & Exit", new Color(240, 160, 60), 16);
        saveExit.setPreferredSize(buttonSize);

        JButton backMenu = new RoundedButton("Back to Menu", new Color(80, 90, 110), 16);
        backMenu.setPreferredSize(buttonSize);

        resign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOverPending) {
                    showContinueOnlyPopup();
                    return;
                }
                handleGameOver("RESIGN");
            }
        });

       saveExit.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                if (gameOverPending) {
                    showContinueOnlyPopup();
                    return;
                }
                if (mainApp != null) {
                    mainApp.write();
                }
                if (menuPanel != null) {
                    menuPanel.refreshStats();
                }
                frame.showMenu();
            }
        });

       backMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOverPending) {
                    showContinueOnlyPopup();
                    return;
                }
                if (menuPanel != null) {
                    menuPanel.refreshStats();
                }
                handleBackToMenu();
            }
       });

        panel.add(resign);
        panel.add(saveExit);
        panel.add(backMenu);

        return panel;
    }

        private void showContinueOnlyPopup() {
        JOptionPane.showMessageDialog(this,
                "Jocul s-a terminat. Apasa Continue.",
                "Info",
                JOptionPane.WARNING_MESSAGE);
    }

    private ResultPanel.ResultType mapResultType(EndGameCondition condition) {
        if (condition == EndGameCondition.WIN_CHECKMATE) {
            return ResultType.VICTORY;
        }
        if (condition == EndGameCondition.RESIGN_OPPONENT || condition == EndGameCondition.DRAW) {
            return ResultType.DRAW;
        }
        return ResultType.DEFEAT;
    }

    private void prepareEndGame(EndGameCondition condition) {
        Player human = game.getHumanPlayer();
        int pointsInGame = (human != null) ? human.getPoints() : 0;
        int bonusPenalty = game.getEndGameBonusPenalty();

        pendingPoints = pointsInGame + bonusPenalty;
        pendingTotalPoints = mainApp.getCurrentUser().getPoints() + pendingPoints;

        mainApp.getCurrentUser().setPoints(pendingTotalPoints);
        mainApp.getCurrentUser().removeGame(game);
        mainApp.getGames().remove(game.getId());

        if (menuPanel != null) {
            menuPanel.cleanupGameObservers(game);
        }
        mainApp.write();

        pendingResultType = mapResultType(condition);
        pendingCondition = condition;
    }

    private void setGameOverTopBar(EndGameCondition condition) {
        String text;

        if (condition == EndGameCondition.WIN_CHECKMATE || condition == EndGameCondition.LOSE_CHECKMATE) {
            Player loser = game.getCheckMatedPlayer();
            String alias = (loser != null) ? loser.getName() : "Player";
            text = "Game over! " + alias + " - Checkmated";
        } else if (condition == EndGameCondition.RESIGN_OPPONENT) {
            text = "Game over! DRAW by repetition";
        } else if (condition == EndGameCondition.DRAW) {
            text = "Game over! Stalemate";
        } else {
            text = "Game over!";
        }

        turnLabel.setText(text);
        turnDot.setVisible(false);
        turnDot.setText("");
    }

    private void enterGameOverPending(EndGameCondition condition) {
        if (gameOverPending) {
            return;
        }
        prepareEndGame(condition);
        gameOverPending = true;

        if (continueButton != null) {
            continueButton.setVisible(true);
        }
        setGameOverTopBar(condition);
        clearSelection();
    }

    private void handleContinueToResult() {
        if (!gameOverPending) {
            return;
        }
        gameOverPending = false;

        if (continueButton != null) {
            continueButton.setVisible(false);
        }

        ResultPanel resultPanel = new ResultPanel(frame, menuPanel);
        resultPanel.setResult(pendingResultType, pendingPoints, pendingTotalPoints);

        frame.setResultPanel(resultPanel);
        frame.showResult();
    }

    
    private void handleGameOver(String reason) {
        if (game == null || mainApp == null) {
            return;
        }

        if (gameOverPending) {
            return;
        }

        EndGameCondition condition = game.evaluateEndGameCondition(reason);
        if (condition == null) {
            return;
        }

        if ("RESIGN".equals(reason)) {
            prepareEndGame(condition);

            ResultPanel resultPanel = new ResultPanel(frame, menuPanel);
            resultPanel.setResult(pendingResultType, pendingPoints, pendingTotalPoints);

            frame.setResultPanel(resultPanel);
            frame.showResult();
            return;
        }

        enterGameOverPending(condition);
    }

    private void handleBackToMenu() {
        // Daca revine in menu, dintr-un joc abia creat
        if (discardOnBackMenu == true) {
            mainApp.getCurrentUser().removeGame(game);
            mainApp.getGames().remove(game.getId());

            if (menuPanel != null) {
                menuPanel.cleanupGameObservers(game);
            }
        }
        // Daca revine in menu, dintr-un joc existent
        else {
            restoreSnapshot();
        }

        if (menuPanel != null) {
            menuPanel.refreshStats();
        }
        frame.showMenu();
    }
    public void addCapturedPiece(Piece piece, boolean capturedByHuman) {
        if (piece == null) {
            return;
        }

        JPanel target = capturedByHuman ? userCaptureIcons : computerCaptureIcons;
        if (target == null) {
            return;
        }
        ImageIcon icon = getPieceIcon(piece, 18);
        if (icon == null) {
            return;
        }
        target.add(new JLabel(icon));
        target.revalidate();
        target.repaint();
    }

    public void setTurn(Player currentPlayer) {
        if (gameOverPending) {
            return;
        }
    
        if (turnLabel == null || turnDot == null || currentPlayer == null) {
            return;
        }

        Player human = game.getHumanPlayer();
        if (human == null) {
            return;
        }

        boolean humanTurn = currentPlayer.equals(human);
        String base = humanTurn ? "Your turn" : "Computer turn";
        if (checkObserver != null && checkObserver.isCheck() && currentPlayer.equals(checkObserver.getCheckedPlayer())) {
            base += " (Check)";
        }

        turnLabel.setText(base);
        turnDot.setText("\u25CF");
        turnDot.setForeground(humanTurn ? Color.GREEN : Color.RED);
        turnDot.setVisible(true);
    }

    public void setScore(int points) {
        if (scoreLabel == null) {
            return;
        }
        scoreLabel.setText("\u2605 " + points + " pts");
    }

    public void clearMoveHistory() {
        if (moveListModel != null) {
            moveListModel.clear();
        }
    }

    public void clearCaptures() {
        if (userCaptureIcons != null) {
            userCaptureIcons.removeAll();
            userCaptureIcons.revalidate();
            userCaptureIcons.repaint();
        }
        if (computerCaptureIcons != null) {
            computerCaptureIcons.removeAll();
            computerCaptureIcons.revalidate();
            computerCaptureIcons.repaint();
        }
    }

    public void appendMoveText(String entry) {
        if (entry == null || moveListModel == null) {
            return;
        }
        moveListModel.addElement(entry);
    }

    public void setMoveHistory(List<String> history) {
        if (moveListModel == null) {
            return;
        }
        moveListModel.clear();
        if (history == null) {
            return;
        }
        for (String s : history) {
            moveListModel.addElement(s);
        }
    }

    public void loadFromGame(LoggerObserver logger) {
        this.loggerObserver = logger;
        captureSnapshot();

        clearMoveHistory();
        clearCaptures();
        
        if (logger != null) {
            setMoveHistory(logger.getMoveHistory());
        }
        
        Player human = game.getHumanPlayer();
        for (Move move : game.getMoves()) {
            if (move.getCapturedPiece() != null) {
                boolean humanCaptured = move.getPlayerColor() == human.getColor();
                addCapturedPiece(move.getCapturedPiece(), humanCaptured);
            }
        }

        Player currentPlayer = game.getCurrentPlayer();
        if (checkObserver != null) {
            checkObserver.onPlayerSwitch(currentPlayer);
        }
        setScore(currentPlayer.getPoints());
        setTurn(currentPlayer);

        refreshBoard();
        handleGameOver(null);
        if (gameOverPending) {
            return;
        }

        if (human != null && currentPlayer != null && !currentPlayer.equals(human)) {
            computerMove();
        }
    }

    private void captureSnapshot() {
        savedBoardPieces = cloneBoardPieces();
        savedMoves = cloneMoves(game.getMoves());
        Player currentPlayer = game.getCurrentPlayer();
        savedCurrentColor = currentPlayer.getColor();
    }

    private void restoreSnapshot() {
        if (savedBoardPieces == null || savedMoves == null || savedCurrentColor == null) {
            return;
        }

        game.setBoard(clonePieces(savedBoardPieces));
        game.setMoves(cloneMoves(savedMoves));
        game.setCurrentPlayerColor(savedCurrentColor.toString());
        game.getPlayer1().initializeOwnedPieces(game.getBoard());
        game.getPlayer2().initializeOwnedPieces(game.getBoard());
        game.getPlayer1().recalculatePointsFromMoves(game.getMoves());
        game.getPlayer2().recalculatePointsFromMoves(game.getMoves());

        if (checkObserver != null) {
            checkObserver.onPlayerSwitch(game.getCurrentPlayer());
        }
        if (loggerObserver != null) {
            loggerObserver.loadFromMoves(savedMoves);
        }
    }

    // Imi salveaza un snapshot la intrare in meci
    private List<Piece> cloneBoardPieces() {
        List<Piece> list = new ArrayList<>();

        char x;
        int y;
        for (x = 'A'; x <= 'H'; x++) {
            for (y = 1; y <= 8; y++) {
                Position position = new Position(x, y);
                Piece piece = game.getBoard().getPieceAt(position);

                if (piece != null) {
                    Piece clonePiece = clonePiece(piece, new Position(x, y));
                    if (clonePiece != null) {
                        list.add(clonePiece);
                    }
                }
            }
        }
        return list;
    }

    // Folosit in restore (Back To Menu la un joc existent)
    private List<Piece> clonePieces(List<Piece> source) {
        List<Piece> list = new ArrayList<>();
        if (source == null) {
            return list;
        }
        for (Piece piece : source) {
            Position position = (piece.getPosition() == null) ? null : new Position(piece.getPosition().getX(), piece.getPosition().getY());
            Piece clone = clonePiece(piece, position);
            if (clone != null) {
                list.add(clone);
            }
        }
        return list;
    }

    private Piece clonePiece(Piece piece, Position position) {
        try {
            return PieceFactory.createPiece(piece.type(), piece.getColor(), position);
        } catch (InvalidCommandException e) {
            return null;
        }
    }

    private List<Move> cloneMoves(List<Move> moves) {
        List<Move> out = new ArrayList<>();
        if (moves == null) {
            return out;
        }

        for (Move move : moves) {
            Position from = new Position(move.getFrom().getX(), move.getFrom().getY());
            Position to = new Position(move.getTo().getX(), move.getTo().getY());
            Piece capturedPiece = null;
            if (move.getCapturedPiece() != null) {
                // Nu ma intereseaza pozitia la cele capturate
                capturedPiece = clonePiece(move.getCapturedPiece(), null);
            }
            out.add(new Move(move.getPlayerColor(), from, to, capturedPiece));
        }
        return out;
    }

    private ImageIcon getPieceIcon(Piece piece, int size) {
        String color = (piece.getColor() == Colors.WHITE) ? "w" : "b";
        char type = Character.toUpperCase(piece.type());
        String path = "assets/pieces/" + color + type + ".png";
        ImageIcon base = new ImageIcon(path);
        Image scaled = base.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);
    }

    public void handlePlayerSwitch(Player currentPlayer) {
        if (gameOverPending) {
            return;
        }

        setTurn(currentPlayer);
        setScore(currentPlayer.getPoints());

        if (game.checkDrawByRepetition() || game.checkForStaleMate()) {
            handleGameOver(null);
            return;
        }

        if (checkObserver != null && checkObserver.isCheckMate()) {
            // Verificam starea
            handleGameOver(null);
        }
    }
}
