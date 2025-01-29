#!/usr/bin/env bash

FILE="${1:-main.py}"

exec docker run \
  -w /host \
  -v $(pwd):/host \
  --network host \
  --rm \
  bitnami/spark:3.5.3-debian-12-r0 \
  spark-submit \
  "$FILE"
