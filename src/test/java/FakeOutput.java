import java.util.ArrayList;
import java.util.List;

public class FakeOutput implements Output {
    public List<String> output = new ArrayList<>();

    @Override
    public void println(String string) {
        output.add(string);
    }

}