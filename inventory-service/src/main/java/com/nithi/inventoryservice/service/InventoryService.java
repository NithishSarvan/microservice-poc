package com.nithi.inventoryservice.service;

import com.nithi.inventoryservice.dto.InventoryResponse;
import com.nithi.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> isInStock(List<String> skuCode){

       /* log.info("Wait started");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Inventory service is slept for 10 sec");
        }
        log.info("wait ended");*/

        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .isInStock(inventory.getQuantity()>0)
                        .skuCode(inventory.getSkuCode())
                        .build()
                ).toList();
    }
}
