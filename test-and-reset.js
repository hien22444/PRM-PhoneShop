// Test script để kiểm tra và reset database
const fs = require('fs');
const path = require('path');

const DATA_FILE = path.join(__dirname, 'data.json');

console.log('🔍 Checking current data.json...');

// Kiểm tra file hiện tại
if (fs.existsSync(DATA_FILE)) {
  const data = JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
  
  console.log('📊 Current database stats:');
  console.log(`   - Users: ${data.users ? data.users.length : 0}`);
  console.log(`   - Products: ${data.products ? data.products.length : 0}`);
  console.log(`   - Carts: ${data.carts ? data.carts.length : 0}`);
  console.log(`   - Orders: ${data.orders ? data.orders.length : 0}`);
  
  if (data.orders && data.orders.length > 0) {
    console.log('\n📦 Existing orders:');
    data.orders.forEach(order => {
      console.log(`   - ${order.id} (User: ${order.userId}) - ${order.status}`);
    });
  }
  
  if (data.carts && data.carts.length > 0) {
    console.log('\n🛒 Existing carts:');
    data.carts.forEach(cart => {
      console.log(`   - User ${cart.userId}: ${cart.items.length} items`);
    });
  }
} else {
  console.log('❌ data.json does not exist yet');
}

// Reset database
console.log('\n🔄 Resetting database...');

const cleanData = {
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

fs.writeFileSync(DATA_FILE, JSON.stringify(cleanData, null, 2));

console.log('✅ Database reset successfully!');
console.log('📁 Clean data.json created with:');
console.log(`   - ${cleanData.products.length} products`);
console.log('   - 0 users');
console.log('   - 0 carts'); 
console.log('   - 0 orders');

console.log('\n🚀 You can now start the server with: node phoneshop-server-updated.js');
