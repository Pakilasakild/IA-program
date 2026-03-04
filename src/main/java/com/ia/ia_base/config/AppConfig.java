package com.ia.ia_base.config;

/**
 * Application configuration class.
 * Controls whether to use database or not.
 * 
 * USAGE:
 * - By default DB is not used (useDatabase = false)
 * - This allows creating UI without DB errors
 * - When ready, change to true or use setUseDatabase(true)
 * 
 * EXAMPLE:
 * AppConfig.setUseDatabase(true);  // Enable DB
 * AppConfig.setUseDatabase(false); // Disable DB
 */
public class AppConfig {
    
    /**
     * Whether to use database.
     * Default: false (not used) - application starts without DB
     * Change to true when ready to use DB
     */
    private static boolean useDatabase = true;
    
    /**
     * Gets whether database is used
     */
    public static boolean isUseDatabase() {
        return useDatabase;
    }
    
    /**
     * Sets whether to use database
     * @param useDatabase true - use DB, false - don't use
     */
    public static void setUseDatabase(boolean useDatabase) {
        AppConfig.useDatabase = useDatabase;
        if (useDatabase) {
            System.out.println("Database usage: ENABLED");
        } else {
            System.out.println("Database usage: DISABLED");
        }
    }
    
    /**
     * Enables database usage
     */
    public static void enableDatabase() {
        setUseDatabase(true);
    }
    
    /**
     * Disables database usage
     */
    public static void disableDatabase() {
        setUseDatabase(false);
    }
}

