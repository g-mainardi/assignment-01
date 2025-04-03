package pcd.ass01;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ListUtils {
    public static <E> List<List<E>> partition(List<E> elems, int numberOfPartitions) {
        List<List<E>> partitions = new ArrayList<>();
        for (int i = 0; i < numberOfPartitions; i++) {
            partitions.add(new ArrayList<E>());
        }
        for (int i = 0; i < elems.size(); i++) {
            partitions.get(i % numberOfPartitions).add(elems.get(i));
        }
        return partitions;
    }

    public static <T> Stream<List<T>> batchStreamByNumber(List<T> lst, int batchNumber) {
        return partition(lst, batchNumber).stream();
    }

    public static <T> Stream<List<T>> batchStreamBySize(List<T> lst, int batchSize) {
        int batchNumber = divideCeil(lst.size(), batchSize);
        return partition(lst, batchNumber).stream();
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
