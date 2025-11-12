// Test Order API Response Format
const axios = require('axios');

const BASE_URL = 'http://localhost:8080';

async function testOrderAPI() {
  console.log('🧪 Testing Order API Response Format...\n');

  try {
    // Test with existing user ID from your logs
    const userId = 'user_1762879316873_vcrgn96gb';
    
    console.log(`📋 Testing GET /api/orders/${userId}`);
    const response = await axios.get(`${BASE_URL}/api/orders/${userId}`);
    
    console.log(`✅ Response received - ${response.data.length} orders`);
    
    if (response.data.length > 0) {
      const order = response.data[0];
      console.log('\n📦 Order Response Format:');
      console.log('----------------------------');
      console.log(`🆔 id: ${order.id}`);
      console.log(`🆔 orderId: ${order.orderId}`);
      console.log(`📅 orderDate: ${order.orderDate}`);
      console.log(`📊 status: ${order.status}`);
      console.log(`💰 totalPrice: ${order.totalPrice}`);
      console.log(`📦 itemCount: ${order.itemCount}`);
      console.log(`👤 fullName: ${order.fullName}`);
      console.log(`📞 phone: ${order.phone}`);
      console.log(`📍 address: ${order.address}`);
      console.log(`💳 paymentMethod: ${order.paymentMethod}`);
      console.log('----------------------------');
      
      // Check Android-expected fields
      console.log('\n✅ Android Field Validation:');
      console.log(`orderId: ${order.orderId ? '✅' : '❌'} ${order.orderId || 'MISSING'}`);
      console.log(`orderDate: ${order.orderDate ? '✅' : '❌'} ${order.orderDate || 'MISSING'}`);
      console.log(`totalPrice: ${order.totalPrice !== undefined ? '✅' : '❌'} ${order.totalPrice !== undefined ? order.totalPrice : 'MISSING'}`);
      console.log(`itemCount: ${order.itemCount !== undefined ? '✅' : '❌'} ${order.itemCount !== undefined ? order.itemCount : 'MISSING'}`);
      console.log(`status: ${order.status ? '✅' : '❌'} ${order.status || 'MISSING'}`);
      
      if (order.orderId && order.orderDate && order.totalPrice !== undefined && order.itemCount !== undefined && order.status) {
        console.log('\n🎉 All Android-expected fields are present!');
      } else {
        console.log('\n❌ Some Android-expected fields are missing!');
      }
      
      // Show items if available
      if (order.items && order.items.length > 0) {
        console.log(`\n📦 Order Items (${order.items.length}):`);
        order.items.forEach((item, index) => {
          console.log(`  ${index + 1}. ${item.name} x${item.quantity} = ${item.price.toLocaleString()} VND`);
        });
      }
      
    } else {
      console.log('❌ No orders found for this user');
      console.log('💡 Try creating an order first or check the userId');
    }

  } catch (error) {
    console.error('❌ API Test failed:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
}

// Test order creation response format
async function testOrderCreation() {
  console.log('\n🧪 Testing Order Creation Response Format...\n');
  
  try {
    const orderData = {
      "userId": "user_1762879316873_vcrgn96gb",
      "customerInfo": {
        "fullName": "Test User",
        "phone": "0123456789",
        "email": "test@example.com",
        "address": "123 Test Street"
      },
      "items": [
        {
          "productId": "p3",
          "price": 5500000,
          "quantity": 1
        }
      ],
      "paymentMethod": "COD",
      "shippingAddress": "123 Test Street"
    };
    
    console.log('📦 Creating test order...');
    const response = await axios.post(`${BASE_URL}/api/orders/from-cart`, orderData);
    
    if (response.data.success) {
      const order = response.data.order;
      console.log('✅ Order created successfully!');
      console.log('\n📦 Creation Response Format:');
      console.log('----------------------------');
      console.log(`🆔 orderId: ${order.orderId}`);
      console.log(`📅 orderDate: ${order.orderDate}`);
      console.log(`💰 totalPrice: ${order.totalPrice}`);
      console.log(`📦 itemCount: ${order.itemCount}`);
      console.log(`📊 status: ${order.status}`);
      console.log('----------------------------');
      
      return order.orderId;
    }
    
  } catch (error) {
    console.error('❌ Order creation test failed:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
}

// Run tests
async function runTests() {
  await testOrderAPI();
  await testOrderCreation();
}

runTests();
