import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ATMApp extends JFrame {

    // ── Design tokens ──────────────────────────────────────────────────────────
    private static final Color BG       = new Color(10, 10, 12);
    private static final Color S1       = new Color(18, 18, 22);
    private static final Color S2       = new Color(24, 24, 30);
    private static final Color S3       = new Color(32, 32, 40);
    private static final Color LINE     = new Color(40, 40, 52);
    private static final Color BLUE     = new Color(82, 156, 255);
    private static final Color GREEN    = new Color(52, 211, 153);
    private static final Color RED      = new Color(248, 113, 113);
    private static final Color AMBER    = new Color(251, 191, 36);
    private static final Color PURPLE   = new Color(167, 139, 250);
    private static final Color T1       = new Color(248, 248, 252);
    private static final Color T2       = new Color(120, 120, 145);
    private static final Color T3       = new Color(55,  55,  70);

    private static final Font FH  = new Font("Segoe UI", Font.BOLD,  26);
    private static final Font FL  = new Font("Segoe UI", Font.BOLD,  34);
    private static final Font FM  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FB  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FS  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FXS = new Font("Segoe UI", Font.BOLD,  10);
    private static final Font FMO = new Font("Consolas", Font.PLAIN, 12);

    // ── State ──────────────────────────────────────────────────────────────────
    private final Bank    bank  = new Bank();
    private       Account acc   = null;

    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    // Login fields
    private final JTextField    fUser = new JTextField();
    private final JPasswordField fPin = new JPasswordField();
    private final JLabel        lErr  = new JLabel(" ");

    // Registration fields
    private final JTextField     rName    = new JTextField();
    private final JTextField     rId      = new JTextField();
    private final JPasswordField rPin1    = new JPasswordField();
    private final JPasswordField rPin2    = new JPasswordField();
    private final JTextField     rDeposit = new JTextField();
    private final JLabel         rErr     = new JLabel(" ");

    // Dashboard labels
    private final JLabel    lName = new JLabel();
    private final JLabel    lBal  = new JLabel();
    private final JLabel    lAcct = new JLabel();
    private final JTextArea lLog  = new JTextArea();

    // ──────────────────────────────────────────────────────────────────────────
    public ATMApp() {
        setTitle("NeoBank");
        setSize(420, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        root.setBackground(BG);
        root.add(loginPanel(),    "L");
        root.add(registerPanel(), "R");
        root.add(dashPanel(),     "D");
        add(root);
        cards.show(root, "L");
        setVisible(true);
    }

    
    //  LOGIN PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel loginPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(BG);

        JLabel wm = new JLabel("neobank.");
        wm.setFont(new Font("Segoe UI", Font.BOLD, 30));
        wm.setForeground(T1);
        wm.setBounds(36, 64, 220, 38);
        p.add(wm);

        JLabel sub = new JLabel("Your money, simplified.");
        sub.setFont(FS); sub.setForeground(T3);
        sub.setBounds(36, 102, 240, 18);
        p.add(sub);

        // ── card
        Pill card = new Pill(14, S1);
        card.setBounds(24, 148, 372, 390);
        card.setBorder(new LineBorder(LINE, 1));
        card.setLayout(null);

        JLabel hd = new JLabel("Sign in");
        hd.setFont(FH); hd.setForeground(T1); hd.setBounds(28, 26, 200, 32); card.add(hd);
        JLabel sd = new JLabel("Welcome back");
        sd.setFont(FS); sd.setForeground(T2); sd.setBounds(28, 58, 200, 18); card.add(sd);

        cap("USER ID", 28, 92, card);
        inp(fUser); fUser.setBounds(28, 110, 316, 42); card.add(fUser);

        cap("PIN", 28, 166, card);
        inp(fPin);  fPin.setBounds(28, 184, 316, 42); card.add(fPin);

        lErr.setFont(FS); lErr.setForeground(RED); lErr.setBounds(28, 236, 316, 16); card.add(lErr);

        JButton btnLogin = primaryBtn("Continue");
        btnLogin.setBounds(28, 258, 316, 46);
        btnLogin.addActionListener(e -> doLogin());
        fPin.addActionListener(e -> doLogin());
        card.add(btnLogin);

        // ── divider + register link
        JLabel divider = new JLabel("─────────── or ───────────", SwingConstants.CENTER);
        divider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        divider.setForeground(T3); divider.setBounds(28, 318, 316, 16); card.add(divider);

        JLabel regLink = new JLabel("New user? Create an account →", SwingConstants.CENTER);
        regLink.setFont(new Font("Segoe UI", Font.BOLD, 11));
        regLink.setForeground(BLUE);
        regLink.setBounds(28, 340, 316, 18);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { regLink.setForeground(new Color(150, 200, 255)); }
            public void mouseExited (MouseEvent e) { regLink.setForeground(BLUE); }
            public void mouseClicked(MouseEvent e) { clearRegFields(); cards.show(root, "R"); }
        });
        card.add(regLink);

        p.add(card);

        JLabel lock = new JLabel("🔒  Bank-grade 256-bit encryption");
        lock.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lock.setForeground(T3); lock.setHorizontalAlignment(SwingConstants.CENTER);
        lock.setBounds(24, 562, 372, 16); p.add(lock);

        return p;
    }

    private void doLogin() {
        String userId = fUser.getText().trim();
        String pin    = new String(fPin.getPassword());
        if (userId.isEmpty() || pin.isEmpty()) {
            lErr.setText("Please enter both User ID and PIN.");
            return;
        }
        Account a = bank.findAccount(userId);
        if (a == null || !a.validatePin(pin)) {
            lErr.setText("Incorrect credentials. Please try again.");
            fPin.setText("");
            return;
        }
        acc = a;
        fUser.setText(""); fPin.setText(""); lErr.setText(" ");
        refreshDash();
        cards.show(root, "D");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REGISTER PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel registerPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(BG);

        JLabel wm = new JLabel("neobank.");
        wm.setFont(new Font("Segoe UI", Font.BOLD, 30));
        wm.setForeground(T1);
        wm.setBounds(36, 40, 220, 38);
        p.add(wm);

        // ── card
        Pill card = new Pill(14, S1);
        card.setBounds(24, 92, 372, 530);
        card.setBorder(new LineBorder(LINE, 1));
        card.setLayout(null);

        JLabel hd = new JLabel("Create account");
        hd.setFont(FH); hd.setForeground(T1); hd.setBounds(28, 24, 316, 32); card.add(hd);
        JLabel sd = new JLabel("Fill in your details to get started.");
        sd.setFont(FS); sd.setForeground(T2); sd.setBounds(28, 56, 316, 18); card.add(sd);

        cap("FULL NAME", 28, 86, card);
        inp(rName); rName.setBounds(28, 104, 316, 42); card.add(rName);

        cap("USER ID", 28, 158, card);
        inp(rId); rId.setBounds(28, 176, 316, 42); card.add(rId);

        cap("PIN (min 4 digits)", 28, 230, card);
        inp(rPin1); rPin1.setBounds(28, 248, 316, 42); card.add(rPin1);

        cap("CONFIRM PIN", 28, 302, card);
        inp(rPin2); rPin2.setBounds(28, 320, 316, 42); card.add(rPin2);

        cap("OPENING DEPOSIT (₹)", 28, 374, card);
        inp(rDeposit); rDeposit.setBounds(28, 392, 316, 42); card.add(rDeposit);

        rErr.setFont(FS); rErr.setForeground(RED); rErr.setBounds(28, 444, 316, 16); card.add(rErr);

        JButton btnReg = primaryBtn("Create Account");
        btnReg.setBounds(28, 464, 316, 46);
        btnReg.addActionListener(e -> doRegister());
        card.add(btnReg);

        p.add(card);

        // ── back link
        JLabel back = new JLabel("← Back to Sign in", SwingConstants.CENTER);
        back.setFont(new Font("Segoe UI", Font.BOLD, 11));
        back.setForeground(T2);
        back.setBounds(24, 638, 372, 18);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { back.setForeground(T1); }
            public void mouseExited (MouseEvent e) { back.setForeground(T2); }
            public void mouseClicked(MouseEvent e) { cards.show(root, "L"); }
        });
        p.add(back);

        return p;
    }

    private void doRegister() {
        String name    = rName.getText().trim();
        String userId  = rId.getText().trim();
        String pin1    = new String(rPin1.getPassword());
        String pin2    = new String(rPin2.getPassword());
        String depStr  = rDeposit.getText().trim();

        // ── Validation
        if (name.isEmpty() || userId.isEmpty() || pin1.isEmpty() || depStr.isEmpty()) {
            rErr.setText("All fields are required."); return;
        }
        if (!userId.matches("[a-zA-Z0-9_]{3,20}")) {
            rErr.setText("User ID: 3–20 chars, letters/numbers/_ only."); return;
        }
        if (pin1.length() < 4 || !pin1.matches("\\d+")) {
            rErr.setText("PIN must be at least 4 digits."); return;
        }
        if (!pin1.equals(pin2)) {
            rErr.setText("PINs do not match."); return;
        }
        double opening;
        try {
            opening = Double.parseDouble(depStr);
            if (opening < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            rErr.setText("Enter a valid opening deposit (≥ ₹0)."); return;
        }
        if (bank.findAccount(userId) != null) {
            rErr.setText("User ID already taken. Choose another."); return;
        }

        // ── Create account
        Account newAcc = bank.register(userId, pin1, name, opening);
        if (newAcc == null) {
            rErr.setText("Registration failed. Please try again."); return;
        }

        clearRegFields();
        toast("Account created! You can now sign in, " + name + ".", true);
        cards.show(root, "L");
    }

    private void clearRegFields() {
        rName.setText(""); rId.setText("");
        rPin1.setText(""); rPin2.setText("");
        rDeposit.setText(""); rErr.setText(" ");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DASHBOARD PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel dashPanel() {
        JPanel p = new JPanel(null);
        p.setBackground(BG);

        // ── top bar
        JPanel bar = new JPanel(null);
        bar.setBackground(S1);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, LINE));
        bar.setBounds(0, 0, 420, 56);

        JLabel wm2 = new JLabel("neobank.");
        wm2.setFont(FB); wm2.setForeground(T1); wm2.setBounds(18, 0, 120, 56); bar.add(wm2);

        lName.setFont(FS); lName.setForeground(T2);
        lName.setBounds(130, 0, 160, 56);
        lName.setHorizontalAlignment(SwingConstants.CENTER); bar.add(lName);

        JButton out = ghostBtn("Sign out");
        out.setBounds(308, 12, 94, 32);
        out.addActionListener(e -> { acc = null; cards.show(root, "L"); });
        bar.add(out);
        p.add(bar);

        // ── balance card
        Pill bc = new Pill(16, new Color(16, 28, 52));
        bc.setLayout(null);
        bc.setBorder(new LineBorder(new Color(34, 64, 120), 1));
        bc.setBounds(18, 68, 384, 118);

        JLabel bLbl = new JLabel("Available balance");
        bLbl.setFont(FS); bLbl.setForeground(new Color(100, 150, 220));
        bLbl.setBounds(18, 14, 240, 18); bc.add(bLbl);

        lBal.setFont(FL); lBal.setForeground(T1); lBal.setBounds(18, 34, 348, 44); bc.add(lBal);
        lAcct.setFont(new Font("Consolas", Font.PLAIN, 11));
        lAcct.setForeground(new Color(80, 110, 170));
        lAcct.setBounds(18, 84, 300, 16); bc.add(lAcct);
        p.add(bc);

        // ── action tiles (3 × 2)
        Object[][] tiles = {
            {"↓", "Deposit",    GREEN,  "DEP"},
            {"↑", "Withdraw",   RED,    "WDR"},
            {"⇄", "Transfer",   BLUE,   "TRF"},
            {"≡", "History",    AMBER,  "HIS"},
            {"◎", "Balance",    PURPLE, "BAL"},
            {"✎", "Change PIN", BLUE,   "PIN"},
        };
        int cols = 3, tw = 118, th = 90, gx = 10, gy = 8;
        int startX = 18, startY = 200;
        for (int i = 0; i < tiles.length; i++) {
            Object[] t = tiles[i];
            int c = i % cols, r = i / cols;
            JPanel tile = tile((String)t[0], (String)t[1], (Color)t[2], (String)t[3]);
            tile.setBounds(startX + c*(tw+gx), startY + r*(th+gy), tw, th);
            p.add(tile);
        }

        // ── divider + log
        JSeparator sep = new JSeparator();
        sep.setForeground(LINE); sep.setBounds(18, 404, 384, 1);
        p.add(sep);

        JLabel logLbl = new JLabel("ACTIVITY");
        logLbl.setFont(FXS); logLbl.setForeground(T3);
        logLbl.setBounds(18, 412, 100, 16);
        p.add(logLbl);

        lLog.setFont(FMO); lLog.setBackground(S1); lLog.setForeground(T2);
        lLog.setEditable(false); lLog.setBorder(new EmptyBorder(10, 12, 10, 12));
        lLog.setLineWrap(true);

        JScrollPane sp = new JScrollPane(lLog);
        sp.setBounds(18, 432, 384, 242);
        sp.setBorder(new LineBorder(LINE, 1));
        sp.getViewport().setBackground(S1);
        sp.getVerticalScrollBar().setBackground(S2);
        p.add(sp);

        return p;
    }

    private JPanel tile(String icon, String label, Color accent, String action) {
        Pill t = new Pill(12, S2);
        t.setLayout(null);
        t.setBorder(new LineBorder(LINE, 1));
        t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel strip = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        strip.setOpaque(false); strip.setBounds(0, 0, 118, 4); t.add(strip);

        JLabel ic = new JLabel(icon, SwingConstants.CENTER);
        ic.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        ic.setForeground(accent); ic.setBounds(0, 14, 118, 28); t.add(ic);

        JLabel lb = new JLabel(label, SwingConstants.CENTER);
        lb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lb.setForeground(T2); lb.setBounds(0, 52, 118, 18); t.add(lb);

        t.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { t.setColor(S3); t.repaint(); lb.setForeground(T1); }
            public void mouseExited (MouseEvent e) { t.setColor(S2); t.repaint(); lb.setForeground(T2); }
            public void mouseClicked(MouseEvent e) { handleAction(action); }
        });
        return t;
    }

    private void refreshDash() {
        // reload from DB so balance is always fresh
        Account fresh = bank.findAccount(acc.getUserId());
        if (fresh != null) acc = fresh;

        lName.setText("Hi, " + acc.getHolderName().split(" ")[0] + " 👋");
        lBal.setText("₹ " + String.format("%,.2f", acc.getBalance()));
        lAcct.setText("•••• •••• •••• "
            + acc.getUserId().replaceAll("[^0-9]", "") + "27  ·  SAVINGS");
        lLog.setText("Session started.\n");
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void handleAction(String a) {
        switch (a) {
            case "BAL" -> {
                // reload from DB
                Account fresh = bank.findAccount(acc.getUserId());
                if (fresh != null) acc = fresh;
                lBal.setText("₹ " + String.format("%,.2f", acc.getBalance()));
                log("◎  Balance: ₹" + fmt(acc.getBalance()));
            }
            case "DEP" -> {
                String v = ask("Deposit", "Amount to deposit (₹)");
                if (v == null) return;
                try {
                    double x = Double.parseDouble(v);
                    if (acc.deposit(x)) { sync(); log("↓  Deposited  ₹" + fmt(x)); toast("Deposited ₹" + fmt(x), true); }
                    else toast("Invalid amount", false);
                } catch (NumberFormatException ex) { toast("Enter a valid number", false); }
            }
            case "WDR" -> {
                String v = ask("Withdraw", "Amount to withdraw (₹)");
                if (v == null) return;
                try {
                    double x = Double.parseDouble(v);
                    if (acc.withdraw(x)) { sync(); log("↑  Withdrew   ₹" + fmt(x)); toast("Withdrawn ₹" + fmt(x), true); }
                    else toast("Insufficient balance", false);
                } catch (NumberFormatException ex) { toast("Enter a valid number", false); }
            }
            case "TRF" -> {
                String id = ask("Transfer", "Recipient User ID");
                if (id == null) return;
                Account to = bank.findAccount(id.trim());
                if (to == null || to.getUserId().equals(acc.getUserId())) {
                    toast("Recipient not found", false); return;
                }
                String v = ask("Transfer to " + to.getHolderName(), "Amount (₹)");
                if (v == null) return;
                try {
                    double x = Double.parseDouble(v);
                    if (acc.transfer(to, x)) {
                        sync();
                        log("⇄  Transferred ₹" + fmt(x) + " → " + to.getHolderName());
                        toast("Transfer successful", true);
                    } else toast("Insufficient balance", false);
                } catch (NumberFormatException ex) { toast("Enter a valid number", false); }
            }
            case "PIN" -> {
                JPasswordField c1 = new JPasswordField(), n1 = new JPasswordField(), n2 = new JPasswordField();
                inp(c1); inp(n1); inp(n2);
                JPanel pn = new JPanel(new GridLayout(6, 1, 0, 5));
                pn.setBackground(S1);
                pn.add(cap2("CURRENT PIN")); pn.add(c1);
                pn.add(cap2("NEW PIN"));     pn.add(n1);
                pn.add(cap2("CONFIRM PIN")); pn.add(n2);
                if (JOptionPane.showConfirmDialog(this, pn, "Change PIN",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
                if (!acc.validatePin(new String(c1.getPassword()))) { toast("Current PIN incorrect", false); return; }
                String np = new String(n1.getPassword());
                if (!np.equals(new String(n2.getPassword()))) { toast("PINs do not match", false); return; }
                if (np.length() < 4) { toast("PIN must be ≥ 4 digits", false); return; }
                acc.changePin(np);
                log("✎  PIN changed.");
                toast("PIN updated", true);
            }
            case "HIS" -> {
                // reload from DB for latest history
                Account fresh = bank.findAccount(acc.getUserId());
                if (fresh != null) acc = fresh;
                List<String> h = acc.getTransactionHistory();
                StringBuilder sb = new StringBuilder("── Transaction History ──────────\n");
                for (int i = h.size()-1; i >= 0; i--) sb.append(h.get(i)).append("\n");
                lLog.setText(sb.toString());
                lLog.setCaretPosition(0);
            }
        }
    }

    private void sync() { lBal.setText("₹ " + String.format("%,.2f", acc.getBalance())); }
    private void log(String m) { lLog.append(m+"\n"); lLog.setCaretPosition(lLog.getDocument().getLength()); }
    private String fmt(double v) { return String.format("%,.2f", v); }

    private String ask(String title, String prompt) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(S1);
        JLabel lb = new JLabel(prompt); lb.setFont(FM); lb.setForeground(T2);
        JTextField f = new JTextField(); inp(f); f.setPreferredSize(new Dimension(260, 40));
        p.add(lb, BorderLayout.NORTH); p.add(f, BorderLayout.CENTER);
        int r = JOptionPane.showConfirmDialog(this, p, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return r == JOptionPane.OK_OPTION ? f.getText().trim() : null;
    }

    private void toast(String msg, boolean ok) {
        JOptionPane.showMessageDialog(this,
            "<html><body style='font-family:Segoe UI;font-size:13px'>" + msg + "</body></html>",
            ok ? "Done" : "Notice",
            ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    // ── Style helpers ─────────────────────────────────────────────────────────
    private void inp(JTextField f) {
        f.setBackground(new Color(26, 26, 34)); f.setForeground(T1);
        f.setCaretColor(BLUE); f.setFont(FM);
        f.setBorder(new CompoundBorder(new LineBorder(LINE, 1), new EmptyBorder(8, 12, 8, 12)));
    }

    private void cap(String t, int x, int y, JPanel p) {
        JLabel l = new JLabel(t); l.setFont(FXS); l.setForeground(T3);
        l.setBounds(x, y, 200, 14); p.add(l);
    }

    private JLabel cap2(String t) {
        JLabel l = new JLabel(t); l.setFont(FXS); l.setForeground(T3); return l;
    }

    private JButton primaryBtn(String text) {
        JButton b = new JButton(text); b.setFont(FB);
        b.setBackground(BLUE); b.setForeground(new Color(8, 8, 12));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(120, 180, 255)); }
            public void mouseExited (MouseEvent e) { b.setBackground(BLUE); }
        });
        return b;
    }

    private JButton ghostBtn(String text) {
        JButton b = new JButton(text); b.setFont(FS);
        b.setBackground(S2); b.setForeground(T2);
        b.setFocusPainted(false); b.setOpaque(true);
        b.setBorder(new LineBorder(LINE, 1));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(RED); b.setBorder(new LineBorder(RED, 1)); }
            public void mouseExited (MouseEvent e) { b.setForeground(T2);  b.setBorder(new LineBorder(LINE, 1)); }
        });
        return b;
    }

    // ── Pill (rounded) panel ──────────────────────────────────────────────────
    static class Pill extends JPanel {
        private int r; private Color col;
        Pill(int r, Color c) { this.r = r; col = c; setOpaque(false); }
        void setColor(Color c) { col = c; }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(col); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(ATMApp::new);
    }
}
