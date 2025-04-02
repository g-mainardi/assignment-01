package pcd.ass01;

import java.util.ArrayList;
import java.util.List;

public class Partitioner {
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
}
