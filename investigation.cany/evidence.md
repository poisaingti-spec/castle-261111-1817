<!-- approved-plan -->
# Plan shown to the user: follow it

## What the target is
Excalidraw is a client-side React whiteboard. It renders SVG/Canvas locally. Data persists in browser localStorage/IndexedDB. Network usage is minimal (analytics, assets).

## Screens to build
1. `MainActivity`: Hosts the `CastleCanvas`.
2. `CanvasScreen`: Full-screen drawing area with overlay toolbars.
3. `LibraryBottomSheet`: Asset selection.
4. `ExportDialog`: Save options.

## Features, screen by screen
**CanvasScreen:**
- TopBar: Logo "castle", Export, Help.
- BottomBar: Tool selector (Hand, Selection, Rectangle, Diamond, Ellipse, Arrow, Line, Draw, Text, Sticky, Eraser).
- Floating Actions: Zoom In/Out, Reset, Undo, Redo.
- Gesture: Pan (Hand), Pinch (Zoom), Draw (Tools).
**Library:** Load/Save JSON scenes.

## Data and network (use the REAL base URLs, endpoints and headers from the evidence — do not invent them; wire the new app to the same servers)
**Local:** Use Room or DataStore to replicate `excalidraw-state` and `excalidraw-library-db` functionality.
**Network:**
- Assets: Load from `https://excalidraw.com/assets/` (Assumption: direct linking allowed, otherwise bundle locally).
- Analytics: `https://sentry.io/api/5179260/envelope/`, `https://scripts.simpleanalyticscdn.com/`, `https://queue.simpleanalyticscdn.com/`.
- Collaboration: Links to `https://plus.excalidraw.com/`.
- CDN: `https://excalidraw.nyc3.cdn.digitaloceanspaces.com/`.
**Warning:** `excalidraw.com` may block non-browser User-Agents. No API endpoints for scene sync were found in evidence; collaboration likely uses WebSockets not captured or server-side logic at `plus.excalidraw.com`. Native app may face CORS or WAF rejection without browser headers.

## Look and feel
**Theme:** Material 3 Dynamic Color disabled.
**Palette:** Maitis Purple. Primary `#7C3AED`, Secondary `#A78BFA`, Surface `#13111C` (Dark).
**Components:** TopAppBar with text "castle". FloatingActionButtons for tools. Canvas background white/transparent.

## Files I will write
- `CastleApplication.kt`: DI setup.
- `MainActivity.kt`: Entry point.
- `ui/theme/Color.kt`: Maitis Purple definitions.
- `ui/canvas/CanvasSurface.kt`: Custom Compose Canvas drawing logic.
- `data/Repository.kt`: Local scene persistence.

## What cannot be reproduced, and assumptions
**Assumption:** Drawing logic (geometry, collision) must be re-implemented in Kotlin; JS bundle cannot be reused directly.
**Limitation:** Real-time collaboration requires backend access not visible in evidence (likely WebSocket on `plus.excalidraw.com`).
**Assumption:** Asset loading from CDN will work without CORS issues.
**Risk:** Server may reject requests lacking specific browser fingerprints or cookies.

## Build order
1. Project setup (Package `com.crafted.castle`).
2. Implement Maitis Purple Theme.
3. Build Custom Canvas Composable (touch handling).
4. Implement Shape Logic (Rect, Line, etc.).
5. Add Toolbars & Gestures.
6. Integrate Local Storage (Save/Load).
7. Add Analytics hooks.

---
<!-- approved-plan -->-end
<!-- rea-evidence -->
The section below is REAL evidence collected by REA. Where it disagrees with anything later in this document, the REA evidence is correct.

# REA evidence for https://excalidraw.com
Produced by REA 6.8.0 (github.com/morluto/rea) attached to a real Chrome that loaded the page and ran its JavaScript.

## Page
- title: Excalidraw Whiteboard
- final URL: https://excalidraw.com/

## What is on screen (REA accessibility tree: 199 nodes, 30 distinct controls shown)
- banner
- heading: Excalidraw
- button: Open Ctrl+O
- button: Help ?
- button: Live collaboration...
- link: Sign up
- link: Try Excalidraw+ 14-day free trial
- button: Library
- link: Blog post on end-to-end encryption in Excalidraw
- heading: Canvas actions
- button: Zoom out
- button: Reset zoom
- button: Zoom in
- button: Undo
- button: Redo
- button: Help
- heading: Shapes
- button: Keep selected tool active after drawing
- button: Hand (panning tool)
- button: Selection
- button: Rectangle
- button: Diamond
- button: Ellipse
- button: Arrow
- button: Line
- button: Draw
- button: Text
- button: Sticky note
- button: Eraser
- button: More tools

## Page structure (REA DOM: 386 nodes)
- elements: div×84, path×70, svg×33, button×26, meta×23, g×20, span×15, link×14, line×13, script×7, circle×7, a×3, style×2, br×2, section×2, kbd×2, h2×2, rect×2, canvas×2, html×1, head×1, title×1

## Scripts and resources (REA: 9 scripts)
- script https://excalidraw.com/assets/index-COQVqmE8.js (1913091 chars, has source map)
- script https://excalidraw.com/assets/workbox-window.prod.es5-B9K5rw8f.js (5781 chars, has source map)
- script https://excalidraw.com/assets/roundRect-0PYZxl1G.js (3097 chars, has source map)
- script https://excalidraw.com/assets/bundler-helpers-DA6W-a70.js (1057 chars, has source map)
- script https://excalidraw.com/ (908 chars)
- script https://excalidraw.com/ (703 chars)
- script https://excalidraw.com/ (597 chars)
- script https://excalidraw.com/ (225 chars)
- script https://excalidraw.com/ (105 chars)
- other resources: Script×4, Stylesheet×1, Manifest×1
- browser storage: keys i18nextLng, version-files, version-dataState, excalidraw-state, excalidraw, excalidraw-theme; IndexedDB excalidraw-library-db, excalidraw-ttd-chats-db, files-db, workbox-expiration

