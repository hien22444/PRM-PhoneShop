const express = require("express");
const cors = require("cors");
const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

const app = express();
app.use(cors());
app.use(express.json());

// 📁 File lưu dữ liệu
const DATA_FILE = path.join(__dirname, "data.json");

// 🔄 Đọc dữ liệu từ file
function loadData() {
  if (fs.existsSync(DATA_FILE)) {
    const data = JSON.parse(fs.readFileSync(DATA_FILE, "utf8"));
    if (Array.isArray(data)) {
      return { products: data, carts: [], users: [], orders: [] }; // nếu là dạng cũ
    }
    // Đảm bảo có đầy đủ các trường cần thiết
    return {
      products: data.products || [],
      carts: data.carts || [],
      users: data.users || [],
      orders: data.orders || []
    };
  }
  return { products: [], carts: [], users: [], orders: [] };
}

// 💾 Ghi dữ liệu vào file
function saveData(data) {
  try {
    fs.writeFileSync(DATA_FILE, JSON.stringify(data, null, 2));
    console.log(`💾 Data saved to ${DATA_FILE} at ${new Date().toISOString()}`);
  } catch (error) {
    console.error(`❌ Error saving data to ${DATA_FILE}:`, error);
    throw error;
  }
}

// 🔐 Hash password (simple MD5 for demo - use bcrypt in production)
function hashPassword(password) {
  return crypto.createHash('md5').update(password).digest('hex');
}

// 🆔 Generate unique user ID
function generateUserId() {
  return 'user_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

// 🆔 Generate unique order ID
function generateOrderId() {
  return 'order_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

// 🎨 Get status color for UI
function getStatusColor(status) {
  switch (status) {
    case 'Đang xử lý':
      return '#FF9800'; // Orange
    case 'Đã xác nhận':
      return '#2196F3'; // Blue
    case 'Đang giao':
      return '#9C27B0'; // Purple
    case 'Hoàn thành':
      return '#4CAF50'; // Green
    case 'Đã hủy':
      return '#F44336'; // Red
    case 'Chờ thanh toán':
      return '#FFC107'; // Amber
    case 'Đã thanh toán':
      return '#00BCD4'; // Cyan
    default:
      return '#757575'; // Grey
  }
}

// 🛠️ Khởi tạo dữ liệu
let db = loadData();

// Đảm bảo có đầy đủ các arrays cần thiết
let needSave = false;

// Luôn đảm bảo có cấu trúc cơ bản
if (!db.carts) {
  db.carts = [];
  needSave = true;
}

if (!db.users) {
  db.users = [];
  needSave = true;
}

if (!db.orders) {
  db.orders = [];
  needSave = true;
}

// Chỉ tạo products mẫu nếu chưa có
if (!db.products || db.products.length === 0) {
  db.products = [
    {
      id: "p1",
      name: "Samsung Galaxy S24 Ultra",
      brand: "Samsung",
      price: 25000000,
      stock: 10,
      visible: true,
      images: ["https://picsum.photos/seed/1/300/300"]
    },
    {
      id: "p2", 
      name: "iPhone 15 Pro Max",
      brand: "Apple",
      price: 30000000,
      stock: 8,
      visible: true,
      images: ["https://picsum.photos/seed/2/300/300"]
    },
    {
      id: "p3",
      name: "Xiaomi 14 Ultra", 
      brand: "Xiaomi",
      price: 20000000,
      stock: 15,
      visible: true,
      images: ["https://picsum.photos/seed/3/300/300"]
    },
    {
      id: "p4",
      name: "OPPO Find X7 Pro",
      brand: "OPPO", 
      price: 18000000,
      stock: 12,
      visible: true,
      images: ["https://picsum.photos/seed/4/300/300"]
    }
  ];
  needSave = true;
}

if (needSave) {
  saveData(db);
  console.log("✅ Initialized clean data.json - NO MOCK DATA");
}

/* =============== 🔐 USER AUTHENTICATION API ==================== */

// Đăng ký người dùng mới
app.post("/api/auth/register", (req, res) => {
  const { fullName, email, username, password } = req.body;

  // Validation
  if (!fullName || !email || !username || !password) {
    return res.status(400).json({
      success: false,
      message: "Vui lòng điền đầy đủ thông tin"
    });
  }

  if (password.length < 6) {
    return res.status(400).json({
      success: false,
      message: "Mật khẩu phải có ít nhất 6 ký tự"
    });
  }

  // Email validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return res.status(400).json({
      success: false,
      message: "Email không hợp lệ"
    });
  }

  // Kiểm tra username đã tồn tại
  const existingUserByUsername = db.users.find(u => u.username === username);
  if (existingUserByUsername) {
    return res.status(409).json({
      success: false,
      message: "Tên đăng nhập đã tồn tại"
    });
  }

  // Kiểm tra email đã tồn tại
  const existingUserByEmail = db.users.find(u => u.email === email);
  if (existingUserByEmail) {
    return res.status(409).json({
      success: false,
      message: "Email đã được sử dụng"
    });
  }

  // Tạo user mới
  const newUser = {
    id: generateUserId(),
    fullName: fullName.trim(),
    email: email.toLowerCase().trim(),
    username: username.trim(),
    password: hashPassword(password),
    phone: "",
    address: "",
    dateOfBirth: "",
    gender: "",
    avatarUrl: "",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isActive: true
  };

  db.users.push(newUser);
  saveData(db);

  // Trả về thông tin user (không bao gồm password)
  const { password: _, ...userResponse } = newUser;
  
  res.status(201).json({
    success: true,
    message: "Đăng ký thành công",
    user: userResponse
  });
});

