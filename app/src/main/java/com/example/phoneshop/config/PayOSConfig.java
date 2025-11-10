package com.example.phoneshop.config;

/**
 * PayOS Configuration class
 */
public class PayOSConfig {
    
    // PayOS API keys - Sử dụng keys thực tế
    public static final String CLIENT_ID = "b274fe20-57bc-4f30-a871-a93818f2bf1c";
    public static final String API_KEY = "70280021-7f59-4faa-8c18-aab0ac8c9fd4";
    public static final String CHECKSUM_KEY = "337f0be5495199b742fd395bd477744dac654f229d623d9510e506f90fd23e07";
    
    // Return URLs
    public static final String RETURN_URL_SUCCESS = "phoneshop://payment-success";
    public static final String RETURN_URL_CANCEL = "phoneshop://payment-cancel";
    
    // PayOS Environment
    public static final boolean IS_SANDBOX = true; // Set to false for production
    
    // PayOS Base URLs
    public static final String SANDBOX_URL = "https://api-merchant.payos.vn";
    public static final String PRODUCTION_URL = "https://api-merchant.payos.vn";
    
    public static String getBaseUrl() {
        return IS_SANDBOX ? SANDBOX_URL : PRODUCTION_URL;
    }
}
