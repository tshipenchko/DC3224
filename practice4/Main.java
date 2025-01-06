import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class Main {
    private static final String OUTPUT_DIR = "./tmp";

    public static void main(String[] args) throws IOException {
        List<String> images = List.of("200x300.jpeg", "1280x720.jpeg", "1920x1080.jpeg", "3840x2160.jpeg");
        processImages(images, new ParallelImageProcessor(50), "processed_parallel");
        processImages(images, new MultithreadedImageProcessor(4, 50), "processed_threaded");
    }

    private static void processImages(List<String> paths, ImageProcessor processor, String prefix) throws IOException {
        ensureOutputDirExists();
        for (String path : paths) {
            path = OUTPUT_DIR + "/" + path;
            long start = System.nanoTime();
            System.out.printf("%s: Going to process %s%n", prefix, path);
            BufferedImage processed = processor.processImage(loadImage(path));
            long duration = System.nanoTime() - start;
            saveImage(processed, OUTPUT_DIR + "/" + prefix + "_" + new File(path).getName());
            System.out.printf("%s: Processed %s in %d ms%n", prefix, path, duration / 1_000_000);
        }
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    private static void saveImage(BufferedImage image, String path) throws IOException {
        ImageIO.write(image, "jpg", new File(path));
    }

    private static void ensureOutputDirExists() {
        new File(OUTPUT_DIR).mkdirs();
    }
}
