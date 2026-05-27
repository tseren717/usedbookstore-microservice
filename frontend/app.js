const API = {
    users: 'http://localhost:8083/api/users',
    books: 'http://localhost:8081/api/books',
    orders: 'http://localhost:8082/api/orders'
};

let currentUser = null;
let allBooks = [];
// Store images locally (base64) keyed by book id
let bookImages = JSON.parse(localStorage.getItem('bookImages') || '{}');

// ===== PAGE NAV =====
function showPage(page) {
    document.getElementById('page-login').classList.add('hidden');
    document.getElementById('page-register').classList.add('hidden');
    document.getElementById('page-app').classList.add('hidden');
    document.getElementById('page-' + page).classList.remove('hidden');
}

// ===== AUTH =====
async function doLogin() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value.trim();
    if (!username || !password) { showAlert('login-alert','Бүх талбарыг бөглөнө үү.','error'); return; }
    try {
        const res = await fetch(API.users + '/login', {
            method: 'POST', headers: {'Content-Type':'application/json'},
            body: JSON.stringify({username, password})
        });
        const data = await res.json();
        if (res.ok) { currentUser = data; localStorage.setItem('user', JSON.stringify(data)); enterApp(); }
        else { showAlert('login-alert', data.error || 'Нэвтрэх нэр эсвэл нууц үг буруу.', 'error'); }
    } catch(e) { showAlert('login-alert','Сервертэй холбогдож чадсангүй.','error'); }
}

async function doRegister() {
    const username = document.getElementById('reg-username').value.trim();
    const password = document.getElementById('reg-password').value.trim();
    const confirmPassword = document.getElementById('reg-confirm').value.trim();
    if (!username || !password || !confirmPassword) { showAlert('register-alert','Бүх талбарыг бөглөнө үү.','error'); return; }
    try {
        const res = await fetch(API.users + '/register', {
            method: 'POST', headers: {'Content-Type':'application/json'},
            body: JSON.stringify({username, password, confirmPassword})
        });
        const data = await res.json();
        if (res.ok) { showAlert('login-alert','Бүртгэл амжилттай! Нэвтэрнэ үү.','success'); showPage('login'); }
        else { showAlert('register-alert', data.error || 'Бүртгэл амжилтгүй.', 'error'); }
    } catch(e) { showAlert('register-alert','Сервертэй холбогдож чадсангүй.','error'); }
}

function doLogout() { currentUser = null; localStorage.removeItem('user'); showPage('login'); }

function enterApp() {
    document.getElementById('nav-user').textContent = currentUser.username + ' (' + currentUser.role + ')';
    showPage('app');
    loadBooks();
}

// ===== NAV =====
function switchNav(event, section) {
    document.querySelectorAll('.nav-link').forEach(a => a.classList.remove('active'));
    event.target.classList.add('active');
    document.getElementById('section-books').classList.add('hidden');
    document.getElementById('section-my-books').classList.add('hidden');
    document.getElementById('section-my-orders').classList.add('hidden');
    document.getElementById('section-' + section).classList.remove('hidden');
    if (section === 'books') loadBooks();
    if (section === 'my-books') loadMyBooks();
    if (section === 'my-orders') loadOrders();
}

// ===== BOOKS =====
async function loadBooks() {
    try {
        const res = await fetch(API.books);
        allBooks = await res.json();
        renderBooks(allBooks);
    } catch(e) {
        document.getElementById('books-tbody').innerHTML = '<tr><td colspan="9" style="text-align:center;color:#dc2626;padding:24px;">Book service-тэй холбогдож чадсангүй.</td></tr>';
    }
}

