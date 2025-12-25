
const API_BASE = '/api/analyze'; // vì backend map @RequestMapping("/api/analyze")

// Lấy / lưu sessionToken trong localStorage
function getSessionToken() {
    return localStorage.getItem('sessionToken');
}

function setSessionToken(token) {
    if (token) {
        localStorage.setItem('sessionToken', token);
    }
}

// Tạo headers, tự động thêm X-Session-Token
function buildHeaders(isJson = true) {
    const headers = {};
    const token = getSessionToken();

    if (isJson) {
        headers['Content-Type'] = 'application/json';
    }
    if (token) {
        headers['X-Session-Token'] = token;
    }
    return headers;
}

// Sau khi call API, nếu response có sessionToken thì lưu lại
function handleSessionFromResponse(data) {
    if (data && data.sessionToken) {
        setSessionToken(data.sessionToken);
    }
}

// ======================
// 1) Gọi API phân tích TEXT
// ======================
async function analyzeTextApi(title, content) {
    const body = {
        title: title || null,
        content: content
    };

    const res = await fetch(`${API_BASE}/text`, {
        method: 'POST',
        headers: buildHeaders(true),
        body: JSON.stringify(body)
    });

    if (!res.ok) {
        throw new Error('Lỗi khi gọi API /text');
    }

    const data = await res.json();
    handleSessionFromResponse(data);
    return data;
}

// ======================
// 2) Gọi API phân tích URL
// ======================
async function analyzeUrlApi(url, title) {
    const body = {
        url: url,
        title: title || null
    };

    const res = await fetch(`${API_BASE}/url`, {
        method: 'POST',
        headers: buildHeaders(true),
        body: JSON.stringify(body)
    });

    if (!res.ok) {
        throw new Error('Lỗi khi gọi API /url');
    }

    const data = await res.json();
    handleSessionFromResponse(data);
    return data;
}

// ======================
// 3) Gọi API phân tích FILE
// ======================
async function analyzeFileApi(file) {
    const formData = new FormData();
    formData.append('file', file);

    const headers = buildHeaders(false); // không set Content-Type, để browser tự set
    const res = await fetch(`${API_BASE}/file`, {
        method: 'POST',
        headers,
        body: formData
    });

    if (!res.ok) {
        throw new Error('Lỗi khi gọi API /file');
    }

    const data = await res.json();
    handleSessionFromResponse(data);
    return data;
}

// ======================
// 4) Gọi API lịch sử
// ======================
async function getHistoryApi() {
    const headers = buildHeaders(false);

    const res = await fetch(`${API_BASE}/history`, {
        method: 'GET',
        headers
    });

    if (!res.ok) {
        throw new Error('Lỗi khi gọi API /history');
    }

    const data = await res.json(); // mảng HistoryItemResponse
    return data;
}
