package pl.polsl.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for managing HTTP cookies.
 * <p>
 * This class provides methods to create, read, update, and delete cookies
 * with proper encoding to handle special characters.
 * </p>
 *
 * @author Maciej Porebski
 * @version 2.0
 */
public class CookieManager {
    
    /** Default cookie expiration time in seconds (30 days). */
    private static final int DEFAULT_MAX_AGE = 30 * 24 * 60 * 60;
    
    /** Short expiration for session-like cookies (1 hour). */
    public static final int SESSION_MAX_AGE = 60 * 60;
    
    /**
     * Creates and adds a cookie to the response.
     * <p>
     * The value is URL-encoded to handle spaces, commas and special characters.
     * </p>
     *
     * @param response the HttpServletResponse to add cookie to
     * @param name     the cookie name
     * @param value    the cookie value
     * @param maxAge   the cookie lifetime in seconds
     */
    public static void setCookie(HttpServletResponse response, String name, 
                                 String value, int maxAge) {
        try {
            // URL encode to handle spaces, commas and special characters
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            
            Cookie cookie = new Cookie(name, encodedValue);
            cookie.setMaxAge(maxAge);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            
            response.addCookie(cookie);
            
        } catch (Exception e) {
            System.err.println("Error setting cookie: " + e.getMessage());
        }
    }
    
    /**
     * Creates and adds a cookie with default expiration time.
     *
     * @param response the HttpServletResponse to add cookie to
     * @param name     the cookie name
     * @param value    the cookie value
     */
    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, DEFAULT_MAX_AGE);
    }
    
    /**
     * Retrieves a cookie value by name.
     * <p>
     * The value is URL-decoded to restore original content.
     * </p>
     *
     * @param request the HttpServletRequest to read cookies from
     * @param name    the cookie name to find
     * @return the decoded cookie value or null if not found
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        String decodedValue = URLDecoder.decode(
                            cookie.getValue(), 
                            StandardCharsets.UTF_8.toString()
                        );
                        return decodedValue;
                    } catch (Exception e) {
                        System.err.println("Error decoding cookie: " + e.getMessage());
                        return cookie.getValue();
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Deletes a cookie by setting its max age to 0.
     *
     * @param response the HttpServletResponse to delete cookie from
     * @param name     the cookie name to delete
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    
    /**
     * Checks if a cookie exists.
     *
     * @param request the HttpServletRequest to check cookies in
     * @param name    the cookie name to find
     * @return true if cookie exists, false otherwise
     */
    public static boolean hasCookie(HttpServletRequest request, String name) {
        return getCookie(request, name) != null;
    }
}