// Đăng nhập
app.post("/api/auth/login", (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).json({
      success: false,
      message: "Vui lòng nhập tên đăng nhập và mật khẩu"
    });
  }

  // Tìm user theo username
  const user = db.users.find(u => u.username === username && u.isActive);
  
  if (!user || user.password !== hashPassword(password)) {
    return res.status(401).json({
      success: false,
      message: "Tên đăng nhập hoặc mật khẩu không đúng"
    });
  }

  // Trả về thông tin user (không bao gồm password)
  const { password: _, ...userResponse } = user;
  
  res.json({
    success: true,
    message: "Đăng nhập thành công",
    user: userResponse
  });
});

// Lấy thông tin user theo ID
app.get("/api/auth/user/:id", (req, res) => {
  const user = db.users.find(u => u.id === req.params.id && u.isActive);
  
  if (!user) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy người dùng"
    });
  }

  const { password: _, ...userResponse } = user;
  res.json({
    success: true,
    user: userResponse
  });
});

// Cập nhật thông tin user
app.put("/api/auth/user/:id", (req, res) => {
  const { fullName, email, phone, address, dateOfBirth, gender } = req.body;
  const userId = req.params.id;

  const userIndex = db.users.findIndex(u => u.id === userId && u.isActive);
  
  if (userIndex === -1) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy người dùng"
    });
  }

  // Kiểm tra email đã được sử dụng bởi user khác
  if (email && email !== db.users[userIndex].email) {
    const existingUser = db.users.find(u => u.email === email.toLowerCase().trim() && u.id !== userId);
    if (existingUser) {
      return res.status(409).json({
        success: false,
        message: "Email đã được sử dụng bởi tài khoản khác"
      });
    }
  }

  // Cập nhật thông tin với validation
  const updatedFields = {};
  
  if (fullName && fullName.trim() !== "") {
    updatedFields.fullName = fullName.trim();
    db.users[userIndex].fullName = fullName.trim();
  }
  
  if (email && email.trim() !== "") {
    updatedFields.email = email.toLowerCase().trim();
    db.users[userIndex].email = email.toLowerCase().trim();
  }
  
  if (phone !== undefined) {
    updatedFields.phone = phone ? phone.trim() : "";
    db.users[userIndex].phone = phone ? phone.trim() : "";
  }
  
  if (address !== undefined) {
    updatedFields.address = address ? address.trim() : "";
    db.users[userIndex].address = address ? address.trim() : "";
  }
  
  if (dateOfBirth !== undefined) {
    updatedFields.dateOfBirth = dateOfBirth;
    db.users[userIndex].dateOfBirth = dateOfBirth;
  }
  
  if (gender !== undefined) {
    updatedFields.gender = gender;
    db.users[userIndex].gender = gender;
  }

  // Cập nhật thời gian sửa đổi
  db.users[userIndex].updatedAt = new Date().toISOString();

  // Lưu vào data.json
  saveData(db);

  const { password: _, ...userResponse } = db.users[userIndex];
  res.json({
    success: true,
    message: "Cập nhật thông tin thành công",
    user: userResponse,
    updatedFields: updatedFields
  });
});

// Cập nhật avatar/profile picture
app.put("/api/auth/user/:id/avatar", (req, res) => {
  const { avatarUrl } = req.body;
  const userId = req.params.id;

  const userIndex = db.users.findIndex(u => u.id === userId && u.isActive);
  
  if (userIndex === -1) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy người dùng"
    });
  }

  // Cập nhật avatar
  db.users[userIndex].avatarUrl = avatarUrl || "";
  db.users[userIndex].updatedAt = new Date().toISOString();

  // Lưu vào data.json
  saveData(db);

  const { password: _, ...userResponse } = db.users[userIndex];
  res.json({
    success: true,
    message: "Cập nhật avatar thành công",
    user: userResponse
  });
});

