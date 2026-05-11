#!/bin/bash

# Parse arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Validate number of Arguments
if [ $# -ne 5 ]; then
  echo "Illegal Number of Arguments"
  exit 1
fi

# Common commands for later
vmstat_out=$(vmstat --unit M | tail -1)
hostname_out="$(hostname -f)"

# Retrieve usage data
memory_free=$(echo "$vmstat_out" | awk -v col="4" '{print $col}' | xargs)
cpu_idle=$(echo "$vmstat_out" | awk -v col="15" '{print $col}' | xargs)
cpu_kernel=$(echo "$vmstat_out" | awk -v col="14" '{print $col}' | xargs)
disk_io=$(vmstat --unit M -d | tail -1 | awk -v col="10" '{print $col}' | xargs)
disk_available=$(df -BM / | tail -1 | awk '{print $4}' | grep -oE "^[0-9]*" | xargs)
timestamp=$(vmstat -t | tail -1 | awk '{print $18, $19}' | xargs)

# Set env variable for psql command(s)
export PGPASSWORD="$psql_password"

# Construct insert statement
# Format taken from https://www.w3schools.com/sql/sql_insert_into_select.asp
psql_insert_statement="INSERT INTO host_usage (\"timestamp\", host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) \
SELECT '$timestamp', id, '$memory_free', '$cpu_idle', '$cpu_kernel', '$disk_io', '$disk_available'\
FROM host_info \
WHERE hostname='$hostname_out';"

# Execute command on given postgres database
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$psql_insert_statement"

exit $?