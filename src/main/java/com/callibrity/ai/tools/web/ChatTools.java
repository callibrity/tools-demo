package com.callibrity.ai.tools.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ChatTools {

// -------------------------- OTHER METHODS --------------------------

    @Tool(description = "Add items to grocery list")
    public String addItemsToGroceryList(List<String> items) {
        log.info("Add items to grocery list: {}", items);
        return "success";
    }

    @Tool(description = "List all dietary preferences")
    public String listAllDietaryPreferences() {
        return "I am on the mediterranean diet.";
    }

    @Tool(description = "List all available ingredients in the kitchen")
    public String listAvailableIngredients() {
        return "We have tomatoes, onions, garlic, spinach, chicken breast, and rice available.";
    }

    @Tool(description = "List all equipment available in the kitchen")
    public String listEquipmentAvailable() {
        return "We have a blender, microwave, oven, and a coffee maker available.";
    }

    @Tool(description = "List all food allergies")
    public String listFoodAllergies() {
        return "I am allergic to soy and shellfish.";
    }

}