// Đổi mật khẩu
app.put("/api/auth/user/:id/password", (req, res) => {
  const { currentPassword, newPassword } = req.body;
  const userId = req.params.id;

  if (!currentPassword || !newPassword) {
    return res.status(400).json({
      success: false,
      message: "Vui lòng nhập mật khẩu hiện tại và mật khẩu mới"
    });
  }

  if (newPassword.length < 6) {
    return res.status(400).json({
      success: false,
      message: "Mật khẩu mới phải có ít nhất 6 ký tự"
    });
  }

  const userIndex = db.users.findIndex(u => u.id === userId && u.isActive);
  
  if (userIndex === -1) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy người dùng"
    });
  }

  // Kiểm tra mật khẩu hiện tại
  if (db.users[userIndex].password !== hashPassword(currentPassword)) {
    return res.status(401).json({
      success: false,
      message: "Mật khẩu hiện tại không đúng"
    });
  }

  // Cập nhật mật khẩu mới
  db.users[userIndex].password = hashPassword(newPassword);
  saveData(db);

  res.json({
    success: true,
    message: "Đổi mật khẩu thành công"
  });
});

// Kiểm tra username có tồn tại không
app.get("/api/auth/check-username/:username", (req, res) => {
  const username = req.params.username;
  const exists = db.users.some(u => u.username === username);
  
  res.json({
    exists: exists,
    available: !exists
  });
});

// Kiểm tra email có tồn tại không
app.get("/api/auth/check-email/:email", (req, res) => {
  const email = req.params.email.toLowerCase();
  const exists = db.users.some(u => u.email === email);
  
  res.json({
    exists: exists,
    available: !exists
  });
});

/* =============== 🛍️ USER API ==================== */
app.get("/api/products", (req, res) => {
  const { q, brand } = req.query;
  let visibleProducts = db.products.filter((p) => p.visible);
  
  // Tìm kiếm theo query
  if (q) {
    const searchQuery = q.toLowerCase();
    visibleProducts = visibleProducts.filter(p => 
      (p.name && p.name.toLowerCase().includes(searchQuery)) ||
      (p.brand && p.brand.toLowerCase().includes(searchQuery)) ||
      (p.category && p.category.toLowerCase().includes(searchQuery)) ||
      (p.description && p.description.toLowerCase().includes(searchQuery))
    );
  }
  
  // Lọc theo brand
  if (brand) {
    visibleProducts = visibleProducts.filter(p => 
      p.brand && p.brand.toLowerCase() === brand.toLowerCase()
    );
  }
  
  res.json({
    content: visibleProducts,
    page: 0,
    size: visibleProducts.length,
    totalPages: 1,
    totalElements: visibleProducts.length,
  });
});

app.get("/api/products/:id", (req, res) => {
  const item = db.products.find((p) => p.id === req.params.id && p.visible);
  if (!item) return res.status(404).json({ message: "Not found" });
  res.json(item);
});

/* =============== 🧑‍💻 ADMIN API ==================== */
app.get("/admin/products", (req, res) => {
  let { page = 0, size = 20, q = "", brand, sort } = req.query;
  page = parseInt(page, 10);
  size = parseInt(size, 10);

  let data = [...db.products];
  if (q)
    data = data.filter((p) =>
      p.name.toLowerCase().includes(String(q).toLowerCase())
    );
  if (brand)
    data = data.filter(
      (p) => p.brand.toLowerCase() === String(brand).toLowerCase()
    );
  if (sort === "price,asc") data.sort((a, b) => a.price - b.price);
  else if (sort === "price,desc") data.sort((a, b) => b.price - a.price);

  const totalElements = data.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const content = data.slice(start, start + size);

  res.json({ content, page, size, totalPages, totalElements });
});

app.get("/admin/products/:id", (req, res) => {
  const item = db.products.find((p) => p.id === req.params.id);
  if (!item) return res.status(404).json({ message: "Not found" });
  res.json(item);
});

app.post("/admin/products", (req, res) => {
  const { name, brand, price, stock, images } = req.body || {};
  if (
    !name ||
    !brand ||
    typeof price !== "number" ||
    typeof stock !== "number"
  ) {
    return res.status(400).json({ message: "Invalid payload" });
  }
  const id = `p${db.products.length + 1}`;
  const newItem = {
    id,
    name,
    brand,
    price,
    stock,
    visible: true,
    images: images || [],
  };
  db.products.unshift(newItem);
  saveData(db);
  return res.status(201).json(newItem);
});

app.put("/admin/products/:id", (req, res) => {
  const idx = db.products.findIndex((p) => p.id === req.params.id);
  if (idx < 0) return res.status(404).json({ message: "Not found" });
  const { name, brand, price, stock, visible, images } = req.body || {};
  db.products[idx] = {
    ...db.products[idx],
    ...(name && { name }),
    ...(brand && { brand }),
    ...(price !== undefined && { price }),
    ...(stock !== undefined && { stock }),
    ...(visible !== undefined && { visible }),
    ...(images && { images }),
  };
  saveData(db);
  res.json(db.products[idx]);
});

app.patch("/admin/products/:id/visibility", (req, res) => {
  const idx = db.products.findIndex((p) => p.id === req.params.id);
  if (idx < 0) return res.status(404).json({ message: "Not found" });
  const { visible } = req.query;
  db.products[idx].visible = String(visible) === "true";
  saveData(db);
  res.json({ ok: true });
});