function renderBooks(books) {
    const tbody = document.getElementById('books-tbody');
    if (books.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;padding:24px;color:#94a3b8;">Ном олдсонгүй</td></tr>';
        return;
    }
    tbody.innerHTML = books.map(book => {
        const img = bookImages[book.id]
            ? `<img class="book-img" src="${bookImages[book.id]}">`
            : `<div class="book-img-placeholder">📖</div>`;
        const actions = [];
        if (book.sellerUsername !== currentUser.username) {
            actions.push(`<button class="btn-buy" onclick="buyBook(${book.id},${book.price},'${book.sellerUsername}')">Худалдаж авах</button>`);
        }
        actions.push(`<button class="btn-detail" onclick="viewDetail(${book.id})">Дэлгэрэнгүй</button>`);
        return `<tr>
            <td>${book.id}</td>
            <td>${img}</td>
            <td><strong>${book.title}</strong></td>
            <td>${book.author}</td>
            <td><span class="price">${Number(book.price).toLocaleString()}₮</span></td>
            <td><span class="badge badge-${book.condition || ''}">${book.condition || '-'}</span></td>
            <td>${book.category || '-'}</td>
            <td>${book.sellerUsername}</td>
            <td><div class="actions">${actions.join('')}</div></td>
        </tr>`;
    }).join('');
}

function applyFilter() {
    const text = document.getElementById('search-text').value.toLowerCase();
    const cat = document.getElementById('filter-category').value;
    const cond = document.getElementById('filter-condition').value;
    const min = parseFloat(document.getElementById('filter-min').value) || 0;
    const max = parseFloat(document.getElementById('filter-max').value) || Infinity;
    const filtered = allBooks.filter(b => {
        const mt = !text || b.title.toLowerCase().includes(text) || b.author.toLowerCase().includes(text) || (b.isbn||'').toLowerCase().includes(text);
        const mc = !cat || b.category === cat;
        const mn = !cond || b.condition === cond;
        const mp = b.price >= min && b.price <= max;
        return mt && mc && mn && mp;
    });
    renderBooks(filtered);
}

