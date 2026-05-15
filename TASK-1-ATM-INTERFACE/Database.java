import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Lightweight file-based database using a plain-text JSON file (neobank_db.json).
 * No external library required — pure Java I/O.
 *
 * Schema (one JSON object per line):
 *   {"userId":"user01","pin":"1234","holderName":"Saipriya","balance":50000.00,"history":["...",...]}
 */
public class Database {

    private static final String DB_FILE = "neobank_db.json";

    // ── Bootstrap ─────────────────────────────────────────────────────────────

    /**
     * Ensures the DB file exists and is seeded with demo accounts.
     * Call once at startup.
     */
    public static void init() {
        File f = new File(DB_FILE);
        if (!f.exists()) {
            List<Account> seed = Arrays.asList(
                new Account("user01", "1234", "Saipriya",  50000.00),
                new Account("user02", "5678", "Vishal",    25000.00),
                new Account("user03", "9999", "Shaaya",    75000.00)
            );
            for (Account a : seed) save(a);
            System.out.println("[DB] Created " + DB_FILE + " with demo accounts.");
        }
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Load all accounts from the DB file. */
    public static Map<String, Account> loadAll() {
        Map<String, Account> map = new LinkedHashMap<>();
        File f = new File(DB_FILE);
        if (!f.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Account a = fromJson(line);
                if (a != null) map.put(a.getUserId(), a);
            }
        } catch (IOException e) {
            System.err.println("[DB] Read error: " + e.getMessage());
        }
        return map;
    }

    /** Find a single account by userId. */
    public static Account findById(String userId) {
        return loadAll().get(userId);
    }

    /** Find a single account by holder name (case-insensitive). */
    public static Account findByName(String name) {
        for (Account a : loadAll().values()) {
            if (a.getHolderName().equalsIgnoreCase(name)) return a;
        }
        return null;
    }

    /** Check whether a userId already exists. */
    public static boolean exists(String userId) {
        return findById(userId) != null;
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    /**
     * Persist (insert or update) an account.
     * The whole file is rewritten to keep it consistent.
     */
    public static void save(Account a) {
        Map<String, Account> all = loadAll();
        all.put(a.getUserId(), a);
        writeAll(all);
    }

    /** Rewrite the entire DB file from the given map. */
    private static void writeAll(Map<String, Account> all) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DB_FILE, false))) {
            for (Account a : all.values()) {
                bw.write(toJson(a));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[DB] Write error: " + e.getMessage());
        }
    }

    // ── Minimal JSON serialisation ─────────────────────────────────────────────
    // We roll our own tiny serialiser to stay dependency-free.

    private static String toJson(Account a) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"userId\":").append(jsonStr(a.getUserId())).append(",");
        sb.append("\"pin\":").append(jsonStr(a.getPinRaw())).append(",");
        sb.append("\"holderName\":").append(jsonStr(a.getHolderName())).append(",");
        sb.append("\"balance\":").append(a.getBalance()).append(",");
        sb.append("\"history\":[");
        List<String> hist = a.getTransactionHistory();
        for (int i = 0; i < hist.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(jsonStr(hist.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    private static Account fromJson(String json) {
        try {
            String userId     = jsonGet(json, "userId");
            String pin        = jsonGet(json, "pin");
            String holderName = jsonGet(json, "holderName");
            double balance    = Double.parseDouble(jsonGet(json, "balance"));

            Account a = new Account(userId, pin, holderName, balance);
            // replace the auto-generated "Account opened" history with stored history
            a.getTransactionHistory().clear();
            List<String> hist = jsonGetArray(json, "history");
            a.getTransactionHistory().addAll(hist);
            return a;
        } catch (Exception e) {
            System.err.println("[DB] Parse error on line: " + json + " → " + e.getMessage());
            return null;
        }
    }

    // Very small JSON helpers (handle basic escaped strings)
    private static String jsonStr(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                       .replace("\n","\\n").replace("\r","") + "\"";
    }

    private static String jsonGet(String json, String key) {
        // Matches "key": "value"  OR  "key": number
        String kp = "\"" + key + "\":";
        int ki = json.indexOf(kp);
        if (ki < 0) throw new IllegalArgumentException("Key not found: " + key);
        int vi = ki + kp.length();
        if (json.charAt(vi) == '"') {
            // string value
            int start = vi + 1;
            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '\\') { end += 2; continue; }
                if (json.charAt(end) == '"')  break;
                end++;
            }
            return unescapeJson(json.substring(start, end));
        } else {
            // numeric value
            int end = vi;
            while (end < json.length() && ",}".indexOf(json.charAt(end)) < 0) end++;
            return json.substring(vi, end).trim();
        }
    }

    private static List<String> jsonGetArray(String json, String key) {
        List<String> list = new ArrayList<>();
        String kp = "\"" + key + "\":[";
        int ki = json.indexOf(kp);
        if (ki < 0) return list;
        int i = ki + kp.length();
        // iterate string elements
        while (i < json.length() && json.charAt(i) != ']') {
            if (json.charAt(i) == '"') {
                int start = i + 1, end = start;
                while (end < json.length()) {
                    if (json.charAt(end) == '\\') { end += 2; continue; }
                    if (json.charAt(end) == '"')  break;
                    end++;
                }
                list.add(unescapeJson(json.substring(start, end)));
                i = end + 1;
            } else {
                i++;
            }
        }
        return list;
    }

    private static String unescapeJson(String s) {
        return s.replace("\\\"", "\"").replace("\\n", "\n")
                .replace("\\\\", "\\");
    }
}
