import java.util.HashSet;
import java.util.Set;
import java.util.Set;

public class PowerSet {

    public static Set<Set<Token[]>> powerSetWithMaxSize(Set<Token[]> originalSet, int i) {
        long resultSize = (long) Math.pow(2, originalSet.size());
        Set<Set<Token[]>> resultPowerSet = new HashSet<>();

        resultPowerSet.add(new HashSet<>(0));

        for (Token[] itemFromOriginalSet : originalSet) {
            long startingResultSize = resultPowerSet.size();
            Set<Set<Token[]>> addAll = new HashSet<>();
            for (Set<Token[]> oldSubset : resultPowerSet) {
                if (oldSubset.size() == i)                      // more than four combinations can't be a winning hand
                    continue;

                Set<Token[]> newSubset = new HashSet<>(oldSubset);
                newSubset.add(itemFromOriginalSet);

                addAll.add(newSubset);
            }
            resultPowerSet.addAll(addAll);
        }
        return resultPowerSet;
    }
}