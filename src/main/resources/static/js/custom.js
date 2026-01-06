// Function to fetch and display novels
async function loadNovels() {
    const listContainer = document.getElementById('novel-list');

    try {
        const response = await apiClient.get('auth/novel/novelList');
        const responseBody = response.data;

        // Based on the user provided JSON structure:
        // { success: true, message: "Novel_List", data: [...] }
        const novels = responseBody.data || [];

        if (novels.length === 0) {
            listContainer.innerHTML = '<div class="loading">No novels found.</div>';
            return;
        }

        listContainer.innerHTML = novels.map(novel => `
            <div class="novel-card">
                <div class="novel-title">${escapeHtml(novel.name || 'Untitled')}</div>
                <div class="novel-info">
                    <span>Read: ${novel.readPercentage}%</span>
                    ${novel.totalLength ? ` | <span>Length: ${novel.totalLength}</span>` : ''}
                    ${novel.lastRead ? ' | <span style="color: green; font-weight: bold;">Last Read</span>' : ''}
                </div>
            </div>
        `).join('');

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
document.addEventListener('DOMContentLoaded', loadNovels);