app.delete("/admin/products/:id", (req, res) => {
  const before = db.products.length;
  db.products = db.products.filter((p) => p.id !== req.params.id);
  if (db.products.length === before)
    return res.status(404).json({ message: "Not found" });
  saveData(db);
  res.status(204).end();
});

// Admin: Lấy danh sách users
app.get("/admin/users", (req, res) => {
  let { page = 0, size = 20, q = "" } = req.query;
  page = parseInt(page, 10);
  size = parseInt(size, 10);

  let users = db.users.map(({ password, ...user }) => user); // Loại bỏ password
  
  if (q) {
    const searchQuery = q.toLowerCase();
    users = users.filter(u => 
      u.fullName.toLowerCase().includes(searchQuery) ||
      u.email.toLowerCase().includes(searchQuery) ||
      u.username.toLowerCase().includes(searchQuery)
    );
  }

  const totalElements = users.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const content = users.slice(start, start + size);

  res.json({ content, page, size, totalPages, totalElements });
});

/* =============== 🛒 CART API ==================== */

// Thêm sản phẩm vào giỏ
app.post("/api/cart/add", (req, res) => {
  const { userId, productId, quantity = 1 } = req.body;
  
  console.log(`🛒 ADD TO CART: User ${userId} adding product ${productId} (qty: ${quantity})`);

  if (!userId || !productId) {
    return res.status(400).json({ message: "Thiếu userId hoặc productId" });
  }

  // Kiểm tra user tồn tại
  const user = db.users.find(u => u.id === userId);
  if (!user) {
    console.log(`❌ User ${userId} not found`);
    return res.status(404).json({ message: "User không tồn tại" });
  }

  const product = db.products.find((p) => p.id === productId);
  if (!product) {
    console.log(`❌ Product ${productId} not found`);
    return res.status(404).json({ message: "Sản phẩm không tồn tại" });
  }

  let cart = db.carts.find((c) => c.userId === userId);
  if (!cart) {
    cart = { userId, items: [] };
    db.carts.push(cart);
    console.log(`✅ Created new cart for user ${userId}`);
  }

  const existing = cart.items.find((i) => i.productId === productId);
  if (existing) {
    existing.quantity += quantity;
    console.log(`✅ Updated existing item ${productId} quantity: ${existing.quantity}`);
  } else {
    cart.items.push({
      productId,
      name: product.name,
      price: product.price,
      image: product.images[0],
      quantity,
    });
    console.log(`✅ Added new item ${productId} to cart`);
  }

  saveData(db);
  console.log(`💾 Cart saved for user ${userId} - Total items: ${cart.items.length}`);
  res.json({ 
    success: true,
    message: "Đã thêm sản phẩm vào giỏ hàng", 
    cart: cart
  });
});

// Xem giỏ hàng
app.get("/api/cart/:userId", (req, res) => {
  const cart = db.carts.find((c) => c.userId === req.params.userId);
  res.json(cart || { userId: req.params.userId, items: [] });
});

// Cập nhật số lượng
app.patch("/api/cart/update", (req, res) => {
  const { userId, productId, quantity } = req.body;
  const cart = db.carts.find((c) => c.userId === userId);
  if (!cart)
    return res.status(404).json({ message: "Không tìm thấy giỏ hàng" });
  const item = cart.items.find((i) => i.productId === productId);
  if (!item)
    return res.status(404).json({ message: "Sản phẩm không có trong giỏ" });

  item.quantity = quantity;
  saveData(db);
  res.json({ message: "Cập nhật số lượng thành công", cart });
});

// Xóa sản phẩm khỏi giỏ
app.delete("/api/cart/remove", (req, res) => {
  const { userId, productId } = req.body;
  
  console.log(`🗑️ REMOVE FROM CART: User ${userId} removing product ${productId}`);
  
  const cart = db.carts.find((c) => c.userId === userId);
  if (!cart) {
    console.log(`❌ Cart not found for user ${userId}`);
    return res.status(404).json({ message: "Không tìm thấy giỏ hàng" });
  }
  
  const beforeCount = cart.items.length;
  cart.items = cart.items.filter((i) => i.productId !== productId);
  const afterCount = cart.items.length;
  
  console.log(`✅ Removed product ${productId} - Items: ${beforeCount} → ${afterCount}`);
  
  saveData(db);
  console.log(`💾 Cart updated for user ${userId}`);
  
  res.json({ 
    success: true,
    message: "Đã xóa sản phẩm khỏi giỏ hàng", 
    cart: cart 
  });
});

// Xóa toàn bộ giỏ hàng
app.delete("/api/cart/clear/:userId", (req, res) => {
  const userId = req.params.userId;
  const cartIndex = db.carts.findIndex((c) => c.userId === userId);
  
  if (cartIndex === -1) {
    return res.status(404).json({ 
      success: false,
      message: "Không tìm thấy giỏ hàng" 
    });
  }
  
  // Xóa giỏ hàng
  db.carts.splice(cartIndex, 1);
  saveData(db);
  
  res.json({ 
    success: true,
    message: "Đã xóa toàn bộ giỏ hàng" 
  });
});

