package com.bitcoinchecker.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_FILE = "log.txt";
    private final BufferedWriter writer;
    private final SimpleDateFormat dateFormat;

    private Logger() throws IOException {
        writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
        dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    }

    private static class InstanceHolder {
        private static final Logger INSTANCE;
        static {
            try {
                INSTANCE = new Logger();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize logger", e);
            }
        }
    }

    public static Logger getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void log(String message) {
        try {
            String timestamp = dateFormat.format(new Date());
            writer.write(timestamp + " " + message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to log: " + e.getMessage());
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to close logger: " + e.getMessage());
        }
    }
}