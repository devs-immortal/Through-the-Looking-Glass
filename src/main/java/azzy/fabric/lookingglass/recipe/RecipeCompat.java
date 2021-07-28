package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.datagen.RecipeJsonGen;
import azzy.fabric.lookingglass.LookingGlassCommon;

public class RecipeCompat {

    private static final String[] GRINDING;

    public static void init() {
        if(LookingGlassCommon.DEV_ENV && LookingGlassCommon.REGEN_RECIPES) {
            for (String oreGroup : GRINDING) {

                String[] components = oreGroup.split("\\|");
                String name = components[0];

                genGrinderRecipe(name + "_ore_to_dusts", new String[]{ "c:" + components[0] + "_ores", "c:" + components[0] + "_dusts", "3", "c:" + components[1] + "_dusts", "1", String.valueOf((Float.parseFloat(components[2]) * 2))});

                genGrinderRecipe(name + "_raw_to_dusts", new String[]{ "c:raw_" + components[0] + "_ores", "c:" + components[0] + "_dusts", "2", "c:" + components[1] + "_dusts", "1", String.valueOf((Float.parseFloat(components[2]) * (2F/3F)))});

                genGrinderRecipe(name + "_ingot_to_dust", new String[]{ "c:" + components[0] + "_ingots", "c:" + components[0] + "_dusts", "1", "c:null", "0", "0" });

                genGrinderRecipe(name + "_block_to_dust", new String[]{ "c:" + components[0] + "_blocks", "c:" + components[0] + "_dusts", "9", "c:null", "0", "0" });
            }
        }


    }

    public static void genGrinderRecipe(String name, String[] components) {
        RecipeJsonGen.genDynamicRecipe(LookingGlassCommon.METADATA, name, "grinding", GRINDER_TAG_PATTERN, components);
    }

    public static final String GRINDER_TAG_PATTERN = """
            {
              "type": "lookingglass:grinding",
              "optional": "true",
              "input": {
                "ingredient": [
                  {
                    "tag": "component_0"
                  }
                ]
              },
              "output": {
                "tag": "component_1",
                "count": component_2
              },
              "secondary": {
                "tag": "component_3",
                "count": component_4
              },
              "chance": component_5
            }
            """;

    static {

        GRINDING = new String[]{
                "copper|gold|0.25",
                "silver|sulfur|1",
                "lead|silver|0.5",
                "aluminum|aluminum|1",
                "manganese|sulfur|1.5",
                "mercury|silver|1",
                "nickel|platinum|0.1",
                "magnesium|sphalerite|0.2",
                "antimony|palladium|0.125",
                "palladium|platinum|0.5",
                "bauxite|titanium|0.05",
                "osmium|platinum|0.01",
                "platinum|nickel|1.25",
                "ruby|garnet|0.5",
                "titanium|aluminum|1",
                "uranium|lead|2",
                "thorium|uranium|0.1",
                "salt|clay|0.5",
                "cobalt|sulfur|0.25",
                "cinnabar|redstone|1",
                "chrome|chrome|0.05",
                "zinc|sapphire|0.1",
                "tin|tin|0.25",
                "sphalerite|cinnabar|0.5",
                "pyrite|sulfur|2",
                "sodalite|lazurite|0.2",
                "sapphire|peridot|0.15",
                "galena|lead|0.45",
                "sulfur|coal|0.25",
                "quartz|silicon|0.125",
                "gold|copper|0.625",
                "iron|nickel|0.15",
                "amethyst|quartz|0.15",
                "peridot|emerald|0.1",
                "metite|lunum|0.1",
                "lunum|metite|0.1",
                "asterite|none|0",
                "galaxium|galaxium|0.1",
                "invar|none|0",
                "electrum|none|0",
                "brass|none|0",
                "bronze|none|0",
                "steel|none|0",
                "stainless_steel|none|0",
                "battery_alloy|none|0",
                "sodium|none|0",
                "lithium|none|0",
                "refined_iron|none|0",
                "silicon|none|0"
        };
    }
}
