$Green = "Green"
$Yellow = "Yellow"
$Red = "Red"

$BaseUrl = "http://localhost:8080"
$PaymentServiceUrl = "http://localhost:8080"

Write-Host "Starting Event-Driven Payments Demo..." -ForegroundColor $Yellow
Write-Host "--------------------------------------------------"

# 1. Check Health
Write-Host "[1] Checking Payment Service connectivity..." -ForegroundColor $Yellow
try {
    $response = Invoke-WebRequest -Uri "$PaymentServiceUrl/actuator/health" -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "Payment Service is UP" -ForegroundColor $Green
    }
}
catch {
    Write-Host "Payment Service is NOT reachable. Please start docker-compose." -ForegroundColor $Red
}

# 2. Create a Payment
Write-Host "`n[2] Creating a new Payment Request..." -ForegroundColor $Yellow
$body = @{
    amount        = 100.00
    currency      = "USD"
    debitorId     = "demo-user"
    beneficiaryId = "demo-merchant"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$PaymentServiceUrl/payments" -Method Post -Body $body -ContentType "application/json"
    $paymentId = $response.id
    
    if (-not $paymentId) {
        throw "No Payment ID returned"
    }
    
    Write-Host "Payment API accepted request." -ForegroundColor $Green
    Write-Host ("Payment ID: {0}" -f $paymentId)
}
catch {
    Write-Host ("Failed to create payment: {0}" -f $Error[0]) -ForegroundColor $Red
    exit 1
}

# 3. Poll for Completion
Write-Host "`n[3] Polling for State Completion (Saga in progress)..." -ForegroundColor $Yellow

for ($i = 1; $i -le 10; $i++) {
    try {
        $status = Invoke-RestMethod -Uri "$PaymentServiceUrl/payments/$paymentId"
        $state = $status.state
        
        Write-Host ("Attempt {0}: Current State = {1}" -f $i, $state) -ForegroundColor $Yellow
        
        if ($state -eq "COMPLETED") {
            Write-Host "`nSAGA COMPLETED SUCCESSFULLY!" -ForegroundColor $Green
            exit 0
        }
        
        if ($state -eq "FAILED" -or $state -eq "CANCELLED") {
            Write-Host ("`nSAGA FAILED with state: {0}" -f $state) -ForegroundColor $Red
            exit 1
        }
    }
    catch {
        Write-Host "  Error checking status..."
    }
    
    Start-Sleep -Seconds 1
}

Write-Host "`nTimed out waiting for completion." -ForegroundColor $Red
exit 1
