import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class ExamApp extends JFrame {

    // ── Theme ─────────────────────────────────────────────────────────────────
    private static final Color BG       = new Color(18, 24, 38);
    private static final Color PANEL    = new Color(26, 34, 52);
    private static final Color CARD     = new Color(32, 42, 64);
    private static final Color ACCENT   = new Color(99, 102, 241);
    private static final Color GREEN    = new Color(34, 197, 94);
    private static final Color RED      = new Color(239, 68, 68);
    private static final Color GOLD     = new Color(250, 204, 21);
    private static final Color TEAL     = new Color(20, 184, 166);
    private static final Color TEXT     = new Color(226, 232, 240);
    private static final Color TEXT_DIM = new Color(100, 116, 139);

    private static final Font F_HEAD  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_MED   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_MONO  = new Font("Consolas", Font.PLAIN, 12);
    private static final Font F_BIG   = new Font("Segoe UI", Font.BOLD,  48);
    private static final Font F_MID   = new Font("Segoe UI", Font.BOLD,  18);

    // ── State ─────────────────────────────────────────────────────────────────
    private User   currentUser = null;
    private List<Question> examQuestions;
    private int   currentQ    = 0;
    private int[] answers;
    private int   secondsLeft;
    private Timer examTimer;
    private String selectedSubject;

    private final CardLayout cards = new CardLayout();
    private final JPanel     root  = new JPanel(cards);

    // ── Login widgets ─────────────────────────────────────────────────────────
    private final JTextField   loginUser = styledField();
    private final JPasswordField loginPass = new JPasswordField();
    private final JLabel       loginErr  = new JLabel(" ");

    // ── Register widgets ──────────────────────────────────────────────────────
    private final JTextField   regUser  = styledField();
    private final JTextField   regName  = styledField();
    private final JTextField   regEmail = styledField();
    private final JPasswordField regPass1 = new JPasswordField();
    private final JPasswordField regPass2 = new JPasswordField();
    private final JLabel       regErr   = new JLabel(" ");

    // ── Exam widgets ──────────────────────────────────────────────────────────
    private final JLabel       qNumLabel = new JLabel();
    private final JLabel       timerLabel = new JLabel();
    private final JLabel       qText     = new JLabel();
    private final ButtonGroup  optGroup  = new ButtonGroup();
    private final JRadioButton[] optBtns = new JRadioButton[4];
    private JButton prevBtn, nextBtn, submitBtn;
    private JProgressBar qProgress;

    // ── Result widgets ────────────────────────────────────────────────────────
    private final JLabel   resultTitle   = new JLabel();
    private final JLabel   resultScore   = new JLabel();
    private final JLabel   resultPercent = new JLabel();
    private final JLabel   resultGrade   = new JLabel();
    private final JTextArea resultDetail  = new JTextArea();

    // ── Profile widgets ───────────────────────────────────────────────────────
    private final JTextField    profileName  = styledField();
    private final JTextField    profileEmail = styledField();
    private final JPasswordField profileOldPass = new JPasswordField();
    private final JPasswordField profileNewPass = new JPasswordField();
    private final JLabel        profileStatus = new JLabel(" ");

    // ── Performance panel ─────────────────────────────────────────────────────
    private JPanel perfContentPanel;   // rebuilt on each show

    // ─────────────────────────────────────────────────────────────────────────
    public ExamApp() {
        ExamDatabase.init();

        setTitle("ExamPortal — Online Examination System");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        root.setBackground(BG);
        root.add(buildLoginPanel(),    "LOGIN");
        root.add(buildRegisterPanel(), "REGISTER");
        root.add(buildSubjectPanel(),  "SUBJECT");
        root.add(buildExamPanel(),     "EXAM");
        root.add(buildResultPanel(),   "RESULT");
        root.add(buildProfilePanel(),  "PROFILE");
        root.add(buildPerfShell(),     "PERF");
        add(root);
        cards.show(root, "LOGIN");
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildLoginPanel() {
        JPanel p = centeredPanel();

        styleField(loginPass);
        loginPass.setPreferredSize(new Dimension(260, 36));
        loginUser.setPreferredSize(new Dimension(260, 36));

        JPanel box = darkBox(380, 450);
        JLabel title = label("📝  ExamPortal", F_HEAD, ACCENT);
        JLabel sub   = label("Online Examination System", F_SMALL, TEXT_DIM);
        title.setAlignmentX(CENTER_ALIGNMENT);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        loginErr.setAlignmentX(CENTER_ALIGNMENT);
        loginErr.setFont(F_SMALL); loginErr.setForeground(RED);

        JButton loginBtn = accentBtn("LOGIN", ACCENT);
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin());

        // Register link
        JLabel regLink = new JLabel("New user? Create an account →");
        regLink.setFont(new Font("Segoe UI", Font.BOLD, 11));
        regLink.setForeground(TEAL);
        regLink.setAlignmentX(CENTER_ALIGNMENT);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { regLink.setForeground(Color.CYAN); }
            public void mouseExited (MouseEvent e) { regLink.setForeground(TEAL); }
            public void mouseClicked(MouseEvent e) { clearRegFields(); cards.show(root, "REGISTER"); }
        });

        JLabel hint = label("Demo: student1/pass123 · admin/admin123",
                new Font("Segoe UI", Font.ITALIC, 11), TEXT_DIM);
        hint.setAlignmentX(CENTER_ALIGNMENT);

        box.add(title); box.add(vgap(4)); box.add(sub); box.add(vgap(28));
        box.add(labelRow("Username")); box.add(vgap(4));  box.add(loginUser); box.add(vgap(12));
        box.add(labelRow("Password")); box.add(vgap(4));  box.add(loginPass); box.add(vgap(20));
        box.add(loginBtn); box.add(vgap(8)); box.add(loginErr); box.add(vgap(14));
        box.add(sep()); box.add(vgap(12));
        box.add(regLink); box.add(vgap(16)); box.add(hint);

        p.add(box); return p;
    }

    private void doLogin() {
        String uname = loginUser.getText().trim();
        String pass  = new String(loginPass.getPassword());
        if (uname.isEmpty() || pass.isEmpty()) { loginErr.setText("❌ Enter username and password."); return; }
        User u = ExamDatabase.findByUsername(uname);
        if (u == null || !u.validatePassword(pass)) {
            loginErr.setText("❌ Invalid username or password."); loginPass.setText(""); return;
        }
        currentUser = u;
        loginUser.setText(""); loginPass.setText(""); loginErr.setText(" ");
        cards.show(root, "SUBJECT");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REGISTER
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildRegisterPanel() {
        JPanel p = centeredPanel();

        styleField(regPass1); styleField(regPass2);
        regUser.setPreferredSize(new Dimension(260, 36));
        regName.setPreferredSize(new Dimension(260, 36));
        regEmail.setPreferredSize(new Dimension(260, 36));
        regPass1.setPreferredSize(new Dimension(260, 36));
        regPass2.setPreferredSize(new Dimension(260, 36));

        JPanel box = darkBox(400, 520);
        JLabel title = label("✏  Create Account", F_HEAD, TEAL);
        title.setAlignmentX(CENTER_ALIGNMENT);
        regErr.setAlignmentX(CENTER_ALIGNMENT);
        regErr.setFont(F_SMALL); regErr.setForeground(RED);

        JButton btn = accentBtn("REGISTER", TEAL);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.addActionListener(e -> doRegister());

        JLabel back = new JLabel("← Back to Login");
        back.setFont(new Font("Segoe UI", Font.BOLD, 11));
        back.setForeground(TEXT_DIM); back.setAlignmentX(CENTER_ALIGNMENT);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { back.setForeground(TEXT); }
            public void mouseExited (MouseEvent e) { back.setForeground(TEXT_DIM); }
            public void mouseClicked(MouseEvent e) { cards.show(root, "LOGIN"); }
        });

        box.add(title); box.add(vgap(18));
        box.add(labelRow("Username"));          box.add(vgap(4)); box.add(regUser);  box.add(vgap(10));
        box.add(labelRow("Full Name"));         box.add(vgap(4)); box.add(regName);  box.add(vgap(10));
        box.add(labelRow("Email"));             box.add(vgap(4)); box.add(regEmail); box.add(vgap(10));
        box.add(labelRow("Password (≥6 chars)")); box.add(vgap(4)); box.add(regPass1); box.add(vgap(10));
        box.add(labelRow("Confirm Password")); box.add(vgap(4)); box.add(regPass2); box.add(vgap(18));
        box.add(btn); box.add(vgap(8)); box.add(regErr); box.add(vgap(12)); box.add(back);

        p.add(box); return p;
    }

    private void doRegister() {
        String uname = regUser.getText().trim();
        String name  = regName.getText().trim();
        String email = regEmail.getText().trim();
        String pass1 = new String(regPass1.getPassword());
        String pass2 = new String(regPass2.getPassword());

        if (uname.isEmpty() || name.isEmpty() || email.isEmpty() || pass1.isEmpty()) {
            regErr.setText("❌ All fields are required."); return; }
        if (!uname.matches("[a-zA-Z0-9_]{3,20}")) {
            regErr.setText("❌ Username: 3–20 chars, letters/numbers/_ only."); return; }
        if (pass1.length() < 6) {
            regErr.setText("❌ Password must be at least 6 characters."); return; }
        if (!pass1.equals(pass2)) {
            regErr.setText("❌ Passwords do not match."); return; }
        if (ExamDatabase.exists(uname)) {
            regErr.setText("❌ Username already taken."); return; }

        User u = new User(uname, pass1, name, email);
        ExamDatabase.save(u);
        clearRegFields();
        JOptionPane.showMessageDialog(this,
            "<html><b>Account created!</b><br>You can now log in as <i>" + uname + "</i>.</html>",
            "Welcome", JOptionPane.INFORMATION_MESSAGE);
        cards.show(root, "LOGIN");
    }

    private void clearRegFields() {
        regUser.setText(""); regName.setText(""); regEmail.setText("");
        regPass1.setText(""); regPass2.setText(""); regErr.setText(" ");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SUBJECT SELECTION
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSubjectPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.add(topBar("Choose Your Exam Subject", true), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(BG);

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(BG);
        col.setBorder(new EmptyBorder(30, 0, 0, 0));

        JLabel sub = label("Select a subject to begin the 10-question timed exam", F_MED, TEXT_DIM);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        col.add(sub); col.add(vgap(28));

        Map<String, List<Question>> subjects = QuestionBank.getAllSubjects();
        String[] icons = {"☕", "🌲", "🌍"};
        String[] descs = {"OOP, Collections, JVM, Threads", "Arrays, Trees, Sorting, Graphs", "Science, History, Geography"};
        int i = 0;
        for (String subj : subjects.keySet()) {
            final String s = subj;
            JPanel card = new JPanel(new BorderLayout(14, 0));
            card.setBackground(CARD);
            card.setBorder(new CompoundBorder(
                new LineBorder(new Color(60, 80, 120), 1, true),
                new EmptyBorder(16, 20, 16, 20)));
            card.setMaximumSize(new Dimension(500, 70));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel icon = label(icons[i], new Font("Segoe UI", Font.PLAIN, 26), TEXT);
            JPanel info = new JPanel(); info.setBackground(CARD);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            JLabel nm = label(subj, F_BOLD, TEXT);
            JLabel dc = label("10 Questions · 10 min · " + descs[i], F_SMALL, TEXT_DIM);
            info.add(nm); info.add(dc);
            JLabel arr = label("▶", F_BOLD, ACCENT);
            card.add(icon, BorderLayout.WEST);
            card.add(info, BorderLayout.CENTER);
            card.add(arr,  BorderLayout.EAST);
            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { startExam(s); }
                public void mouseEntered(MouseEvent e) { card.setBackground(new Color(40,55,85)); }
                public void mouseExited (MouseEvent e) { card.setBackground(CARD); }
            });
            col.add(card); col.add(vgap(10));
            i++;
        }

        centre.add(col);
        p.add(centre, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXAM PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildExamPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(14, 20, 32));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        qNumLabel.setFont(F_BOLD); qNumLabel.setForeground(TEXT);
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 16)); timerLabel.setForeground(GOLD);

        qProgress = new JProgressBar(0, 10);
        qProgress.setBackground(new Color(30, 40, 60));
        qProgress.setForeground(ACCENT);
        qProgress.setPreferredSize(new Dimension(0, 5));
        qProgress.setBorderPainted(false);

        header.add(qNumLabel, BorderLayout.WEST);
        header.add(timerLabel, BorderLayout.EAST);

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setBackground(new Color(14, 20, 32));
        headerWrap.add(header, BorderLayout.CENTER);
        headerWrap.add(qProgress, BorderLayout.SOUTH);
        p.add(headerWrap, BorderLayout.NORTH);

        JPanel qCard = new JPanel();
        qCard.setLayout(new BoxLayout(qCard, BoxLayout.Y_AXIS));
        qCard.setBackground(PANEL);
        qCard.setBorder(new EmptyBorder(32, 40, 24, 40));

        qText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qText.setForeground(TEXT); qText.setAlignmentX(LEFT_ALIGNMENT);
        qCard.add(qText); qCard.add(vgap(24));

        for (int i = 0; i < 4; i++) {
            optBtns[i] = new JRadioButton();
            optBtns[i].setFont(F_MED); optBtns[i].setForeground(TEXT);
            optBtns[i].setBackground(PANEL); optBtns[i].setFocusPainted(false);
            optBtns[i].setAlignmentX(LEFT_ALIGNMENT);
            optGroup.add(optBtns[i]);
            qCard.add(optBtns[i]); qCard.add(vgap(8));
        }
        p.add(qCard, BorderLayout.CENTER);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(14, 20, 32));
        nav.setBorder(new EmptyBorder(10, 20, 14, 20));

        prevBtn   = accentBtn("◀  Previous", CARD);
        nextBtn   = accentBtn("Next  ▶",     ACCENT);
        submitBtn = accentBtn("✓  Submit Exam", GREEN);
        prevBtn.addActionListener(e -> navigate(-1));
        nextBtn.addActionListener(e -> navigate(1));
        submitBtn.addActionListener(e -> finishExam());

        JPanel leftNav  = new JPanel(new FlowLayout(FlowLayout.LEFT,  0, 0)); leftNav.setBackground(new Color(14,20,32));  leftNav.add(prevBtn);
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); rightNav.setBackground(new Color(14,20,32)); rightNav.add(nextBtn); rightNav.add(Box.createHorizontalStrut(8)); rightNav.add(submitBtn);
        nav.add(leftNav, BorderLayout.WEST); nav.add(rightNav, BorderLayout.EAST);
        p.add(nav, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RESULT PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildResultPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.add(topBar("Exam Results", false), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridBagLayout()); centre.setBackground(BG);

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(BG);
        col.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel scoreCard = darkBox(440, 200);
        resultTitle.setAlignmentX(CENTER_ALIGNMENT);   resultTitle.setFont(F_BOLD);   resultTitle.setForeground(TEXT_DIM);
        resultScore.setAlignmentX(CENTER_ALIGNMENT);   resultScore.setFont(F_BIG);
        resultPercent.setAlignmentX(CENTER_ALIGNMENT); resultPercent.setFont(F_MED);  resultPercent.setForeground(TEXT_DIM);
        resultGrade.setAlignmentX(CENTER_ALIGNMENT);   resultGrade.setFont(F_MID);
        scoreCard.add(resultTitle); scoreCard.add(vgap(8));
        scoreCard.add(resultScore); scoreCard.add(resultPercent); scoreCard.add(vgap(8)); scoreCard.add(resultGrade);

        resultDetail.setFont(F_MONO); resultDetail.setBackground(new Color(14,20,32));
        resultDetail.setForeground(TEXT); resultDetail.setEditable(false);
        resultDetail.setBorder(new EmptyBorder(10, 14, 10, 14));
        JScrollPane scroll = new JScrollPane(resultDetail);
        scroll.setPreferredSize(new Dimension(500, 160));
        scroll.setBorder(new LineBorder(new Color(40,60,90), 1));

        JButton retakeBtn  = accentBtn("🔄  Try Another Subject", ACCENT);
        JButton analysisBtn = accentBtn("📊  Performance Analysis", TEAL);
        retakeBtn.setAlignmentX(CENTER_ALIGNMENT);
        analysisBtn.setAlignmentX(CENTER_ALIGNMENT);
        retakeBtn.addActionListener(e -> cards.show(root, "SUBJECT"));
        analysisBtn.addActionListener(e -> showPerformance());

        col.add(scoreCard); col.add(vgap(14));
        col.add(scroll);    col.add(vgap(16));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setBackground(BG); btnRow.add(retakeBtn); btnRow.add(analysisBtn);
        col.add(btnRow);

        centre.add(col);
        p.add(centre, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PROFILE PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildProfilePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.add(topBar("My Profile", true), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridBagLayout()); centre.setBackground(BG);
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(BG);
        col.setBorder(new EmptyBorder(24, 0, 0, 0));

        JPanel infoCard = darkBox(440, 320);
        JLabel t = label("Update Profile", F_BOLD, TEXT); t.setAlignmentX(CENTER_ALIGNMENT);
        styleField(profileOldPass); styleField(profileNewPass);
        profileName.setPreferredSize(new Dimension(280, 34));
        profileEmail.setPreferredSize(new Dimension(280, 34));
        profileOldPass.setPreferredSize(new Dimension(280, 34));
        profileNewPass.setPreferredSize(new Dimension(280, 34));

        JButton saveBtn = accentBtn("SAVE CHANGES", ACCENT);
        saveBtn.setAlignmentX(CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> saveProfile());
        profileStatus.setAlignmentX(CENTER_ALIGNMENT); profileStatus.setFont(F_SMALL);

        infoCard.add(t); infoCard.add(vgap(16));
        infoCard.add(labelRow("Full Name"));  infoCard.add(vgap(4)); infoCard.add(profileName);  infoCard.add(vgap(10));
        infoCard.add(labelRow("Email"));      infoCard.add(vgap(4)); infoCard.add(profileEmail); infoCard.add(vgap(10));
        infoCard.add(labelRow("Current Password (to change)")); infoCard.add(vgap(4)); infoCard.add(profileOldPass); infoCard.add(vgap(10));
        infoCard.add(labelRow("New Password (blank = no change)")); infoCard.add(vgap(4)); infoCard.add(profileNewPass); infoCard.add(vgap(18));
        infoCard.add(saveBtn); infoCard.add(vgap(6)); infoCard.add(profileStatus);

        col.add(infoCard);
        centre.add(col);
        p.add(centre, BorderLayout.CENTER);
        return p;
    }

    private void refreshProfile() {
        profileName.setText(currentUser.getFullName());
        profileEmail.setText(currentUser.getEmail());
        profileOldPass.setText(""); profileNewPass.setText("");
        profileStatus.setText(" ");
        cards.show(root, "PROFILE");
    }

    private void saveProfile() {
        String name  = profileName.getText().trim();
        String email = profileEmail.getText().trim();
        String oldP  = new String(profileOldPass.getPassword());
        String newP  = new String(profileNewPass.getPassword());

        if (name.isEmpty() || email.isEmpty()) {
            profileStatus.setForeground(RED); profileStatus.setText("❌ Name/email cannot be empty."); return; }
        currentUser.setFullName(name);
        currentUser.setEmail(email);
        if (!newP.isEmpty()) {
            if (!currentUser.validatePassword(oldP)) {
                profileStatus.setForeground(RED); profileStatus.setText("❌ Current password incorrect."); return; }
            if (newP.length() < 6) {
                profileStatus.setForeground(RED); profileStatus.setText("❌ Password must be ≥ 6 chars."); return; }
            currentUser.changePassword(newP);
        }
        ExamDatabase.save(currentUser);
        profileStatus.setForeground(GREEN); profileStatus.setText("✅ Profile updated!");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PERFORMANCE ANALYSIS PANEL  (rebuilt each time)
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildPerfShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BG);
        shell.add(topBar("Performance Analysis", true), BorderLayout.NORTH);
        perfContentPanel = new JPanel(new BorderLayout());
        perfContentPanel.setBackground(BG);
        shell.add(new JScrollPane(perfContentPanel) {{
            getViewport().setBackground(BG);
            setBorder(null);
            getVerticalScrollBar().setUnitIncrement(14);
        }}, BorderLayout.CENTER);
        return shell;
    }

    private void showPerformance() {
        perfContentPanel.removeAll();
        perfContentPanel.add(buildPerfContent(), BorderLayout.CENTER);
        perfContentPanel.revalidate();
        perfContentPanel.repaint();
        cards.show(root, "PERF");
    }

    private JPanel buildPerfContent() {
        // reload fresh from DB
        User u = ExamDatabase.findByUsername(currentUser.getUsername());
        if (u != null) currentUser = u;

        List<User.Attempt> attempts = currentUser.getAttempts();

        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(20, 24, 20, 24));

        if (attempts.isEmpty()) {
            JLabel noData = label("No exams taken yet. Take an exam to see your analytics!", F_MED, TEXT_DIM);
            noData.setAlignmentX(CENTER_ALIGNMENT);
            wrap.add(vgap(80)); wrap.add(noData);
            return wrap;
        }

        // ── Summary row (4 stat tiles)
        JPanel summaryRow = new JPanel(new GridLayout(1, 4, 12, 0));
        summaryRow.setBackground(BG);
        summaryRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        int    total   = currentUser.getTotalAttempts();
        double avg     = currentUser.getAverageScore();
        int    best    = currentUser.getBestScore();
        int    worst   = currentUser.getWorstScore();

        summaryRow.add(statTile("Total Exams", String.valueOf(total),   ACCENT));
        summaryRow.add(statTile("Average",     String.format("%.1f/10", avg), GOLD));
        summaryRow.add(statTile("Best Score",  best  + "/10",           GREEN));
        summaryRow.add(statTile("Worst Score", worst + "/10",           RED));

        wrap.add(summaryRow); wrap.add(vgap(18));

        // ── Per-subject breakdown
        Map<String, int[]> stats = currentUser.getSubjectStats();
        JPanel subjRow = new JPanel(new GridLayout(1, stats.size(), 12, 0));
        subjRow.setBackground(BG);
        subjRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        Color[] subjectColors = {new Color(99,102,241), TEAL, GOLD};
        int ci = 0;
        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            int count   = e.getValue()[0];
            double subAvg = (double) e.getValue()[1] / count;
            JPanel tile = darkBox(200, 110);
            tile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            JLabel subjName = label(e.getKey(), F_SMALL, TEXT_DIM); subjName.setAlignmentX(CENTER_ALIGNMENT);
            JLabel avgLbl   = label(String.format("%.1f / 10", subAvg),
                    new Font("Segoe UI", Font.BOLD, 22), subjectColors[ci % 3]);
            avgLbl.setAlignmentX(CENTER_ALIGNMENT);
            JLabel cntLbl   = label(count + " attempt" + (count > 1 ? "s" : ""),
                    F_SMALL, TEXT_DIM); cntLbl.setAlignmentX(CENTER_ALIGNMENT);
            JLabel gradeL   = label("Grade: " + User.gradeFor(subAvg), F_SMALL,
                    colorForScore((int)Math.round(subAvg))); gradeL.setAlignmentX(CENTER_ALIGNMENT);
            tile.add(subjName); tile.add(vgap(4)); tile.add(avgLbl);
            tile.add(vgap(2)); tile.add(cntLbl); tile.add(vgap(4)); tile.add(gradeL);
            subjRow.add(tile);
            ci++;
        }
        wrap.add(subjRow); wrap.add(vgap(18));

        // ── Score-trend bar chart
        JPanel chartBox = darkBox(800, 160);
        chartBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        JLabel chartTitle = label("Score Trend (last 15 attempts)", F_BOLD, TEXT);
        chartTitle.setAlignmentX(LEFT_ALIGNMENT);
        chartBox.add(chartTitle); chartBox.add(vgap(8));

        List<User.Attempt> recent = attempts.subList(Math.max(0, attempts.size() - 15), attempts.size());
        chartBox.add(new BarChart(recent)); 

        wrap.add(chartBox); wrap.add(vgap(18));

        // ── Grade distribution
        JPanel distBox = darkBox(800, 100);
        distBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel distTitle = label("Grade Distribution", F_BOLD, TEXT);
        distTitle.setAlignmentX(LEFT_ALIGNMENT);
        distBox.add(distTitle); distBox.add(vgap(8));

        Map<String, Integer> gradeDist = new LinkedHashMap<>();
        gradeDist.put("A+ (9-10)", 0); gradeDist.put("B (7-8)", 0);
        gradeDist.put("C (5-6)", 0);   gradeDist.put("D (0-4)", 0);
        for (User.Attempt a : attempts) {
            if      (a.score >= 9) gradeDist.merge("A+ (9-10)", 1, Integer::sum);
            else if (a.score >= 7) gradeDist.merge("B (7-8)",   1, Integer::sum);
            else if (a.score >= 5) gradeDist.merge("C (5-6)",   1, Integer::sum);
            else                   gradeDist.merge("D (0-4)",   1, Integer::sum);
        }
        Color[] gc = {GREEN, TEAL, GOLD, RED};
        JPanel distRow = new JPanel(new GridLayout(1, 4, 8, 0));
        distRow.setBackground(PANEL); distRow.setOpaque(false);
        int gi = 0;
        for (Map.Entry<String, Integer> e : gradeDist.entrySet()) {
            JPanel gt = new JPanel();
            gt.setLayout(new BoxLayout(gt, BoxLayout.Y_AXIS));
            gt.setBackground(new Color(gc[gi].getRed(), gc[gi].getGreen(), gc[gi].getBlue(), 30));
            gt.setBorder(new LineBorder(gc[gi], 1, true));
            gt.setBorder(new CompoundBorder(new LineBorder(gc[gi], 1, true), new EmptyBorder(6,8,6,8)));
            JLabel gl = label(e.getValue().toString(), new Font("Segoe UI", Font.BOLD, 20), gc[gi]);
            gl.setAlignmentX(CENTER_ALIGNMENT);
            JLabel gn = label(e.getKey(), new Font("Segoe UI", Font.PLAIN, 10), TEXT_DIM);
            gn.setAlignmentX(CENTER_ALIGNMENT);
            gt.add(gl); gt.add(gn);
            distRow.add(gt); gi++;
        }
        distBox.add(distRow);
        wrap.add(distBox); wrap.add(vgap(18));

        // ── Attempt history table
        JPanel histBox = darkBox(800, 200);
        histBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        JLabel histTitle = label("Attempt History", F_BOLD, TEXT);
        histTitle.setAlignmentX(LEFT_ALIGNMENT);
        histBox.add(histTitle); histBox.add(vgap(8));

        JTextArea histTable = new JTextArea();
        histTable.setFont(F_MONO); histTable.setBackground(new Color(14,20,32));
        histTable.setForeground(TEXT); histTable.setEditable(false);
        histTable.setBorder(new EmptyBorder(8,12,8,12));
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s  %-26s  %-6s  %-6s  %s%n", "#", "Subject", "Score", "Grade", "Date"));
        sb.append("─".repeat(68)).append("\n");
        List<User.Attempt> allAttempts = new ArrayList<>(attempts);
        Collections.reverse(allAttempts);
        int idx = allAttempts.size();
        for (User.Attempt a : allAttempts) {
            sb.append(String.format("%-4d  %-26s  %-6s  %-6s  %s%n",
                idx--, a.subject, a.score + "/10", User.gradeFor(a.score), a.date));
        }
        histTable.setText(sb.toString());
        histTable.setCaretPosition(0);
        JScrollPane sp = new JScrollPane(histTable);
        sp.setBorder(new LineBorder(new Color(40,60,90),1));
        sp.setPreferredSize(new Dimension(800, 130));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        histBox.add(sp);
        wrap.add(histBox);

        return wrap;
    }

    // ── Mini bar chart ────────────────────────────────────────────────────────
    private class BarChart extends JPanel {
        private final List<User.Attempt> data;
        BarChart(List<User.Attempt> data) {
            this.data = data;
            setPreferredSize(new Dimension(800, 100));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            setBackground(new Color(14, 20, 32));
            setAlignmentX(LEFT_ALIGNMENT);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int n = data.size();
            int w = getWidth(), h = getHeight();
            int barW = Math.min(40, (w - 20) / n - 4);
            int totalW = n * (barW + 4) - 4;
            int startX = (w - totalW) / 2;
            for (int i = 0; i < n; i++) {
                int score = data.get(i).score;
                int barH = (int)((score / 10.0) * (h - 28));
                int x = startX + i * (barW + 4);
                int y = h - 20 - barH;
                Color c = colorForScore(score);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
                g2.fillRoundRect(x, y, barW, barH, 4, 4);
                g2.setColor(c);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.drawString(String.valueOf(score), x + barW/2 - 3, y - 2);
            }
            // baseline
            g2.setColor(new Color(60,80,100));
            g2.drawLine(startX - 4, h - 20, startX + totalW + 4, h - 20);
            g2.dispose();
        }
    }

    // ── Stat tile ─────────────────────────────────────────────────────────────
    private JPanel statTile(String title, String value, Color accent) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(CARD);
        tile.setBorder(new CompoundBorder(
            new LineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80), 1, true),
            new EmptyBorder(10, 12, 10, 12)));
        JLabel v = label(value, new Font("Segoe UI", Font.BOLD, 24), accent);
        v.setAlignmentX(CENTER_ALIGNMENT);
        JLabel t = label(title, F_SMALL, TEXT_DIM);
        t.setAlignmentX(CENTER_ALIGNMENT);
        tile.add(v); tile.add(vgap(4)); tile.add(t);
        return tile;
    }

    private Color colorForScore(int s) {
        if (s >= 9) return GREEN;
        if (s >= 7) return TEAL;
        if (s >= 5) return GOLD;
        return RED;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXAM LOGIC
    // ══════════════════════════════════════════════════════════════════════════
    private void startExam(String subject) {
        selectedSubject = subject;
        List<Question> all = new ArrayList<>(QuestionBank.getAllSubjects().get(subject));
        Collections.shuffle(all);
        examQuestions = all.subList(0, 10);
        answers = new int[10]; Arrays.fill(answers, -1);
        currentQ = 0; secondsLeft = 600;
        cards.show(root, "EXAM");
        renderQuestion(); startTimer();
    }

    private void renderQuestion() {
        Question q = examQuestions.get(currentQ);
        qNumLabel.setText("  Question " + (currentQ + 1) + " of 10  ·  " + selectedSubject);
        qText.setText("<html><body style='width:520px'>" + (currentQ+1) + ". " + q.getQuestionText() + "</body></html>");
        qProgress.setValue(currentQ + 1);
        optGroup.clearSelection();
        String[] opts = q.getOptions(); String[] labels = {"A","B","C","D"};
        for (int i = 0; i < 4; i++) {
            optBtns[i].setText("  " + labels[i] + ".  " + opts[i]);
            optBtns[i].setSelected(answers[currentQ] == i);
        }
        prevBtn.setEnabled(currentQ > 0);
        nextBtn.setVisible(currentQ < 9);
        submitBtn.setVisible(currentQ == 9);
    }

    private void navigate(int dir) { saveCurrentAnswer(); currentQ += dir; currentQ = Math.max(0, Math.min(9, currentQ)); renderQuestion(); }

    private void saveCurrentAnswer() {
        for (int i = 0; i < 4; i++) if (optBtns[i].isSelected()) { answers[currentQ] = i; return; }
        answers[currentQ] = -1;
    }

    private void startTimer() {
        if (examTimer != null) examTimer.cancel();
        examTimer = new Timer();
        examTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            public void run() {
                secondsLeft--;
                int m = secondsLeft / 60, s = secondsLeft % 60;
                Color c = secondsLeft <= 60 ? RED : GOLD;
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText(String.format("⏱  %02d:%02d  ", m, s));
                    timerLabel.setForeground(c);
                });
                if (secondsLeft <= 0) {
                    examTimer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(ExamApp.this, "⏰ Time's up! Auto-submitting.", "Time Up", JOptionPane.WARNING_MESSAGE);
                        finishExam();
                    });
                }
            }
        }, 1000, 1000);
    }

    private void finishExam() {
        saveCurrentAnswer();
        if (examTimer != null) examTimer.cancel();

        int score = 0;
        StringBuilder detail = new StringBuilder();
        detail.append(String.format("%-4s %-48s %-12s %s%n", "Q", "Question", "Your Ans", "Result"));
        detail.append("─".repeat(80)).append("\n");

        for (int i = 0; i < 10; i++) {
            Question q = examQuestions.get(i);
            boolean correct = answers[i] >= 0 && q.isCorrect(answers[i]);
            if (correct) score++;
            String[] opts = q.getOptions();
            String yourAns = answers[i] >= 0 ? opts[answers[i]] : "Skipped";
            String result  = correct ? "✓ Correct" : "✗ Wrong (→ " + opts[q.getCorrectIndex()] + ")";
            String qShort  = q.getQuestionText().length() > 44 ? q.getQuestionText().substring(0,44)+"…" : q.getQuestionText();
            detail.append(String.format("%-4d %-48s %-12s %s%n", i+1, qShort, yourAns.length()>10?yourAns.substring(0,10):yourAns, result));
        }

        // Persist to database
        ExamDatabase.recordAttempt(currentUser, selectedSubject, score);

        double pct = score * 10.0;
        String grade; Color gradeColor;
        if      (score >= 9) { grade = "🏆 Outstanding — A+"; gradeColor = GREEN; }
        else if (score >= 7) { grade = "✅ Good — B";          gradeColor = TEAL;  }
        else if (score >= 5) { grade = "⚠ Average — C";        gradeColor = GOLD;  }
        else                 { grade = "❌ Needs Improvement";  gradeColor = RED;   }

        resultTitle.setText(selectedSubject + " · Exam Complete");
        resultScore.setText(score + " / 10"); resultScore.setForeground(gradeColor);
        resultPercent.setText(pct + "% correct");
        resultGrade.setText(grade); resultGrade.setForeground(gradeColor);
        resultDetail.setText(detail.toString()); resultDetail.setCaretPosition(0);
        cards.show(root, "RESULT");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel topBar(String title, boolean showProfile) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(14, 20, 32));
        bar.setBorder(new EmptyBorder(12, 20, 12, 20));
        bar.add(label("📝  " + title, F_BOLD, TEXT), BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(new Color(14, 20, 32));
        if (showProfile) {
            JButton profBtn = accentBtn("👤 Profile", CARD);
            profBtn.addActionListener(e -> refreshProfile());
            JButton perfBtn = accentBtn("📊 Analytics", new Color(20,80,80));
            perfBtn.addActionListener(e -> showPerformance());
            right.add(perfBtn); right.add(profBtn);
        }
        JButton logout = accentBtn("Logout", new Color(70,20,20));
        logout.setForeground(RED);
        logout.addActionListener(e -> { if (examTimer != null) examTimer.cancel(); currentUser = null; cards.show(root, "LOGIN"); });
        right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel centeredPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBackground(BG); return p; }

    private JPanel darkBox(int w, int h) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(PANEL);
        box.setBorder(new CompoundBorder(new LineBorder(new Color(50,70,110),1,true), new EmptyBorder(24,32,24,32)));
        box.setPreferredSize(new Dimension(w, h));
        box.setMaximumSize(new Dimension(w, Integer.MAX_VALUE));
        box.setAlignmentX(CENTER_ALIGNMENT);
        return box;
    }

    private JLabel label(String t, Font f, Color c) { JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); return l; }

    private JLabel labelRow(String t) { JLabel l = label(t, F_SMALL, TEXT_DIM); l.setAlignmentX(LEFT_ALIGNMENT); return l; }

    private static JTextField styledField() { JTextField f = new JTextField(); styleField(f); return f; }

    private static void styleField(JTextField f) {
        f.setBackground(new Color(14,20,32)); f.setForeground(new Color(226,232,240));
        f.setCaretColor(new Color(99,102,241));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(50,70,110),1,true), new EmptyBorder(6,10,6,10)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JButton accentBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN); btn.setBackground(bg); btn.setForeground(TEXT);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8,18,8,18));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private Component vgap(int h) { return Box.createVerticalStrut(h); }

    private JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(50,70,110));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(ExamApp::new);
    }
}
