import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class ParallelImageProcessor implements ImageProcessor {
    private final BrightnessAdjuster adjuster;

    public ParallelImageProcessor(int adjustment) {
        this.adjuster = new BrightnessAdjuster(adjustment);
    }

    @Override
    public BufferedImage processImage(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        IntStream.range(0, image.getHeight()).parallel().forEach(y ->
            IntStream.range(0, image.getWidth()).forEach(x -> {
                Color adjusted = adjuster.adjust(new Color(image.getRGB(x, y)));
                result.setRGB(x, y, adjusted.getRGB());
            })
        );
        return result;
    }
}
