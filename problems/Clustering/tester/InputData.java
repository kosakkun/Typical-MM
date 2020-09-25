import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public class InputData implements Cloneable
{
    public static final int MAX_N = 1000;
    public static final int MIN_N = 100;
    public static final int MAX_K = 20;
    public static final int MIN_K = 5;
    public static final int MAX_X = 1000;
    public static final int MAX_Y = 1000;

    public int N;
    public int K;
    public int[] x;
    public int[] y;

    public InputData (
        final int N,
        final int K)
    {
        this.N = N;
        this.K = K;
        this.x = new int[N];
        this.y = new int[N];
    }

    @Override
    public String toString ()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(N).append(' ');
        sb.append(K).append('\n');
        for (int i = 0; i < N; ++i) {
            sb.append(x[i]).append(' ');
            sb.append(y[i]).append('\n');
        }
        
        return sb.toString();
    }

    @Override
    public InputData clone ()
    {
        InputData id = null;

        try {
            id = (InputData)super.clone();
            id.x = this.x.clone();
            id.y = this.y.clone();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public static InputData genInputData (
        final long seed)
        throws Exception
    {
        SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
        rnd.setSeed(seed);

        final int N = rnd.nextInt(MAX_N - MIN_N + 1) + MIN_N;
        final int K = rnd.nextInt(MAX_K - MIN_K + 1) + MIN_K;
        InputData id = new InputData(N, K);
        
        Set<Pair<Integer,Integer>> used = new HashSet<>();
        while (used.size() < id.N) {
            final int xt = rnd.nextInt(MAX_X + 1);
            final int yt = rnd.nextInt(MAX_Y + 1);
            Pair<Integer,Integer> p = Pair.of(xt, yt);
            if (used.contains(p)) continue;
            id.x[used.size()] = xt;
            id.y[used.size()] = yt;
            used.add(p);
        }

        return id;
    }
}
