# PowerShell script to test sensitive data encryption

$BASE_URL = "http://localhost:8080/api/auth"

Write-Host "=== Testing Sensitive Data Encryption Flow ===" -ForegroundColor Cyan
Write-Host ""

# Step 1: Register a test user
Write-Host "1. Registering test user..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser_encrypt_$(Get-Date -Format 'yyyyMMddHHmmss')"
    email = "testencrypt_$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"
    password = "password123"
    fullName = "Test User Encryption"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$BASE_URL/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "Register Success!" -ForegroundColor Green
    $userId = $registerResponse.user.id
    $username = $registerResponse.user.username
    Write-Host "User ID: $userId" -ForegroundColor Green
    Write-Host "Username: $username" -ForegroundColor Green
    Write-Host ""

    # Step 2: Update profile with sensitive data
    Write-Host "2. Updating profile with sensitive data..." -ForegroundColor Yellow
    $updateBody = @{
        fullName = "Test User Encryption"
        gender = "Nam"
        dateOfBirth = "01/01/1990"
        hometown = "Ha Noi"
        phoneNumber = "0123456789"
        cccd = "001234567890"
        cccdIssueDate = "01/01/2020"
        cccdIssuePlace = "Ha Noi Police Department"
    } | ConvertTo-Json

    $updateResponse = Invoke-RestMethod -Uri "$BASE_URL/profile/$userId" `
        -Method PUT `
        -ContentType "application/json" `
        -Body $updateBody
    
    Write-Host "Update Success!" -ForegroundColor Green
    Write-Host ""

    # Step 3: Get user profile to verify data was saved
    Write-Host "3. Getting user profile..." -ForegroundColor Yellow
    $getResponse = Invoke-RestMethod -Uri "$BASE_URL/user/$username" `
        -Method GET

    Write-Host "Get Success!" -ForegroundColor Green
    Write-Host ""

    # Step 4: Display results
    Write-Host "=== Profile Data ===" -ForegroundColor Cyan
    Write-Host "Full Name: $($getResponse.fullName)"
    Write-Host "Gender: $($getResponse.gender)"
    Write-Host "Date of Birth: $($getResponse.dateOfBirth)"
    Write-Host "Hometown: $($getResponse.hometown)"
    Write-Host ""

    Write-Host "=== Sensitive Fields (Should be Encrypted) ===" -ForegroundColor Cyan
    Write-Host "Phone Number: $($getResponse.phoneNumber)"
    Write-Host "CCCD: $($getResponse.cccd)"
    Write-Host "CCCD Issue Date: $($getResponse.cccdIssueDate)"
    Write-Host "CCCD Issue Place: $($getResponse.cccdIssuePlace)"
    Write-Host ""

    # Step 5: Verify encryption
    Write-Host "=== Encryption Verification ===" -ForegroundColor Cyan
    
    if ($getResponse.phoneNumber -and $getResponse.phoneNumber -match ":") {
        Write-Host "[✓] phoneNumber is ENCRYPTED" -ForegroundColor Green
        Write-Host "    Format check: Contains ':' separators" -ForegroundColor Gray
    } elseif ($getResponse.phoneNumber) {
        Write-Host "[✗] phoneNumber is NOT encrypted!" -ForegroundColor Red
        Write-Host "    Value: $($getResponse.phoneNumber)" -ForegroundColor Red
    } else {
        Write-Host "[!] phoneNumber is NULL or empty" -ForegroundColor Yellow
    }

    if ($getResponse.cccd -and $getResponse.cccd -match ":") {
        Write-Host "[✓] cccd is ENCRYPTED" -ForegroundColor Green
        Write-Host "    Format check: Contains ':' separators" -ForegroundColor Gray
    } elseif ($getResponse.cccd) {
        Write-Host "[✗] cccd is NOT encrypted!" -ForegroundColor Red
        Write-Host "    Value: $($getResponse.cccd)" -ForegroundColor Red
    } else {
        Write-Host "[!] cccd is NULL or empty" -ForegroundColor Yellow
    }

    if ($getResponse.cccdIssueDate -and $getResponse.cccdIssueDate -match ":") {
        Write-Host "[✓] cccdIssueDate is ENCRYPTED" -ForegroundColor Green
    } elseif ($getResponse.cccdIssueDate) {
        Write-Host "[✗] cccdIssueDate is NOT encrypted!" -ForegroundColor Red
    } else {
        Write-Host "[!] cccdIssueDate is NULL or empty" -ForegroundColor Yellow
    }

    if ($getResponse.cccdIssuePlace -and $getResponse.cccdIssuePlace -match ":") {
        Write-Host "[✓] cccdIssuePlace is ENCRYPTED" -ForegroundColor Green
    } elseif ($getResponse.cccdIssuePlace) {
        Write-Host "[✗] cccdIssuePlace is NOT encrypted!" -ForegroundColor Red
    } else {
        Write-Host "[!] cccdIssuePlace is NULL or empty" -ForegroundColor Yellow
    }

} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
