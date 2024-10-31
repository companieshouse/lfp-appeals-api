#!/bin/bash
#
# Start script for lfp-appeals-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "lfp-appeals-api.jar"
