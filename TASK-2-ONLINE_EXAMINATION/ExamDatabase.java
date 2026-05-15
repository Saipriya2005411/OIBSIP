import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Zero-dependency file-based database.
 * Stores all user data in  examportal_db.json  (one JSON line per user).
 *
 * Each line schema:
 *  {"username":"...","password":"...","fullName":"...","email":"...",
 *   "attempts":[{"subject":"...","score":8,"date":"2024-05-09 14:32"},…]}
 */
public class ExamDatabase {

    private static final String DB_FILE = "examportal_db.json";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── Bootstrap ─────────────────────────────────────────────────────────────

    public static void init() {
        File f = new File(DB_FILE);
        if (!f.exists()) {
            save(new User("student1", "pass123",  "Saipriya Sharma",  "sai@email.com"));
            save(new User("student2", "java2024", "Rahul Verma",      "rahul@email.com"));
            save(new User("admin",    "admin123", "Admin User",       "admin@exam.com"));
            System.out.println("[DB] Created " + DB_FILE + " with demo accounts.");
        }
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public static Map<String, User> loadAll() {
        Map<String, User> map = new LinkedHashMap<>();
        File f = new File(DB_FILE);
        if (!f.exists()) return map;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                User u = fromJson(line);
                if (u != null) map.put(u.getUsername(), u);
            }
        } catch (IOException e) { System.err.println("[DB] Read error: " + e.getMessage()); }
        return map;
    }

    public static User findByUsername(String username) {
        return loadAll().get(username);
    }

    public static boolean exists(String username) {
        return findByUsername(username) != null;
    }

    // ── Persist ───────────────────────────────────────────────────────────────

    public static void save(User u) {
        Map<String, User> all = loadAll();
        all.put(u.getUsername(), u);
        writeAll(all);
    }

    /** Record a completed exam attempt and immediately persist. */
    public static void recordAttempt(User u, String subject, int score) {
        String date = LocalDateTime.now().format(FMT);
        u.addAttempt(new User.Attempt(subject, score, date));
        save(u);
    }

    private static void writeAll(Map<String, User> all) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DB_FILE, false))) {
            for (User u : all.values()) { bw.write(toJson(u)); bw.newLine(); }
        } catch (IOException e) { System.err.println("[DB] Write error: " + e.getMessage()); }
    }

    // ── Minimal JSON (no external libs) ──────────────────────────────────────

    static String toJson(User u) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"username\":").append(js(u.getUsername())).append(",");
        sb.append("\"password\":").append(js(u.getPasswordRaw())).append(",");
        sb.append("\"fullName\":").append(js(u.getFullName())).append(",");
        sb.append("\"email\":").append(js(u.getEmail())).append(",");
        sb.append("\"attempts\":[");
        List<User.Attempt> attempts = u.getAttempts();
        for (int i = 0; i < attempts.size(); i++) {
            if (i > 0) sb.append(",");
            User.Attempt a = attempts.get(i);
            sb.append("{\"subject\":").append(js(a.subject))
              .append(",\"score\":").append(a.score)
              .append(",\"date\":").append(js(a.date)).append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static User fromJson(String json) {
        try {
            String username = jget(json, "username");
            String password = jget(json, "password");
            String fullName = jget(json, "fullName");
            String email    = jget(json, "email");
            User u = new User(username, password, fullName, email);

            // parse attempts array
            String arrKey = "\"attempts\":[";
            int ai = json.indexOf(arrKey);
            if (ai >= 0) {
                int start = ai + arrKey.length();
                int end   = json.lastIndexOf(']');
                String arrContent = json.substring(start, end);
                // split by },{ boundaries
                for (String chunk : splitObjects(arrContent)) {
                    try {
                        String subj  = jget(chunk, "subject");
                        int    score = Integer.parseInt(jget(chunk, "score"));
                        String date  = jget(chunk, "date");
                        u.addAttempt(new User.Attempt(subj, score, date));
                    } catch (Exception ignored) {}
                }
            }
            return u;
        } catch (Exception e) {
            System.err.println("[DB] Parse error: " + e.getMessage());
            return null;
        }
    }

    /** Split "{ ... },{ ... },{ ... }" into individual object strings. */
    private static List<String> splitObjects(String s) {
        List<String> list = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) { list.add(s.substring(start, i + 1)); start = -1; }
            }
        }
        return list;
    }

    // tiny JSON helpers
    private static String js(String s) {
        return "\"" + s.replace("\\","\\\\").replace("\"","\\\"")
                       .replace("\n","\\n").replace("\r","") + "\"";
    }

    private static String jget(String json, String key) {
        String kp = "\"" + key + "\":";
        int ki = json.indexOf(kp);
        if (ki < 0) throw new IllegalArgumentException("Key missing: " + key);
        int vi = ki + kp.length();
        if (json.charAt(vi) == '"') {
            int s = vi + 1, e = s;
            while (e < json.length()) {
                if (json.charAt(e) == '\\') { e += 2; continue; }
                if (json.charAt(e) == '"') break;
                e++;
            }
            return json.substring(s, e).replace("\\\"","\"").replace("\\n","\n").replace("\\\\","\\");
        } else {
            int e = vi;
            while (e < json.length() && ",}]".indexOf(json.charAt(e)) < 0) e++;
            return json.substring(vi, e).trim();
        }
    }
}
