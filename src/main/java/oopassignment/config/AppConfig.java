package oopassignment.config;

import java.util.List;

public final class AppConfig {

    private AppConfig() {
    }

    // Environment + configuration switches
    public static final String ENVIRONMENT = System.getProperty("bootsdo.env", "PROD");

    // Auth and pricing knobs
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    public static final long LOCK_DURATION_MS = 60_000L; // 1 minute lockout
    public static final double MEMBER_DISCOUNT_RATE = 0.05;

    // UI helpers
    public static final String BACK_TOKEN = "X";
    public static final List<String> ALLOWED_PRODUCT_CATEGORIES = List.of("clothes", "shoes");

    // DB config
    public static final String DB_URL = "jdbc:sqlite:bootsdo.db";
    public static final int SCHEMA_VERSION = 1;
}
