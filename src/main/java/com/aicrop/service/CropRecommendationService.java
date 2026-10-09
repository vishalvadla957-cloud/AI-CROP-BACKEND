package com.aicrop.service;

import com.aicrop.dto.RecommendationDTO;
import com.aicrop.dto.SoilInputDTO;
import com.aicrop.model.FarmingHistory;
import com.aicrop.model.User;
import com.aicrop.repository.FarmingHistoryRepository;
import com.aicrop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CropRecommendationService {

    @Autowired
    private FarmingHistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crop parameter ranges: [N_min, N_max, P_min, P_max, K_min, K_max,
     *                          pH_min, pH_max, Temp_min, Temp_max,
     *                          Humidity_min, Humidity_max, Rainfall_min, Rainfall_max]
     */
    private static final Map<String, double[]> CROP_PARAMS = new LinkedHashMap<>();
    private static final Map<String, String> CROP_DESCRIPTIONS = new HashMap<>();
    private static final Map<String, String> CROP_TIPS = new HashMap<>();
    private static final Map<String, String> CROP_SEASONS = new HashMap<>();

    static {
        // Rice
        CROP_PARAMS.put("Rice",        new double[]{60,  100, 30, 60, 30, 60, 5.5, 7.0, 20, 35, 80, 95, 150, 300});
        // Wheat
        CROP_PARAMS.put("Wheat",       new double[]{100, 150, 50, 80, 40, 70, 6.0, 7.5, 10, 25, 50, 70,  50, 100});
        // Maize
        CROP_PARAMS.put("Maize",       new double[]{80,  120, 40, 70, 30, 60, 5.5, 7.5, 18, 30, 55, 75,  60, 110});
        // Chickpea
        CROP_PARAMS.put("Chickpea",    new double[]{20,  40,  60, 100, 50, 80, 6.0, 8.0, 15, 30, 15, 65,  80, 120});
        // Kidney Beans
        CROP_PARAMS.put("Kidney Beans",new double[]{15,  30,  70, 130, 60, 100, 5.5, 7.0, 15, 30, 50, 75,  80, 120});
        // Pigeon Peas
        CROP_PARAMS.put("Pigeon Peas", new double[]{15,  30,  60, 100, 40, 80, 5.5, 7.5, 20, 35, 40, 80, 100, 200});
        // Moth Beans
        CROP_PARAMS.put("Moth Beans",  new double[]{15,  35,  30, 70,  25, 60, 6.0, 7.5, 25, 35, 25, 60,  50, 100});
        // Mung Bean
        CROP_PARAMS.put("Mung Bean",   new double[]{15,  35,  30, 70,  25, 60, 6.2, 7.2, 25, 35, 60, 85,  60, 120});
        // Black Gram
        CROP_PARAMS.put("Black Gram",  new double[]{15,  40,  30, 70,  25, 60, 5.5, 7.0, 25, 35, 60, 80,  60, 120});
        // Lentil
        CROP_PARAMS.put("Lentil",      new double[]{15,  35,  60, 100, 50, 80, 5.5, 7.0, 10, 25, 50, 75,  40,  80});
        // Pomegranate
        CROP_PARAMS.put("Pomegranate", new double[]{15,  40,  20, 60,  30, 60, 5.5, 7.5, 25, 40, 40, 75,  50, 100});
        // Banana
        CROP_PARAMS.put("Banana",      new double[]{100, 150, 75, 100, 50, 80, 5.5, 6.5, 25, 35, 75, 90, 100, 200});
        // Mango
        CROP_PARAMS.put("Mango",       new double[]{15,  40,  10, 30,  30, 50, 5.5, 7.5, 25, 38, 50, 80,  50, 120});
        // Grapes
        CROP_PARAMS.put("Grapes",      new double[]{15,  40,  10, 30,  15, 35, 5.5, 7.0, 15, 30, 50, 80,  60, 120});
        // Watermelon
        CROP_PARAMS.put("Watermelon",  new double[]{50,  100, 30, 60,  30, 60, 6.0, 7.5, 25, 35, 70, 85,  60, 120});
        // Muskmelon
        CROP_PARAMS.put("Muskmelon",   new double[]{50,  100, 30, 60,  25, 50, 6.0, 7.5, 25, 35, 70, 90,  50, 100});
        // Apple
        CROP_PARAMS.put("Apple",       new double[]{15,  40,  15, 40,  15, 40, 5.5, 6.5,  5, 20, 50, 80,  50, 150});
        // Orange
        CROP_PARAMS.put("Orange",      new double[]{15,  40,  10, 30,  15, 35, 6.0, 7.5, 20, 35, 70, 90,  75, 150});
        // Papaya
        CROP_PARAMS.put("Papaya",      new double[]{50,  100, 30, 70,  30, 60, 6.0, 7.0, 25, 35, 70, 90,  75, 150});
        // Coconut
        CROP_PARAMS.put("Coconut",     new double[]{15,  30,  15, 40,  15, 40, 5.0, 8.0, 25, 35, 80, 95, 100, 250});
        // Cotton
        CROP_PARAMS.put("Cotton",      new double[]{100, 150, 60, 100, 80, 120, 5.5, 8.0, 25, 38, 50, 85,  60, 110});
        // Jute
        CROP_PARAMS.put("Jute",        new double[]{60,  100, 35, 60,  35, 60, 5.0, 7.5, 25, 35, 70, 90, 150, 250});
        // Coffee
        CROP_PARAMS.put("Coffee",      new double[]{100, 140, 15, 35,  30, 50, 4.5, 6.5, 15, 30, 65, 90, 150, 250});
        // Sugarcane
        CROP_PARAMS.put("Sugarcane",   new double[]{100, 150, 50, 100, 100, 150, 6.0, 8.0, 25, 38, 65, 90, 100, 200});
        // Turmeric
        CROP_PARAMS.put("Turmeric",    new double[]{40,  80,  30, 60,  50, 80, 5.5, 7.0, 20, 30, 70, 90, 100, 200});

        // Descriptions
        CROP_DESCRIPTIONS.put("Rice",         "Staple cereal crop requiring flooded fields. Ideal for humid tropical climates with heavy rainfall.");
        CROP_DESCRIPTIONS.put("Wheat",        "Major rabi cereal crop grown in cool, dry winters. Backbone of Indian food security.");
        CROP_DESCRIPTIONS.put("Maize",        "Versatile cereal used for food, fodder and industry. Highly adaptable across varied agro-climates.");
        CROP_DESCRIPTIONS.put("Chickpea",     "Protein-rich legume (rabi). Fixes atmospheric nitrogen, improving soil fertility.");
        CROP_DESCRIPTIONS.put("Kidney Beans", "High-protein legume. Excellent nitrogen-fixer suited for well-drained soils.");
        CROP_DESCRIPTIONS.put("Pigeon Peas",  "Deep-rooted drought-tolerant legume, popular in semi-arid tropics.");
        CROP_DESCRIPTIONS.put("Moth Beans",   "Extremely drought-hardy legume suited for arid and semi-arid regions.");
        CROP_DESCRIPTIONS.put("Mung Bean",    "Short-duration nutritious legume ideal for summer and intercropping.");
        CROP_DESCRIPTIONS.put("Black Gram",   "High-protein pulse grown in kharif season, tolerates waterlogging.");
        CROP_DESCRIPTIONS.put("Lentil",       "Cool-season legume rich in protein and iron. Popular rabi pulse crop.");
        CROP_DESCRIPTIONS.put("Pomegranate",  "Hardy fruit crop tolerating saline and alkaline soils, suited for dry regions.");
        CROP_DESCRIPTIONS.put("Banana",       "High-value tropical fruit crop requiring rich fertile soils and consistent moisture.");
        CROP_DESCRIPTIONS.put("Mango",        "King of fruits — semi-evergreen tropical tree with high market demand.");
        CROP_DESCRIPTIONS.put("Grapes",       "Temperate vine crop with excellent export potential and high value.");
        CROP_DESCRIPTIONS.put("Watermelon",   "Fast-growing summer fruit with high water content and consumer demand.");
        CROP_DESCRIPTIONS.put("Muskmelon",    "Warm-season melon with sweet flesh, popular in summer markets.");
        CROP_DESCRIPTIONS.put("Apple",        "Temperate fruit requiring chilling hours. High-value horticulture crop.");
        CROP_DESCRIPTIONS.put("Orange",       "Popular citrus fruit rich in Vitamin C. High market demand year-round.");
        CROP_DESCRIPTIONS.put("Papaya",       "Fast-growing tropical fruit with high nutritional value and medicinal uses.");
        CROP_DESCRIPTIONS.put("Coconut",      "Multipurpose palm crop providing food, oil, fiber and timber.");
        CROP_DESCRIPTIONS.put("Cotton",       "White gold of agriculture — major cash crop and textile raw material.");
        CROP_DESCRIPTIONS.put("Jute",         "Golden fiber — eco-friendly industrial crop suited for humid tropics.");
        CROP_DESCRIPTIONS.put("Coffee",       "Beverage crop requiring specific hill climate with high export value.");
        CROP_DESCRIPTIONS.put("Sugarcane",    "Major commercial crop for sugar, ethanol and bio-energy production.");
        CROP_DESCRIPTIONS.put("Turmeric",     "High-value spice and medicinal crop with consistent market demand.");

        // Growing tips
        CROP_TIPS.put("Rice",         "Maintain 5–7 cm water level during vegetative stage. Use SRI method for 30% water savings.");
        CROP_TIPS.put("Wheat",        "Sow in October–November. First irrigation critical at crown root initiation stage (21 DAS).");
        CROP_TIPS.put("Maize",        "Ensure proper drainage. Apply 1/3 N at sowing, 1/3 at knee-height, 1/3 at tasseling.");
        CROP_TIPS.put("Chickpea",     "Treat seeds with Rhizobium culture. Avoid excess moisture; one pre-sowing irrigation sufficient.");
        CROP_TIPS.put("Kidney Beans", "Use raised beds for drainage. Mulching reduces soil moisture loss.");
        CROP_TIPS.put("Pigeon Peas",  "Deep-tap root; drought-tolerant once established. Excellent for intercropping with cereals.");
        CROP_TIPS.put("Moth Beans",   "Thrives on marginal land. Minimal irrigation; relies on residual moisture.");
        CROP_TIPS.put("Mung Bean",    "Short 60–70 day duration. Harvest pods when 90% turned black-brown.");
        CROP_TIPS.put("Black Gram",   "Sow in July for kharif. Avoid waterlogging; improves soil organic matter.");
        CROP_TIPS.put("Lentil",       "Sow October–November on residual moisture. Weed control critical in early stages.");
        CROP_TIPS.put("Pomegranate",  "Prune for open canopy. Drip irrigation saves 40% water. Bag fruits to prevent pests.");
        CROP_TIPS.put("Banana",       "Plant at 1.5×1.5 m spacing. Earthing up and propping essential against wind.");
        CROP_TIPS.put("Mango",        "Allow 2 years establishment. Do not irrigate 3 months before flowering.");
        CROP_TIPS.put("Grapes",       "Train on bower/Y-trellis. Gibberellin spray improves berry size in table varieties.");
        CROP_TIPS.put("Watermelon",   "Use plastic mulch and drip irrigation. Pollinator presence essential for fruit set.");
        CROP_TIPS.put("Muskmelon",    "Direct sow on ridges. Stop irrigation 10 days before harvest for sweetness.");
        CROP_TIPS.put("Apple",        "Requires 1000–1200 chilling hours. Thinning at marble stage improves fruit size.");
        CROP_TIPS.put("Orange",       "Budding on Rangpur lime rootstock improves drought tolerance.");
        CROP_TIPS.put("Papaya",       "Plant female/hermaphrodite seedlings. Virus-free planting material is critical.");
        CROP_TIPS.put("Coconut",      "Apply 2 kg salt/tree at base in coastal areas. Cross-pollination improves nut set.");
        CROP_TIPS.put("Cotton",       "Bt cotton reduces pesticide cost. Monitor for pink bollworm; use pheromone traps.");
        CROP_TIPS.put("Jute",         "Retting in clean running water for 20 days gives superior fibre quality.");
        CROP_TIPS.put("Coffee",       "Grow under shade trees. Pulping and wet processing critical for Arabica quality.");
        CROP_TIPS.put("Sugarcane",    "Use single-eye or two-eye sett method. Trash mulching conserves moisture.");
        CROP_TIPS.put("Turmeric",     "Plant in May–June. Mulch with paddy straw after 45 days. Cure rhizomes before storage.");

        // Seasons
        CROP_SEASONS.put("Rice", "Kharif (June–November)");
        CROP_SEASONS.put("Wheat", "Rabi (October–March)");
        CROP_SEASONS.put("Maize", "Kharif / Rabi / Zaid");
        CROP_SEASONS.put("Chickpea", "Rabi (October–March)");
        CROP_SEASONS.put("Kidney Beans", "Kharif / Rabi");
        CROP_SEASONS.put("Pigeon Peas", "Kharif (June–December)");
        CROP_SEASONS.put("Moth Beans", "Kharif (June–October)");
        CROP_SEASONS.put("Mung Bean", "Kharif / Zaid");
        CROP_SEASONS.put("Black Gram", "Kharif (June–October)");
        CROP_SEASONS.put("Lentil", "Rabi (October–March)");
        CROP_SEASONS.put("Pomegranate", "Perennial (Harvest: Feb–May / Aug–Nov)");
        CROP_SEASONS.put("Banana", "Year-round (12–18 months)");
        CROP_SEASONS.put("Mango", "Perennial (Harvest: April–July)");
        CROP_SEASONS.put("Grapes", "Rabi (Harvest: Feb–April)");
        CROP_SEASONS.put("Watermelon", "Zaid (Feb–June)");
        CROP_SEASONS.put("Muskmelon", "Zaid (Feb–June)");
        CROP_SEASONS.put("Apple", "Rabi (Harvest: July–October)");
        CROP_SEASONS.put("Orange", "Year-round (Harvest: Nov–Jan)");
        CROP_SEASONS.put("Papaya", "Year-round (9–11 months)");
        CROP_SEASONS.put("Coconut", "Perennial (Year-round)");
        CROP_SEASONS.put("Cotton", "Kharif (April–November)");
        CROP_SEASONS.put("Jute", "Kharif (March–July)");
        CROP_SEASONS.put("Coffee", "Perennial (Harvest: Nov–Feb)");
        CROP_SEASONS.put("Sugarcane", "Year-round (12–18 months)");
        CROP_SEASONS.put("Turmeric", "Kharif (May–January)");
    }

    /**
     * Core recommendation logic: computes a fit score for each crop
     * based on how well the 7 input parameters fall within ideal ranges.
     */
    public RecommendationDTO recommend(SoilInputDTO input, String username) {
        Map<String, Double> scores = new LinkedHashMap<>();

        for (Map.Entry<String, double[]> entry : CROP_PARAMS.entrySet()) {
            String crop = entry.getKey();
            double[] p = entry.getValue();

            double score = computeScore(
                input.getNitrogen(),    p[0],  p[1],
                input.getPhosphorus(),  p[2],  p[3],
                input.getPotassium(),   p[4],  p[5],
                input.getPh(),          p[6],  p[7],
                input.getTemperature(), p[8],  p[9],
                input.getHumidity(),    p[10], p[11],
                input.getRainfall(),    p[12], p[13]
            );
            scores.put(crop, score);
        }

        // Sort by score descending
        List<Map.Entry<String, Double>> sorted = scores.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toList());

        String primaryCrop = sorted.get(0).getKey();
        double primaryScore = sorted.get(0).getValue();

        List<RecommendationDTO.AlternativeCrop> alternatives = new ArrayList<>();
        for (int i = 1; i <= Math.min(4, sorted.size() - 1); i++) {
            String altCrop = sorted.get(i).getKey();
            double altScore = sorted.get(i).getValue();
            alternatives.add(RecommendationDTO.AlternativeCrop.builder()
                .cropName(altCrop)
                .confidence(Math.round(altScore * 1000.0) / 10.0)
                .reason("Soil and climate compatibility: " + String.format("%.0f%%", altScore * 100))
                .build());
        }

        // Soil health analysis
        String soilHealth = analyzeSoilHealth(input);
        List<String> insights = generateInsights(input);
        List<String> advice = generateActionableAdvice(input, primaryCrop);

        // Calendar
        Map<String, String> calendar = new LinkedHashMap<>();
        String season = CROP_SEASONS.getOrDefault(primaryCrop, "Varies");
        calendar.put("Season", season);
        calendar.put("Sowing Window", getSowingWindow(primaryCrop));
        calendar.put("Expected Harvest", getHarvestWindow(primaryCrop));

        // Persist to history
        Long historyId = null;
        if (username != null) {
            try {
                User user = userRepository.findByUsername(username)
                    .orElse(null);
                if (user != null) {
                    String alt1 = sorted.size() > 1 ? sorted.get(1).getKey() : null;
                    String alt2 = sorted.size() > 2 ? sorted.get(2).getKey() : null;

                    FarmingHistory history = FarmingHistory.builder()
                        .user(user)
                        .nitrogen(input.getNitrogen())
                        .phosphorus(input.getPhosphorus())
                        .potassium(input.getPotassium())
                        .ph(input.getPh())
                        .temperature(input.getTemperature())
                        .humidity(input.getHumidity())
                        .rainfall(input.getRainfall())
                        .recommendedCrop(primaryCrop)
                        .alternativeCrop1(alt1)
                        .alternativeCrop2(alt2)
                        .confidenceScore(Math.round(primaryScore * 1000.0) / 10.0)
                        .season(input.getSeason())
                        .notes(input.getNotes())
                        .build();
                    FarmingHistory saved = historyRepository.save(history);
                    historyId = saved.getId();
                }
            } catch (Exception e) {
                // Non-critical — continue
            }
        }

        return RecommendationDTO.builder()
            .primaryCrop(primaryCrop)
            .primaryConfidence(Math.round(primaryScore * 1000.0) / 10.0)
            .primaryDescription(CROP_DESCRIPTIONS.getOrDefault(primaryCrop, ""))
            .primaryGrowingTips(CROP_TIPS.getOrDefault(primaryCrop, ""))
            .alternatives(alternatives)
            .soilHealthStatus(soilHealth)
            .soilInsights(insights)
            .actionableAdvice(advice)
            .cropCalendar(calendar)
            .historyId(historyId)
            .build();
    }

    /**
     * Computes a composite fit score [0, 1] for one crop.
     * Returns 1.0 if value is within ideal range, tapers off outside.
     */
    private double computeScore(double n, double nMin, double nMax,
                                double p, double pMin, double pMax,
                                double k, double kMin, double kMax,
                                double ph, double phMin, double phMax,
                                double temp, double tMin, double tMax,
                                double humid, double hMin, double hMax,
                                double rain, double rMin, double rMax) {
        double[] weights = {0.18, 0.15, 0.15, 0.17, 0.15, 0.10, 0.10};
        double[] paramScores = {
            paramScore(n, nMin, nMax),
            paramScore(p, pMin, pMax),
            paramScore(k, kMin, kMax),
            paramScore(ph, phMin, phMax),
            paramScore(temp, tMin, tMax),
            paramScore(humid, hMin, hMax),
            paramScore(rain, rMin, rMax)
        };
        double total = 0;
        for (int i = 0; i < 7; i++) total += weights[i] * paramScores[i];
        return total;
    }

    private double paramScore(double value, double min, double max) {
        if (value >= min && value <= max) return 1.0;
        double mid = (min + max) / 2.0;
        double range = (max - min) / 2.0;
        if (range == 0) return value == min ? 1.0 : 0.0;
        double distance = value < min ? (min - value) : (value - max);
        double penalty = distance / range;
        return Math.max(0, 1.0 - penalty * 0.6);
    }

    private String analyzeSoilHealth(SoilInputDTO input) {
        int goodCount = 0;
        if (input.getNitrogen() >= 40 && input.getNitrogen() <= 140) goodCount++;
        if (input.getPhosphorus() >= 20 && input.getPhosphorus() <= 100) goodCount++;
        if (input.getPotassium() >= 20 && input.getPotassium() <= 100) goodCount++;
        if (input.getPh() >= 6.0 && input.getPh() <= 7.5) goodCount++;
        if (goodCount == 4) return "EXCELLENT";
        if (goodCount == 3) return "GOOD";
        if (goodCount == 2) return "MODERATE";
        return "NEEDS_IMPROVEMENT";
    }

    private List<String> generateInsights(SoilInputDTO input) {
        List<String> insights = new ArrayList<>();
        double n = input.getNitrogen(), p = input.getPhosphorus(),
               k = input.getPotassium(), ph = input.getPh();

        if (n < 40)       insights.add("⚠ Low Nitrogen: Apply urea or DAP to boost vegetative growth.");
        else if (n > 140) insights.add("⚠ Excess Nitrogen: Reduce N fertilizer to avoid lodging and pest attack.");
        else              insights.add("✓ Nitrogen level is within optimal range.");

        if (p < 20)       insights.add("⚠ Low Phosphorus: Apply SSP or rock phosphate to improve root development.");
        else if (p > 100) insights.add("⚠ High Phosphorus: May cause zinc deficiency — monitor micronutrients.");
        else              insights.add("✓ Phosphorus level is adequate.");

        if (k < 20)       insights.add("⚠ Low Potassium: Apply MOP (muriate of potash) for disease resistance.");
        else if (k > 100) insights.add("⚠ Excess Potassium: Can inhibit magnesium uptake.");
        else              insights.add("✓ Potassium level is satisfactory.");

        if (ph < 5.5)     insights.add("⚠ Acidic soil (pH " + ph + "): Apply agricultural lime to raise pH.");
        else if (ph > 8.0)insights.add("⚠ Alkaline soil (pH " + ph + "): Apply gypsum or elemental sulfur.");
        else if (ph >= 6.0 && ph <= 7.5) insights.add("✓ Soil pH is ideal for most crops.");
        else              insights.add("ℹ Soil pH (" + ph + ") is acceptable but can be optimised.");

        return insights;
    }

    private List<String> generateActionableAdvice(SoilInputDTO input, String crop) {
        List<String> advice = new ArrayList<>();
        advice.add("🌱 " + CROP_TIPS.getOrDefault(crop, "Follow recommended agronomic practices."));
        advice.add("💧 Ensure adequate moisture during critical growth stages.");
        advice.add("🌡 Monitor temperature extremes that may stress " + crop + " plants.");
        advice.add("📋 Maintain records of inputs used for informed decision-making next season.");
        advice.add("🔬 Conduct soil test every 2 years to track nutrient changes.");
        return advice;
    }

    private String getSowingWindow(String crop) {
        Map<String, String> sowing = new HashMap<>();
        sowing.put("Rice", "June – July"); sowing.put("Wheat", "October – November");
        sowing.put("Maize", "June – July"); sowing.put("Cotton", "April – May");
        sowing.put("Sugarcane", "February – March"); sowing.put("Chickpea", "October – November");
        sowing.put("Banana", "June – September"); sowing.put("Mango", "July – August");
        sowing.put("Turmeric", "May – June"); sowing.put("Coffee", "June – July");
        return sowing.getOrDefault(crop, "Consult local extension officer");
    }

    private String getHarvestWindow(String crop) {
        Map<String, String> harvest = new HashMap<>();
        harvest.put("Rice", "October – December"); harvest.put("Wheat", "March – April");
        harvest.put("Maize", "September – November"); harvest.put("Cotton", "October – January");
        harvest.put("Sugarcane", "January – March"); harvest.put("Chickpea", "February – March");
        harvest.put("Banana", "12–18 months after planting"); harvest.put("Mango", "May – July");
        harvest.put("Turmeric", "January – February"); harvest.put("Coffee", "November – February");
        return harvest.getOrDefault(crop, "Varies by sowing date");
    }

    public Map<String, Object> getDashboardStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return stats;

        long totalConsultations = historyRepository.countByUserId(user.getId());
        List<FarmingHistory> recent = historyRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        String mostRecentCrop = recent.isEmpty() ? "None" : recent.get(0).getRecommendedCrop();
        List<Object[]> freq = historyRepository.findCropFrequencyByUserId(user.getId());
        String topCrop = freq.isEmpty() ? "None" : freq.get(0)[0].toString();

        stats.put("totalConsultations", totalConsultations);
        stats.put("mostRecentCrop", mostRecentCrop);
        stats.put("topCrop", topCrop);
        stats.put("user", user);
        return stats;
    }

    public List<String> getAllCrops() {
        return new ArrayList<>(CROP_PARAMS.keySet());
    }

    public Map<String, Object> getCropInfo(String cropName) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", cropName);
        info.put("description", CROP_DESCRIPTIONS.getOrDefault(cropName, ""));
        info.put("tips", CROP_TIPS.getOrDefault(cropName, ""));
        info.put("season", CROP_SEASONS.getOrDefault(cropName, ""));
        double[] params = CROP_PARAMS.getOrDefault(cropName, new double[]{});
        if (params.length > 0) {
            info.put("idealN", params[0] + "–" + params[1]);
            info.put("idealP", params[2] + "–" + params[3]);
            info.put("idealK", params[4] + "–" + params[5]);
            info.put("idealPH", params[6] + "–" + params[7]);
            info.put("idealTemp", params[8] + "–" + params[9] + " °C");
            info.put("idealHumidity", params[10] + "–" + params[11] + " %");
            info.put("idealRainfall", params[12] + "–" + params[13] + " mm");
        }
        return info;
    }
}
