// Function to set the access token
function setAccessToken(token) {
    if (token) {
        localStorage.setItem('novelsite', token);
    } else {
        localStorage.removeItem('novelsite');
    }
}

// Function to get the access token
function getAccessToken() {
    return localStorage.getItem('novelsite');
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

// Variables to handle concurrent refresh requests
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// Response interceptor to handle token expiration (401)
apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        // Prevent infinite loops for login or refresh endpoints
        if (originalRequest.url.includes('/auth/login') || originalRequest.url.includes('/auth/refresh')) {
            return Promise.reject(error);
        }

        // If error is 401 and we haven't retried yet
        if (error.response && error.response.status === 401 && !originalRequest._retry) {

            if (isRefreshing) {
                return new Promise(function(resolve, reject) {
                    failedQueue.push({resolve, reject});
                }).then(token => {
                    originalRequest._retry = true;
                    originalRequest.headers['Authorization'] = `Bearer ${token}`;
                    return apiClient(originalRequest);
                }).catch(err => {
                    return Promise.reject(err);
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                console.log("Access token expired or missing. Attempting to refresh...");

                // Call refresh endpoint. Cookies are sent automatically due to withCredentials: true
                const response = await apiClient.post('/auth/refresh');

                // Extract new token from response
                const newAccessToken = response.data.data?.accessToken || response.data.accessToken;

                if (newAccessToken) {
                    setAccessToken(newAccessToken);

                    // Update the header for the original request
                    originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                    console.log("Successfully get a new access token.");

                    // Process any queued requests
                    processQueue(null, newAccessToken);

                    // Retry the original request
                    return apiClient(originalRequest);
                } else {
                    throw new Error("No access token returned from refresh");
                }
            } catch (refreshError) {
                console.error("Refresh token failed", refreshError);

                // Fail all queued requests
                processQueue(refreshError, null);

                // Clear token on failure
                setAccessToken(null);
                // Redirect to login page if not already there
                if (!window.location.pathname.endsWith('/web/login.html')) {
                    window.location.href = '/web/login.html';
                }
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
);
