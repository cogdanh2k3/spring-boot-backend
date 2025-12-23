#!/bin/bash
# Test script to verify sensitive data encryption and storage

BASE_URL="http://localhost:8080/api/auth"

echo "=== Testing Sensitive Data Encryption Flow ==="
echo ""

# Step 1: Register a test user
echo "1. Registering test user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser_encrypt",
    "email": "testencrypt@example.com",
    "password": "password123",
    "fullName": "Test User Encryption"
  }')

echo "Register Response: $REGISTER_RESPONSE"
USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo "User ID: $USER_ID"
echo ""

# Step 2: Update profile with sensitive data
echo "2. Updating profile with sensitive data..."
UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/profile/$USER_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User Encryption",
    "gender": "Nam",
    "dateOfBirth": "01/01/1990",
    "hometown": "Ha Noi",
    "phoneNumber": "0123456789",
    "cccd": "001234567890",
    "cccdIssueDate": "01/01/2020",
    "cccdIssuePlace": "Ha Noi Police Department"
  }')

echo "Update Response:"
echo $UPDATE_RESPONSE | jq '.'
echo ""

# Step 3: Get user profile to verify data was saved
echo "3. Getting user profile..."
GET_RESPONSE=$(curl -s -X GET "$BASE_URL/user/testuser_encrypt")

echo "Get Response:"
echo $GET_RESPONSE | jq '.'
echo ""

# Step 4: Check if sensitive fields are encrypted
echo "4. Checking encryption status..."
PHONE=$(echo $GET_RESPONSE | jq -r '.phoneNumber')
CCCD=$(echo $GET_RESPONSE | jq -r '.cccd')

if [[ $PHONE == *":"* ]]; then
  echo "✓ phoneNumber is ENCRYPTED: ${PHONE:0:50}..."
else
  echo "✗ phoneNumber is NOT encrypted: $PHONE"
fi

if [[ $CCCD == *":"* ]]; then
  echo "✓ cccd is ENCRYPTED: ${CCCD:0:50}..."
else
  echo "✗ cccd is NOT encrypted: $CCCD"
fi

echo ""
echo "=== Test Complete ==="
