document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const errorMsg = document.getElementById('error-message');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Clear previous errors
        errorMsg.style.display = 'none';
        errorMsg.textContent = '';

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await apiClient.post('/login', {
                username: username,
                password: password
            });

            const data = response.data;

            if (data.success) {
                // Store the access token in memory (via axios-config.js helper)
                // Note: Since we are redirecting, this in-memory token will be lost.
                // However, the browser will store the HttpOnly refresh token cookie.
                // When index.html loads, it should attempt to refresh the token immediately
                // or handle the 401 to get a new access token.

                // If you want to persist the access token across page loads without using localStorage,
                // you typically can't (unless you use sessionStorage which is similar to localStorage).
                // The standard pattern with HttpOnly cookies is:
                // 1. Login -> Server sets HttpOnly Refresh Token Cookie + returns Access Token
                // 2. Redirect to App
                // 3. App loads -> Access Token is gone (memory cleared)
                // 4. App makes first API call -> 401 -> Interceptor catches it -> Calls /refresh-token -> Success -> Replays request

                // So we don't strictly need to do anything with data.data.accessToken here *if* we are redirecting immediately,
                // EXCEPT if we were a Single Page Application (SPA) staying on the same page.
                // But since we are redirecting to index.html:

                console.log('Login successful, redirecting...');
                window.location.href = '/web/index.html';
            } else {
                showError(data.message || 'Login failed');
            }

        } catch (error) {
            console.error('Login error:', error);
            let message = 'An error occurred during login.';
            if (error.response && error.response.data && error.response.data.message) {
                message = error.response.data.message;
            } else if (error.message) {
                message = error.message;
            }
            showError(message);
        }
    });

    function showError(message) {
        errorMsg.textContent = message;
        errorMsg.style.display = 'block';
    }
});