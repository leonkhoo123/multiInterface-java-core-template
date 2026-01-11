const reader = document.getElementById("reader");
const container = document.getElementById("sections");

// ==== CONFIG ====
let currentStart = 0;
let currentEnd = 12;           // start with 12 visible
const keepBefore = 2;          // keep 2 behind
const preloadAhead = 10;       // load 10 ahead
const totalSections = 200;

// ==== CACHES ====
const sectionMap = new Map();
const sectionCache = new Map(); // cache HTML for efficiency
const pendingFetches = new Map(); // Track in-flight requests

// ==== URL PARAMS ====
const urlParams = new URLSearchParams(window.location.search);
const novelId = urlParams.get('novelId');

// ==== FONT SIZE ====
const fontPlusBtn = document.getElementById("font-plus");
const fontMinusBtn = document.getElementById("font-minus");
const novelNameTitle = document.getElementById("novel-name");
const backBtn = document.getElementById("back-button");
const bottomBar = document.getElementById("btm-bar");
const progressFill = document.getElementById('progress-fill');
const progressText = document.getElementById('progress-text');
const FONT_STORAGE_KEY = "novelSite-fontsize";

// ==== NOVEL INFO ===
let totalSeq = 0;

function loadFontSize() {
    const stored = localStorage.getItem(FONT_STORAGE_KEY);
    if (stored) {
        return parseFloat(stored);
    }
    return 1.0;
}

function saveFontSize(size) {
    localStorage.setItem(FONT_STORAGE_KEY, size.toFixed(1));
}

function applyFontSize(size) {
    container.style.fontSize = `${size}rem`;
}

function updateProgressUi(seq){
    const percentage = totalSeq > 0 ? ((seq / totalSeq) * 100).toFixed(2) : "0.00";
    progressText.textContent = `${percentage}% (${seq}/${totalSeq})`;
    progressFill.style.width = `${percentage}%`;
}

let currentFontSize = loadFontSize();
applyFontSize(currentFontSize);

fontPlusBtn.addEventListener("click", () => {
    currentFontSize = parseFloat((currentFontSize + 0.1).toFixed(1));
    applyFontSize(currentFontSize);
    saveFontSize(currentFontSize);
});

fontMinusBtn.addEventListener("click", () => {
    if (currentFontSize > 0.2) {
        currentFontSize = parseFloat((currentFontSize - 0.1).toFixed(1));
        applyFontSize(currentFontSize);
        saveFontSize(currentFontSize);
    }
});

backBtn.addEventListener("click",()=> {
    window.location.href = '/web/index.html?stay=true';
});

// ==== PROGRESS TRACKING ====
let lastUpdateTimestamp = 0;
let updateTimeout = null;
let lastSentSeq = -1;

function sendUpdate(seqNo) {
    updateProgressUi(seqNo);
    lastUpdateTimestamp = Date.now();
    lastSentSeq = seqNo;
    updateTimeout = null;

    apiClient.post('private/novel/updateUserNovelProgress', {
        novelId: parseInt(novelId),
        seqNo: seqNo
    }).catch(e => console.error("Progress save failed", e));
}

function triggerProgressUpdate(seqNo) {
    if (seqNo === lastSentSeq) return;

    const now = Date.now();
    const timeSinceLast = now - lastUpdateTimestamp;

    if (timeSinceLast >= 5000) {
        sendUpdate(seqNo);
    } else {
        if (updateTimeout) clearTimeout(updateTimeout);
        updateTimeout = setTimeout(() => {
            sendUpdate(seqNo);
        }, 5000 - timeSinceLast);
    }
}

// ==== API SERVER ====
async function fetchSection(index) {
  if (sectionCache.has(index)) return sectionCache.get(index);

  // if sequence = 0
  if (index === 0) {
    return `<section data-index="${index}"><div>=== Start ===</div></section>`
  }
  if (index > totalSeq) {
//    return `<section data-index="${index}"><div>=== End ===</div></section>`
    return null
  }

  // If a fetch is already in progress for this index, return that promise
  if (pendingFetches.has(index)) return pendingFetches.get(index);

  const fetchPromise = (async () => {
      try {
          const response = await apiClient.post('private/novel/novelContent', {
              novelId: parseInt(novelId),
              nextSeqId: index
          });

          const data = response.data.data;
          if (!data) throw new Error("No data received");

          const content = data.content.replace(/\n/g, "<br>");
          const html = `<section data-index="${index}">
              <div>${content}</div>
          </section>`;

          sectionCache.set(index, html);
          return html;
      } catch (error) {
          console.error(`Error fetching section ${index}:`, error);
//          return `<section data-index="${index}"><p>Error loading content.</p></section>`;
            return null
      } finally {
          pendingFetches.delete(index);
      }
  })();

  pendingFetches.set(index, fetchPromise);
  return fetchPromise;
}

// ==== TRIM ====
function trimSections(newStart, newEnd, pinIndex) {
  const pinEl = sectionMap.get(pinIndex);
  if (!pinEl) return;
  const topBefore = pinEl.getBoundingClientRect().top;

  // remove above
  for (let i = currentStart; i < newStart; i++) {
    const el = sectionMap.get(i);
    if (el) {
      el.remove();
      sectionMap.delete(i);
    }
  }
  // remove below
  for (let i = newEnd; i < currentEnd; i++) {
    const el = sectionMap.get(i);
    if (el) {
      el.remove();
      sectionMap.delete(i);
    }
  }

  const topAfter = pinEl.getBoundingClientRect().top;
  reader.scrollTop += (topAfter - topBefore);

  currentStart = newStart;
  currentEnd = newEnd;
}

