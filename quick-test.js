// Quick test for order creation with items in request
const axios = require('axios');

const BASE_URL = 'http://localhost:8080';

async function quickTest() {
  console.log('🚀 Quick Order Test...\n');

  try {
    // Test order creation with items in request (like Android sends)
    console.log('📦 Testing order creation with items in request...');
    
    const orderData = {
      "address": "wqe",
      "customerInfo": {
        "address": "wqe", 
        "email": "",
        "fullName": "qw",
        "phone": "1"
      },
      "fullName": "qw",
      "items": [
        {
          "price": 5500000,
          "productId": "p3",
          "quantity": 1
        }
      ],
      "paymentMethod": "COD",
      "phone": "1",
      "shippingAddress": "wqe",
      "userId": "user_1762879316873_vcrgn96gb"
    };
    
    const orderResponse = await axios.post(`${BASE_URL}/api/orders/from-cart`, orderData);
    
    if (orderResponse.data.success) {
      console.log('✅ Order created successfully!');
      console.log(`📦 Order ID: ${orderResponse.data.order.id}`);
      console.log(`💰 Total: ${orderResponse.data.order.totalAmount.toLocaleString()} VND`);
      
      // Check if order appears in history
      console.log('\n📋 Checking order history...');
      const historyResponse = await axios.get(`${BASE_URL}/api/orders/${orderData.userId}`);
      
      console.log(`📋 Found ${historyResponse.data.length} orders in history`);
      if (historyResponse.data.length > 0) {
        console.log('✅ Order appears in history!');
        const latestOrder = historyResponse.data[0];
        console.log(`📦 Latest order: ${latestOrder.id}`);
        console.log(`📅 Created: ${latestOrder.createdAt}`);
        console.log(`📊 Status: ${latestOrder.status}`);
      } else {
        console.log('❌ Order not found in history');
      }
      
    } else {
      console.log('❌ Order creation failed:', orderResponse.data.message);
    }

  } catch (error) {
    console.error('❌ Test failed:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
}

quickTest();
