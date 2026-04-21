package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import controller.GuiController;
import game.Game;
import game.Player;
import game.User;
import main.Main;
import model.Colors;
import observers.CheckObserver;
import observers.GuiObserver;
import observers.LoggerObserver;
import observers.ScoreObserver;
import utils.UserUtils;

public class MenuPanel extends JPanel {
    private final Set<Integer> observedGameIds = new HashSet<>();
    private final Map<Integer, LoggerObserver> loggerByGameId = new HashMap<>();
    private final Map<Integer, GuiObserver> guiByGameId = new HashMap<>();
    private final Map<Integer, CheckObserver> checkByGameId = new HashMap<>();
    private final Map<Integer, ScoreObserver> scoreByGameId = new HashMap<>();

    private JLabel pointsValueLabel;
    private JLabel gamesValueLabel;

    private final Main mainApp;
    private final MainFrame frame;

    public MenuPanel(Main mainApp, MainFrame frame) {
        this.mainApp = mainApp;
        this.frame = frame;

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
        overlay.add(buildCenterContent(), BorderLayout.CENTER);
        overlay.add(buildFooter(), BorderLayout.SOUTH);

        background.add(overlay, BorderLayout.CENTER);
        this.add(background, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(24, 32, 12, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        ImageIcon crowIcon = new ImageIcon("assets/pieces/king.png");
        Image scaled = crowIcon.getImage().getScaledInstance(26, 26, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(scaled));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Chess Master");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        titles.add(title);

        JLabel subtitle = new JLabel("Main Menu");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(160, 170, 185));
        titles.add(subtitle);

        left.add(icon);
        left.add(Box.createHorizontalStrut(10));
        left.add(titles);

        JPanel right = buildUserChip();

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildUserChip() {
        User user = mainApp.getCurrentUser();
        String name = UserUtils.getName(user.getEmail());
        
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        right.add(nameLabel);

        right.add(Box.createHorizontalStrut(10));

        CircleAvatar avatar = new CircleAvatar(name.substring(0, 1).toUpperCase(), new Color(65, 140, 70), Color.WHITE, 42);
        right.add(avatar);

        return right;
    }

    private JPanel buildCenterContent() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel stats = buildStatsRow();
        stats.setAlignmentX(CENTER_ALIGNMENT);
        content.add(stats);

        content.add(Box.createVerticalStrut(26));
        JPanel actions = buildActionsPanel();
        actions.setAlignmentX(CENTER_ALIGNMENT);
        content.add(actions);

        center.add(content);
        return center;
    }

    private JPanel buildStatsRow() {
        User user = mainApp.getCurrentUser();
        int points = (user != null) ? user.getPoints() : 0;
        int games = (user != null && user.getActiveGames() != null) ? user.getActiveGames().size() : 0;

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        pointsValueLabel = new JLabel(String.valueOf(points));
        JPanel card1 = buildStatCard("\u2605", pointsValueLabel, "Total Points");
        row.add(card1);

        row.add(Box.createHorizontalStrut(16));

        gamesValueLabel = new JLabel(String.valueOf(games));
        JPanel card2 = buildStatCard("\u265F", gamesValueLabel, "Active Games");
        row.add(card2);


        return row;
    }
    
    private JPanel buildStatCard(String icon, JLabel valueLabel, String title) {
        RoundedPanel card = new RoundedPanel(12, new Color(33, 40, 55, 220));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        card.setPreferredSize(new Dimension(170, 90));
        
        JPanel topRow = new JPanel();
        topRow.setOpaque(false);
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        iconLabel.setForeground(new Color(230, 230, 90));
        iconLabel.setAlignmentX(LEFT_ALIGNMENT);
        topRow.add(iconLabel);

        topRow.add(Box.createHorizontalStrut(6));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);
        topRow.add(valueLabel);

        card.add(topRow);
        card.add(Box.createVerticalStrut(4));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(150, 160, 175));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titleLabel);
        
        return card;
    }

    private JPanel buildActionsPanel() {
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setAlignmentX(CENTER_ALIGNMENT);

        JPanel newGame = buildActionRow("\u2658", "New Game",
                "Start a new match against the computer", new Color(90, 160, 90));
        JPanel continueGame = buildActionRow("\u25B6", "Continue Game",
                "Resume a game in progress", new Color(80, 120, 200));
        JPanel logout = buildActionRow("\u21AA", "Logout",
                "Return to login screen", new Color(200, 80, 70));
        
        attachClick(newGame, 1);
        attachClick(continueGame, 2);
        attachClick(logout, 3);

        list.add(newGame);
        list.add(Box.createVerticalStrut(12));
        list.add(continueGame);
        list.add(Box.createVerticalStrut(12));
        list.add(logout);
        return list;
    }

    private JPanel buildActionRow(String icon, String title, String subtitle, Color accent) {
        RoundedPanel row = new RoundedPanel(12, new Color(30, 36, 48, 220));
        row.setLayout(new BorderLayout());
        row.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 14));
        row.setPreferredSize(new Dimension(420, 66));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(4, 1));
        row.add(accentBar, BorderLayout.WEST);
        
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setPreferredSize(new Dimension(26, 26));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        text.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(160, 170, 185));
        text.add(subtitleLabel);

        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(new Color(160, 170, 185));
        row.add(arrow, BorderLayout.EAST);

        center.add(iconLabel);
        center.add(Box.createHorizontalStrut(12));
        center.add(text);
        row.add(center, BorderLayout.CENTER);
        
        return row;
    }

    private void attachClick(JPanel panel, final int action) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action == 1) {
                    handleNewGame();
                }
                else if (action == 2) {
                    handleContinueGame();
                }
                else if (action == 3) {
                    handleLogout();
                }
            }
        });
    }

    private JPanel buildFooter() {
        JLabel footer = new JLabel("POO 2025 - Chess Game Project", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setForeground(new Color(120, 130, 145));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(footer, BorderLayout.CENTER);
        return panel;
    }

    private void handleNewGame() {
        // TODO: deschide dialogul pentru alias + culoare
        final JDialog dialog = new JDialog(frame, "New Game", true);
        dialog.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        content.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel();
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel aliasLabel = new JLabel("Alias");
        aliasLabel.setAlignmentX(CENTER_ALIGNMENT);

        final JTextField aliasField = new JTextField();
        aliasField.setPreferredSize(new Dimension(240, 28));
        aliasField.setAlignmentX(CENTER_ALIGNMENT);
        aliasField.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel colorLabel = new JLabel("Color");
        colorLabel.setAlignmentX(CENTER_ALIGNMENT);

        final JRadioButton whiteButton = new JRadioButton("WHITE");
        final JRadioButton blackButton = new JRadioButton("BLACK");
        whiteButton.setOpaque(false);
        blackButton.setOpaque(false);
        whiteButton.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(whiteButton);
        group.add(blackButton);

        JPanel colorRow = new JPanel();
        colorRow.setOpaque(false);
        colorRow.setLayout(new BoxLayout(colorRow, BoxLayout.X_AXIS));
        colorRow.setAlignmentX(CENTER_ALIGNMENT);

        colorRow.add(whiteButton);
        colorRow.add(Box.createHorizontalStrut(10));
        colorRow.add(blackButton);

        JButton startButton = new JButton("Start");
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(18, 23, 33, 210));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(18, 23, 33, 210));

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setAlignmentX(CENTER_ALIGNMENT);
        buttons.add(startButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(cancelButton);

        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(aliasLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(aliasField);
        content.add(Box.createVerticalStrut(12));
        content.add(colorLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(colorRow);
        content.add(Box.createVerticalStrut(22));
        content.add(buttons);

        dialog.add(content);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String alias = aliasField.getText().trim();
                if (alias.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Alias is required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Colors humanColor = whiteButton.isSelected() ? Colors.WHITE : Colors.BLACK;
                Colors computerColor = (humanColor == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;

                Player human = new Player(alias, humanColor);
                Player computer = new Player("computer", computerColor);

                int id = 1;
                if (mainApp.getGames().isEmpty() == false) {
                    id = Collections.max(mainApp.getGames().keySet()) + 1;
                }
                
                Game game = new Game(id, human, computer);
                game.start();
                
                mainApp.getGames().put(id, game);
                mainApp.getCurrentUser().addGame(game);

                attachObserversIfNeeded(game);
                openGame(game, true);
                
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void handleContinueGame() {
        // TODO: deschide lista jocurilor salvate
        User user = mainApp.getCurrentUser();
        List<Game> games = (user != null) ? user.getActiveGames() : null;

        if (games == null || games.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No active games.");
            return;
        }

        final JDialog dialog = new JDialog(frame, "Continue Game", true);
        dialog.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Select a game");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(new Color(60, 60, 60));
        title.setAlignmentX(CENTER_ALIGNMENT);

        final DefaultListModel<Game> model = new DefaultListModel<>();
        for (Game g : games) {
            model.addElement(g);
        }

        final JList<Game> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(Math.min(6, model.getSize()));
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Game) {
                    Game g = (Game) value;
                    setText("Game #" + g.getId() + " | Moves: " + g.getMoves().size());
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(360, 140));
        scroll.setAlignmentX(CENTER_ALIGNMENT);

        JButton continueButton = new JButton("Continue");
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(new Color(64, 72, 86));

        JButton infoButton = new JButton("See information");
        infoButton.setForeground(Color.WHITE);
        infoButton.setBackground(new Color(64, 72, 86));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(64, 72, 86));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(64, 72, 86));

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setAlignmentX(CENTER_ALIGNMENT);

        buttons.add(continueButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(infoButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(deleteButton);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(cancelButton);

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(scroll);
        content.add(Box.createVerticalStrut(18));
        content.add(buttons);

        dialog.add(content);

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game selectedGame = list.getSelectedValue();
                if (selectedGame == null) {
                    JOptionPane.showMessageDialog(dialog, "Select a game!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                attachObserversIfNeeded(selectedGame);
                selectedGame.resume();
                openGame(selectedGame, false);
                dialog.dispose();
            }
        });

        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game selectedGame = list.getSelectedValue();
                if (selectedGame == null) {
                    JOptionPane.showMessageDialog(dialog, "Select a game!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                selectedGame.getPlayer1().recalculatePointsFromMoves(selectedGame.getMoves());
                selectedGame.getPlayer2().recalculatePointsFromMoves(selectedGame.getMoves());

                showGameInfo(selectedGame);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game selectedGame = list.getSelectedValue();
                if (selectedGame == null) {
                    JOptionPane.showMessageDialog(dialog, "Select a game!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mainApp.getCurrentUser().removeGame(selectedGame);
                mainApp.getGames().remove(selectedGame.getId());
                mainApp.write();
                model.removeElement(selectedGame);
                refreshStats();

                cleanupGameObservers(selectedGame);

                if (model.isEmpty()) {
                    refreshStats();
                    dialog.dispose();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshStats();
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showGameInfo(Game game) {
        // E facuta deja verificarea in infoButton->actionPerformed, dar un programator bun verifica orice! :0
        if (game == null) {
            return;
        }
        JDialog dialog = new JDialog(frame, "Game information", true);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(64, 72, 86));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel headerTitle = new JLabel("Game #" + game.getId());
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);

        header.add(headerTitle, BorderLayout.WEST);
        dialog.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        center.setBackground(Color.WHITE);
        center.setOpaque(true);

        JLabel playersTitle = new JLabel("Players");
        playersTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        playersTitle.setForeground(new Color(60, 60, 60));
        playersTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel playersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        playersRow.setOpaque(false);
        playersRow.add(playersTitle);
        center.add(playersRow);

        center.add(Box.createVerticalStrut(8));
        center.add(buildPlayerRow(game.getPlayer1()));
        center.add(Box.createVerticalStrut(6));
        center.add(buildPlayerRow(game.getPlayer2()));
        center.add(Box.createVerticalStrut(16));

        JLabel moveHistory = new JLabel("Move History (" + game.getMoves().size() + " moves)");
        moveHistory.setFont(new Font("SansSerif", Font.BOLD, 12));
        moveHistory.setForeground(new Color(60, 60, 60));
        moveHistory.setAlignmentX(LEFT_ALIGNMENT);

        JPanel historyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        historyRow.setOpaque(false);
        historyRow.add(moveHistory);
        center.add(historyRow);

        center.add(Box.createVerticalStrut(8));

        DefaultListModel<String> model = new DefaultListModel<>();
        LoggerObserver logger = loggerByGameId.get(game.getId());
        if (logger == null) {
            logger = new LoggerObserver();
            logger.loadFromMoves(game.getMoves());
            loggerByGameId.put(game.getId(), logger);
        }

        for (String entry : logger.getMoveHistoryWithCaptures()) {
            model.addElement(entry);
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("Monospaced", Font.PLAIN, 12));
        list.setVisibleRowCount(8);
        
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(420, 160));

        center.add(scroll);
        dialog.add(center, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(64, 72, 86));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        footer.add(closeButton);

        dialog.add(footer, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setMinimumSize(new Dimension(520, 420));

        // Pentru refresh-ul automat 
        dialog.addWindowListener(new WindowAdapter() {
            // Cand apas "X" -> nu trece prin dispose()
            @Override
            public void windowClosing(WindowEvent e) {
                refreshStats();
            }
            
            // Daca trec prind dialog.dispose()
            @Override
            public void windowClosed(WindowEvent e) {
                refreshStats();
            }
        });
        dialog.setVisible(true);
    }

    private JPanel buildPlayerRow(Player player) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(true);

        String circle = (player.getColor() == Colors.WHITE) ? "\u25CB" : "\u25CF";
        String name = player.getName();
        if ("computer".equals(name)) {
            name = "Computer";
        }

        JLabel left = new JLabel(circle +  " " + name + " (" + player.getColor() + ")");
        left.setFont(new Font("SansSerif", Font.PLAIN, 12));
        left.setForeground(new Color(70, 70, 70));

        JLabel right = new JLabel("Points: " + player.getPoints());
        right.setFont(new Font("SansSerif", Font.BOLD, 12));
        right.setForeground(new Color(65, 140, 70));

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private void handleLogout() {
        mainApp.logout();
        frame.showLogin();
    }

    private void openGame(Game game, boolean isNewGame) {
        attachObserversIfNeeded(game);

        LoggerObserver logger = loggerByGameId.get(game.getId());
        CheckObserver checkObserver = checkByGameId.get(game.getId());
        // Am nevoie de flag-urile lui CheckObserver
        GamePanel panel = new GamePanel(mainApp, frame, game, this, checkObserver, isNewGame);

        GuiObserver existing = guiByGameId.get(game.getId());
        if (existing != null) {
            game.removeObserver(existing);
        }

        GuiObserver guiObserver = new GuiObserver(game, panel, logger);
        game.addObserver(guiObserver);
        guiByGameId.put(game.getId(), guiObserver);
        
        panel.loadFromGame(logger);

        frame.setGamePanel(panel);
        frame.showGame();
    }

    public void refreshStats() {
        User user = mainApp.getCurrentUser();
        int points = (user != null) ? user.getPoints() : 0;
        int games = (user != null && user.getActiveGames() != null) ? user.getActiveGames().size() : 0;

        if (pointsValueLabel != null) {
            pointsValueLabel.setText(String.valueOf(points));
        }
        if (gamesValueLabel != null) {
            gamesValueLabel.setText(String.valueOf(games));
        }
    }

    // Pentru a reseta metricile pentru un nou joc (Situaie: Checkmate-NewGame-MoveHistory)
    public void cleanupGameObservers(Game game) {
        if (game == null) {
            return;
        }

        int id = game.getId();
        observedGameIds.remove(id);

        LoggerObserver loggerObserver = loggerByGameId.remove(id);
        GuiObserver guiObserver = guiByGameId.remove(id);
        CheckObserver checkObserver = checkByGameId.remove(id);
        ScoreObserver scoreObserver = scoreByGameId.remove(id);

        if (loggerObserver != null) {
            game.removeObserver(loggerObserver);
        }
        if (guiObserver != null) {
            game.removeObserver(guiObserver);
        }
        if (checkObserver != null) {
            game.removeObserver(checkObserver);
        }
        if (scoreObserver != null) {
            game.removeObserver(scoreObserver);
        }
    }

    private void attachObserversIfNeeded(Game game) {
        if (observedGameIds.contains(game.getId())) {
            return;
        }

        LoggerObserver logger = new LoggerObserver();
        logger.loadFromMoves(game.getMoves());
        loggerByGameId.put(game.getId(), logger);

        ScoreObserver scoreObserver = new ScoreObserver(game.getPlayer1(), game.getPlayer2());
        scoreByGameId.put(game.getId(), scoreObserver);
        CheckObserver checkObserver = new CheckObserver(game);
        checkByGameId.put(game.getId(), checkObserver);
        
        game.addObserver(logger);
        game.addObserver(scoreObserver);
        game.addObserver(checkObserver);

        observedGameIds.add(game.getId());
    }

    private static class CircleAvatar extends JPanel {
        private final String text;
        private final Color bg;
        private final Color fg;
        private final int size;

        CircleAvatar(String text, Color bg, Color fg, int size) {
            this.text = text;
            this.bg = bg;
            this.fg = fg;
            this.size = size;
            setOpaque(false);

            Dimension d = new Dimension(size, size);
            setPreferredSize(d);
            setMinimumSize(d);  
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int s = Math.min(getWidth(), getHeight());
            int x = (getWidth() - s) / 2;
            int y = (getHeight() - s) / 2;

            g2.setColor(bg);
            g2.fillOval(x, y, s, s);

            g2.setColor(fg);
            g2.setFont(new Font("SansSerif", Font.BOLD, s / 2));

            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent() + fm.getDescent();

            int tx = x + (s - textW) / 2;
            int ty = y + (s - textH) / 2 + fm.getAscent();

            g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }
}
