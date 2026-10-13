package git.artdeell.noblocklist;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;

public class PreMain {

    private static void doUrlTransform(Instrumentation inst) {
        BlockerTransformer blockerTransformer = new BlockerTransformer();
        inst.addTransformer(blockerTransformer, true);
        try {
            inst.retransformClasses(URL.class);
        } catch (UnmodifiableClassException e) {
            throw new RuntimeException(e);
        }
        inst.removeTransformer(blockerTransformer);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        doUrlTransform(inst);
        // We left no references in this frame, so System.gc should clean up everything related to the agent
        System.gc();
    }
}