// ==== LOAD RANGE ====
async function ensureSections(rangeStart, rangeEnd) {
  // Create an array of promises for the range
  const promises = [];
  for (let i = rangeStart; i < rangeEnd; i++) {
        if (sectionMap.has(i)) {
          promises.push(Promise.resolve({ index: i, html: null, exists: true }));
        } else {
          promises.push(fetchSection(i).then(html => ({ index: i, html, exists: false })));
        }
  }

  // Wait for all to resolve
  const results = await Promise.all(promises);

  // Append/Insert in correct order
  for (const result of results) {
    if (!result || result.html === null) continue;
    // Double check if it exists now (race condition check)
    if (sectionMap.has(result.index)) continue;

    const temp = document.createElement("div");
    temp.innerHTML = result.html;
    const section = temp.firstElementChild;

    // Find the correct position to insert
    // We want to insert it before the first element that has an index > result.index
    let inserted = false;
    const children = Array.from(container.children);
    for (const child of children) {
        const childIndex = parseInt(child.getAttribute('data-index'));
        if (childIndex > result.index) {
            container.insertBefore(section, child);
            inserted = true;
            break;
        }
    }
    if (!inserted) {
        container.appendChild(section);
    }

    sectionMap.set(result.index, section);
  }
}

// ==== MAIN SCROLL LOGIC ====
async function handleScrollLogic() {
  const scrollTop = reader.scrollTop;
  const scrollHeight = reader.scrollHeight;
  const scrollBottom = scrollTop + reader.clientHeight;

  // --- DETECT CURRENT READING SECTION ---
  const children = container.children;
  for (let i = 0; i < children.length; i++) {
      const section = children[i];
      const rect = section.getBoundingClientRect();
      // If the bottom of the section is below the top of the viewport (plus some offset),
      // it's the one currently occupying the top space.
      if (rect.bottom > 100) {
          const index = parseInt(section.getAttribute('data-index'));
          triggerProgressUpdate(index);
          break;
      }
  }

  // --- LOAD NEXT (scrolling down) ---
  if (scrollBottom > scrollHeight - 800) {
    await ensureSections(currentEnd, currentEnd + preloadAhead);
    currentEnd += preloadAhead;
  }

  // --- LOAD PREVIOUS (scrolling up) ---
  if (scrollTop < 800 && currentStart > 0) {
    const loadFrom = Math.max(0, currentStart - preloadAhead);
    const pinSection = sectionMap.get(currentStart);
    const topBefore = pinSection?.getBoundingClientRect().top ?? 0;
    await ensureSections(loadFrom, currentStart);
    currentStart = loadFrom;
    const topAfter = pinSection?.getBoundingClientRect().top ?? 0;
    reader.scrollTop += (topAfter - topBefore);
  }

  // --- TRIM OLD ---
  for (let [index, el] of sectionMap.entries()) {
    const rect = el.getBoundingClientRect();
    if (rect.top >= 0 && rect.top < 50) {
      const newStart = Math.max(index - keepBefore, 0);
      const newEnd = newStart + 12;
      trimSections(newStart, newEnd, index);
      break;
    }
  }

  // --- IDLE PREFETCH ---
  if ("requestIdleCallback" in window) {
    requestIdleCallback(() => {
      ensureSections(currentEnd, currentEnd + preloadAhead);
    });
  }
}

// ==== HYBRID SCROLL HANDLER ====
let ticking = false;
let lastCheck = 0;
const CHECK_INTERVAL = 150; // ms

reader.addEventListener("scroll", () => {
  if (!ticking) {
    requestAnimationFrame(() => {
      const now = performance.now();
      if (now - lastCheck > CHECK_INTERVAL) {
        handleScrollLogic();
        lastCheck = now;
      }
      ticking = false;
    });
    ticking = true;
  }
});

// ==== SYNC LOGIC ====
async function syncLatestChapter() {
    if (!novelId) return;

    try {
        const response = await apiClient.get(`private/novel/getUserNovelProgress/${novelId}`);
        if (response.data.success) {
            const serverSeq = response.data.data.readUntil;
            totalSeq = response.data.data.totalSeq;
            novelNameTitle.textContent = response.data.data.novelName;
            updateProgressUi(serverSeq);

            // Determine current visible section
            let currentVisible = -1;
            const children = container.children;
            for (let i = 0; i < children.length; i++) {
                const section = children[i];
                const rect = section.getBoundingClientRect();
                if (rect.bottom > 100) {
                    currentVisible = parseInt(section.getAttribute('data-index'));
                    break;
                }
            }

            // If we are close to the server sequence, don't reload
            if (currentVisible !== -1 && Math.abs(currentVisible - serverSeq) < 5) {
                return;
            }

            // Reset and load
            container.innerHTML = "";
            sectionMap.clear();
            // Keep sectionCache to avoid re-fetching if possible

            currentStart = serverSeq;
            currentEnd = currentStart + 12;

            await ensureSections(currentStart, currentEnd);
            reader.scrollTop = 0;
        }
    } catch (e) {
        console.error("Sync failed", e);
        // Fallback for initial load if API fails
        if (sectionMap.size === 0) {
             const fallback = parseInt(urlParams.get('nextSeqId')) || 0;
             currentStart = fallback;
             currentEnd = currentStart + 12;
             await ensureSections(currentStart, currentEnd);
        }
    }
}

window.addEventListener("pageshow", (e) => {
    if (e.persisted) {
        syncLatestChapter();
    }
});
document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") {
        syncLatestChapter();
    }
});

// ==== INITIAL LOAD ====
(async () => {
  if (!novelId) {
      container.innerHTML = "<p>Error: No novel ID provided.</p>";
      return;
  }
  await syncLatestChapter();
})();
