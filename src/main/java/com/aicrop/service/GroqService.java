package com.aicrop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class GroqService {

    private static final Logger log = LoggerFactory.getLogger(GroqService.class);

    @Value("${groq.api.key:}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    @Value("${groq.model:openai/gpt-oss-120b}")
    private String model;

    private final WebClient webClient = WebClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_SYSTEM_PROMPT = """
        You are KrishiMitra (कृषि मित्र), an expert agricultural AI assistant for Indian farmers.
        You specialize in Indian agronomy, crop management, soil health (NPK, pH), IPM pest control, irrigation, and government schemes (PM-KISAN, PMFBY, KCC).
        If asked in Hindi or a regional Indian language, respond fluently in that language.
        Always strictly follow the formatting instruction provided at the start of the user query.
        """;

    public String askAi(String userQuery, String context, String mode) {
        String effectiveKey = getEffectiveApiKey();
        if (effectiveKey == null || effectiveKey.isBlank() || effectiveKey.contains("YOUR_GROQ_API_KEY")) {
            log.warn("Groq API key not configured. Using fallback advisory response.");
            return getFallbackResponse(userQuery, mode);
        }

        try {
            boolean isShort = mode != null && mode.trim().equalsIgnoreCase("short");
            
            // Craft prompt based on mode
            StringBuilder promptBuilder = new StringBuilder();
            if (isShort) {
                promptBuilder.append("[MODE: SHORT & CONCISE. You MUST answer strictly in 2 to 3 brief bullet points, maximum 50 words total. Do NOT output long text, tables, or preamble.]\n\n");
            } else {
                promptBuilder.append("[MODE: DETAILED & COMPREHENSIVE. Provide a complete, structured agricultural breakdown with step-by-step guidance, product names, exact dosages per acre/ha, markdown tables for timing/dosages, and safety precautions.]\n\n");
            }

            if (context != null && !context.isBlank()) {
                promptBuilder.append("Current Context: ").append(context).append("\n\n");
            }

            promptBuilder.append("Farmer's Question: ").append(userQuery);

            int maxTokens = isShort ? 250 : 1000;

            Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", BASE_SYSTEM_PROMPT
            );

            Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", promptBuilder.toString()
            );

            Map<String, Object> requestBody = Map.of(
                "model", model != null && !model.isBlank() ? model : "openai/gpt-oss-120b",
                "messages", List.of(systemMessage, userMessage),
                "temperature", isShort ? 0.4 : 0.6,
                "max_tokens", maxTokens
            );

            String response = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + effectiveKey.trim())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return extractContentFromGroqResponse(response);

        } catch (Exception e) {
            log.error("Groq API call error: {}", e.getMessage());
            return getFallbackResponse(userQuery, mode);
        }
    }

    public String askAi(String userQuery, String context) {
        return askAi(userQuery, context, "detailed");
    }

    private String getEffectiveApiKey() {
        if (apiKey != null && !apiKey.isBlank() && !apiKey.contains("YOUR_GROQ_API_KEY")) {
            return apiKey;
        }
        String envKey = System.getenv("GROQ_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey;
        }
        String envGroq = System.getenv("GROQ_API");
        if (envGroq != null && !envGroq.isBlank()) {
            return envGroq;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractContentFromGroqResponse(String json) {
        try {
            Map<String, Object> response = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null && message.get("content") != null) {
                    return message.get("content").toString().trim();
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Groq response JSON: {}", e.getMessage());
        }
        return "I'm sorry, I could not generate a response. Please try asking again.";
    }

    private String getFallbackResponse(String query, String mode) {
        boolean isShort = mode != null && mode.equalsIgnoreCase("short");
        String lower = query.toLowerCase();

        if (lower.contains("photosynthesis")) {
            if (isShort) {
                return "• Photosynthesis turns sunlight, water, and CO₂ into plant food (glucose) and oxygen.\n• More sunlight and good irrigation increase photosynthesis for higher crop yield.\n• Keep fields weed-free so crop leaves receive maximum sunshine.";
            }
            return "🌿 **Photosynthesis for Farmers**:\n\n" +
                   "Photosynthesis is how your crop uses sunlight, water, and air (CO₂) to produce sugars and starches for grain/fruit development.\n\n" +
                   "| Factor | Practical Farm Action |\n" +
                   "|---|---|\n" +
                   "| **Sunlight** | Maintain proper row spacing and remove tall weeds. |\n" +
                   "| **Water** | Ensure adequate soil moisture during vegetative stages. |\n" +
                   "| **Nutrients** | Nitrogen and Magnesium are vital for chlorophyll formation. |";
        }

        if (lower.contains("fertilizer") || lower.contains("manure") || lower.contains("khad")) {
            if (isShort) {
                return "• Apply balanced NPK based on soil test (Urea, DAP, MOP).\n• Split Nitrogen: 50% at sowing, 25% at tillering, 25% at flowering.\n• Add 5 tonnes/ha FYM or compost to improve soil organic carbon.";
            }
            return "💡 **Fertilizer Guidance**:\n\n" +
                   "• **Soil Test First**: Always follow NPK recommendations from a recent Soil Health Card.\n" +
                   "• **Standard Sources**: Urea for Nitrogen, DAP/SSP for Phosphorus, MOP for Potassium.\n" +
                   "• **Split Application**: Apply 50% N + 100% P & K at basal, remaining N in split top-dressings.\n" +
                   "• **Organic Manure**: Incorporate FYM @ 5–10 tonnes/ha.";
        }

        if (isShort) {
            return "• Ensure proper soil moisture and timely nutrient application for healthy crop growth.\n• Monitor fields regularly for early detection of pests or nutrient deficiencies.\n• Consult local KVK or Kisan Call Centre (1800-180-1551) for crop-specific dosage.";
        }

        return "🌾 **KrishiMitra Advisory**:\n\n" +
               "Thank you for your question regarding: *" + query + "*.\n\n" +
               "• For immediate live support, call **Kisan Call Centre**: `1800-180-1551` (Toll-Free, 6 AM – 10 PM).\n" +
               "• Check our **Disease Library** for common crop diseases and chemical/organic treatments.\n" +
               "• Use our **Crop Recommendation** engine for location-specific soil NPK crop selection.";
    }
}
