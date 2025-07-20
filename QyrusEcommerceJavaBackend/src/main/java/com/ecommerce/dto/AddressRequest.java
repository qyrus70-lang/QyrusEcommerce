package com.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @JsonProperty("user_email")
    private String email;
    
    @NotNull(message = "Address is required")
    @NotBlank(message = "Address cannot be blank")
    @Size(min = 10, max = 500, message = "Address must be between 10 and 500 characters")
    @JsonProperty("user_address")
    private String address;
    
    @JsonProperty("address_name")
    @Size(max = 50, message = "Address name cannot exceed 50 characters")
    private String addressName; // Optional: "Home", "Work", "Billing", etc.
    
    @JsonProperty("phone_number")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber; // Optional phone number
    
    @JsonProperty("delivery_instructions")
    @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
    private String deliveryInstructions; // Optional delivery notes
    
    @JsonProperty("is_billing_address")
    private Boolean isBillingAddress = false;
    
    @JsonProperty("is_shipping_address")
    private Boolean isShippingAddress = true;
    
    @JsonProperty("set_as_default")
    private Boolean setAsDefault = false;
    
    /**
     * Validates the basic requirements for address creation
     */
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               address.length() >= 10 && address.length() <= 500;
    }
    
    /**
     * Returns a sanitized version of the address (removes potentially harmful content)
     */
    public String getSanitizedAddress() {
        if (address == null) return null;
        
        return address.replaceAll("<[^>]*>", "") // Remove HTML tags
                     .replaceAll("javascript:", "") // Remove javascript
                     .replaceAll("script", "") // Remove script references
                     .trim();
    }
    
    /**
     * Gets the display name for this address request
     */
    public String getDisplayName() {
        if (addressName != null && !addressName.trim().isEmpty()) {
            return addressName.trim();
        }
        return "New Address";
    }
    
    /**
     * Determines if this address can be used for billing
     */
    public boolean canBeUsedForBilling() {
        return isBillingAddress != null && isBillingAddress;
    }
    
    /**
     * Determines if this address can be used for shipping
     */
    public boolean canBeUsedForShipping() {
        return isShippingAddress == null || isShippingAddress; // Default to true
    }
    
    /**
     * Checks if this address should be set as default
     */
    public boolean shouldSetAsDefault() {
        return setAsDefault != null && setAsDefault;
    }
} 