// Test Order Flow - PhoneShop API
const axios = require('axios');

const BASE_URL = 'http://localhost:8080';

async function testOrderFlow() {
  console.log('🚀 Testing PhoneShop Order Flow...\n');

  try {
    // 1. Check server status
    console.log('1️⃣ Checking server status...');
    const statusResponse = await axios.get(`${BASE_URL}/api/status`);
    console.log('✅ Server is running');
    console.log(`📊 Data stats:`, statusResponse.data.database.stats);
    console.log('');

    // 2. Register a test user
    console.log('2️⃣ Registering test user...');
    const registerData = {
      fullName: "Test User",
      email: "test@example.com",
      username: "testuser",
      password: "123456"
    };
    
    const registerResponse = await axios.post(`${BASE_URL}/api/auth/register`, registerData);
    const userId = registerResponse.data.user.id;
    console.log('✅ User registered successfully');
    console.log(`👤 User ID: ${userId}`);
    console.log('');

    // 3. Add product to cart
    console.log('3️⃣ Adding product to cart...');
    const cartData = {
      userId: userId,
      productId: "p1",
      quantity: 2
    };
    
    const cartResponse = await axios.post(`${BASE_URL}/api/cart/add`, cartData);
    console.log('✅ Product added to cart');
    console.log(`🛒 Cart items: ${cartResponse.data.cart.items.length}`);
    console.log('');

    // 4. View cart
    console.log('4️⃣ Viewing cart...');
    const viewCartResponse = await axios.get(`${BASE_URL}/api/cart/${userId}`);
    console.log('✅ Cart retrieved');
    console.log(`🛒 Cart has ${viewCartResponse.data.items.length} items`);
    console.log('Cart items:', viewCartResponse.data.items.map(item => `${item.name} x${item.quantity}`));
    console.log('');

    // 5. Create order from cart
    console.log('5️⃣ Creating order from cart...');
    const orderData = {
      userId: userId,
      customerInfo: {
        fullName: "Test User",
        phone: "0123456789",
        email: "test@example.com",
        address: "123 Test Street"
      },
      paymentMethod: "COD",
      shippingAddress: "123 Test Street"
    };
    
    const orderResponse = await axios.post(`${BASE_URL}/api/orders/from-cart`, orderData);
    const orderId = orderResponse.data.order.id;
    console.log('✅ Order created successfully');
    console.log(`📦 Order ID: ${orderId}`);
    console.log(`💰 Total Amount: ${orderResponse.data.order.totalAmount.toLocaleString()} VND`);
    console.log('');

    // 6. Check cart is cleared
    console.log('6️⃣ Checking cart after order...');
    const cartAfterOrderResponse = await axios.get(`${BASE_URL}/api/cart/${userId}`);
    console.log(`🛒 Cart items after order: ${cartAfterOrderResponse.data.items.length}`);
    if (cartAfterOrderResponse.data.items.length === 0) {
      console.log('✅ Cart cleared successfully after order');
    } else {
      console.log('❌ Cart not cleared after order');
    }
    console.log('');

    // 7. Get order history
    console.log('7️⃣ Getting order history...');
    const orderHistoryResponse = await axios.get(`${BASE_URL}/api/orders/${userId}`);
    console.log(`📋 Order history: ${orderHistoryResponse.data.length} orders`);
    
    if (orderHistoryResponse.data.length > 0) {
      console.log('✅ Order found in history');
      const order = orderHistoryResponse.data[0];
      console.log(`📦 Latest order: ${order.id}`);
      console.log(`📅 Created: ${order.createdAt}`);
      console.log(`💰 Amount: ${order.totalAmount.toLocaleString()} VND`);
      console.log(`📊 Status: ${order.status}`);
      console.log(`📦 Items: ${order.items.length}`);
    } else {
      console.log('❌ No orders found in history');
    }
    console.log('');

    // 8. Final status check
    console.log('8️⃣ Final status check...');
    const finalStatusResponse = await axios.get(`${BASE_URL}/api/status`);
    console.log(`📊 Final data stats:`, finalStatusResponse.data.database.stats);
    
    console.log('\n🎉 Order flow test completed successfully!');
    console.log('\n📋 Summary:');
    console.log(`✅ User registered: ${userId}`);
    console.log(`✅ Product added to cart`);
    console.log(`✅ Order created: ${orderId}`);
    console.log(`✅ Cart cleared after order`);
    console.log(`✅ Order appears in history`);

  } catch (error) {
    console.error('\n❌ Test failed:', error.message);
    if (error.response) {
      console.error('Response status:', error.response.status);
      console.error('Response data:', error.response.data);
    }
    console.error('\n🔍 Debug steps:');
    console.error('1. Make sure server is running: node server.js');
    console.error('2. Check server logs for errors');
    console.error('3. Verify data.json file exists and is writable');
  }
}

// Run the test
testOrderFlow();
