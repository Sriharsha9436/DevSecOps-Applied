package com.example;

import java.io.*;
import java.sql.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/*
 * Vulnerable patterns:
 * 1) Hard-coded credentials
 * 2) SQL Injection via string concatenation
 * 3) Command execution with unvalidated input
 * 4) Insecure cryptography (DES with static key)
 * 5) XXE via insecure XML parsing
 */

public class VulnerableApp {
    // 1) Hard-coded credentials (should be stored in a secret manager)
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "P@ssw0rd123"; // intentional hard-coded secret

    public static void main(String[] args) throws Exception {
        // SQL injection demo (do not use in production)
        String usernameFromUser = args.length > 0 ? args[0] : "alice'; DROP TABLE users; --";
        demonstrateSqlInjection(usernameFromUser);

        // Command injection demo (incorrectly trusting input)
        String fileName = args.length > 1 ? args[1] : "somefile.txt; echo vulnerable";
        demonstrateCommandExecution(fileName);

        // Insecure crypto usage
        String secret = "somesensitive";
        byte[] encrypted = insecureEncrypt(secret);
        System.out.println("Encrypted (insecure): " + new String(encrypted));

        // XXE demo (parses sample.xml from resources)
        parseXmlWithXxe();

        // Unsafe deserialization (reads object from a file in working directory)
        UnsafeDeserialization.readObjectFromFile("untrusted-obj.bin");
    }

    // SQL injection via string concatenation
    public static void demonstrateSqlInjection(String username) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // Using H2 in-memory DB for compilation/testing
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb", DB_USER, DB_PASS);
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(100))");
            stmt.execute("INSERT INTO users (id, name) VALUES (1, 'alice')");

            // Vulnerable: concatenating untrusted input into SQL
            String sql = "SELECT * FROM users WHERE name = '" + username + "'";
            System.out.println("Executing SQL: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println("Found user: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    // Command execution with unvalidated input
    public static void demonstrateCommandExecution(String fileName) {
        try {
            // Vulnerable: directly appending input into command string
            String[] cmd = {"/bin/sh", "-c", "ls " + fileName};
            System.out.println("Running command: " + String.join(" ", cmd));
            Process p = Runtime.getRuntime().exec(cmd);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Command error: " + e.getMessage());
        }
    }

    // Insecure cryptography: DES with static key and insecure mode/IV
    public static byte[] insecureEncrypt(String plaintext) {
        try {
            javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec("01234567".getBytes("UTF-8"), "DES");
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plaintext.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // XXE example: insecure XML parsing
    public static void parseXmlWithXxe() {
        try {
            InputStream is = VulnerableApp.class.getResourceAsStream("/sample.xml");
            if (is == null) {
                System.out.println("sample.xml not found in resources");
                return;
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // NOT disabling external entities (vulnerable)
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(is));
            NodeList nl = doc.getElementsByTagName("message");
            if (nl.getLength() > 0) {
                System.out.println("XML message: " + nl.item(0).getTextContent());
            }
        } catch (Exception e) {
            System.err.println("XML parse error: " + e.getMessage());
        }
    }
}
