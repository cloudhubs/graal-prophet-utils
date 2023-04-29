#!/bin/bash

input_directory="./output_trainticket"
restcalls_output="$input_directory/restcalls_total.csv"
endpoints_output="$input_directory/endpoints_total.csv"

# Initialize output files with header row
#echo "header1,header2,header3" > "$restcalls_output"
#echo "header1,header2,header3" > "$endpoints_output"

# Process files in the input directory
find "$input_directory" -type f \( -iname "*restcalls.csv" -o -iname "*endpoints.csv" \) | while read -r file; do
  if [[ $file == *restcalls.csv ]]; then
    tail -n +1 "$file" >> "$restcalls_output"
  elif [[ $file == *endpoints.csv ]]; then
    tail -n +1 "$file" >> "$endpoints_output"
  fi
done
