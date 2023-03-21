#LIST ALL MICROSERVICE ROOTS IN SYSTEM
microservices=("ts-admin-basic-info-service" "ts-admin-order-service" "ts-admin-route-service"  "ts-admin-travel-service" "ts-admin-user-service" "ts-assurance-service" "ts-auth-service" "ts-basic-service" "ts-cancel-service" "ts-common" "ts-config-service" "ts-consign-price-service" "ts-consign-service" "ts-contacts-service" "ts-delivery-service""ts-execute-service" "ts-food-delivery-service" "ts-food-service" "ts-gateway-service" "ts-inside-payment-service" "ts-notification-service" "ts-order-other-service" "ts-order-service" "ts-payment-service" "ts-preserve-other-service" "ts-preserve-service" "ts-price-service" "ts-rebook-service" "ts-route-plan-service" "ts-route-service" "ts-seat-service" "ts-security-service" "ts-station-food-service" "ts-station-service" "ts-train-food-service" "ts-train-service" "ts-travel-plan-service" "ts-travel-service" "ts-travel2-service" "ts-user-service" "ts-verification-code-service" "ts-wait-order-service")
#ALTER THIS TO MS SYSTEM ROOT 
directory="/home/richardh/capstone/train-ticket/"
directory2="/target/"
ending="-1.0.jar"

for element in "${microservices[@]}"; do
    path="$directory$element$directory2$element$ending"
    path2="$directory$element$directory2"
    unzip -o $path -d $path2
    yaml="$directory$element$directory2/BOOT-INF/lib/"
    file=$(find "$yaml" -name "snakeyaml*" -print -quit)
    if [ -f "$file" ]; then
        rm "$file"
    fi
done