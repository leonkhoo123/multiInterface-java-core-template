document.addEventListener('DOMContentLoaded', () => {
    checkSession();

    const loginForm = document.getElementById('login-form');
    const errorMsg = document.getElementById('error-message');

    async function checkSession() {
        try {
            const response = await apiClient.get('/private/test', {});
            const data = response.data;
            if (data.success && data.message === 'SESSION_ALIVE') {
                window.location.href = '/web/index.html';
            }
        } catch (error) {
            // User is not logged in or token expired, stay on login page
        }
    }

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Clear previous errors
        errorMsg.style.display = 'none';
        errorMsg.textContent = '';

        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');

        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        // Validate input
        if (!username) {
            showError('Please enter your username');
            usernameInput.focus();
            return;
        }

        if (!password) {
            showError('Please enter your password');
            passwordInput.focus();
            return;
        }

        try {
            const response = await apiClient.post('/auth/login', {
                username: username,
                password: password,
                deviceId: crypto.randomUUID()
            });

            const data = response.data;

            if (data.success) {
                // Login successful
                // Save the token if provided (assuming standard structure)
                const token = data.data?.accessToken || data.accessToken;
                if (token) {
                    setAccessToken(token);
                }

                console.log('Login successful, redirecting...');
                window.location.href = '/web/index.html';
            } else {
                // Handle success: false (e.g. 200 OK but logic error)
                showError(data.message || 'Login failed');
                // Clear password field on failure
                passwordInput.value = '';
            }

        } catch (error) {
            console.error('Login error:', error);
            let message = 'An error occurred during login.';

            if (error.response && error.response.data) {
                // Handle error response from server (e.g. 401, 400)
                const errData = error.response.data;
                if (errData.message) {
                    message = errData.message;
                }
            } else if (error.message) {
                message = error.message;
            }
            showError(message);
            // Clear password field on error
            passwordInput.value = '';
        }
    });

    function showError(message) {
        errorMsg.textContent = message;
        errorMsg.style.display = 'block';
    }
});
