package com.example;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Base64;

public class App {
    public static void main(String[] args) {
        try {
            // ---------------------------
            // 1) Hard-coded credentials
            // ---------------------------
            // 🚨 SECURITY HOTSPOT: hard-coded credentials
            String user = "admin";
            String password = "Password123"; // intentionally insecure

            Connection conn = null;
            try {
                // Attempt to connect to a local DB (for demonstration only)
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", user, password);

                // ---------------------------
                // 2) SQL Injection
                // ---------------------------
                // 🚨 VULNERABILITY: building SQL with unsanitized input
                String unsafeInput = "1 OR 1=1"; // simulate attacker-controlled input
                Statement stmt = conn.createStatement();
                stmt.executeQuery("SELECT * FROM users WHERE id = " + unsafeInput);
            } catch (Exception e) {
                // swallow exception for demonstration
            } finally {
                if (conn != null) {
                    try { conn.close(); } catch (Exception ignored) {}
                }
            }

            // ---------------------------
            // 3) Insecure deserialization
            // ---------------------------
            // 🚨 VULNERABILITY: reading serialized objects from an untrusted source
            // (Demonstration only — file must exist to run without exception)
            try (FileInputStream fis = new FileInputStream("data/obj.bin");
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                Object obj = ois.readObject(); // unsafe deserialization
                System.out.println("Deserialized object class: " + obj.getClass().getName());
            } catch (Exception ex) {
                // ignore errors for demo
            }

            // ---------------------------
            // 4) Unsafe command execution
            // ---------------------------
            // 🚨 RCE-like pattern: invoking shell with unsanitized input
            try {
                String cmd = "/bin/sh -c ls " + "/tmp"; // concatenation (demonstration)
                Runtime.getRuntime().exec(cmd);
            } catch (Exception ex) {
                // ignore
            }

            // ---------------------------
            // 5) Weak cryptography usage / predictable token
            // ---------------------------
            String token = Base64.getEncoder().encodeToString("secret-token".getBytes()); // predictable
            System.out.println("Using token: " + token);

            System.out.println("Vulnerable code executed!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
