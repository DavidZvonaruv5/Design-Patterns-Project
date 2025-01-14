package com.bitcoinchecker.util;


import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Singleton logger implementation for application-wide logging.
 * Features:
 * - Writes logs to file and displays in UI
 * - Color coded logging (green/red)
 * - Timestamp formatting
 * - File persistence with reload on startup
 * - Thread-safe logging via SwingUtilities.invokeLater
 * Uses static initialization holder pattern for thread-safe singleton.
 */
public class Logger {
    private static final String LOG_FILE = "logs.txt";
    private JTextPane logArea;
    private final SimpleAttributeSet greenStyle;
    private final SimpleAttributeSet redStyle;
    private final BufferedWriter writer;
    private final SimpleDateFormat dateFormat;
    private static JTextPane uiLogArea;

    private Logger(JTextPane logArea) throws IOException {
        this.logArea = logArea;
        this.writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
        this.dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

        greenStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(greenStyle, new Color(0, 128, 0));

        redStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(redStyle, Color.RED);

        loadExistingLogs();
    }

    private void loadExistingLogs() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(LOG_FILE));
            for(String line : lines) {
                if(line.contains("Error") || line.contains("failed")) {
                    appendToLogArea(line + "\n", redStyle);
                } else {
                    appendToLogArea(line + "\n", greenStyle);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load logs: " + e.getMessage());
        }
    }

    public void log(String message, boolean isError) {
        String timestamp = dateFormat.format(new Date());
        String fullMessage = timestamp + " " + message;

        try {
            writer.write(fullMessage + "\n");
            writer.flush();

            SwingUtilities.invokeLater(() -> {
                appendToLogArea(fullMessage + "\n", isError ? redStyle : greenStyle);
            });
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    private void appendToLogArea(String text, SimpleAttributeSet style) {
        StyledDocument doc = (StyledDocument) logArea.getDocument();
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            System.err.println("Error appending to log area: " + e.getMessage());
        }
    }

    public static void setLogArea(JTextPane logArea) {
        uiLogArea = logArea;
        InstanceHolder.INSTANCE.logArea = logArea;
        InstanceHolder.INSTANCE.loadExistingLogs();
    }

    private static class InstanceHolder {
        private static final Logger INSTANCE;
        static {
            try {
                INSTANCE = new Logger(new JTextPane());
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize logger", e);
            }
        }
    }

    public static Logger getInstance() {
        return InstanceHolder.INSTANCE;
    }

}