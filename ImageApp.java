package image_project;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageApp {
	private static final String INPUT_IMAGE_PATH = "Rain_Tree.jpg";
    private static final String OUTPUT_IMAGE_PATH = "Equalized_Image.jpg";
    private static final int NUM_BINS = 256;

    public static void main(String[] args) {
        // Load the input image
        BufferedImage inputImage = loadImage(INPUT_IMAGE_PATH);

        // Single-threaded version
        long start = System.currentTimeMillis();
        BufferedImage singleThreadResult = performImageApp(inputImage);
        long singleThreadTime = System.currentTimeMillis() - start;

        // Multi-threaded version
        int numOfThreads = 4; // Number of threads for parallel processing
        start = System.currentTimeMillis();
        BufferedImage multiThreadResult = performImageAppParallel(inputImage, numOfThreads);
        long multiThreadTime = System.currentTimeMillis() - start;

        // Save the results
        saveImage(singleThreadResult, "SingleThread_" + OUTPUT_IMAGE_PATH);
        saveImage(multiThreadResult, "MultiThread_" + OUTPUT_IMAGE_PATH);

        // Print the execution times
        System.out.println("Single-threaded execution time: " + singleThreadTime + " ms");
        System.out.println("Multi-threaded execution time: " + multiThreadTime + " ms");
    }

	private static BufferedImage performImageApp(BufferedImage inputImage) {
		int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        int[] histogram = computeHistogram(inputImage);
        int[] cumulativeHistogram = computeCumulativeHistogram(histogram, width * height);

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Perform histogram equalization on each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(inputImage.getRGB(x, y));
                int red = pixelColor.getRed();
                int green = pixelColor.getGreen();
                int blue = pixelColor.getBlue();

                // Apply equalization using the cumulative histogram
                int newRed = (int) (cumulativeHistogram[red] * (NUM_BINS - 1) / (width * height));
                int newGreen = (int) (cumulativeHistogram[green] * (NUM_BINS - 1) / (width * height));
                int newBlue = (int) (cumulativeHistogram[blue] * (NUM_BINS - 1) / (width * height));

                Color newPixelColor = new Color(newRed, newGreen, newBlue);
                outputImage.setRGB(x, y, newPixelColor.getRGB());
            }
        }

        return outputImage;
	}

	private static BufferedImage performImageAppParallel(BufferedImage inputImage, int numOfThreads) {
		int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        int[] histogram = computeHistogram(inputImage);
        int[] cumulativeHistogram = computeCumulativeHistogram(histogram, width * height);

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Thread[] threads = new Thread[numOfThreads];
        int rowsPerThread = height / numOfThreads;

        for (int i = 0; i < numOfThreads; i++) {
            final int threadIndex = i;

            threads[i] = new Thread(() -> {
                int startRow = threadIndex * rowsPerThread;
                int endRow = (threadIndex == numOfThreads - 1) ? height : (startRow + rowsPerThread);

                for (int y = startRow; y < endRow; y++) {
                    for (int x = 0; x < width; x++) {
                        Color pixelColor = new Color(inputImage.getRGB(x, y));
                        int red = pixelColor.getRed();
                        int green = pixelColor.getGreen();
                        int blue = pixelColor.getBlue();

                        // Apply equalization using the cumulative histogram
                        int newRed = (int) (cumulativeHistogram[red] * (NUM_BINS - 1) / (width * height));
                        int newGreen = (int) (cumulativeHistogram[green] * (NUM_BINS - 1) / (width * height));
                        int newBlue = (int) (cumulativeHistogram[blue] * (NUM_BINS - 1) / (width * height));

                        Color newPixelColor = new Color(newRed, newGreen, newBlue);
                        outputImage.setRGB(x, y, newPixelColor.getRGB());
                    }
                }
            });

            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return outputImage;
	}

	private static int[] computeCumulativeHistogram(int[] histogram, int i) {
		int[] histogram = new int[NUM_BINS];

        int width = image.getWidth();
        int height = image.getHeight();

        // Count pixel occurrences
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                int red = pixelColor.getRed();
                int green = pixelColor.getGreen();
                int blue = pixelColor.getBlue();

                int grayValue = (red + green + blue) / 3;
                histogram[grayValue]++;
            }
        }

        return histogram;
	}

	private static int[] computeHistogram(BufferedImage inputImage) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void saveImage(BufferedImage multiThreadResult, String string) {
		try {
            File output = new File(outputPath);
            ImageIO.write(image, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

	private static BufferedImage loadImage(String inputImagePath) {
		// TODO Auto-generated method stub
		return null;
	}  
    
    
    
    
    
    
}
	