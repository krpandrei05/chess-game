package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import game.User;

import javax.swing.ImageIcon;


import main.Main;

public class LoginPanel extends JPanel {
    private final Main mainApp;
    private final MainFrame frame;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(Main mainApp, MainFrame frame) {
        this.mainApp = mainApp;
        this.frame = frame;

        setLayout(new BorderLayout());

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel left = leftPanel();
        JPanel right = rightPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        split.setDividerSize(0);
        split.setEnabled(false);
        split.setOpaque(false);
        split.setBorder(null);
        container.add(split, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    public JPanel leftPanel() {
        BackgroundPanel left = new BackgroundPanel("assets/photos/Screenshot 2026-01-06 112838.png");
        left.setLayout(new BorderLayout());
        left.setOpaque(false);

        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(27, 34, 49, 200));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);

        JPanel content = new JPanel();

        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));
        
        // Icon-ul jocului
        ImageIcon crownIcon = new ImageIcon("assets/pieces/king.png");
        Image scaled = crownIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        JLabel crown = new JLabel(new ImageIcon(scaled));
        JPanel crownPanel = new JPanel();
        crownPanel.setOpaque(false);
        crownPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        crownPanel.add(crown);

        content.add(crownPanel);
        content.add(Box.createVerticalStrut(12));


        // Titlu
        JLabel title = new JLabel("Chess Master");
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        // Subtitlu
        JLabel subtitle = new JLabel("Experience the Art of Strategy");
        subtitle.setFont(new Font("SansSerif", Font.ITALIC, 16));
        subtitle.setForeground(new Color(200, 200, 200));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Bullet-urile de sub titlu
        JLabel bullet1 = new JLabel("\u2713 Play against friends");
        bullet1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel bullet2 = new JLabel("\u2713 Track your progress");
        bullet2.setAlignmentX(CENTER_ALIGNMENT);

        JLabel bullet3 = new JLabel("\u2713 Improve your skills");
        bullet3.setAlignmentX(CENTER_ALIGNMENT);

        Font bulletFont = new Font("Segoe UI Symbol", Font.PLAIN, 14);
        bullet1.setFont(bulletFont);
        bullet2.setFont(bulletFont);
        bullet3.setFont(bulletFont);

        bullet1.setForeground(new Color(200, 200, 200));
        bullet2.setForeground(new Color(200, 200, 200));
        bullet3.setForeground(new Color(200, 200, 200));

        content.add(Box.createVerticalStrut(20));
        content.add(title);
        content.add(Box.createVerticalStrut(13));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(bullet1);
        content.add(Box.createVerticalStrut(10));
        content.add(bullet2);
        content.add(Box.createVerticalStrut(10));
        content.add(bullet3);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        overlay.add(content, gbc);
        left.add(overlay, BorderLayout.CENTER);
        return left;
    }

    public JPanel rightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(247, 248, 250));
        right.setBorder(BorderFactory.createEmptyBorder(80, 60, 80, 60));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        final int FORM_WIDTH = 280;
        Dimension fieldSize = new Dimension(FORM_WIDTH, 36);
        Dimension buttonSize = new Dimension(FORM_WIDTH, 38);

        JLabel header = new JLabel("Welcome Back");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setForeground(new Color(47, 51, 63));
        header.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subHeader = new JLabel("Sign in to continue your game");
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subHeader.setForeground(new Color(120, 124, 135));
        subHeader.setAlignmentX(CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("EMAIL ADDRESS");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        emailLabel.setForeground(new Color(110, 114, 125));

        emailField = new JTextField();
        emailField.setHorizontalAlignment(SwingConstants.CENTER);
        emailField.setPreferredSize(new Dimension(280, 36));
        emailField.setMaximumSize(new Dimension(280, 36));


        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        passLabel.setForeground(new Color(110, 114, 125));

        passwordField = new JPasswordField();
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setPreferredSize(new Dimension(280, 36));
        passwordField.setMaximumSize(new Dimension(280, 36));

        JPanel emailBlock = buildFieldBlock(emailLabel, emailField, fieldSize);
        JPanel passBlock = buildFieldBlock(passLabel, passwordField, fieldSize);

        
        JButton signInButton = new JButton("Sign In");
        signInButton.setBackground(new Color(79, 127, 60));
        signInButton.setForeground(Color.WHITE);
        signInButton.setFocusPainted(false);
        signInButton.setAlignmentX(CENTER_ALIGNMENT);
        signInButton.setPreferredSize(buttonSize);
        signInButton.setMaximumSize(buttonSize);


        // JLabel orLabel = new JLabel("OR", SwingConstants.CENTER);
        // orLabel.setForeground(new Color(150, 150, 150));
        // orLabel.setAlignmentX(LEFT_ALIGNMENT);

        JButton createAccountButton = new JButton("Create New Account");
        createAccountButton.setBackground(new Color(247, 248, 250));
        createAccountButton.setForeground(new Color(79, 127, 60));
        createAccountButton.setFocusPainted(false);
        createAccountButton.setBorder(BorderFactory.createLineBorder(new Color(79, 127, 60)));
        createAccountButton.setAlignmentX(CENTER_ALIGNMENT);
        createAccountButton.setPreferredSize(buttonSize);
        createAccountButton.setMaximumSize(buttonSize);


        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateAccount();
            }
        });

        form.add(header);
        form.add(Box.createVerticalStrut(6));
        form.add(subHeader);
        form.add(Box.createVerticalStrut(30));
        form.add(emailBlock);
        form.add(Box.createVerticalStrut(16));
        form.add(passBlock);
        form.add(Box.createVerticalStrut(20));

        form.add(signInButton);
        form.add(Box.createVerticalStrut(12));

        JPanel orPanel = buildOrSeparator();
        orPanel.setAlignmentX(CENTER_ALIGNMENT);
        form.add(orPanel);
        form.add(Box.createVerticalStrut(12));
        form.add(createAccountButton);
        form.add(Box.createVerticalStrut(30));

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(form);

        right.add(center, BorderLayout.CENTER);

        return right;
    }   

    private JPanel buildOrSeparator() {
        JPanel orPanel = new JPanel();
        orPanel.setOpaque(false);
        orPanel.setLayout(new BoxLayout(orPanel, BoxLayout.X_AXIS));
        orPanel.setMaximumSize(new Dimension(280, 20));
        orPanel.setPreferredSize(new Dimension(280, 20));

        JPanel lineLeft = new JPanel();
        lineLeft.setBackground(new Color(200, 200, 200));
        lineLeft.setPreferredSize(new Dimension(1, 1));
        lineLeft.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));   

        JPanel lineRight = new JPanel();
        lineRight.setBackground(new Color(200, 200, 200));
        lineRight.setPreferredSize(new Dimension(1, 1));
        lineRight.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel orLabel = new JLabel("OR");
        orLabel.setForeground(new Color(150, 150, 150));
        orLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        orLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        orPanel.add(lineLeft);
        orPanel.add(orLabel);
        orPanel.add(lineRight);

        return orPanel;
    }

    private JPanel buildFieldBlock(JLabel label, JComponent field, Dimension fieldSize) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(CENTER_ALIGNMENT);

        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setPreferredSize(fieldSize);
        field.setMaximumSize(fieldSize);

        block.add(label);
        block.add(Box.createVerticalStrut(6));
        block.add(field);
        return block;
    }


    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = mainApp.login(email, password);
        if (user != null) {
            frame.setMenuPanel(new MenuPanel(mainApp, frame));
            frame.showMenu();
        }
        else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = mainApp.newAccount(email, password);
        if (user != null) {
            frame.setMenuPanel(new MenuPanel(mainApp, frame));
            frame.showMenu();
        } 
        else {
            JOptionPane.showMessageDialog(this, "Account already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
