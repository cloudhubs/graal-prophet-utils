#!/usr/bin/env bash
set -e

#LIST ALL MICROSERVICE ROOTS IN SYSTEM. LIST MICROSERVICES HERE
#v 0.2.0
#microservices=("ts-admin-basic-info-service" "ts-admin-order-service" "ts-admin-route-service" "ts-admin-travel-service" "ts-admin-user-service" "ts-assurance-service" "ts-auth-service" "ts-avatar-service" "ts-basic-service" "ts-cancel-service" "ts-common" "ts-config-service" "ts-consign-price-service" "ts-consign-service" "ts-contacts-service" "ts-delivery-service" "ts-execute-service" "ts-food-map-service" "ts-food-service" "ts-inside-payment-service" "ts-news-service" "ts-notification-service" "ts-order-other-service" "ts-order-service" "ts-payment-service" "ts-preserve-other-service" "ts-preserve-service" "ts-price-service" "ts-rebook-service" "ts-route-plan-service" "ts-route-service" "ts-seat-service" "ts-security-service" "ts-station-service" "ts-ticket-office-service" "ts-ticketinfo-service" "ts-train-service" "ts-travel-plan-service" "ts-travel-service" "ts-travel2-service" "ts-ui-dashboard" "ts-user-service" "ts-verification-code-service" "ts-voucher-service")
#v 1.0.0
microservices=("ts-admin-basic-info-service" "ts-admin-order-service" "ts-admin-route-service"  "ts-admin-travel-service" "ts-admin-user-service" "ts-assurance-service" "ts-auth-service" "ts-basic-service" "ts-cancel-service" "ts-common" "ts-config-service" "ts-consign-price-service" "ts-consign-service" "ts-contacts-service" "ts-delivery-service" "ts-execute-service" "ts-food-delivery-service" "ts-food-service" "ts-gateway-service" "ts-inside-payment-service" "ts-notification-service" "ts-order-other-service" "ts-order-service" "ts-payment-service" "ts-preserve-other-service" "ts-preserve-service" "ts-price-service" "ts-rebook-service" "ts-route-plan-service" "ts-route-service" "ts-seat-service" "ts-security-service" "ts-station-food-service" "ts-station-service" "ts-train-food-service" "ts-train-service" "ts-travel-plan-service" "ts-travel-service" "ts-travel2-service" "ts-user-service" "ts-verification-code-service" "ts-wait-order-service")

if [[ -z "${MS_ROOT}" ]]; then
  >&2 echo "MS_ROOT not set"
  exit 1
fi

# the base directory of the benchmark
directory="${MS_ROOT}"

#NOTHING NEEDS TO CHANGE BELOW HERE
target_dir="/target/"
ending="1.0.jar"

for microservice in "${microservices[@]}"; do
    fatjar="${directory}/${microservice}/${target_dir}/${microservice}*${ending}"
    output_path="${directory}/${microservice}${target_dir}"
    unzip -o $fatjar -d $output_path
    lib_dir="${directory}/${microservice}/${target_dir}/BOOT-INF/lib/"
    if [ -d "$lib_dir" ]; then
      file=$(find "$lib_dir" -name "snakeyaml*" -print -quit)
      if [ -f "$file" ]; then
          rm "$file"
      fi
    fi
done