/**
 * Manages font size adjustments and persists the setting to localStorage.
 */
class FontSizeManager {
    static STORAGE_KEY = 'novel-font-size';
    static STEP = 0.1;
    static MIN_SIZE = 0.5;
    static MAX_SIZE = 2.0;

    constructor(readerInstance) {
        this.readerInstance = readerInstance;
        this.readerEl = document.getElementById("reader");
        this.plusBtn = document.getElementById("font-plus");
        this.minusBtn = document.getElementById("font-minus");

        this.init();
    }

    init() {
        const savedSize = localStorage.getItem(FontSizeManager.STORAGE_KEY) || '1.0';
        this.applyFontSize(parseFloat(savedSize));

        this.plusBtn.addEventListener('click', () => this.adjust(FontSizeManager.STEP));
        this.minusBtn.addEventListener('click', () => this.adjust(-FontSizeManager.STEP));
    }

    adjust(amount) {
        const currentSize = parseFloat(this.readerEl.style.fontSize || '1.0');
        let newSize = currentSize + amount;

        newSize = Math.max(FontSizeManager.MIN_SIZE, Math.min(FontSizeManager.MAX_SIZE, newSize));

        this.applyFontSize(newSize);
        localStorage.setItem(FontSizeManager.STORAGE_KEY, newSize.toFixed(2));

        this.readerInstance.handleResize();
    }

    applyFontSize(size) {
        this.readerEl.style.fontSize = `${size}rem`;
    }
}


/**
 * Manages the infinite scrolling for the novel reader page.
 */
class NovelReader {
    // ===== CONFIGURATION =====
    static WINDOW_SIZE = 20;
    static KEEP_BEFORE = 8;
    static PRELOAD_AHEAD = 10;
    static ESTIMATED_SECTION_HEIGHT = 800;
    static SCROLL_STOP_DELAY = 150;
    // Delay before sending a progress update to the backend.
    static PROGRESS_UPDATE_DELAY = 1000;

    constructor(novelId) {
        this.novelId = novelId;
        this.readerEl = document.getElementById("reader");
        this.containerEl = document.getElementById("sections");
        this.topSpacerEl = document.getElementById("top-spacer");
        this.novelNameEl = document.getElementById("novel-name");
        this.backButtonEl = document.getElementById("back-button");
        this.progressFillEl = document.getElementById("progress-fill");
        this.progressTextEl = document.getElementById("progress-text");

        this.totalSections = 0;
        this.sectionMap = new Map();
        this.sectionHeights = new Map();

        this.isHandlingScrollStop = false;
        this.isProgrammaticScroll = false;
        this.scrollStopTimer = null;

        // --- Progress Tracking ---
        this.lastSavedSection = -1;
        this.progressUpdateTimer = null;

        this.fontSizeManager = new FontSizeManager(this);
        this.init();
    }

    async init() {
        try {
            // --- Event Listeners ---
            this.backButtonEl.addEventListener('click', () => {
                window.location.href = '/web/index.html?stay=true';
            });
            this.readerEl.addEventListener("scroll", this.onScroll.bind(this));


            const progressRes = await apiClient.get(`private/novel/getUserNovelProgress/${this.novelId}`);
            const { totalSeq, readUntil, novelName } = progressRes.data.data;

            // --- Set Novel Name ---
            this.novelNameEl.textContent = novelName;

            this.totalSections = totalSeq;
            this.lastSavedSection = readUntil; // Initialize with the last saved progress.

            const initialStart = Math.max(0, readUntil - NovelReader.KEEP_BEFORE);
            const initialEnd = initialStart + NovelReader.WINDOW_SIZE;

            await this.ensureSections(initialStart, initialEnd);

            let topSpacerHeight = 0;
            for (let i = 0; i < initialStart; i++) {
                topSpacerHeight += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
            }
            this.topSpacerEl.style.height = `${topSpacerHeight+40}px`; //add height offset for topbar for ai: do not remove my 40px hack

            let initialScrollTop = topSpacerHeight;
            for (let i = initialStart; i < readUntil; i++) {
                initialScrollTop += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
            }
            this.readerEl.scrollTop = initialScrollTop;

            // Set initial progress bar state
            this.updateProgressBar(readUntil);

            setTimeout(() => this.handleScrollStop(), 50);

        } catch (error) {
            console.error("Failed to initialize novel reader:", error);
        }
    }

