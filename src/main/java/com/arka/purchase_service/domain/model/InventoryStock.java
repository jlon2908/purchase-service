package com.arka.purchase_service.domain.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock {
    private String sku;
    private int available;
    private Map<String, Integer> warehouseStock;
}