// Lấy tổng số lượng sản phẩm trong giỏ hàng
app.get("/api/cart/:userId/count", (req, res) => {
  const cart = db.carts.find((c) => c.userId === req.params.userId);
  const totalItems = cart ? cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
  
  res.json({ 
    success: true,
    totalItems: totalItems,
    itemCount: cart ? cart.items.length : 0
  });
});

/* =============== 📦 ORDER API ==================== */

// Tạo đơn hàng mới
app.post("/api/orders", (req, res) => {
  const { 
    userId, 
    customerInfo, 
    items, 
    paymentMethod, 
    totalAmount,
    shippingAddress 
  } = req.body;

  // Validation
  if (!userId || !customerInfo || !items || !paymentMethod || !totalAmount) {
    return res.status(400).json({
      success: false,
      message: "Thiếu thông tin đơn hàng"
    });
  }

  if (!Array.isArray(items) || items.length === 0) {
    return res.status(400).json({
      success: false,
      message: "Đơn hàng phải có ít nhất một sản phẩm"
    });
  }

  // Kiểm tra user tồn tại
  const user = db.users.find(u => u.id === userId);
  if (!user) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy người dùng"
    });
  }

  // Tạo đơn hàng mới
  const newOrder = {
    id: generateOrderId(),
    userId: userId,
    customerInfo: {
      fullName: customerInfo.fullName || user.fullName,
      phone: customerInfo.phone,
      email: customerInfo.email || user.email,
      address: shippingAddress || customerInfo.address
    },
    items: items.map(item => ({
      productId: item.productId,
      name: item.name,
      price: item.price,
      quantity: item.quantity,
      image: item.image
    })),
    paymentMethod: paymentMethod,
    totalAmount: totalAmount,
    status: paymentMethod === "COD" ? "Đang xử lý" : "Chờ thanh toán",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  // Lưu đơn hàng
  db.orders.push(newOrder);
  console.log(`📦 ORDER CREATED: ${newOrder.id} for user ${userId}`);
  
  // Xóa giỏ hàng sau khi tạo đơn hàng thành công
  const cartIndex = db.carts.findIndex(c => c.userId === userId);
  if (cartIndex !== -1) {
    const cartItemCount = db.carts[cartIndex].items.length;
    db.carts.splice(cartIndex, 1);
    console.log(`🗑️ CART CLEARED: Removed ${cartItemCount} items from user ${userId} cart after order creation`);
  } else {
    console.log(`⚠️ No cart found to clear for user ${userId}`);
  }
  
  saveData(db);
  console.log(`💾 Order saved and cart cleared for user ${userId}`);

  res.status(201).json({
    success: true,
    message: "Tạo đơn hàng thành công",
    order: newOrder
  });
});

