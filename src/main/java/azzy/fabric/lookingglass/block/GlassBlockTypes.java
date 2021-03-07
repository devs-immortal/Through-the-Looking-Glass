package azzy.fabric.lookingglass.block;

public enum GlassBlockTypes {
    DARK_GLASS("dark_glass"),
    GHOST_GLASS("ghost_glass"),
    DARK_GHOST_GLASS("dark_ghost_glass"),
    RED_GLASS("red_glass"),
    GLOW_GLASS("glow_glass"),
    ETHEREAL("ethereal"),
    DARK_ETHEREAL("dark_ethereal"),
    REVERSE_ETHEREAL("reverse_ethereal"),
    DARK_REVERSE_ETHEREAL("reverse_dark_ethereal");

    private final String glassType;

    GlassBlockTypes(String glassType) {
        this.glassType = glassType;
    }
}