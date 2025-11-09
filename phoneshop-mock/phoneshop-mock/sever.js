const express = require("express");
const cors = require("cors");
const app = express();
app.use(cors());
app.use(express.json());

let products = Array.from({ length: 57 }).map((_, i) => ({
  id: `p${i + 1}`,
  name: `Phone ${i + 1}`,
  brand: ["Samsung", "Apple", "Xiaomi", "OPPO"][i % 4],
  price: 5000000 + i * 250000,
  stock: 5 + (i % 20),
  // some products can be hidden from public; adjust as needed
  visible: i % 3 !== 0,
  images: [`https://picsum.photos/seed/${i}/300/300`],
}));

app.get("/admin/products", (req, res) => {
  let { page = 0, size = 20, q = "", brand, sort } = req.query;
  page = parseInt(page, 10);
  size = parseInt(size, 10);

  let data = products.slice();
  if (q)
    data = data.filter((p) => p.name.toLowerCase().includes(q.toLowerCase()));
  if (brand)
    data = data.filter((p) => p.brand.toLowerCase() === brand.toLowerCase());

  if (sort === "price,asc") data.sort((a, b) => a.price - b.price);
  else if (sort === "price,desc") data.sort((a, b) => b.price - a.price);

  const totalElements = data.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const content = data.slice(start, start + size);

  res.json({ content, page, size, totalPages, totalElements });
});

// Lấy chi tiết sản phẩm theo ID
app.get("/admin/products/:id", (req, res) => {
  const product = products.find((p) => p.id === req.params.id);
  if (!product) {
    return res.status(404).json({ message: "Product not found" });
  }

  // Thêm thông tin chi tiết cho sản phẩm
  const detailedProduct = {
    ...product,
    description: `Mô tả chi tiết cho ${product.name}. Đây là một sản phẩm chất lượng cao từ thương hiệu ${product.brand}. Sản phẩm được thiết kế với công nghệ tiên tiến, mang lại trải nghiệm tuyệt vời cho người dùng.`,
    category: "Điện thoại di động",
    rating: 4.5,
    reviewCount: 100,
    specifications: [
      "Màn hình: 6.5 inch AMOLED",
      "RAM: 8GB",
      "Bộ nhớ: 128GB",
      "Camera: 48MP",
      "Pin: 5000mAh",
      "Hệ điều hành: Android 14",
    ],
    videoUrl: "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    isFlashSale: product.id === "p1" || product.id === "p2",
    flashSalePrice:
      product.id === "p1" || product.id === "p2"
        ? Math.round(product.price * 0.8)
        : null,
  };

  res.json(detailedProduct);
});

app.post("/admin/products", (req, res) => {
  const { name, brand, price, stock, images = [] } = req.body || {};
  if (
    !name ||
    !brand ||
    typeof price !== "number" ||
    typeof stock !== "number"
  ) {
    return res.status(400).json({ message: "Invalid payload" });
  }
  const id = `p${products.length + 1}`;
  const item = { id, name, brand, price, stock, visible: true, images };
  products.unshift(item);
  res.status(201).json(item);
});

app.put("/admin/products/:id", (req, res) => {
  const idx = products.findIndex((p) => p.id === req.params.id);
  if (idx < 0) return res.status(404).json({ message: "Not found" });
  const { name, brand, price, stock, visible, images } = req.body || {};
  products[idx] = {
    ...products[idx],
    ...(name !== undefined ? { name } : {}),
    ...(brand !== undefined ? { brand } : {}),
    ...(price !== undefined ? { price } : {}),
    ...(stock !== undefined ? { stock } : {}),
    ...(visible !== undefined ? { visible } : {}),
    ...(images !== undefined ? { images } : {}),
  };
  res.json(products[idx]);
});

app.delete("/admin/products/:id", (req, res) => {
  const before = products.length;
  products = products.filter((p) => p.id !== req.params.id);
  if (products.length === before)
    return res.status(404).json({ message: "Not found" });
  res.status(204).send();
});

// ----------------- PUBLIC ROUTES (for app users) -----------------
// Public endpoints will use the same behavior as admin by default
// If you want public to see only visible products, change the filter in these handlers

app.get("/api/products", (req, res) => {
  let { page = 0, size = 20, q = "", brand, sort } = req.query;
  page = parseInt(page, 10);
  size = parseInt(size, 10);

  // To make public see the same products as admin, do not filter by visible here.
  // If you prefer to hide non-visible items from public, uncomment the filter below.
  let data = products.slice();
  // let data = products.slice().filter(p => p.visible);

  if (q)
    data = data.filter((p) => p.name.toLowerCase().includes(q.toLowerCase()));
  if (brand)
    data = data.filter((p) => p.brand.toLowerCase() === brand.toLowerCase());

  if (sort === "price,asc") data.sort((a, b) => a.price - b.price);
  else if (sort === "price,desc") data.sort((a, b) => b.price - a.price);

  const totalElements = data.length;
  const totalPages = Math.ceil(totalElements / size);
  const start = page * size;
  const content = data.slice(start, start + size);

  res.json({ content, page, size, totalPages, totalElements });
});

app.get("/api/products/:id", (req, res) => {
  // Public sees same product details as admin by default; to restrict to visible only,
  // uncomment the visibility check in the find below.
  const item = products.find((p) => p.id === req.params.id /* && p.visible */);
  if (!item) return res.status(404).json({ message: "Not found" });
  res.json(item);
});

const PORT = process.env.PORT ? parseInt(process.env.PORT, 10) : 8080;
// Bind to 0.0.0.0 so devices on same network (phones) can access the mock server
app.listen(PORT, "0.0.0.0", () =>
  console.log(`Mock API at http://0.0.0.0:${PORT}`)
);
