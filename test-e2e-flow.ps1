# PowerShell Test Script for End-to-End Assessment Flow
# Manual testing script for API endpoints

Write-Host "🧪 TESTING END-TO-END ASSESSMENT FLOW" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Base URL
$BaseUrl = "http://localhost:8080/api/v1"

Write-Host ""
Write-Host "1️⃣  Testing GET /api/v1/roles" -ForegroundColor Yellow
Write-Host "Expected: List of available roles"

try {
    $rolesResponse = Invoke-RestMethod -Uri "$BaseUrl/roles" -Method GET -ContentType "application/json"
    Write-Host "✅ Roles endpoint working" -ForegroundColor Green
    $rolesResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error accessing roles endpoint: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "2️⃣  Testing GET /api/v1/roles/{roleId}/questions" -ForegroundColor Yellow
Write-Host "Expected: Questions for specific role"

try {
    $questionsResponse = Invoke-RestMethod -Uri "$BaseUrl/roles/software-engineer/questions" -Method GET -ContentType "application/json"
    Write-Host "✅ Questions endpoint working" -ForegroundColor Green
    $questionsResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error accessing questions endpoint: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "3️⃣  Testing POST /api/v1/assessments" -ForegroundColor Yellow
Write-Host "Expected: Create new assessment"

$AssessmentPayload = @{
    roleId = "software-engineer"
    userEmail = "test@example.com"
    answers = @(
        @{
            questionId = "q1"
            selectedOption = "option1"
        },
        @{
            questionId = "q2"
            selectedOption = "option2"
        }
    )
} | ConvertTo-Json -Depth 3

try {
    $assessmentResponse = Invoke-RestMethod -Uri "$BaseUrl/assessments" -Method POST -Body $AssessmentPayload -ContentType "application/json"
    Write-Host "✅ Assessment creation working" -ForegroundColor Green
    $assessmentResponse | ConvertTo-Json -Depth 3
    
    $AssessmentId = $assessmentResponse.id
    
    if ($AssessmentId) {
        Write-Host ""
        Write-Host "4️⃣  Testing GET /api/v1/assessments/{id}" -ForegroundColor Yellow
        Write-Host "Assessment ID: $AssessmentId"
        
        try {
            $getAssessmentResponse = Invoke-RestMethod -Uri "$BaseUrl/assessments/$AssessmentId" -Method GET -ContentType "application/json"
            Write-Host "✅ Get assessment working" -ForegroundColor Green
            $getAssessmentResponse | ConvertTo-Json -Depth 3
        } catch {
            Write-Host "❌ Error getting assessment: $($_.Exception.Message)" -ForegroundColor Red
        }
        
        Write-Host ""
        Write-Host "5️⃣  Testing POST /api/v1/assessments/{id}/report" -ForegroundColor Yellow
        Write-Host "Expected: Generate PDF report"
        
        $ReportPayload = @{
            title = "Test Report"
            includeCharts = $true
        } | ConvertTo-Json
        
        try {
            $reportResponse = Invoke-RestMethod -Uri "$BaseUrl/assessments/$AssessmentId/report" -Method POST -Body $ReportPayload -ContentType "application/json"
            Write-Host "✅ Report generation working" -ForegroundColor Green
            $reportResponse | ConvertTo-Json -Depth 3
            
            $ReportId = $reportResponse.reportId
            
            if ($ReportId) {
                Write-Host ""
                Write-Host "6️⃣  Testing GET /api/v1/reports/{reportId}/download" -ForegroundColor Yellow
                Write-Host "Report ID: $ReportId"
                
                try {
                    $downloadResponse = Invoke-WebRequest -Uri "$BaseUrl/reports/$ReportId/download" -Method GET
                    Write-Host "✅ Report download working - Status: $($downloadResponse.StatusCode)" -ForegroundColor Green
                } catch {
                    Write-Host "❌ Error downloading report: $($_.Exception.Message)" -ForegroundColor Red
                }
                
                Write-Host ""
                Write-Host "7️⃣  Testing GET /api/v1/assessments/{id}/summary" -ForegroundColor Yellow
                Write-Host "Expected: Dashboard summary data"
                
                try {
                    $summaryResponse = Invoke-RestMethod -Uri "$BaseUrl/assessments/$AssessmentId/summary" -Method GET -ContentType "application/json"
                    Write-Host "✅ Summary endpoint working" -ForegroundColor Green
                    $summaryResponse | ConvertTo-Json -Depth 3
                } catch {
                    Write-Host "❌ Error accessing summary: $($_.Exception.Message)" -ForegroundColor Red
                }
            }
        } catch {
            Write-Host "❌ Error generating report: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "❌ Error creating assessment: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "✅ End-to-End API Test Complete" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend URLs to test manually:" -ForegroundColor Cyan
Write-Host "- Assessment Start: http://localhost:5173/assessment" -ForegroundColor White
Write-Host "- Admin Dashboard: http://localhost:5173/admin" -ForegroundColor White
Write-Host ""