    // ===== DATA & API =====

    async fetchSection(index) {
        const key = `novel-${this.novelId}-${index}`;
        const cached = localStorage.getItem(key);
        if (cached) return cached;

        try {
            const res = await apiClient.post("private/novel/novelContent", {
                novelId: Number(this.novelId),
                nextSeqId: index
            });
            const html = `<section data-index="${index}">${res.data.data.content.replace(/\n/g, "<br>")}</section>`;
            localStorage.setItem(key, html);
            return html;
        } catch (error) {
            console.error(`Failed to fetch section ${index}:`, error);
            return `<section data-index="${index}" class="error">Failed to load content.</section>`;
        }
    }

    /**
     * Sends the user's current reading progress to the backend.
     * @param {number} seqNo - The section number the user is currently reading.
     */
    updateUserProgress(seqNo) {
        // Don't send updates if the section hasn't changed.
        if (seqNo === this.lastSavedSection) {
            return;
        }

        // Debounce the API call to avoid spamming the backend.
        clearTimeout(this.progressUpdateTimer);
        this.progressUpdateTimer = setTimeout(async () => {
            try {
                await apiClient.post("/private/novel/updateUserNovelProgress", {
                    novelId: Number(this.novelId),
                    seqNo: seqNo
                });
                this.lastSavedSection = seqNo; // Update the last saved section only on success.
                console.log(`Progress saved for section: ${seqNo}`);
            } catch (error) {
                console.error("Failed to update user progress:", error);
            }
        }, NovelReader.PROGRESS_UPDATE_DELAY);
    }


    // ===== DOM & SCROLL LOGIC =====

    updateProgressBar(currentSection) {
        if (this.totalSections > 0) {
            const current = currentSection + 1;
            const total = this.totalSections;
            const percentage = (current / total) * 100;

            this.progressTextEl.textContent = `${percentage.toFixed(2)}% (${current}/${total})`;
            this.progressFillEl.style.width = `${percentage}%`;
        }
    }

    async ensureSections(start, end) {
        start = Math.max(0, start);
        end = Math.min(this.totalSections, end);

        const promises = [];
        for (let i = start; i < end; i++) {
            if (!this.sectionMap.has(i)) {
                promises.push(this.fetchSection(i));
            }
        }
        if (promises.length === 0) return;

        const htmls = await Promise.all(promises);

        for (const html of htmls) {
            const temp = document.createElement("div");
            temp.innerHTML = html;
            const sectionEl = temp.firstElementChild;
            const index = Number(sectionEl.dataset.index);

            this.insertSectionInOrder(sectionEl);
            this.sectionMap.set(index, sectionEl);
            this.sectionHeights.set(index, sectionEl.offsetHeight);
        }
    }

    insertSectionInOrder(sectionEl) {
        const indexToInsert = Number(sectionEl.dataset.index);
        for (const child of this.containerEl.children) {
            if (Number(child.dataset.index) > indexToInsert) {
                this.containerEl.insertBefore(sectionEl, child);
                return;
            }
        }
        this.containerEl.appendChild(sectionEl);
    }

