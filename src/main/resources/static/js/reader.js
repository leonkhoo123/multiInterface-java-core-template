/**
 * Manages font size adjustments and persists the setting to localStorage.
 */
class FontSizeManager {
    static STORAGE_KEY = 'novelSite-fontsize';
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
    static PROGRESS_UPDATE_DELAY = 1000;
    static MAX_SPACER_HEIGHT = 200000;
    // A more conservative hard cap on the total spacer height for maximum mobile compatibility.
    static TOTAL_SPACER_CAP = 200000; // 200k pixels

    constructor(novelId) {
        this.novelId = novelId;
        this.readerEl = document.getElementById("reader");
        this.containerEl = document.getElementById("sections");
        this.topSpacerContainerEl = document.getElementById("top-spacer-container");
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

        this.lastSavedSection = -1;
        this.progressUpdateTimer = null;

        this.fontSizeManager = new FontSizeManager(this);
        this.init();
    }

    async init() {
        try {
            this._clearOldNovelCache();

            this.backButtonEl.addEventListener('click', () => {
                window.location.href = '/web/index.html?stay=true';
            });
            this.readerEl.addEventListener("scroll", this.onScroll.bind(this));
            document.addEventListener('visibilitychange', this.handleVisibilityChange.bind(this));

            // Auto-retry loading when internet connection is restored.
            window.addEventListener('online', () => {
                console.log("Network online. Retrying content load.");
                this.handleScrollStop();
            });

            const progressRes = await apiClient.get(`private/novel/getUserNovelProgress/${this.novelId}`);
            const { totalSeq, readUntil, novelName } = progressRes.data.data;

            this.novelNameEl.textContent = novelName;
            this.totalSections = totalSeq;
            this.lastSavedSection = readUntil;

            const initialStart = Math.max(0, readUntil - NovelReader.KEEP_BEFORE);
            const initialEnd = initialStart + NovelReader.WINDOW_SIZE;

            await this.ensureSectionsInDom(initialStart, initialEnd);

            let topSpacerHeight = 0;
            for (let i = 0; i < initialStart; i++) {
                topSpacerHeight += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
            }
            this.updateTopSpacers(topSpacerHeight);

            let scrollOffsetWithinContent = 0;
            for (let i = initialStart; i < readUntil; i++) {
                scrollOffsetWithinContent += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
            }
            this.readerEl.scrollTop = Math.min(topSpacerHeight, NovelReader.TOTAL_SPACER_CAP) + scrollOffsetWithinContent;

            setTimeout(() => {
                const anchorEl = this.sectionMap.get(readUntil);
                if (!anchorEl) return;

                const readerRect = this.readerEl.getBoundingClientRect();
                const anchorRect = anchorEl.getBoundingClientRect();
                const targetTop = readerRect.top + 40;

                const scrollCorrection = anchorRect.top - targetTop;

                if (Math.abs(scrollCorrection) > 1) {
                    this.isProgrammaticScroll = true;
                    this.readerEl.scrollTop += scrollCorrection;
                    requestAnimationFrame(() => {
                        this.isProgrammaticScroll = false;
                    });
                }
            }, 50);

            this.updateProgressBar(readUntil);
            this.preloadSections(initialEnd, initialEnd + NovelReader.PRELOAD_AHEAD);

        } catch (error) {
            console.error("Failed to initialize novel reader:", error);
        }
    }

    // ===== DATA & API =====

    _clearOldNovelCache() {
        const currentNovelPrefix = `novel-${this.novelId}-`;
        const keysToRemove = [];

        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key.startsWith('novel-') && !key.startsWith(currentNovelPrefix)) {
                keysToRemove.push(key);
            }
        }

        for (const key of keysToRemove) {
            localStorage.removeItem(key);
        }
    }

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
            // Return null on failure so no error section is created.
            return null;
        }
    }

    updateUserProgress(seqNo) {
        if (seqNo === this.lastSavedSection) return;

        clearTimeout(this.progressUpdateTimer);
        this.progressUpdateTimer = setTimeout(async () => {
            try {
                await apiClient.post("/private/novel/updateUserNovelProgress", {
                    novelId: Number(this.novelId),
                    seqNo: seqNo
                });
                this.lastSavedSection = seqNo;
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

    updateTopSpacers(totalHeight) {
        this.topSpacerContainerEl.innerHTML = '';
        const cappedHeight = Math.min(totalHeight, NovelReader.TOTAL_SPACER_CAP);
        if (cappedHeight <= 0) return;

        const numSpacers = Math.ceil(cappedHeight / NovelReader.MAX_SPACER_HEIGHT);
        const heightPerSpacer = cappedHeight / numSpacers;

        for (let i = 0; i < numSpacers; i++) {
            const spacer = document.createElement('div');
            spacer.style.height = `${heightPerSpacer}px`;
            this.topSpacerContainerEl.appendChild(spacer);
        }
    }

    preloadSections(start, end) {
        start = Math.max(0, start);
        end = Math.min(this.totalSections, end);

        for (let i = start; i < end; i++) {
            this.fetchSection(i);
        }
    }

    async ensureSectionsInDom(start, end) {
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
            // Skip if fetch failed (null)
            if (!html) continue;

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

        this.updateUserProgress(current);
        this.updateProgressBar(current);

        const anchorEl = this.sectionMap.get(current);
        const oldRectTop = anchorEl ? anchorEl.getBoundingClientRect().top : 0;

        const newStart = Math.max(0, current - NovelReader.KEEP_BEFORE);
        const newEnd = newStart + NovelReader.WINDOW_SIZE;

        this.preloadSections(newEnd, newEnd + NovelReader.PRELOAD_AHEAD);
        await this.ensureSectionsInDom(newStart, newEnd);

        for (const [idx, el] of this.sectionMap) {
            if (idx < newStart || idx >= newEnd) {
                this.sectionHeights.set(idx, el.offsetHeight);
                el.remove();
                this.sectionMap.delete(idx);

                const key = `novel-${this.novelId}-${idx}`;
                localStorage.removeItem(key);
            }
        }

        let newTopSpacerHeight = 0;
        for (let i = 0; i < newStart; i++) {
            newTopSpacerHeight += this.sectionHeights.get(i) || NovelReader.ESTIMATED_SECTION_HEIGHT;
        }

        this.updateTopSpacers(newTopSpacerHeight);

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
        this.updateTopSpacers(newTopSpacerHeight);

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
        const detectionLine = readerRect.top + (readerRect.height * 0.3); // 30% from the top

        let closestIndex = -1;
        let smallestDistance = Infinity;

        if (this.sectionMap.size > 0) {
            for (const [index, element] of this.sectionMap.entries()) {
                const elementRect = element.getBoundingClientRect();
                const elementCenter = elementRect.top + elementRect.height / 2;
                const distance = Math.abs(elementCenter - detectionLine);

                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    closestIndex = index;
                }
            }
            return closestIndex;
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

    async handleVisibilityChange() {
        if (document.visibilityState === 'visible') {
            try {
                const progressRes = await apiClient.get(`private/novel/getUserNovelProgress/${this.novelId}`);
                const { readUntil: remoteReadUntil } = progressRes.data.data;

                const currentIndex = this.detectCurrentIndex();

                if (remoteReadUntil > currentIndex + 1) {
                    window.location.reload();
                }
            } catch (error) {
                console.error("Failed to check for remote progress:", error);
            }
        }
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
