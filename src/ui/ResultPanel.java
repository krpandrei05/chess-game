package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ResultPanel extends JPanel {
    public enum ResultType {VICTORY, DEFEAT, DRAW}

    private final MainFrame frame;
    private final MenuPanel menuPanel;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel pointsLabel;
    private JLabel totalLabel;
    private BadgeIcon badge;

    public ResultPanel(MainFrame frame, MenuPanel menuPanel) {
        this.frame = frame;
        this.menuPanel = menuPanel;

        setLayout(new BorderLayout());

        BackgroundPanel background = new BackgroundPanel("assets/photos/Screenshot 2026-01-06 112838.png");
        background.setLayout(new BorderLayout());

        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(18, 23, 33, 210));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.add(buildResultCard());

        background.add(overlay, BorderLayout.CENTER);
        add(background, BorderLayout.CENTER);
    }

    private JPanel buildResultCard() {
        JPanel resultCard = new JPanel();
        resultCard.setOpaque(false);
        resultCard.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));

        badge = new BadgeIcon(new Color(202, 164, 58));
        badge.setAlignmentX(CENTER_ALIGNMENT);

        titleLabel = new JLabel("VICTORY!");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(220, 200, 120));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Congratulations, you won the game!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 190, 205));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        RoundedPanel pointsCard = new RoundedPanel(16, new Color(30, 38, 54));
        pointsCard.setLayout(new BoxLayout(pointsCard, BoxLayout.Y_AXIS));
        pointsCard.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        pointsCard.setAlignmentX(CENTER_ALIGNMENT);

        pointsLabel = new JLabel("+0");
        pointsLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        pointsLabel.setForeground(new Color(100, 220, 140));
        pointsLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel pointsText = new JLabel("Points earned this game");
        pointsText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pointsText.setForeground(new Color(180, 190, 205));
        pointsText.setAlignmentX(CENTER_ALIGNMENT);

        totalLabel = new JLabel("Total: 0");
        totalLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        totalLabel.setForeground(new Color(180, 190, 205));
        totalLabel.setAlignmentX(CENTER_ALIGNMENT);

        pointsCard.add(pointsLabel);
        pointsCard.add(Box.createVerticalStrut(4));
        pointsCard.add(pointsText);
        pointsCard.add(Box.createVerticalStrut(8));
        pointsCard.add(totalLabel);

        RoundedButton backToMenu = new RoundedButton("Back to Menu", new Color(84, 163, 72), 18);
        backToMenu.setAlignmentX(CENTER_ALIGNMENT);
        backToMenu.setPreferredSize(new Dimension(220, 36));
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuPanel != null) {
                    menuPanel.refreshStats();
                }
                frame.showMenu();
            }
        });

        RoundedButton exitApp = new RoundedButton("Exit App", new Color(60, 70, 90), 18);
        exitApp.setAlignmentX(CENTER_ALIGNMENT);
        exitApp.setPreferredSize(new Dimension(220, 36));
        exitApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        resultCard.add(badge);
        resultCard.add(Box.createVerticalStrut(12));
        resultCard.add(titleLabel);
        resultCard.add(Box.createVerticalStrut(6));
        resultCard.add(subtitleLabel);
        resultCard.add(Box.createVerticalStrut(18));
        resultCard.add(pointsCard);
        resultCard.add(Box.createVerticalStrut(18));
        resultCard.add(backToMenu);
        resultCard.add(Box.createVerticalStrut(10));
        resultCard.add(exitApp);

        return resultCard;
    }

    public void setResult(ResultType type, int points, int totalPoints) {
        String sign = (points > 0) ? "+" : "";
        pointsLabel.setText(sign + points);
        totalLabel.setText("Total: " + totalPoints);

        if (type == ResultType.VICTORY) {
            titleLabel.setText("VICTORY!");
            subtitleLabel.setText("Congratulations, you won the game!");
            titleLabel.setForeground(new Color(220, 200, 120));
            pointsLabel.setForeground(new Color(100, 220, 140));
            badge.setColor(new Color(202, 164, 58));
        }
        else if (type == ResultType.DEFEAT) {
            titleLabel.setText("DEFEAT");
            subtitleLabel.setText("You lost the game.");
            titleLabel.setForeground(new Color(230, 100, 100));
            pointsLabel.setForeground(new Color(230, 100, 100));
            badge.setColor(new Color(200, 70, 70));
        }
        else {
            titleLabel.setText("DRAW");
            subtitleLabel.setText("The game ended in a draw.");
            titleLabel.setForeground(new Color(170, 180, 200));
            pointsLabel.setForeground(new Color(170, 180, 200));
            badge.setColor(new Color(110, 130, 160));
        }
        repaint();
    }

    private static class BadgeIcon extends JPanel {
        private Color color;
        private final ImageIcon icon;

        public BadgeIcon(Color color) {
            this.color = color;
            this.icon = new ImageIcon("assets/pieces/king.png");
            setPreferredSize(new Dimension(72, 72));
            setOpaque(false);
        }

        public void setColor(Color color) {
            this.color = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(color);
            g2.fillOval(x, y, size, size);

            Image img = icon.getImage();
            int iconSize = (int) (size * 0.45);
            int ix = (getWidth() - iconSize) / 2;
            int iy = (getHeight() - iconSize) / 2;
            
            g2.drawImage(img, ix, iy, iconSize, iconSize, null);

            g2.dispose();
        }
    }
}
