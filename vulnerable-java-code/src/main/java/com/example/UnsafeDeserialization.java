package com.example;

import java.io.*;

/*
 * Very simple example showing unsafe deserialization pattern:
 * - Reading a serialized object from a file without validation.
 * This is intentionally insecure for SAST detection. Do NOT do this in real code.
 */

public class UnsafeDeserialization {
    public static void readObjectFromFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("Deserialization file not found: " + path);
            return;
        }
        try (FileInputStream fis = new FileInputStream(f);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Vulnerable: directly reading object from untrusted source
            Object obj = ois.readObject();
            System.out.println("Deserialized object class: " + obj.getClass().getName());
        } catch (Exception e) {
            System.err.println("Deserialization error: " + e.getMessage());
        }
    }
}
