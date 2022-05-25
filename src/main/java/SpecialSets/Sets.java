package SpecialSets;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;

public class Sets<T> {

    /**
     * Creates a powerset, where each set has a max size of the given param.
     *
     * @param originalSet set to create a powerset of
     * @param max         max size of the sets
     * @return the powerset
     */
    public Set<Set<T>> powerSetWithMaxSize(Set<T> originalSet, int max) {
        long resultSize = (long) Math.pow(2, originalSet.size());
        Set<Set<T>> resultPowerSet = new HashSet<>();

        resultPowerSet.add(new HashSet<>(0));

        for (T itemFromOriginalSet : originalSet) {
            Set<Set<T>> addAll = new HashSet<>();
            for (Set<T> oldSubset : resultPowerSet) {
                if (oldSubset.size() == max)                      // more than four combinations can't be a winning hand
                    continue;

                Set<T> newSubset = new HashSet<>(oldSubset);
                newSubset.add(itemFromOriginalSet);

                addAll.add(newSubset);
            }
            resultPowerSet.addAll(addAll);
        }
        return resultPowerSet;
    }

    /**
     * Creates subsets of a list with minimum size of 3. All partitions have consecutive elements.
     *
     * @param list Set to create subsets of
     * @param min  minimum size of subsets
     * @return all subsets of a given set, that have consecutive elements
     */
    public Set<T[]> subsetsWithMinSize(Class<T> clazz, List<T> list, int min) {
        Set<T[]> res = new HashSet<>();
        T[] template = (T[]) Array.newInstance(clazz, 0);

        for (int i = 0, s = list.size(); i <= s - min; i++)                             // i is start of T[]
            for (int j = 0; s - j >= i + min; j++)                                      // j makes range of T[] smaller
                res.add(list.subList(i, list.size() - j).toArray(template));
        return res;
    }
}