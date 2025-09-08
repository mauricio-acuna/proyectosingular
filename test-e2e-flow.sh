#!/bin/bash

# Test End-to-End Assessment Flow
# Manual testing script for API endpoints

echo "🧪 TESTING END-TO-END ASSESSMENT FLOW"
echo "====================================="

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo ""
echo "1️⃣  Testing GET /api/v1/roles"
echo "Expected: List of available roles"
curl -X GET "${BASE_URL}/roles" \
  -H "Content-Type: application/json" \
  2>/dev/null | jq . || echo "❌ Error accessing roles endpoint"

echo ""
echo "2️⃣  Testing GET /api/v1/roles/{roleId}/questions"
echo "Expected: Questions for specific role"
curl -X GET "${BASE_URL}/roles/software-engineer/questions" \
  -H "Content-Type: application/json" \
  2>/dev/null | jq . || echo "❌ Error accessing questions endpoint"

echo ""
echo "3️⃣  Testing POST /api/v1/assessments"
echo "Expected: Create new assessment"
ASSESSMENT_PAYLOAD='{
  "roleId": "software-engineer",
  "userEmail": "test@example.com",
  "answers": [
    {
      "questionId": "q1",
      "selectedOption": "option1"
    },
    {
      "questionId": "q2", 
      "selectedOption": "option2"
    }
  ]
}'

ASSESSMENT_RESPONSE=$(curl -X POST "${BASE_URL}/assessments" \
  -H "Content-Type: application/json" \
  -d "${ASSESSMENT_PAYLOAD}" \
  2>/dev/null)

echo "${ASSESSMENT_RESPONSE}" | jq . || echo "❌ Error creating assessment"

# Extract assessment ID from response
ASSESSMENT_ID=$(echo "${ASSESSMENT_RESPONSE}" | jq -r '.id' 2>/dev/null)

if [ "${ASSESSMENT_ID}" != "null" ] && [ -n "${ASSESSMENT_ID}" ]; then
    echo ""
    echo "4️⃣  Testing GET /api/v1/assessments/{id}"
    echo "Assessment ID: ${ASSESSMENT_ID}"
    curl -X GET "${BASE_URL}/assessments/${ASSESSMENT_ID}" \
      -H "Content-Type: application/json" \
      2>/dev/null | jq . || echo "❌ Error accessing assessment"

    echo ""
    echo "5️⃣  Testing POST /api/v1/assessments/{id}/report"
    echo "Expected: Generate PDF report"
    REPORT_RESPONSE=$(curl -X POST "${BASE_URL}/assessments/${ASSESSMENT_ID}/report" \
      -H "Content-Type: application/json" \
      -d '{"title": "Test Report", "includeCharts": true}' \
      2>/dev/null)

    echo "${REPORT_RESPONSE}" | jq . || echo "❌ Error generating report"

    # Extract report ID
    REPORT_ID=$(echo "${REPORT_RESPONSE}" | jq -r '.reportId' 2>/dev/null)

    if [ "${REPORT_ID}" != "null" ] && [ -n "${REPORT_ID}" ]; then
        echo ""
        echo "6️⃣  Testing GET /api/v1/reports/{reportId}/download"
        echo "Report ID: ${REPORT_ID}"
        curl -X GET "${BASE_URL}/reports/${REPORT_ID}/download" \
          -I 2>/dev/null || echo "❌ Error downloading report"

        echo ""
        echo "7️⃣  Testing GET /api/v1/assessments/{id}/summary"
        echo "Expected: Dashboard summary data"
        curl -X GET "${BASE_URL}/assessments/${ASSESSMENT_ID}/summary" \
          -H "Content-Type: application/json" \
          2>/dev/null | jq . || echo "❌ Error accessing summary"
    fi
else
    echo "❌ Failed to create assessment - cannot continue with report tests"
fi

echo ""
echo "✅ End-to-End API Test Complete"
echo ""
echo "Frontend URLs to test manually:"
echo "- Assessment Start: http://localhost:5173/assessment"
echo "- Admin Dashboard: http://localhost:5173/admin"
echo ""
