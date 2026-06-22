package com.inventory.management.feature.settings.entity;

import com.inventory.management.feature.auth.entity.Tenant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "settings")
public class Settings {

    @Id
    private Long id = 1L;

    private Integer safetyStock;
    private Integer minimumThreshold;
    private Integer reorderLevel;

    private String storeName;
    private String currencySymbol;
    
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}