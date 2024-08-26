#!/bin/bash

# Color coded echos
function echo_y { echo -e "\033[1;33m"'$@'"\033[0m"; } # yellow
function echo_r { echo -e "\033[0;31m"'$@'"\033[0m"; } # red
function echo_c { echo -e "\033[0;36m"'$@'"\033[0m"; } # cyan

# 1. Load environment variables
echo_y "\nLoad environment variables from '$ENV_FILE' ..."
ENV_FILE=$(pwd)/.env
# shellcheck source=$(pwd)/.env
source "$ENV_FILE"

# 2. Ensure all of the environmental variables are present
if [[ "$DB_HOST" == "" || "$DB_PORT" == "" || "$DB_NAME" == "" || "$DB_USERNAME" == "" || "$DB_PASSWORD" == "" ||
  "$DB_ADMIN_USERNAME" == "" || "$DB_ADMIN_PASSWORD" == "" || "$LOGICAL_ENV_NAME" == "" || "$SERVICE_NAME" == "" ]]; then
    echo_r "\nDid you miss the environment variables for $ENV_FILE file?"
    echo_y "\nSee the README.md for template"
    exit
fi

# 3. Export the variables from the .env file
export DB_HOST=$DB_HOST
export DB_PORT=$DB_PORT
export DB_NAME=$DB_NAME
export DB_USERNAME=$DB_USERNAME
export DB_PASSWORD=$DB_PASSWORD
export DB_ADMIN_USERNAME=$DB_ADMIN_USERNAME
export DB_ADMIN_PASSWORD=$DB_ADMIN_PASSWORD
export LOGICAL_ENV_NAME=$LOGICAL_ENV_NAME
export SERVICE_NAME=$SERVICE_NAME

# 4. Run Spring boot
mvn clean spring-boot:run

