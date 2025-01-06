import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadedImageProcessor implements ImageProcessor {
    private final int threads;
    private final BrightnessAdjuster adjuster;

    public MultithreadedImageProcessor(int threads, int adjustment) {
        this.threads = threads;
        this.adjuster = new BrightnessAdjuster(adjustment);
    }

    @Override
    public BufferedImage processImage(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        int blockHeight = image.getHeight() / threads;

        for (int i = 0; i < threads; i++) {
            int startY = i * blockHeight;
            int endY = (i == threads - 1) ? image.getHeight() : (i + 1) * blockHeight;
            executor.execute(() -> processBlock(image, result, startY, endY));
        }

        executor.shutdown();
        while (!executor.isTerminated());
        return result;
    }

    private void processBlock(BufferedImage src, BufferedImage dest, int startY, int endY) {
        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                Color adjusted = adjuster.adjust(new Color(src.getRGB(x, y)));
                dest.setRGB(x, y, adjusted.getRGB());
            }
        }
    }
}
