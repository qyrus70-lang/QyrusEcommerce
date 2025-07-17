package com.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AddToCartRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Product ID is required")
    @JsonProperty("product_id")
    private Long productId;

    // Product variant properties
    private String color;
    private String provider;
    private String size;
    private String material;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;
    
    // Price and discount properties
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;
    
    @JsonProperty("discount_code")
    @Size(max = 50, message = "Discount code cannot exceed 50 characters")
    private String discountCode;
    
    @JsonProperty("applied_discount_percentage")
    @DecimalMin(value = "0.0", message = "Discount percentage must be non-negative")
    @DecimalMin(value = "100.0", inclusive = false, message = "Discount percentage must be less than 100")
    private BigDecimal appliedDiscountPercentage;
    
    // Gift and customization properties
    @JsonProperty("is_gift")
    private boolean isGift = false;
    
    @JsonProperty("gift_message")
    @Size(max = 500, message = "Gift message cannot exceed 500 characters")
    private String giftMessage;
    
    @JsonProperty("gift_wrap_type")
    private String giftWrapType;
    
    // Delivery and priority properties
    @JsonProperty("preferred_delivery_date")
    private LocalDateTime preferredDeliveryDate;
    
    @Pattern(regexp = "STANDARD|EXPRESS|OVERNIGHT", message = "Priority must be STANDARD, EXPRESS, or OVERNIGHT")
    private String priority = "STANDARD";
    
    @JsonProperty("delivery_instructions")
    @Size(max = 300, message = "Delivery instructions cannot exceed 300 characters")
    private String deliveryInstructions;
    
    // Tracking and session properties
    @JsonProperty("session_id")
    private String sessionId;
    
    @JsonProperty("referral_source")
    private String referralSource;
    
    @JsonProperty("campaign_id")
    private String campaignId;
    
    // Additional product specifications
    private BigDecimal weight;
    
    @JsonProperty("product_variant_id")
    private Long productVariantId;
    
    // Special instructions and notes
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    @JsonProperty("special_instructions")
    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;
    
    // Inventory and availability
    @JsonProperty("warehouse_id")
    private Long warehouseId;
    
    @JsonProperty("expected_availability_date")
    private LocalDateTime expectedAvailabilityDate;
    
    // Customer preferences
    @JsonProperty("send_notifications")
    private boolean sendNotifications = true;
    
    @JsonProperty("save_for_later")
    private boolean saveForLater = false;
} 