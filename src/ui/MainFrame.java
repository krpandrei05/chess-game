package ui;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    public static final String CARD_LOGIN = "login";
    public static final String CARD_MENU = "menu";
    public static final String CARD_GAME = "game";
    public static final String CARD_RESULT = "result";

    private final CardLayout cardLayout;
    private final JPanel cards;

    private JPanel loginPanel;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JPanel resultPanel;

    public MainFrame() {
        super("Chess Master");
        setSize(1280, 720);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        setContentPane(cards);
    }

    public void setLoginPanel(JPanel panel) {
        if (loginPanel != null) {
            cards.remove(loginPanel);
        }
        loginPanel = panel;
        cards.add(panel, CARD_LOGIN);
        cards.revalidate();
        cards.repaint();
    }

    public void setMenuPanel(JPanel panel) {
        if (menuPanel != null) {
            cards.remove(menuPanel);
        }
        menuPanel = panel;
        cards.add(panel, CARD_MENU);
        cards.revalidate();
        cards.repaint();
    }

    public void setGamePanel(JPanel panel) {
        if (gamePanel != null) {
            cards.remove(gamePanel);
        }
        gamePanel = panel;
        cards.add(panel, CARD_GAME);
        cards.revalidate();
        cards.repaint();
    }

    public void setResultPanel(JPanel panel) {
        if (resultPanel != null) {
            cards.remove(resultPanel);
        }
        resultPanel = panel;
        cards.add(panel, CARD_RESULT);
        cards.revalidate();
        cards.repaint();
    }

    public void showLogin() {
        cardLayout.show(cards, CARD_LOGIN);
    }

    public void showMenu() {
        cardLayout.show(cards, CARD_MENU);
    }

    public void showGame() {
        cardLayout.show(cards, CARD_GAME);
    }

    public void showResult() {
        cardLayout.show(cards, CARD_RESULT);
    }
}
