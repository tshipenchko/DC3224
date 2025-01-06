import java.awt.Color;

public class BrightnessAdjuster {
    private final int adjustment;

    public BrightnessAdjuster(int adjustment) {
        this.adjustment = adjustment;
    }

    public Color adjust(Color color) {
        int red = adjustChannel(color.getRed());
        int green = adjustChannel(color.getGreen());
        int blue = adjustChannel(color.getBlue());
        return new Color(red, green, blue);
    }

    private int adjustChannel(int value) {
        return Math.min(255, Math.max(0, value + adjustment));
    }
}
