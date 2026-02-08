#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080" # Assuming Nginx or direct service port. Adjusting to 8081 for Payment Service based on docker-compose.
PAYMENT_SERVICE_URL="http://localhost:8081"

echo -e "${YELLOW}Starting Event-Driven Payments Demo...${NC}"
echo "--------------------------------------------------"

# 1. Check Health (Simple connectivity check)
echo -e "${YELLOW}[1] Checking Payment Service connectivity...${NC}"
curl -s "${PAYMENT_SERVICE_URL}/actuator/health" > /dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Payment Service is UP${NC}"
else
    echo -e "${RED}✗ Payment Service is NOT reachable. Please start docker-compose.${NC}"
    # Continue anyway for demo purposes or exit? Let's continue but warn.
fi

# 2. Create a Payment
echo -e "\n${YELLOW}[2] Creating a new Payment Request...${NC}"
PAYMENT_ID=$(curl -s -X POST "${PAYMENT_SERVICE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "currency": "USD",
    "debitorId": "demo-user",
    "beneficiaryId": "demo-merchant"
  }' | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

if [ -z "$PAYMENT_ID" ]; then
    echo -e "${RED}✗ Failed to create payment.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Payment API accepted request.${NC}"
echo -e "  Payment ID: ${PAYMENT_ID}"

# 3. Poll for Completion (Simulating looking at the frontend)
echo -e "\n${YELLOW}[3] Polling for State Completion (Saga in progress)...${NC}"

for i in {1..10}; do
    STATUS_JSON=$(curl -s "${PAYMENT_SERVICE_URL}/payments/${PAYMENT_ID}")
    STATE=$(echo "$STATUS_JSON" | sed -n 's/.*"state":"\([^"]*\)".*/\1/p')
    
    echo -e "  Attempt $i: Current State = ${YELLOW}$STATE${NC}"
    
    if [ "$STATE" == "COMPLETED" ]; then
        echo -e "\n${GREEN}✓ SAGA COMPLETED SUCCESSFULLY!${NC}"
        exit 0
    fi
    
    if [ "$STATE" == "FAILED" ] || [ "$STATE" == "CANCELLED" ]; then
        echo -e "\n${RED}✗ SAGA FAILED with state: $STATE${NC}"
        exit 1
    fi
    
    sleep 1
done

echo -e "\n${RED}✗ Timed out waiting for completion.${NC}"
exit 1
