package com.aicrop.service;

import com.aicrop.model.Disease;
import com.aicrop.repository.DiseaseRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @PostConstruct
    public void seedDiseases() {
        if (diseaseRepository.count() > 0) return;

        List<Disease> diseases = List.of(
            Disease.builder().name("Blast").affectedCrop("Rice").category("FUNGAL").severity("HIGH")
                .symptoms("Diamond-shaped lesions with grey centers on leaves; neck rot at panicle base; sheath blight.")
                .causes("Fungus Magnaporthe oryzae. Favoured by high humidity, dense planting, excess nitrogen.")
                .remedialMeasures("Spray Tricyclazole 75 WP @ 0.6 g/L or Carbendazim 50 WP @ 1 g/L at first sign.")
                .preventiveMeasures("Use resistant varieties. Balanced nitrogen application. Avoid dense planting. Treat seeds with Thiram.").build(),

            Disease.builder().name("Brown Plant Hopper").affectedCrop("Rice").category("PEST").severity("CRITICAL")
                .symptoms("Hopperburn — circular drying patches. Honeydew secretion causing sooty mold. Plants collapse.")
                .causes("Insect pest Nilaparvata lugens. Monoculture and excessive nitrogen promote outbreaks.")
                .remedialMeasures("Apply Imidacloprid 17.8 SL @ 0.25 ml/L or Buprofezin @ 1.5 ml/L.")
                .preventiveMeasures("Use BPH-resistant varieties. Avoid excess urea. Maintain 5–7 cm water level.").build(),

            Disease.builder().name("Rust (Yellow/Brown)").affectedCrop("Wheat").category("FUNGAL").severity("HIGH")
                .symptoms("Orange/yellow pustules on leaves (yellow rust) or brown urediniospores (brown rust). Yellowing and leaf drying.")
                .causes("Puccinia striiformis (yellow) or P. recondita (brown). Cool humid weather favours spread.")
                .remedialMeasures("Spray Propiconazole 25 EC @ 0.1% or Mancozeb 75 WP @ 0.25% at first appearance.")
                .preventiveMeasures("Plant rust-resistant varieties. Avoid late sowing. Treat seeds with Carboxin.").build(),

            Disease.builder().name("Loose Smut").affectedCrop("Wheat").category("FUNGAL").severity("MEDIUM")
                .symptoms("Entire ear replaced by black powdery mass (teliospores). Affected ears appear before healthy ones.")
                .causes("Seed-borne fungus Ustilago tritici. Infection occurs at flowering in moist conditions.")
                .remedialMeasures("No effective field spray once infected. Remove and destroy smutted ears immediately.")
                .preventiveMeasures("Use certified seed treated with Carboxin 75 WP or Vitavax Power.").build(),

            Disease.builder().name("Downy Mildew").affectedCrop("Maize").category("FUNGAL").severity("HIGH")
                .symptoms("White downy fungal growth on underside of leaves. Systemic infection causes complete leaf chlorosis.")
                .causes("Peronosclerospora sorghi / P. philippinensis. Spread by air-borne conidia in humid conditions.")
                .remedialMeasures("Spray Metalaxyl + Mancozeb (Ridomil Gold) @ 2 g/L or Dithane M-45 @ 2.5 g/L.")
                .preventiveMeasures("Seed treatment with Metalaxyl 35 SD @ 6 g/kg seed. Grow tolerant hybrids.").build(),

            Disease.builder().name("Fall Armyworm").affectedCrop("Maize").category("PEST").severity("CRITICAL")
                .symptoms("Windowpane feeding on leaves; large irregular holes; saw-dust like frass in whorl. Defoliation.")
                .causes("Invasive pest Spodoptera frugiperda. Rapid spread in warm humid conditions.")
                .remedialMeasures("Apply Emamectin benzoate 5 SG @ 0.4 g/L or Spinetoram @ 0.5 ml/L into whorl.")
                .preventiveMeasures("Set up pheromone traps @ 5/ha. Early monitoring; fall ploughing destroys pupae.").build(),

            Disease.builder().name("Wilt").affectedCrop("Chickpea").category("FUNGAL").severity("HIGH")
                .symptoms("Sudden wilting of plants without yellowing. Dark brown discolouration in vascular tissue.")
                .causes("Soilborne fungus Fusarium oxysporum f.sp. ciceris. Survives in soil for years.")
                .remedialMeasures("No effective cure. Uproot and destroy wilted plants. Avoid replanting chickpea in same field.")
                .preventiveMeasures("Use wilt-resistant varieties (JG 74, WR 315). Treat seed with Trichoderma viride @ 4 g/kg.").build(),

            Disease.builder().name("Pod Borer").affectedCrop("Chickpea").category("PEST").severity("HIGH")
                .symptoms("Larvae bore into pods and feed on developing seeds. Flowers and young pods desiccate.")
                .causes("Helicoverpa armigera. High pest pressure during podding under warm dry conditions.")
                .remedialMeasures("Spray Emamectin benzoate 5 SG @ 0.4 g/L or Indoxacarb 15.8 SC @ 0.75 ml/L.")
                .preventiveMeasures("Set pheromone traps @ 5/ha. Install T-shaped perches for bird predators. Neem-based sprays at pre-pod stage.").build(),

            Disease.builder().name("Bollworm Complex").affectedCrop("Cotton").category("PEST").severity("CRITICAL")
                .symptoms("Bore holes in squares, flowers and bolls. Reddish frass. Premature square shedding.")
                .causes("American (Helicoverpa armigera), Spotted (Earias vittella) and Pink (Pectinophora gossypiella) bollworms.")
                .remedialMeasures("Spray Indoxacarb 15.8 SC @ 0.5 ml/L alternating with Emamectin benzoate 5 SG @ 0.3 g/L.")
                .preventiveMeasures("Grow Bt cotton. Pheromone traps @ 5/ha. Collect and destroy fallen bolls.").build(),

            Disease.builder().name("Leaf Curl Virus").affectedCrop("Cotton").category("VIRAL").severity("CRITICAL")
                .symptoms("Upward/downward leaf curling, thickened veins, enations on underside, stunted growth.")
                .causes("Cotton Leaf Curl Virus (CLCuV) transmitted by whitefly (Bemisia tabaci).")
                .remedialMeasures("No cure. Remove and destroy infected plants. Control whitefly vector immediately.")
                .preventiveMeasures("Use resistant varieties. Control whitefly with Imidacloprid or Thiamethoxam soil drench.").build(),

            Disease.builder().name("Panama Wilt").affectedCrop("Banana").category("FUNGAL").severity("CRITICAL")
                .symptoms("Yellowing of older leaves progressing inward. Split pseudostem reveals brown vascular discolouration. Plant death.")
                .causes("Fusarium oxysporum f.sp. cubense. Soil-borne; persists indefinitely. Race 4 affects Cavendish.")
                .remedialMeasures("No cure. Destroy affected plants. Fumigate soil with Metham sodium.")
                .preventiveMeasures("Plant resistant varieties (Grand Naine where possible). Use tissue-culture planting material. Avoid soil movement.").build(),

            Disease.builder().name("Sigatoka Leaf Spot").affectedCrop("Banana").category("FUNGAL").severity("HIGH")
                .symptoms("Yellow streaks on leaves that enlarge into brown necrotic spots with yellow halo. Premature leaf death.")
                .causes("Mycosphaerella musicola (Yellow Sigatoka) or M. fijiensis (Black Sigatoka). Spread by rain splash.")
                .remedialMeasures("Spray Propiconazole 25 EC @ 1 ml/L or Mancozeb 75 WP @ 2.5 g/L alternately.")
                .preventiveMeasures("Remove dead leaves. Ensure adequate spacing for air circulation. Avoid excess irrigation.").build(),

            Disease.builder().name("Powdery Mildew").affectedCrop("Grapes").category("FUNGAL").severity("HIGH")
                .symptoms("White powdery coating on young leaves, shoots and berries. Infected berries crack and dry.")
                .causes("Uncinula necator. Favoured by moderate temperatures (22–28°C) and high humidity.")
                .remedialMeasures("Spray Wettable Sulphur 80 WP @ 3 g/L or Triadimefon 25 WP @ 0.5 g/L.")
                .preventiveMeasures("Train vines for open canopy and air circulation. Copper-based dormant sprays.").build(),

            Disease.builder().name("Nitrogen Deficiency").affectedCrop("General").category("NUTRIENT_DEFICIENCY").severity("MEDIUM")
                .symptoms("Uniform yellowing (chlorosis) of older leaves first. Stunted growth. Pale green to yellow colour throughout plant.")
                .causes("Insufficient nitrogen in soil; leaching; waterlogging; poor organic matter.")
                .remedialMeasures("Apply urea @ 20 kg/ha as top dressing or 1% urea foliar spray for quick recovery.")
                .preventiveMeasures("Split nitrogen application. Incorporate organic matter. Avoid waterlogging.").build(),

            Disease.builder().name("Potassium Deficiency").affectedCrop("General").category("NUTRIENT_DEFICIENCY").severity("MEDIUM")
                .symptoms("Marginal scorching (burning) of older leaves. Brown leaf margins. Weak stems; poor grain filling.")
                .causes("Leached or inherently K-deficient sandy soils. Excess nitrogen or calcium inhibits K uptake.")
                .remedialMeasures("Apply Muriate of Potash (MOP) @ 60 kg K₂O/ha. Foliar K spray at flowering.")
                .preventiveMeasures("Maintain soil K balance. Avoid one-sided N application. Test soil annually.").build(),

            Disease.builder().name("Anthracnose").affectedCrop("Mango").category("FUNGAL").severity("HIGH")
                .symptoms("Dark sunken lesions on fruits, flowers and leaves. Flower blight causes fruit drop. Tear-stain on ripe fruits.")
                .causes("Colletotrichum gloeosporioides. Wet weather during flowering promotes infection.")
                .remedialMeasures("Spray Mancozeb 75 WP @ 2 g/L or Carbendazim 50 WP @ 1 g/L at bud break and fortnightly.")
                .preventiveMeasures("Prune for open canopy. Collect and destroy fallen leaves/fruits. Use copper-based pre-bloom sprays.").build(),

            Disease.builder().name("Stem Borer").affectedCrop("Sugarcane").category("PEST").severity("HIGH")
                .symptoms("Dead hearts in early stage; internodal reddening; tunnelling in nodes. Internode shortening.")
                .causes("Chilo infuscatellus (early); Scirpophaga nivella (top/internode borer). Active in warm humid weather.")
                .remedialMeasures("Apply Chlorantraniliprole (Coragen) 18.5 SC @ 0.3 ml/L or release Trichogramma egg parasitoids.")
                .preventiveMeasures("Use healthy seed stools. Hot water treatment of setts. Early monitoring with light traps.").build()
        );

        diseaseRepository.saveAll(diseases);
    }

    public List<Disease> searchDiseases(String keyword) {
        return diseaseRepository.searchBySymptomOrName(keyword);
    }

    public List<Disease> getDiseasesByCrop(String crop) {
        return diseaseRepository.findByAffectedCropIgnoreCase(crop);
    }

    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }
}
