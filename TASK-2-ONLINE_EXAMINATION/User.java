import java.util.*;

public class User {

    /** A single completed exam attempt. */
    public static class Attempt {
        public final String subject;
        public final int    score;
        public final String date;

        public Attempt(String subject, int score, String date) {
            this.subject = subject;
            this.score   = score;
            this.date    = date;
        }
    }

    private String username;
    private String password;
    private String fullName;
    private String email;
    private List<Attempt> attempts = new ArrayList<>();

    public User(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
    }

    // ── Auth ──────────────────────────────────────────────────────────────────
    public boolean validatePassword(String input) { return this.password.equals(input); }
    public void    changePassword(String newPass)  { this.password = newPass; }
    public String  getPasswordRaw()               { return password; } // for DB only

    // ── Attempts ──────────────────────────────────────────────────────────────
    public void         addAttempt(Attempt a)     { attempts.add(a); }
    public List<Attempt> getAttempts()            { return attempts; }

    /** Most recent attempt, or null. */
    public Attempt getLastAttempt() {
        return attempts.isEmpty() ? null : attempts.get(attempts.size() - 1);
    }

    /** Legacy helpers kept for compatibility with ExamApp. */
    public void setLastResult(String subject, int score) {
        // Actual persistence is handled by ExamDatabase.recordAttempt()
    }
    public int    getLastScore()   { Attempt a = getLastAttempt(); return a == null ? -1 : a.score; }
    public String getLastSubject() { Attempt a = getLastAttempt(); return a == null ? ""  : a.subject; }

    // ── Profile ───────────────────────────────────────────────────────────────
    public String getUsername()        { return username; }
    public String getFullName()        { return fullName; }
    public String getEmail()           { return email; }
    public void   setFullName(String n){ this.fullName = n; }
    public void   setEmail(String e)   { this.email   = e; }

    // ── Analytics helpers ─────────────────────────────────────────────────────

    /** Total number of exams taken. */
    public int getTotalAttempts() { return attempts.size(); }

    /** Average score across all attempts, or -1 if none. */
    public double getAverageScore() {
        if (attempts.isEmpty()) return -1;
        return attempts.stream().mapToInt(a -> a.score).average().orElse(-1);
    }

    /** Best score across all attempts, or -1 if none. */
    public int getBestScore() {
        return attempts.stream().mapToInt(a -> a.score).max().orElse(-1);
    }

    /** Worst score across all attempts, or -1 if none. */
    public int getWorstScore() {
        return attempts.stream().mapToInt(a -> a.score).min().orElse(-1);
    }

    /** Per-subject statistics: subject → {count, totalScore}. */
    public Map<String, int[]> getSubjectStats() {
        Map<String, int[]> map = new LinkedHashMap<>();
        for (Attempt a : attempts) {
            map.computeIfAbsent(a.subject, k -> new int[2]);
            map.get(a.subject)[0]++;
            map.get(a.subject)[1] += a.score;
        }
        return map;
    }

    /** Grade for a given score (0–10). */
    public static String gradeFor(double score) {
        if (score >= 9) return "A+";
        if (score >= 7) return "B";
        if (score >= 5) return "C";
        return "D";
    }
}
