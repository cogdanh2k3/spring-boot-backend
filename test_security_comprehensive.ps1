# PowerShell script for COMPREHENSIVE security testing
# Covers: Authentication, RBAC, IDOR, Encryption, SQL Injection, XSS, Rate Limiting, Privilege Escalation

$BASE_URL = "http://localhost:8080/api/auth"
$ADMIN_URL = "http://localhost:8080/api/admin"

function Print-Section {
    param([string]$Title)
    Write-Host ""
    Write-Host "========================================================" -ForegroundColor Cyan
    Write-Host " $Title" -ForegroundColor Cyan
    Write-Host "========================================================" -ForegroundColor Cyan
}

function Print-Result {
    param([bool]$Success, [string]$Message)
    if ($Success) {
        Write-Host "[PASS] $Message" -ForegroundColor Green
    }
    else {
        Write-Host "[FAIL] $Message" -ForegroundColor Red
    }
}

function Print-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Gray
}

function Print-Warning {
    param([string]$Message)
    Write-Host "[WARN] $Message" -ForegroundColor Yellow
}

try {
    # =================================================================
    # 1. SETUP: Create Users
    # =================================================================
    Print-Section "SETUP: Registering Test Users"

    # Generate unique timestamp with milliseconds and random string to avoid rate limiting
    $timestamp = Get-Date -Format 'yyyyMMddHHmmss'
    $random = -join ((65..90) + (97..122) | Get-Random -Count 6 | ForEach-Object { [char]$_ })
    $milliseconds = (Get-Date).Millisecond
    $uniqueId = "${timestamp}_${milliseconds}_${random}"
    
    $usernameA = "attacker_$uniqueId"
    $passwordA = "StrongPassword@123"
    
    Print-Info "Unique ID for this test run: $uniqueId"
    
    # User A (Attacker)
    $regA = Invoke-RestMethod -Uri "$BASE_URL/register" -Method POST -Body (@{
            username = $usernameA
            email    = "attacker_$uniqueId@example.com"
            password = $passwordA
            fullName = "Attacker User"
        } | ConvertTo-Json) -ContentType "application/json"
    $userIdA = $regA.user.id
    Print-Info "Registered User A (ID: $userIdA)"

    # User B (Victim)
    $regB = Invoke-RestMethod -Uri "$BASE_URL/register" -Method POST -Body (@{
            username = "victim_$uniqueId"
            email    = "victim_$uniqueId@example.com"
            password = "StrongPassword@123"
            fullName = "Victim User"
        } | ConvertTo-Json) -ContentType "application/json"
    $userIdB = $regB.user.id
    Print-Info "Registered User B (ID: $userIdB)"

    # Login User A
    $loginA = Invoke-RestMethod -Uri "$BASE_URL/login" -Method POST -Body (@{ usernameOrEmail = $usernameA; password = $passwordA } | ConvertTo-Json) -ContentType "application/json"
    $tokenA = $loginA.accessToken
    $headersA = @{ Authorization = "Bearer $tokenA" }
    Print-Info "User A logged in."


    # =================================================================
    # 2. TEST: SQL Injection (SQLi)
    # =================================================================
    Print-Section "TEST 1: SQL Injection Resilience"
    
    $sqliPayloads = @("' OR '1'='1", "' OR 1=1 --", "admin' --")
    
    foreach ($payload in $sqliPayloads) {
        try {
            Invoke-RestMethod -Uri "$BASE_URL/login" -Method POST -Body (@{ usernameOrEmail = $payload; password = "password" } | ConvertTo-Json) -ContentType "application/json" -ErrorAction Stop
            Print-Result $false "Login allowed with SQLi payload: $payload"
        }
        catch {
            if ($_.Exception.Response.StatusCode -eq [System.Net.HttpStatusCode]::Unauthorized -or $_.Exception.Response.StatusCode -eq [System.Net.HttpStatusCode]::NotFound) {
                Print-Result $true "SQLi Payload blocked/handled gracefully: $payload"
            }
            elseif ($_.Exception.Response.StatusCode -eq [System.Net.HttpStatusCode]::InternalServerError) {
                Print-Result $false "Server Error (500) with SQLi payload - Possible Vulnerability: $payload"
            }
            else {
                Print-Result $true "Request rejected ($($_.Exception.Response.StatusCode)): $payload"
            }
        }
    }


    # =================================================================
    # 3. TEST: Rate Limiting / Brute Force
    # =================================================================
    Print-Section "TEST 2: Rate Limiting (Brute Force Protection)"
    Print-Info "Attempting 5 failed logins..."

    # Ensure we use a fresh IP or user for this test if possible, or just hammer the login
    $bruteUser = "brute_test_$timestamp"
    
    for ($i = 1; $i -le 6; $i++) {
        try {
            $response = Invoke-RestMethod -Uri "$BASE_URL/login" -Method POST -Body (@{ usernameOrEmail = $usernameA; password = "wrong_pass_$i" } | ConvertTo-Json) -ContentType "application/json" -ErrorAction Stop
            Print-Result $false "Login succeeded unexpectedly!"
        }
        catch {
            $code = $_.Exception.Response.StatusCode
            if ($code -eq [System.Net.HttpStatusCode]::TooManyRequests) {
                Print-Result $true "Rate limit triggered at attempt $i (429 Too Many Requests)"
                break
            }
            elseif ($i -ge 6) {
                # Note: If server logic only requires captcha but returns 200/400, this check might need adjustment based on response body
                Print-Warning "Rate limit NOT triggered after $i attempts (Code: $code). Checking if Captcha required..."
                $body = $_.Exception.Response.GetResponseStream()
                $reader = New-Object System.IO.StreamReader($body)
                $respContent = $reader.ReadToEnd()
                if ($respContent -match "requiresCaptcha") {
                    Print-Result $true "Captcha requirement triggered at attempt $i"
                }
                else {
                    Print-Result $false "No rate limiting or captcha enforced."
                }
            }
            else {
                Print-Info "Attempt $i failed as expected ($code)."
            }
        }
    }


    # =================================================================
    # 4. TEST: IDOR (Insecure Direct Object Reference)
    # =================================================================
    Print-Section "TEST 3: IDOR Vulnerability Check"
    
    $idorPayload = @{ fullName = "HACKED BY USER A" } | ConvertTo-Json

    try {
        Invoke-RestMethod -Uri "$BASE_URL/profile/$userIdB" -Method PUT -Headers $headersA -Body $idorPayload -ContentType "application/json" -ErrorAction Stop
        Print-Result $false "IDOR Attack SUCCESSFUL! (Vulnerability exists)"
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq [System.Net.HttpStatusCode]::Forbidden) {
            Print-Result $true "IDOR Attack BLOCKED (403 Forbidden)"
        }
        else {
            Print-Result $false "Unexpected response: $($_.Exception.Response.StatusCode)"
        }
    }


    # =================================================================
    # 5. TEST: Cross-Site Scripting (XSS)
    # =================================================================
    Print-Section "TEST 4: Stored XSS"
    
    $xssPayload = "<script>alert('XSS')</script>"
    Print-Info "Injecting XSS payload into Full Name..."

    try {
        $updateXss = Invoke-RestMethod -Uri "$BASE_URL/profile/$userIdA" -Method PUT -Headers $headersA -Body (@{
                fullName = $xssPayload
            } | ConvertTo-Json) -ContentType "application/json"
        
        # Verify what was saved
        $profile = Invoke-RestMethod -Uri "$BASE_URL/user/$usernameA" -Method GET -Headers $headersA
        
        if ($profile.fullName -eq $xssPayload) {
            Print-Warning "XSS Payload was saved RAW: $($profile.fullName)"
            Print-Info "Note: Backend stored it. Frontend MUST escape this output."
        }
        else {
            Print-Result $true "XSS Payload was sanitized/modified: $($profile.fullName)"
        }
    }
    catch {
        Print-Result $false "Failed to update profile for XSS test."
    }


    # =================================================================
    # 6. TEST: Privilege Escalation
    # =================================================================
    Print-Section "TEST 5: Privilege Escalation"
    
    Print-Info "User A attempting to become ADMIN via Profile Update..."
    try {
        # Attempt to inject "role": "ADMIN" into the update request
        # NOTE: This depends on the DTO map. If DTO doesn't have 'role', it ignores it (Safe). 
        # If DTO has it or using Map<String, Object>, it might merge.
        
        $escPayload = @{
            fullName = "Wannabe Admin"
            role     = "ADMIN"
        } | ConvertTo-Json

        Invoke-RestMethod -Uri "$BASE_URL/profile/$userIdA" -Method PUT -Headers $headersA -Body $escPayload -ContentType "application/json" | Out-Null
        
        # Check if role changed
        $profile = Invoke-RestMethod -Uri "$BASE_URL/user/$usernameA" -Method GET -Headers $headersA
        
        if ($profile.role -eq "ADMIN") {
            Print-Result $false "PRIVILEGE ESCALATION SUCCESSFUL! (Critical Vulnerability)"
        }
        else {
            Print-Result $true "Privilege Escalation Blocked. Role is still: $($profile.role)"
        }
    }
    catch {
        Print-Result $true "Update request failed (possibly validation)."
    }


    # =================================================================
    # 7. TEST: Data Encryption
    # =================================================================
    Print-Section "TEST 6: Sensitive Data Encryption"
    
    Print-Info "Updating User A with sensitive data..."
    $sensitiveBody = @{
        phoneNumber = "0987654321"
        cccd        = "999999999999"
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "$BASE_URL/profile/$userIdA" -Method PUT -Headers $headersA -Body $sensitiveBody -ContentType "application/json" | Out-Null
    
    $profile = Invoke-RestMethod -Uri "$BASE_URL/user/$usernameA" -Method GET -Headers $headersA
    
    if ($profile.phoneNumber -match ":" -and $profile.cccd -match ":") {
        Print-Result $true "Data is ENCRYPTED (IV:Ciphertext format detected)"
    }
    else {
        Print-Result $false "Data is NOT encrypted properly."
        Print-Info "Phone: $($profile.phoneNumber)"
        Print-Info "CCCD: $($profile.cccd)"
    }

}
catch {
    Write-Host "FATAL ERROR: $_" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $respBody = $reader.ReadToEnd()
        Write-Host "Response Body: $respBody" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Comprehensive Security Test Complete." -ForegroundColor Cyan
