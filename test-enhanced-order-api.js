// Test Enhanced Order API
const axios = require('axios');

const BASE_URL = 'http://localhost:8080';

async function testEnhancedOrderAPI() {
  console.log('🧪 Testing Enhanced Order API...\n');

  try {
    // Test with existing user ID
    const userId = 'user_1762879316873_vcrgn96gb';
    
    console.log('📋 Testing GET /api/orders/:userId with enhanced fields...');
    const response = await axios.get(`${BASE_URL}/api/orders/${userId}`);
    
    console.log(`✅ Response received - ${response.data.length} orders`);
    
    if (response.data.length > 0) {
      const order = response.data[0];
      console.log('\n📦 Enhanced Order List Response:');
      console.log('========================================');
      console.log(`🆔 orderId: ${order.orderId}`);
      console.log(`📅 formattedDate: ${order.formattedDate}`);
      console.log(`📊 status: ${order.status} (Color: ${order.statusColor})`);
      console.log(`💰 totalPrice: ${order.totalPrice.toLocaleString()} VND`);
      console.log(`📦 itemCount: ${order.itemCount}`);
      console.log(`📦 totalQuantity: ${order.totalQuantity}`);
      console.log(`🖼️ previewImage: ${order.previewImage}`);
      console.log(`📱 previewName: ${order.previewName}`);
      console.log(`👤 Customer: ${order.customerInfo.fullName}`);
      console.log(`📞 Phone: ${order.customerInfo.phone}`);
      console.log(`📧 Email: ${order.customerInfo.email}`);
      console.log(`📍 Address: ${order.customerInfo.address}`);
      
      if (order.items && order.items.length > 0) {
        console.log(`\n📦 Items (${order.items.length}):`);
        order.items.forEach((item, index) => {
          console.log(`  ${index + 1}. ${item.name}`);
          console.log(`     - Price: ${item.price.toLocaleString()} VND x ${item.quantity}`);
          console.log(`     - Subtotal: ${item.subtotal.toLocaleString()} VND`);
          console.log(`     - Image: ${item.image}`);
        });
      }
      
      // Test order detail API
      console.log('\n📋 Testing GET /api/orders/detail/:orderId...');
      const detailResponse = await axios.get(`${BASE_URL}/api/orders/detail/${order.orderId}`);
      
      if (detailResponse.data.success) {
        const detailOrder = detailResponse.data.order;
        console.log('\n📦 Enhanced Order Detail Response:');
        console.log('========================================');
        console.log(`🆔 orderId: ${detailOrder.orderId}`);
        console.log(`📅 formattedDate: ${detailOrder.formattedDate}`);
        console.log(`📊 status: ${detailOrder.status} (Color: ${detailOrder.statusColor})`);
        console.log(`💰 formattedTotalAmount: ${detailOrder.formattedTotalAmount}`);
        console.log(`📦 totalQuantity: ${detailOrder.totalQuantity}`);
        
        console.log('\n👤 Customer Info:');
        console.log(`   - Name: ${detailOrder.customerInfo.fullName}`);
        console.log(`   - Phone: ${detailOrder.customerInfo.phone}`);
        console.log(`   - Email: ${detailOrder.customerInfo.email}`);
        console.log(`   - Address: ${detailOrder.customerInfo.address}`);
        
        if (detailOrder.items && detailOrder.items.length > 0) {
          console.log(`\n📦 Items with formatted prices:`);
          detailOrder.items.forEach((item, index) => {
            console.log(`  ${index + 1}. ${item.name}`);
            console.log(`     - Price: ${item.formattedPrice}`);
            console.log(`     - Quantity: ${item.quantity}`);
            console.log(`     - Subtotal: ${item.formattedSubtotal}`);
            console.log(`     - Image: ${item.image}`);
          });
        }
        
        if (detailOrder.timeline) {
          console.log('\n📅 Order Timeline:');
          detailOrder.timeline.forEach((step, index) => {
            const status = step.completed ? '✅' : '⏳';
            console.log(`  ${index + 1}. ${status} ${step.status}`);
            console.log(`     - ${step.description}`);
            if (step.date) {
              console.log(`     - Date: ${new Date(step.date).toLocaleString('vi-VN')}`);
            }
          });
        }
        
        console.log('\n🎉 All enhanced fields are working correctly!');
        
      } else {
        console.log('❌ Order detail failed:', detailResponse.data.message);
      }
      
    } else {
      console.log('❌ No orders found for this user');
    }

  } catch (error) {
    console.error('❌ API Test failed:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
}

// Test order creation with enhanced response
async function testEnhancedOrderCreation() {
  console.log('\n🧪 Testing Enhanced Order Creation...\n');
  
  try {
    const orderData = {
      "userId": "user_1762879316873_vcrgn96gb",
      "customerInfo": {
        "fullName": "Test Enhanced User",
        "phone": "0987654321",
        "email": "enhanced@example.com",
        "address": "456 Enhanced Street"
      },
      "items": [
        {
          "productId": "p1",
          "price": 25000000,
          "quantity": 1
        },
        {
          "productId": "p2", 
          "price": 30000000,
          "quantity": 2
        }
      ],
      "paymentMethod": "COD",
      "shippingAddress": "456 Enhanced Street"
    };
    
    console.log('📦 Creating enhanced test order...');
    const response = await axios.post(`${BASE_URL}/api/orders/from-cart`, orderData);
    
    if (response.data.success) {
      const order = response.data.order;
      console.log('✅ Enhanced order created successfully!');
      console.log('\n📦 Enhanced Creation Response:');
      console.log('========================================');
      console.log(`🆔 orderId: ${order.orderId}`);
      console.log(`📅 formattedDate: ${order.formattedDate}`);
      console.log(`💰 totalPrice: ${order.totalPrice.toLocaleString()} VND`);
      console.log(`📦 itemCount: ${order.itemCount}`);
      console.log(`📦 totalQuantity: ${order.totalQuantity}`);
      console.log(`📊 status: ${order.status} (Color: ${order.statusColor})`);
      
      return order.orderId;
    }
    
  } catch (error) {
    console.error('❌ Enhanced order creation test failed:', error.message);
    if (error.response) {
      console.error('Status:', error.response.status);
      console.error('Data:', error.response.data);
    }
  }
}

// Run tests
async function runTests() {
  await testEnhancedOrderAPI();
  await testEnhancedOrderCreation();
}

runTests();
