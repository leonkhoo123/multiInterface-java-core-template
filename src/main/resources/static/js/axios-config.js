// In-memory storage for the access token
let accessToken = null;

// Function to set the access token
function setAccessToken(token) {
    accessToken = token;
}

// Function to get the access token
function getAccessToken() {
    return accessToken;
}

// Create an Axios instance with configuration for future authentication
const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    withCredentials: true, // Essential for handling httpOnly refresh tokens in cookies
    headers: {
        'Content-Type': 'application/json'
    }
});

// Request interceptor to add Bearer token
apiClient.interceptors.request.use(
    (config) => {
        const token = getAccessToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle token expiration (401)
apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        // Prevent infinite loops for login or refresh endpoints
        if (originalRequest.url.includes('/login') || originalRequest.url.includes('/refresh')) {
            return Promise.reject(error);
        }

        // If error is 401 and we haven't retried yet
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                console.log("Access token expired or missing. Attempting to refresh...");

                // Call refresh endpoint. Cookies are sent automatically due to withCredentials: true
                // We use a new axios instance or the same one? Same one is fine as long as we handle the loop check above.
                const response = await apiClient.post('/refresh');

                // Extract new token from response
                // Assuming response structure: { success: true, data: { accessToken: "..." } }
                const newAccessToken = response.data.data?.accessToken || response.data.accessToken;

                if (newAccessToken) {
                    setAccessToken(newAccessToken);

                    // Update the header for the original request
                    originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

                    // Retry the original request
                    return apiClient(originalRequest);
                } else {
                    throw new Error("No access token returned from refresh");
                }
            } catch (refreshError) {
                console.error("Refresh token failed", refreshError);
                // Redirect to login page if not already there
                if (!window.location.pathname.endsWith('/web/login.html')) {
                    window.location.href = '/web/login.html';
                }
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);