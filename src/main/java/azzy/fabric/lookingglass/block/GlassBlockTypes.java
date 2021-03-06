package azzy.fabric.lookingglass.block;

public enum GlassBlockTypes {
    ETHEREAL("ethereal"),
    DARK_ETHEREAL("dark_ethereal"),
    REDSTONE("redstone"),
    REVERSE_ETHEREAL("reverse_ethereal");

    private final String glassType;

    GlassBlockTypes(String glassType) {
        this.glassType = glassType;
    }
}