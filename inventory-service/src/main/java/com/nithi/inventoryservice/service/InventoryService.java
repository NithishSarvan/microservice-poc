package com.nithi.inventoryservice.service;

import com.nithi.inventoryservice.dto.InventoryResponse;
import com.nithi.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .isInStock(inventory.getQuantity()>0)
                        .skuCode(inventory.getSkuCode())
                        .build()
                ).toList();
    }
}
