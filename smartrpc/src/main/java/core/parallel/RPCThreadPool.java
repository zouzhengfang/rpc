package core.parallel;

import core.parallel.policy.*;

import java.util.Timer;
import java.util.concurrent.*;

public class RPCThreadPool {
    private static final Timer TIMER = new Timer("ThreadPoolMonitor", true);
    private static long monitorDelay = 100L;
    private static long monitorPeriod = 300L;

    private static RejectedExecutionHandler createPolicy() {
        RejectedPolicyType rejectedPolicyType = RejectedPolicyType.fromString("AbortPolicy");

        switch (rejectedPolicyType) {
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case ABORT_POLICY:
                return new AbortPolicy();
            case REJECTED_POLICY:
                return new RejectedPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            default: {
                break;
            }
        }

        return null;
    }

    private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
        BlockingQueueType queueType = BlockingQueueType.fromString( "LinkedBlockingQueue");

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<Runnable>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<Runnable>( Math.max(2, Runtime.getRuntime().availableProcessors()) * queues);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<Runnable>();
            default: {
                break;
            }
        }

        return null;
    }

    public static Executor getExecutor(int threads, int queues) {
        System.out.println("ThreadPool Core[threads:" + threads + ", queues:" + queues + "]");
        String name = "RpcThreadPool";
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(queues),
                new NamedThreadFactory(name, true), createPolicy());
        return executor;
    }
}
