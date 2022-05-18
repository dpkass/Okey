package SpecialSets;

import java.util.*;

public class Sets<T> {


    /**
     * @param originalSet
     * @param max
     * @return
     */
    public Set<Set<T>> powerSetWithMaxSize(Set<T> originalSet, int max) {
        long resultSize = (long) Math.pow(2, originalSet.size());
        Set<Set<T>> resultPowerSet = new HashSet<>();

        resultPowerSet.add(new HashSet<>(0));

        for (T itemFromOriginalSet : originalSet) {
            long startingResultSize = resultPowerSet.size();
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
     * @param set Set to create subsets of
     * @param min minimum size of subsets
     * @return all subsets of a given set, that have consecutive elements
     */
    public Set<T[]> subsetsWithMinSize(List<T> list, int min) {
        Set<T[]> res = new HashSet<>();

        for (int i = 0; i <= list.size() - min; i++)                           // i is start of T[]
            for (int j = 0; list.size() - j >= i + min; j++) {                 // j makes range of T[] smaller (min 3)
                List<T> temp = new ArrayList<>();
                for (int k = i; k < list.size() - j; k++) temp.add(list.get(k));
                res.add((T[]) temp.toArray());
            }
        return res;
    }
}