## JavaScript bundle (REA static analysis: 9 of 9 scripts parsed)
- No routes or endpoints could be read from the code (minified bundles often hide them).

## Screenshot
- saved as screenshot.png (1280×757)

## Look and feel (read from the rendered page by a browser script, not by REA)
- body: background rgba(0, 0, 0, 0), text rgb(0, 0, 0), font "Times New Roman", size 16px
- background colours by use: rgb(236, 236, 244) x5, rgb(255, 255, 255) x2, rgb(224, 223, 255) x1, rgb(240, 239, 255) x1, rgb(105, 101, 219) x1
- text colours by use: rgb(184, 184, 184) x13, rgb(153, 153, 153) x4, rgb(27, 27, 31) x3, rgb(3, 0, 100) x1, rgb(105, 101, 219) x1, rgb(139, 136, 224) x1
- fonts by use: Assistant x16, Arial x5, monospace x2; sizes: 10px x12, 14px x6, 12px x4, 16px x1
- framework signs: React (likely)
- layout regions (size at position, 1280px wide window): header 1280x0 at 0,0; svg 40x40 at 499,206; svg 229x28 at 551,212; img 16x16 at 503,388; img 16x16 at 503,432; img 16x16 at 503,476; img 16x16 at 503,521; img 16x16 at 26,26; img 0x0 at 0,0; section 608x44 at 336,16; img 0x0 at 0,0; img 16x16 at 383,30; img 16x16 at 432,30; img 16x16 at 472,30; img 16x16 at 512,30; img 16x16 at 552,30; img 16x16 at 592,30; img 16x16 at 632,30; img 16x16 at 672,30; img 16x16 at 712,30; img 16x16 at 752,30; img 16x16 at 792,30; img 16x16 at 832,30; img 16x16 at 881,30; img 20x20 at 1036,24; img 14x36 at 1191,16; img 16x16 at 1238,26; contentinfo 1280x36 at 0,705; section 214x36 at 16,705; img 16x16 at 26,715; img 16x16 at 122,715; img 16x16 at 168,715; img 16x16 at 204,715; img 19x19 at 1190,713; img 0x0 at 0,0; img 16x16 at 1238,715; svg 1280x757 at 0,0; canvas 1280x757 at 0,0; canvas 1280x757 at 0,0
- headings: Excalidraw | Shapes | Canvas actions
- buttons: Open Ctrl+O | Help ? | Live collaboration... | Keep selected tool active after drawing | Hand (panning tool) | Selection | Rectangle | Diamond | Ellipse | Arrow | Line | Draw | Text | Sticky note | Eraser | More tools | Library | Zoom out | Reset zoom | Zoom in | Undo | Redo | Help
- links: Sign up -> https://plus.excalidraw.com/plus?utm_source=excalidraw&utm_medium=app&utm_content=welcomeScreenGuest | Try Excalidraw+ 14-day free trial -> https://plus.excalidraw.com/plus?utm_source=excalidraw&utm_medium=app&utm_content=guestBanner#excalidraw-redirect | Blog post on end-to-end encryption in Excalidraw -> https://plus.excalidraw.com/blog/end-to-end-encryption
- localStorage keys: i18nextLng, version-files, version-dataState, excalidraw-state, excalidraw, excalidraw-theme

### Visible text (first part)
Excalidraw
Your drawings are saved in your browser's storage.
Browser storage can be cleared unexpectedly.
Save your work to a file regularly to avoid losing it.
Open
Ctrl+O
Help
?
Live collaboration...
Sign up
To move canvas, hold Scroll wheel or Space while dragging, or use the hand tool
Shapes
V
R
D
O
A
L
P
T
N
E
Try Excalidraw+
14-day free trial
Canvas actions
100%

## Requests while the page loaded (21 total, 1 data calls; browser capture)
- POST https://sentry.io/api/5179260/envelope/?… -> 200 application/json
- hosts contacted: excalidraw.com×9, excalidraw.nyc3.cdn.digitaloceanspaces.com×7, data:image×2, scripts.simpleanalyticscdn.com×1, queue.simpleanalyticscdn.com×1, sentry.io×1

## Limits of this evidence
- Only what a signed-out visitor sees was observed; pages behind a login, and logic that runs on the site's servers, are not in this evidence.
- Artifact identity is unavailable for this observation.
- All page targets with supported HTTP(S) URLs are listed.
- CDP discovery omitted attachment state for one or more targets; attached is reported as unknown.
- Browser host platform and architecture were not observed.

(Collected in 53 s.)


---

# 🏛️ Craft Anything Web-to-Android Architect Study (`investigation.cany`)
- **Source Web App**: `https://excalidraw.com` (Excalidraw Whiteboard)
- **Target Platform**: **100% Native Android App (Kotlin + Jetpack Compose + Material 3)**
- **Inspection Mode**: `Clean room (Default)` (284 layout & feature inspection checks executed on phone)
- **New Crafted Android App Name**: `castle` (TopAppBar Logo: "castle")
- **Material 3 Branding & Color Palette**: Custom Android Material 3 Brand Palette: 💜 Maitis Purple (Primary `#7C3AED`, Secondary `#A78BFA`, Dark Surface `#13111C`)
- **Android Form Factors Targeted**: Compact Phone (360×800 dp) · Large Phone / Foldable (600×960 dp) · Android Tablet (840×1200 dp)

