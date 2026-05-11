#!/bin/bash

# Parse arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Validate number of arguments
if [ $# -ne 5 ]; then
  echo "Illegal Number of Arguments"
  exit 1
fi

# Common info for later commands
lscpu_out=$(lscpu)

# Retrieve hardware info
hostname_out="$(hostname -f)"
cpu_number=$(echo "$lscpu_out" | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out" | egrep "^Model name:" | awk -F ':' '{print $2}' | xargs)
cpu_mhz=$(echo "$cpu_model" | grep -oE "[0-9]*\.[0-9]*" | awk '{printf("%.3f", $1*1000)}' | xargs)
l2_cache=$(echo "$lscpu_out" | grep "^L2" | awk '{print $3}' | xargs)
total_mem=$(vmstat --unit K | tail -1| awk '{print $4}' | xargs)
timestamp=$(vmstat -t | tail -1 | awk '{print $18, $19}' | xargs)

# Construct insert statement
psql_insert_statement="INSERT INTO host_info (id, hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, \"timestamp\", total_mem) \
VALUES(DEFAULT, '$hostname_out', '$cpu_number', '$cpu_architecture', '$cpu_model', '$cpu_mhz', '$l2_cache', '$timestamp', '$total_mem');"

# set env variable for psql command(s)
export PGPASSWORD=$psql_password

# Execute command on given postgres database
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$psql_insert_statement"

exit $?