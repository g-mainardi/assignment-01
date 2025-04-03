package pcd.ass01;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ListUtils {
    public static <E> List<List<E>> partitionByNumber(List<E> elems, int numberOfPartitions) {
        List<List<E>> partitions = new ArrayList<>();
        for (int i = 0; i < numberOfPartitions; i++) {
            partitions.add(new ArrayList<E>());
        }
        for (int i = 0; i < elems.size(); i++) {
            partitions.get(i % numberOfPartitions).add(elems.get(i));
        }
        return partitions;
    }

    public static <T> List<List<T>> partitionBySize(List<T> lst, int batchSize) {
        int batchNumber = divideCeil(lst.size(), batchSize);
        return partitionByNumber(lst, batchNumber);
    }

    public static <T> Stream<List<T>> batchStreamByNumber(List<T> lst, int batchNumber) {
        return partitionByNumber(lst, batchNumber).stream();
    }

    public static <T> Stream<List<T>> batchStreamBySize(List<T> lst, int batchSize) {
        return partitionBySize(lst, batchSize).stream();
    }

    private static int divideCeil(int dividend, int divisor) {
        return (dividend + divisor - 1) / divisor;
    }

    public static void testBatchStream() {
        List<Integer> numbers = IntStream.range(0, 25)
                .mapToObj(i->i+1)
                .toList();
        int batchSize = 10;
        int batchNumber = 5;

        batchStreamBySize(numbers, batchSize).forEach(batch -> {
            System.out.println("Processing batch by size: " + batch);
        });
        batchStreamByNumber(numbers, batchNumber).forEach(batch -> {
            System.out.println("Processing batch by number: " + batch);
        });
    }
}