// Tạo đơn hàng từ giỏ hàng
app.post("/api/orders/from-cart", (req, res) => {
  console.log(`📦 CREATE ORDER FROM CART: Received request`);
  console.log(`📦 Request body:`, JSON.stringify(req.body, null, 2));
  
  const { 
    userId, 
    customerInfo, 
    paymentMethod,
    shippingAddress 
  } = req.body;

  // Validation
  if (!userId || !customerInfo || !paymentMethod) {
    console.log(`❌ Validation failed - userId: ${userId}, customerInfo: ${!!customerInfo}, paymentMethod: ${paymentMethod}`);
    return res.status(400).json({
      success: false,
      message: "Thiếu thông tin đơn hàng"
    });
  }

  // Kiểm tra user tồn tại
  const user = db.users.find(u => u.id === userId);
  if (!user) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy người dùng"
    });
  }

  // Check if request has items directly or need to get from cart
  let orderItems = [];
  let totalAmount = 0;
  
  if (req.body.items && req.body.items.length > 0) {
    // Case 1: Items sent directly in request (Android sends items)
    console.log(`📦 Using items from request: ${req.body.items.length} items`);
    
    // Convert request items to order items with product details
    orderItems = req.body.items.map(item => {
      const product = db.products.find(p => p.id === item.productId);
      return {
        productId: item.productId,
        name: product ? product.name : `Product ${item.productId}`,
        price: item.price,
        quantity: item.quantity,
        image: product ? product.images[0] : "https://picsum.photos/300/300"
      };
    });
    
    totalAmount = req.body.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    
  } else {
    // Case 2: Get items from user's cart (fallback)
    console.log(`📦 Getting items from user cart`);
    
    const cart = db.carts.find(c => c.userId === userId);
    if (!cart || !cart.items || cart.items.length === 0) {
      return res.status(400).json({
        success: false,
        message: "Giỏ hàng trống và không có items trong request"
      });
    }
    
    orderItems = cart.items.map(item => ({
      productId: item.productId,
      name: item.name,
      price: item.price,
      quantity: item.quantity,
      image: item.image
    }));
    
    totalAmount = cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  }

  console.log(`📦 Order will have ${orderItems.length} items, total: ${totalAmount.toLocaleString()} VND`);

  // Tạo đơn hàng mới
  const newOrder = {
    id: generateOrderId(),
    userId: userId,
    customerInfo: {
      fullName: customerInfo.fullName || user.fullName,
      phone: customerInfo.phone,
      email: customerInfo.email || user.email,
      address: shippingAddress || customerInfo.address
    },
    items: orderItems,
    paymentMethod: paymentMethod,
    totalAmount: totalAmount,
    status: paymentMethod === "COD" ? "Đang xử lý" : "Chờ thanh toán",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  // Lưu đơn hàng
  db.orders.push(newOrder);
  console.log(`📦 ORDER CREATED: ${newOrder.id} for user ${userId} (${orderItems.length} items)`);
  
  // Xóa giỏ hàng sau khi tạo đơn hàng thành công (nếu có)
  const cartIndex = db.carts.findIndex(c => c.userId === userId);
  if (cartIndex !== -1) {
    const cartItemCount = db.carts[cartIndex].items.length;
    db.carts.splice(cartIndex, 1);
    console.log(`🗑️ CART CLEARED: Removed ${cartItemCount} items from user ${userId} cart after order`);
  } else {
    console.log(`ℹ️ No cart found for user ${userId} to clear`);
  }
  
  saveData(db);
  console.log(`💾 Order from cart saved and cart cleared for user ${userId}`);

  // Transform order response to match Android Order model
  const transformedOrder = {
    id: newOrder.id,
    orderId: newOrder.id,  // Android expects orderId field
    orderDate: newOrder.createdAt,  // Android expects orderDate field
    status: newOrder.status,
    totalPrice: newOrder.totalAmount,  // Android expects totalPrice field
    itemCount: newOrder.items ? newOrder.items.length : 0,  // Android expects itemCount field
    fullName: newOrder.customerInfo ? newOrder.customerInfo.fullName : '',
    phone: newOrder.customerInfo ? newOrder.customerInfo.phone : '',
    address: newOrder.customerInfo ? newOrder.customerInfo.address : '',
    paymentMethod: newOrder.paymentMethod,
    paymentUrl: newOrder.paymentUrl || '',
    // Keep original fields for compatibility
    userId: newOrder.userId,
    customerInfo: newOrder.customerInfo,
    items: newOrder.items,
    totalAmount: newOrder.totalAmount,
    createdAt: newOrder.createdAt,
    updatedAt: newOrder.updatedAt
  };

  res.status(201).json({
    success: true,
    message: "Tạo đơn hàng từ giỏ hàng thành công",
    order: transformedOrder
  });
});

// Lấy danh sách đơn hàng của user
app.get("/api/orders/:userId", (req, res) => {
  const userId = req.params.userId;
  const { page = 0, size = 20 } = req.query;
  
  console.log(`📋 GET ORDERS: Fetching orders for user ${userId}`);
  
  const userOrders = db.orders.filter(order => order.userId === userId);
  
  // Sắp xếp theo thời gian tạo (mới nhất trước)
  userOrders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  
  console.log(`📋 Found ${userOrders.length} orders for user ${userId}`);
  
  // Transform orders to match Android Order model expectations with complete info
  const transformedOrders = userOrders.map(order => {
    // Calculate summary info
    const itemCount = order.items ? order.items.length : 0;
    const totalQuantity = order.items ? order.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
    
    // Get first item for preview (most common case)
    const firstItem = order.items && order.items.length > 0 ? order.items[0] : null;
    const previewImage = firstItem ? firstItem.image : 'https://picsum.photos/300/300';
    const previewName = firstItem ? firstItem.name : 'Sản phẩm';
    
    // Format date for display
    const orderDate = new Date(order.createdAt);
    const formattedDate = orderDate.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: '2-digit', 
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
    
    return {
      // Android expected fields
      id: order.id,
      orderId: order.id,
      orderDate: order.createdAt,
      status: order.status,
      totalPrice: order.totalAmount,
      itemCount: itemCount,
      fullName: order.customerInfo ? order.customerInfo.fullName : '',
      phone: order.customerInfo ? order.customerInfo.phone : '',
      address: order.customerInfo ? order.customerInfo.address : '',
      paymentMethod: order.paymentMethod,
      paymentUrl: order.paymentUrl || '',
      
      // Enhanced fields for better UI
      formattedDate: formattedDate,
      totalQuantity: totalQuantity,
      previewImage: previewImage,
      previewName: previewName,
      statusColor: getStatusColor(order.status),
      
      // Complete customer info
      customerInfo: {
        fullName: order.customerInfo ? order.customerInfo.fullName : '',
        phone: order.customerInfo ? order.customerInfo.phone : '',
        email: order.customerInfo ? order.customerInfo.email : '',
        address: order.customerInfo ? order.customerInfo.address : ''
      },
      
      // Complete items with enhanced info
      items: order.items ? order.items.map(item => ({
        productId: item.productId,
        name: item.name || 'Sản phẩm',
        price: item.price || 0,
        quantity: item.quantity || 1,
        image: item.image || 'https://picsum.photos/300/300',
        subtotal: (item.price || 0) * (item.quantity || 1)
      })) : [],
      
      // Original fields for compatibility
      userId: order.userId,
      totalAmount: order.totalAmount,
      createdAt: order.createdAt,
      updatedAt: order.updatedAt
    };
  });
  
  console.log(`📋 Transformed ${transformedOrders.length} orders with Android-compatible fields`);
  if (transformedOrders.length > 0) {
    console.log(`📋 Sample order: ID=${transformedOrders[0].orderId}, Items=${transformedOrders[0].itemCount}, Total=${transformedOrders[0].totalPrice}`);
  }
  
  // Android expects array directly, not wrapped in object
  res.json(transformedOrders);
});

