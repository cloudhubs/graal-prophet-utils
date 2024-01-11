#!/usr/bin/env bash
set -e
pid=$1
if [[ -z "${pid}" ]]; then
  >&2 echo "Pid not set"
  exit 1
fi
echo "pid is $pid"

if [[ -z "${PROPHET_PLUGIN_HOME}" ]]; then
  >&2 echo "PROPHET_PLUGIN_HOME not set"
  exit 1
fi

logfile="${PROPHET_PLUGIN_HOME}/rss.log"

while mem=$(ps -o rss= -p "$pid"); do
    printf "%d\n" "$mem" >> "$logfile"
    printf "%d\n" "$mem"
    sleep .5
done

printf "Find the log at %s\n" "$logfile"
