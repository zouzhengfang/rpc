package core.parallel.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class CallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(CallerRunsPolicy.class);

    private String threadName;

    public CallerRunsPolicy() {
        this(null);
    }

    public CallerRunsPolicy(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOG.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        super.rejectedExecution(runnable, executor);
    }
}