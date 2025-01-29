#!/usr/bin/env bash

parallel_count=10

if [ ! -z "$1" ]; then
  parallel_count=$1
fi

echo "Running $parallel_count instances of ./submit.sh in parallel..."

for i in $(seq 1 $parallel_count)
do
   ./submit.sh &
done

wait

echo "Completed running $parallel_count instances of ./submit.sh in parallel."
