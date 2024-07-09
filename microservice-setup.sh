#!/usr/bin/env bash
set -e

#LIST ALL MICROSERVICE ROOTS IN SYSTEM
microservices=$(jq -r '.microservices[].microserviceName' $1 | tr '\n' ' ')
microservices=($microservices)

# the base directory of the benchmark
if [[ -z "${MS_ROOT}" ]]; then
  >&2 echo "MS_ROOT not set"
  exit 1
fi

directory="${MS_ROOT}"

#NOTHING NEEDS TO CHANGE BELOW HERE
target_dir="target"
ending=".jar"

for microservice in "${microservices[@]}"; do
    fatjar="${directory}/${microservice}/${target_dir}/${microservice}*${ending}"
    output_path="${directory}/${microservice}/${target_dir}"
    unzip -o $fatjar -d $output_path
    lib_dir="${directory}/${microservice}/${target_dir}/BOOT-INF/lib/"
    if [ -d "$lib_dir" ]; then
      file=$(find "$lib_dir" -name "snakeyaml*" -print -quit)
      if [ -f "$file" ]; then
          rm "$file"
      fi
    fi
done