// Function to fetch and display novels
async function loadNovels() {
    const listContainer = document.getElementById('novel-list');

    try {
        const response = await apiClient.get('auth/novel/getNovelList');
        const responseBody = response.data;

        // Updated structure based on user input
        // { success: true, message: "", data: { novelInfoList: [...] } }
        const novels = responseBody.data && responseBody.data.novelInfoList ? responseBody.data.novelInfoList : [];

        if (novels.length === 0) {
            listContainer.innerHTML = '<div class="loading">No novels found.</div>';
            return;
        }

        listContainer.innerHTML = novels.map(novel => {
            const percentage = novel.seqCount > 0 ? ((novel.readUntil / novel.seqCount) * 100).toFixed(1) : '0.0';
            const lastReadDate = novel.lastRead ? new Date(novel.lastRead).toLocaleString() : null;

            return `
            <div class="novel-card" onclick="openReader(${novel.novelId}, ${novel.readUntil})">
                <div class="novel-title">${escapeHtml(novel.novelName || 'Untitled')}</div>
                <div class="novel-info">
                   <span>Length: ${novel.characterCount}</span> |
                    <span>Progress: ${percentage}%</span>
                    <br><span style="font-size: 0.9em; color: #555;">
                        ${lastReadDate ? `Last Read: ${lastReadDate}` : 'New'}
                    </span>
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
    window.location.href = `/web/reader.html?novelId=${novelId}&nextSeqId=${readUntil}`;
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