// Lấy chi tiết đơn hàng
app.get("/api/orders/detail/:orderId", (req, res) => {
  const orderId = req.params.orderId;
  
  console.log(`📋 GET ORDER DETAIL: Fetching order ${orderId}`);
  
  const order = db.orders.find(o => o.id === orderId);
  
  if (!order) {
    console.log(`❌ Order ${orderId} not found`);
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy đơn hàng"
    });
  }
  
  // Transform order detail to match Android expectations with enhanced info
  const itemCount = order.items ? order.items.length : 0;
  const totalQuantity = order.items ? order.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
  
  // Format date for display
  const orderDate = new Date(order.createdAt);
  const formattedDate = orderDate.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
  
  const transformedOrder = {
    // Android expected fields
    id: order.id,
    orderId: order.id,
    orderDate: order.createdAt,
    status: order.status,
    totalPrice: order.totalAmount,
    itemCount: itemCount,
    fullName: order.customerInfo ? order.customerInfo.fullName : '',
    phone: order.customerInfo ? order.customerInfo.phone : '',
    address: order.customerInfo ? order.customerInfo.address : '',
    paymentMethod: order.paymentMethod,
    paymentUrl: order.paymentUrl || '',
    
    // Enhanced fields for better UI
    formattedDate: formattedDate,
    totalQuantity: totalQuantity,
    statusColor: getStatusColor(order.status),
    
    // Complete customer info for detail view
    customerInfo: {
      fullName: order.customerInfo ? order.customerInfo.fullName : '',
      phone: order.customerInfo ? order.customerInfo.phone : '',
      email: order.customerInfo ? order.customerInfo.email : '',
      address: order.customerInfo ? order.customerInfo.address : ''
    },
    
    // Complete items with enhanced info for detail view
    items: order.items ? order.items.map(item => ({
      productId: item.productId,
      name: item.name || 'Sản phẩm',
      price: item.price || 0,
      quantity: item.quantity || 1,
      image: item.image || 'https://picsum.photos/300/300',
      subtotal: (item.price || 0) * (item.quantity || 1),
      formattedPrice: (item.price || 0).toLocaleString('vi-VN') + ' ₫',
      formattedSubtotal: ((item.price || 0) * (item.quantity || 1)).toLocaleString('vi-VN') + ' ₫'
    })) : [],
    
    // Formatted totals for display
    formattedTotalAmount: order.totalAmount.toLocaleString('vi-VN') + ' ₫',
    
    // Timeline info
    timeline: [
      {
        status: 'Đặt hàng',
        date: order.createdAt,
        completed: true,
        description: 'Đơn hàng đã được tạo'
      },
      {
        status: 'Xác nhận',
        date: order.status !== 'Đang xử lý' ? order.updatedAt : null,
        completed: order.status !== 'Đang xử lý',
        description: 'Đơn hàng đã được xác nhận'
      },
      {
        status: 'Đang giao',
        date: order.status === 'Đang giao' || order.status === 'Hoàn thành' ? order.updatedAt : null,
        completed: order.status === 'Đang giao' || order.status === 'Hoàn thành',
        description: 'Đơn hàng đang được giao'
      },
      {
        status: 'Hoàn thành',
        date: order.status === 'Hoàn thành' ? order.updatedAt : null,
        completed: order.status === 'Hoàn thành',
        description: 'Đơn hàng đã hoàn thành'
      }
    ],
    
    // Original fields for compatibility
    userId: order.userId,
    totalAmount: order.totalAmount,
    createdAt: order.createdAt,
    updatedAt: order.updatedAt
  };
  
  console.log(`✅ Order detail transformed: ${transformedOrder.orderId} with ${transformedOrder.itemCount} items`);
  
  res.json({
    success: true,
    order: transformedOrder
  });
});