function viewDetail(id) {
    const book = allBooks.find(b => b.id === id);
    if (!book) return;

    // Remove existing modal if any
    const existing = document.getElementById('detail-modal');
    if (existing) existing.remove();

    const isOwner = book.sellerUsername === currentUser.username;
    const imgSrc = bookImages[book.id] || '';

    const modal = document.createElement('div');
    modal.id = 'detail-modal';
    modal.innerHTML = `
        <div class="modal-overlay" onclick="closeModal()"></div>
        <div class="modal-box">
            <h3>${isOwner ? 'Ном засах' : 'Номын дэлгэрэнгүй'}</h3>
            <div class="modal-body">
                ${imgSrc ? `<img src="${imgSrc}" class="modal-img">` : ''}
                <div class="modal-form">
                    <div class="mf-row"><label>Номын нэр:</label><input type="text" id="m-title" value="${book.title}" ${!isOwner?'readonly':''}></div>
                    <div class="mf-row"><label>Зохиолч:</label><input type="text" id="m-author" value="${book.author}" ${!isOwner?'readonly':''}></div>
                    <div class="mf-row"><label>ISBN:</label><input type="text" id="m-isbn" value="${book.isbn||''}" ${!isOwner?'readonly':''}></div>
                    <div class="mf-row"><label>Үнэ (₮):</label><input type="number" id="m-price" value="${book.price}" ${!isOwner?'readonly':''}></div>
                    <div class="mf-row"><label>Нөхцөл:</label>${isOwner ?
                        `<select id="m-condition"><option ${book.condition==='NEW'?'selected':''}>NEW</option><option ${book.condition==='LIKE_NEW'?'selected':''}>LIKE_NEW</option><option ${book.condition==='GOOD'?'selected':''}>GOOD</option><option ${book.condition==='FAIR'?'selected':''}>FAIR</option><option ${book.condition==='POOR'?'selected':''}>POOR</option></select>` :
                        `<input type="text" value="${book.condition||'-'}" readonly>`}</div>
                    <div class="mf-row"><label>Ангилал:</label>${isOwner ?
                        `<select id="m-category"><option ${book.category==='FICTION'?'selected':''}>FICTION</option><option ${book.category==='NON_FICTION'?'selected':''}>NON_FICTION</option><option ${book.category==='SCIENCE'?'selected':''}>SCIENCE</option><option ${book.category==='HISTORY'?'selected':''}>HISTORY</option><option ${book.category==='OTHER'?'selected':''}>OTHER</option></select>` :
                        `<input type="text" value="${book.category||'-'}" readonly>`}</div>
                    <div class="mf-row"><label>Тайлбар:</label><input type="text" id="m-desc" value="${book.description||''}" ${!isOwner?'readonly':''}></div>
                    <div class="mf-row"><label>Худалдагч:</label><input type="text" value="${book.sellerUsername}" readonly></div>
                    <div class="mf-row"><label>Төлөв:</label><input type="text" value="${book.status||'available'}" readonly></div>
                </div>
            </div>
            <div class="modal-actions">
                ${isOwner && book.status==='available' ? `<button class="btn-primary" onclick="saveEdit(${book.id})">Хадгалах</button>` : ''}
                <button class="btn-cancel" onclick="closeModal()">Хаах</button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

function closeModal() {
    const m = document.getElementById('detail-modal');
    if (m) m.remove();
}

async function saveEdit(id) {
    const updated = {
        title: document.getElementById('m-title').value.trim(),
        author: document.getElementById('m-author').value.trim(),
        isbn: document.getElementById('m-isbn').value.trim(),
        price: parseFloat(document.getElementById('m-price').value) || 0,
        condition: document.getElementById('m-condition').value,
        category: document.getElementById('m-category').value,
        description: document.getElementById('m-desc').value.trim(),
        sellerUsername: currentUser.username,
        status: 'available'
    };
    try {
        const res = await fetch(API.books + '/' + id, {
            method: 'PUT', headers: {'Content-Type':'application/json'},
            body: JSON.stringify(updated)
        });
        if (res.ok) {
            closeModal();
            loadBooks();
        } else {
            const d = await res.json();
            alert('Алдаа: ' + (d.error || ''));
        }
    } catch(e) { alert('Сервертэй холбогдож чадсангүй.'); }
}

// ===== ADD BOOK =====
async function addBook() {
    const title = document.getElementById('f-title').value.trim();
    const author = document.getElementById('f-author').value.trim();
    if (!title || !author) { alert('Номын нэр болон зохиолч заавал бөглөнө.'); return; }

    const book = {
        title: title,
        author: author,
        isbn: document.getElementById('f-isbn').value.trim(),
        price: parseFloat(document.getElementById('f-price').value) * 1000 || 0,
        description: document.getElementById('f-desc').value.trim(),
        condition: document.getElementById('f-condition').value,
        category: document.getElementById('f-category').value,
        sellerUsername: currentUser.username,
        status: 'available'
    };

    try {
        const res = await fetch(API.books, {
            method: 'POST', headers: {'Content-Type':'application/json'},
            body: JSON.stringify(book)
        });
        const data = await res.json();
        if (res.ok) {
            // Save image locally if selected
            const fileInput = document.getElementById('f-image');
            if (fileInput.files.length > 0) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    bookImages[data.id] = e.target.result;
                    localStorage.setItem('bookImages', JSON.stringify(bookImages));
                    loadBooks();
                };
                reader.readAsDataURL(fileInput.files[0]);
            } else {
                loadBooks();
            }
            // Clear form
            document.getElementById('f-title').value = '';
            document.getElementById('f-author').value = '';
            document.getElementById('f-isbn').value = '';
            document.getElementById('f-price').value = '';
            document.getElementById('f-desc').value = '';
            document.getElementById('f-image').value = '';
        } else {
            alert('Алдаа: ' + (data.error || ''));
        }
    } catch(e) { alert('Сервертэй холбогдож чадсангүй.'); }
}

// ===== MY BOOKS =====
async function loadMyBooks() {
    try {
        const res = await fetch(API.books + '?seller=' + currentUser.username);
        const books = await res.json();
        const tbody = document.getElementById('my-books-tbody');
        if (books.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;padding:24px;color:#94a3b8;">Та ном нэмээгүй байна.</td></tr>';
            return;
        }
        tbody.innerHTML = books.map(book => {
            const img = bookImages[book.id]
                ? `<img class="book-img" src="${bookImages[book.id]}">`
                : `<div class="book-img-placeholder">📖</div>`;
            return `<tr>
                <td>${book.id}</td>
                <td>${img}</td>
                <td><strong>${book.title}</strong></td>
                <td>${book.author}</td>
                <td><span class="price">${Number(book.price).toLocaleString()}₮</span></td>
                <td><span class="badge badge-${book.condition||''}">${book.condition||'-'}</span></td>
                <td><span class="badge badge-${book.status}">${book.status}</span></td>
                <td><div class="actions">${book.status === 'available' ? `<button class="btn-delete" onclick="deleteBook(${book.id})">Устгах</button>` : '-'}</div></td>
            </tr>`;
        }).join('');
    } catch(e) {
        document.getElementById('my-books-tbody').innerHTML = '<tr><td colspan="8" style="text-align:center;color:#dc2626;">Холбогдож чадсангүй.</td></tr>';
    }
}

async function deleteBook(id) {
    if (!confirm('Энэ номыг устгах уу?')) return;
    try { await fetch(API.books + '/' + id, { method: 'DELETE' }); loadMyBooks(); } catch(e) { alert('Алдаа'); }
}

// ===== BUY =====
async function buyBook(bookId, price, sellerUsername) {
    if (!confirm('Энэ номыг худалдаж авах уу?')) return;
    try {
        const res = await fetch(API.orders, {
            method: 'POST', headers: {'Content-Type':'application/json'},
            body: JSON.stringify({ bookId, buyerUsername: currentUser.username, sellerUsername, price, status: 'pending' })
        });
        if (res.ok) { alert('Захиалга амжилттай!'); loadBooks(); }
        else { const d = await res.json(); alert('Алдаа: '+(d.error||'')); }
    } catch(e) { alert('Order service-тэй холбогдож чадсангүй.'); }
}

// ===== ORDERS =====
async function loadOrders() {
    try {
        const res = await fetch(API.orders + '?buyer=' + currentUser.username);
        const orders = await res.json();
        const tbody = document.getElementById('orders-tbody');
        if (orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:24px;color:#94a3b8;">Захиалга байхгүй</td></tr>';
            return;
        }
        tbody.innerHTML = orders.map(o => `<tr>
            <td>${o.id}</td><td>${o.bookId}</td><td>${o.sellerUsername}</td>
            <td><span class="price">${Number(o.price).toLocaleString()}₮</span></td>
            <td><span class="badge badge-${o.status}">${o.status}</span></td>
            <td>${o.createdAt ? new Date(o.createdAt).toLocaleDateString('mn-MN') : '-'}</td>
            <td>${o.status==='pending' ? `<button class="btn-cancel" onclick="cancelOrder(${o.id})">Цуцлах</button>` : '-'}</td>
        </tr>`).join('');
    } catch(e) {
        document.getElementById('orders-tbody').innerHTML = '<tr><td colspan="7" style="text-align:center;color:#dc2626;">Холбогдож чадсангүй.</td></tr>';
    }
}

async function cancelOrder(id) {
    if (!confirm('Захиалга цуцлах уу?')) return;
    try { await fetch(API.orders+'/'+id+'?action=cancel',{method:'PUT'}); loadOrders(); } catch(e) {}
}

// ===== HELPERS =====
function showAlert(id, msg, type) {
    document.getElementById(id).innerHTML = `<div class="alert alert-${type}">${msg}</div>`;
    setTimeout(() => { document.getElementById(id).innerHTML = ''; }, 5000);
}

// ===== INIT =====
window.onload = function() {
    const saved = localStorage.getItem('user');
    if (saved) { currentUser = JSON.parse(saved); enterApp(); }
};
document.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
        if (!document.getElementById('page-login').classList.contains('hidden')) doLogin();
        if (!document.getElementById('page-register').classList.contains('hidden')) doRegister();
    }
});
