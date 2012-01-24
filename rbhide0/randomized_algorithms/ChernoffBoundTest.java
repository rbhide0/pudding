package rbhide0.randomized_algorithms;

import java.util.Arrays;

public class ChernoffBoundTest {
        // Generates a given number of random numbers.
        private static double[] generate(int number) {
                number = Math.max(number, 0);
                double[] array = new double[number];

                // Generate random numbers.
                for (int i=0; i<number; i++) {
                        array[i] = Math.random();
                }

                return array;
        }

        private static double mean(double[] numbers) {
                double sum = 0;
                for (int i=0; i<numbers.length; i++) {
                        sum += numbers[i];
                }

                return numbers.length > 0 ? (sum / numbers.length) : 0;
        }

        private static double variance(double[] numbers) {
                double mean = mean(numbers);

                double variance = 0;
                for (int i=0; i<numbers.length; i++) {
                        variance += ((numbers[i] - mean) * (numbers[i] - mean));
                }

                return variance;
        }

        private static double markovBound(double mean, double x) {
                double probabilityBound = (x > 0) && (mean > 0) ? mean/x : 1;
                // TODO: Sanitize result?
                /*
                if (probabilityBound > 1) {
                        probabilityBound = 1;
                } else if (probabilityBound < 0) {
                        probabilityBound = 0;
                }
                */
                return probabilityBound;
        }

        private static double chebychevBound(double mean, double variance, double x) {
                // TODO: assert that variance is non-negative.
                // P(|X-mean| >= c) <= variance/c^2
                double distanceFromMean = x - mean;
                return variance / (distanceFromMean * distanceFromMean);
        }

        private static double chernoffBound(double mean, double x) {
                if (x > mean) {
                        // 1/(e^mean) * (mean * e / x)^x
                        return Math.pow(Math.E, -mean) * Math.pow((mean * Math.E / x), x);
                } else {
                        // e^{(mean - x)^2/(2*mean)}
                        return Math.pow(Math.E, -((mean - x) * (mean - x))/(2*mean));
                }
        }

        private static void print(double[] numbers) {
                for (int i=0; i<numbers.length; i++) {
                        System.out.format("%.3f ", numbers[i]);
                }
                System.out.println();
        }

        public static void main(String[] args) {
                double[] numbers = generate(10);
                Arrays.sort(numbers);
                print(numbers);
                double mean = mean(numbers);
                double variance = variance(numbers);
                double sdeviation = Math.sqrt(variance);
                System.out.format("Mean: %.3f, Std Deviation: %.3f, Variance: %.3f\n", mean, sdeviation, variance);

                System.out.println("NUMBER\tMARKOV\tCHEBY\tCHERNOFF");
                for (int i=0; i<numbers.length; i++) {
                        double x = numbers[i];
                        double markovBound = markovBound(mean, x);
                        double chebychevBound = chebychevBound(mean, variance, x);
                        double chernoffBound = chernoffBound(mean, x);
                        System.out.format("%.3f\t%.3f\t%.3f\t%.3f\n", x, markovBound, chebychevBound, chernoffBound);
                }
        }
}
