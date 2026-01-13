// Function to check if user has a last read novel and redirect
async function checkLastRead() {
    // Check if URL has stay=true parameter
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('stay') === 'true') {
        return;
    }

    try {
        const response = await apiClient.get('/private/novel/getUserLastRead');
        const result = response.data;
        if (result.success && result.data && result.data.novelId) {
            window.location.href = `/web/reader.html?novelId=${result.data.novelId}`;
        }
    } catch (error) {
        // User never read anything or other error, stay on index page
        console.log('Check last read status:', error);
    }
}

// Function to fetch and display novels
async function loadNovels() {
    const listContainer = document.getElementById('novel-list');

    try {
        const response = await apiClient.get('private/novel/getNovelList');
        const responseBody = response.data;

        // Updated structure based on user input
        // { success: true, message: "", data: { novelInfoList: [...] } }
        const novels = responseBody.data && responseBody.data.novelInfoList ? responseBody.data.novelInfoList : [];

        if (novels.length === 0) {
            listContainer.innerHTML = '<div class="loading">No novels found.</div>';
            return;
        }

        listContainer.innerHTML = novels.map(novel => {
            const rawPercent = novel.seqCount > 0 ? (novel.readUntil / novel.seqCount) * 100 : 0;
            const percentage = rawPercent.toFixed(2);
            const lastReadDate = novel.lastRead ? new Date(novel.lastRead).toLocaleDateString(undefined, {
                month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
            }) : null;

            // Formatter for large numbers (e.g., 1.2M chars)
            const charCount = new Intl.NumberFormat('en-US', { notation: 'compact' }).format(novel.characterCount);

            return `
            <div class="novel-card" onclick="openReader(${novel.novelId}, ${novel.readUntil})">
                <div class="novel-title">${escapeHtml(novel.novelName || 'Untitled')}</div>

                <div class="novel-info">
                    <span class="badge">📖 ${charCount} chars</span>
                    <span class="badge">🎯 ${percentage}%</span>
                </div>

                <div class="progress-container">
                    <div class="progress-bar" style="width: ${percentage}%"></div>
                </div>

                <div class="last-read">
                    ${lastReadDate ? `🕒 Last read: ${lastReadDate}` : '✨ <span class="new-novel">New Novel</span>'}
                </div>
            </div>
        `}).join('');

    } catch (error) {
        console.error('Error loading novels:', error);
        listContainer.innerHTML = `
            <div class="error-message">
                <h3>Error loading data</h3>
                <p>${error.message}</p>
                ${error.response ? `<p>Status: ${error.response.status}</p>` : ''}
            </div>
        `;
    }
}

// Function to redirect to reader
function openReader(novelId, readUntil) {
    window.location.href = `/web/reader.html?novelId=${novelId}`;
}

// Function to handle logout
async function logout() {
    try {
        // Call logout endpoint with empty body
        // Headers (Bearer token) are handled by axios-config interceptor
        const response = await apiClient.post('/auth/logout', {});

        if (response.data && response.data.success) {
            console.log(response.data.message); // "Logout successful"
        }
    } catch (error) {
        console.error('Logout failed:', error);
    } finally {
        // Always clear token and redirect, even if server call failed
        setAccessToken(null);
        window.location.href = '/web/login.html';
    }
}

// Utility to prevent XSS
function escapeHtml(text) {
    if (!text && text !== 0) return '';
    return String(text)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    checkLastRead();
    loadNovels();
});
