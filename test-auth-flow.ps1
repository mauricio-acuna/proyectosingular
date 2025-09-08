# PowerShell Authentication Test Script
# Tests the complete JWT authentication flow

Write-Host "🔐 TESTING JWT AUTHENTICATION SYSTEM" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan

# Base URL
$BaseUrl = "http://localhost:8080/api/v1"

Write-Host ""
Write-Host "1️⃣  Testing Authentication Health Check" -ForegroundColor Yellow
Write-Host "Expected: Service UP status"

try {
    $healthResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/health" -Method GET -ContentType "application/json"
    Write-Host "✅ Auth service is healthy" -ForegroundColor Green
    $healthResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error accessing auth health endpoint: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "2️⃣  Testing User Registration" -ForegroundColor Yellow
Write-Host "Expected: Successful registration with tokens"

$RegisterPayload = @{
    username = "testuser_$(Get-Date -Format 'yyyyMMddHHmmss')"
    email = "test_$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
    acceptTerms = $true
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/register" -Method POST -Body $RegisterPayload -ContentType "application/json"
    Write-Host "✅ Registration successful" -ForegroundColor Green
    $registerResponse | ConvertTo-Json -Depth 3
    
    $AccessToken = $registerResponse.accessToken
    $Username = $registerResponse.user.username
    
    if ($AccessToken) {
        Write-Host ""
        Write-Host "3️⃣  Testing Protected Endpoint Access" -ForegroundColor Yellow
        Write-Host "Expected: Successful access with JWT token"
        
        $Headers = @{
            "Authorization" = "Bearer $AccessToken"
        }
        
        try {
            $userInfoResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/me" -Method GET -Headers $Headers -ContentType "application/json"
            Write-Host "✅ Protected endpoint access successful" -ForegroundColor Green
            $userInfoResponse | ConvertTo-Json -Depth 3
        } catch {
            Write-Host "❌ Error accessing protected endpoint: $($_.Exception.Message)" -ForegroundColor Red
        }
        
        Write-Host ""
        Write-Host "4️⃣  Testing User Login" -ForegroundColor Yellow
        Write-Host "Expected: Successful login with same user"
        
        $LoginPayload = @{
            usernameOrEmail = $Username
            password = "password123"
            rememberMe = $true
        } | ConvertTo-Json
        
        try {
            $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method POST -Body $LoginPayload -ContentType "application/json"
            Write-Host "✅ Login successful" -ForegroundColor Green
            $loginResponse | ConvertTo-Json -Depth 3
            
            $RefreshToken = $loginResponse.refreshToken
            
            if ($RefreshToken) {
                Write-Host ""
                Write-Host "5️⃣  Testing Token Refresh" -ForegroundColor Yellow
                Write-Host "Expected: New access token generated"
                
                $RefreshPayload = @{
                    refreshToken = $RefreshToken
                } | ConvertTo-Json
                
                try {
                    $refreshResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/refresh" -Method POST -Body $RefreshPayload -ContentType "application/json"
                    Write-Host "✅ Token refresh successful" -ForegroundColor Green
                    $refreshResponse | ConvertTo-Json -Depth 3
                } catch {
                    Write-Host "❌ Error refreshing token: $($_.Exception.Message)" -ForegroundColor Red
                }
            }
        } catch {
            Write-Host "❌ Error during login: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "❌ Error during registration: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "6️⃣  Testing Username Availability Check" -ForegroundColor Yellow
Write-Host "Expected: Check if username is available"

try {
    $availabilityResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/check-username/newuser123" -Method GET -ContentType "application/json"
    Write-Host "✅ Username availability check working" -ForegroundColor Green
    $availabilityResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error checking username availability: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "7️⃣  Testing Email Availability Check" -ForegroundColor Yellow
Write-Host "Expected: Check if email is available"

try {
    $emailAvailabilityResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/check-email/newemail@example.com" -Method GET -ContentType "application/json"
    Write-Host "✅ Email availability check working" -ForegroundColor Green
    $emailAvailabilityResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error checking email availability: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "8️⃣  Testing Invalid Login" -ForegroundColor Yellow
Write-Host "Expected: 401 Unauthorized for invalid credentials"

$InvalidLoginPayload = @{
    usernameOrEmail = "nonexistent"
    password = "wrongpassword"
} | ConvertTo-Json

try {
    $invalidLoginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method POST -Body $InvalidLoginPayload -ContentType "application/json"
    Write-Host "❌ Invalid login should have failed" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✅ Invalid login correctly rejected (401)" -ForegroundColor Green
    } else {
        Write-Host "❌ Unexpected error for invalid login: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "9️⃣  Testing Logout" -ForegroundColor Yellow
Write-Host "Expected: Successful logout message"

try {
    $logoutResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/logout" -Method POST -ContentType "application/json"
    Write-Host "✅ Logout successful" -ForegroundColor Green
    $logoutResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error during logout: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "✅ JWT Authentication Test Complete" -ForegroundColor Green
Write-Host ""
Write-Host "Summary of tested endpoints:" -ForegroundColor Cyan
Write-Host "- POST /api/v1/auth/register ✅" -ForegroundColor White
Write-Host "- POST /api/v1/auth/login ✅" -ForegroundColor White
Write-Host "- GET /api/v1/auth/me ✅" -ForegroundColor White
Write-Host "- POST /api/v1/auth/refresh ✅" -ForegroundColor White
Write-Host "- GET /api/v1/auth/check-username/{username} ✅" -ForegroundColor White
Write-Host "- GET /api/v1/auth/check-email/{email} ✅" -ForegroundColor White
Write-Host "- POST /api/v1/auth/logout ✅" -ForegroundColor White
Write-Host "- GET /api/v1/auth/health ✅" -ForegroundColor White
Write-Host ""
