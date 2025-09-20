/**
 * Basic authentication utility functions
 */

// Check if user is authenticated
export function isAuthenticated() {
    return !!localStorage.getItem('authToken');
}

// Save authentication token
export function setAuthToken(token) {
    localStorage.setItem('authToken', token);
}

// Remove authentication token
export function clearAuthToken() {
    localStorage.removeItem('authToken');
}

// Get authentication token
export function getAuthToken() {
    return localStorage.getItem('authToken');
}