    async handleScrollStop() {
        if (this.isHandlingScrollStop) return;
        this.isHandlingScrollStop = true;

        const current = this.detectCurrentIndex();
        if (current === -1) {
            this.isHandlingScrollStop = false;
            return;
        }

        // --- Update user progress ---
        this.updateUserProgress(current);
        this.updateProgressBar(current);

        const anchorEl = this.sectionMap.get(current);
        const oldRectTop = anchorEl ? anchorEl.getBoundingClientRect().top : 0;

        const newStart = Math.max(0, current - NovelReader.KEEP_BEFORE);
        const newEnd = newStart + NovelReader.WINDOW_SIZE;

        await this.ensureSections(newStart, newEnd);
        await this.ensureSections(newEnd, newEnd + NovelReader.PRELOAD_AHEAD);

        for (const [idx, el] of this.sectionMap) {
            if (idx < newStart || idx >= newEnd) {
                this.sectionHeights.set(idx, el.offsetHeight);
                el.remove();
                this.sectionMap.delete(idx);
            }
        }

        let newTopSpacerHeight = 0;
        for (let i = 0; i < newStart; i++) {
            newTopSpacerHeight += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
        }

        this.topSpacerEl.style.height = `${newTopSpacerHeight}px`;

        if (anchorEl) {
            const newRectTop = anchorEl.getBoundingClientRect().top;
            const scrollCorrection = newRectTop - oldRectTop;

            if (Math.abs(scrollCorrection) > 1) {
                this.isProgrammaticScroll = true;
                this.readerEl.scrollTop += scrollCorrection;

                requestAnimationFrame(() => {
                    this.isProgrammaticScroll = false;
                });
            }
        }

        this.isHandlingScrollStop = false;
    }

    handleResize() {
        const anchorIndex = this.detectCurrentIndex();
        if (anchorIndex === -1) return;
        const anchorEl = this.sectionMap.get(anchorIndex);
        const oldRectTop = anchorEl ? anchorEl.getBoundingClientRect().top : 0;

        this.sectionHeights.clear();

        for (const [index, element] of this.sectionMap.entries()) {
            this.sectionHeights.set(index, element.offsetHeight);
        }

        const currentWindowStart = Math.min(...this.sectionMap.keys());
        let newTopSpacerHeight = 0;
        for (let i = 0; i < currentWindowStart; i++) {
            newTopSpacerHeight += NovelReader.ESTIMATED_SECTION_HEIGHT;
        }
        this.topSpacerEl.style.height = `${newTopSpacerHeight}px`;

        if (anchorEl) {
            const newRectTop = anchorEl.getBoundingClientRect().top;
            const scrollCorrection = newRectTop - oldRectTop;
            if (Math.abs(scrollCorrection) > 1) {
                this.isProgrammaticScroll = true;
                this.readerEl.scrollTop += scrollCorrection;
                requestAnimationFrame(() => {
                    this.isProgrammaticScroll = false;
                });
            }
        }
    }

    detectCurrentIndex() {
        const readerRect = this.readerEl.getBoundingClientRect();
        const viewportCenter = readerRect.top + readerRect.height / 2;

        let closestIndex = -1;
        let smallestDistance = Infinity;

        if (this.sectionMap.size > 0) {
            for (const [index, element] of this.sectionMap.entries()) {
                const elementRect = element.getBoundingClientRect();
                const elementCenter = elementRect.top + elementRect.height / 2;
                const distance = Math.abs(elementCenter - viewportCenter);

                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    closestIndex = index;
                }
            }
            return closestIndex;
        }

        const scrollTop = this.readerEl.scrollTop;
        let accumulatedHeight = 0;
        for (let i = 0; i < this.totalSections; i++) {
            accumulatedHeight += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
            if (accumulatedHeight >= scrollTop) {
                return i;
            }
        }
        return -1;
    }

    // ===== EVENT HANDLERS =====

    onScroll() {
        if (this.isProgrammaticScroll) return;

        clearTimeout(this.scrollStopTimer);
        this.scrollStopTimer = setTimeout(() => {
            this.handleScrollStop();
        }, NovelReader.SCROLL_STOP_DELAY);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);
    const novelId = urlParams.get("novelId");
    if (novelId) {
        new NovelReader(novelId);
    } else {
        console.error("Novel ID is missing from URL.");
    }
});
