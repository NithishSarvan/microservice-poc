package com.nithi.orderservice.service;

import com.nithi.orderservice.dto.InventoryResponse;
import com.nithi.orderservice.dto.OrderLineItemsDto;
import com.nithi.orderservice.dto.OrderRequest;
import com.nithi.orderservice.event.OrderPlacedEvent;
import com.nithi.orderservice.model.Order;
import com.nithi.orderservice.model.OrderLineItems;
import com.nithi.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineitemsList(orderRequest.getOrderLineItemsDtos().stream().map(this::mapToDto).toList());

        List<String> skuCodes = order.getOrderLineitemsList().stream().map(OrderLineItems::getSkuCode).toList();

        // Check whether the stock is available
        // Make Http Request for Inventory Service by using "WebClient(Web flux) "

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean productIsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (productIsInStock){

            orderRepository.save(order);
           kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());

        return orderLineItems;
    }

}