// Cập nhật trạng thái đơn hàng
app.patch("/api/orders/:orderId/status", (req, res) => {
  const orderId = req.params.orderId;
  const { status } = req.body;
  
  if (!status) {
    return res.status(400).json({
      success: false,
      message: "Thiếu trạng thái đơn hàng"
    });
  }
  
  const orderIndex = db.orders.findIndex(o => o.id === orderId);
  
  if (orderIndex === -1) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy đơn hàng"
    });
  }
  
  // Cập nhật trạng thái
  db.orders[orderIndex].status = status;
  db.orders[orderIndex].updatedAt = new Date().toISOString();
  
  saveData(db);
  
  res.json({
    success: true,
    message: "Cập nhật trạng thái đơn hàng thành công",
    order: db.orders[orderIndex]
  });
});

// Hủy đơn hàng
app.delete("/api/orders/:orderId", (req, res) => {
  const orderId = req.params.orderId;
  const { userId } = req.body;
  
  const orderIndex = db.orders.findIndex(o => o.id === orderId);
  
  if (orderIndex === -1) {
    return res.status(404).json({
      success: false,
      message: "Không tìm thấy đơn hàng"
    });
  }
  
  const order = db.orders[orderIndex];
  
  // Kiểm tra quyền hủy đơn hàng
  if (order.userId !== userId) {
    return res.status(403).json({
      success: false,
      message: "Không có quyền hủy đơn hàng này"
    });
  }
  
  // Chỉ cho phép hủy đơn hàng chưa được xử lý
  if (order.status !== "Đang xử lý" && order.status !== "Chờ thanh toán") {
    return res.status(400).json({
      success: false,
      message: "Không thể hủy đơn hàng đã được xử lý"
    });
  }
  
  // Cập nhật trạng thái thành "Đã hủy" thay vì xóa
  db.orders[orderIndex].status = "Đã hủy";
  db.orders[orderIndex].updatedAt = new Date().toISOString();
  
  saveData(db);
  
  res.json({
    success: true,
    message: "Hủy đơn hàng thành công"
  });
});

/* =============== 📊 SERVER STATUS API ==================== */

// API để kiểm tra trạng thái server và dữ liệu
app.get("/api/status", (req, res) => {
  const stats = {
    server: {
      status: "running",
      port: 8080,
      timestamp: new Date().toISOString()
    },
    database: {
      file: DATA_FILE,
      exists: fs.existsSync(DATA_FILE),
      stats: {
        users: db.users ? db.users.length : 0,
        products: db.products ? db.products.length : 0,
        carts: db.carts ? db.carts.length : 0,
        orders: db.orders ? db.orders.length : 0
      }
    }
  };
  
  res.json(stats);
});

// API để reset/xóa tất cả dữ liệu test (DANGER - chỉ dùng cho development)
app.post("/api/reset-database", (req, res) => {
  try {
    // Reset về trạng thái clean
    db = {
      products: [
        {
          id: "p1",
          name: "Samsung Galaxy S24 Ultra",
          brand: "Samsung",
          price: 25000000,
          stock: 10,
          visible: true,
          images: ["https://picsum.photos/seed/1/300/300"]
        },
        {
          id: "p2", 
          name: "iPhone 15 Pro Max",
          brand: "Apple",
          price: 30000000,
          stock: 8,
          visible: true,
          images: ["https://picsum.photos/seed/2/300/300"]
        },
        {
          id: "p3",
          name: "Xiaomi 14 Ultra", 
          brand: "Xiaomi",
          price: 20000000,
          stock: 15,
          visible: true,
          images: ["https://picsum.photos/seed/3/300/300"]
        },
        {
          id: "p4",
          name: "OPPO Find X7 Pro",
          brand: "OPPO", 
          price: 18000000,
          stock: 12,
          visible: true,
          images: ["https://picsum.photos/seed/4/300/300"]
        }
      ],
      carts: [],
      users: [],
      orders: []
    };
    
    saveData(db);
    
    res.json({
      success: true,
      message: "Database reset successfully - all test data cleared",
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Database reset failed",
      error: error.message
    });
  }
});

// API để backup dữ liệu
app.post("/api/backup", (req, res) => {
  try {
    const backupFile = `data_backup_${Date.now()}.json`;
    const backupPath = path.join(__dirname, backupFile);
    fs.writeFileSync(backupPath, JSON.stringify(db, null, 2));
    
    res.json({
      success: true,
      message: "Backup created successfully",
      backupFile: backupFile,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: "Backup failed",
      error: error.message
    });
  }
});

/* =============== 🚀 SERVER START ==================== */
const PORT = 8080;
app.listen(PORT, "0.0.0.0", () => {
  console.log(`🚀 PhoneShop API Server running at http://localhost:${PORT}`);
  console.log(`📁 Data file: ${DATA_FILE}`);
  console.log(`📊 Initial data stats:`);
  console.log(`   - Users: ${db.users.length}`);
  console.log(`   - Products: ${db.products.length}`);
  console.log(`   - Carts: ${db.carts.length}`);
  console.log(`   - Orders: ${db.orders.length}`);
  console.log(`🔗 Status endpoint: http://localhost:${PORT}/api/status`);
});
