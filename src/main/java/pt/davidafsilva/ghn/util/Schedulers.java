package pt.davidafsilva.ghn.util;

import reactor.core.scheduler.Scheduler;

/**
 * @author david
 */
public final class Schedulers {

  private static final class Holder {

    private static final Schedulers INSTANCE = new Schedulers();
  }

  private final Scheduler ioScheduler;
  private final Scheduler rpcScheduler;

  private Schedulers() {
    ioScheduler = reactor.core.scheduler.Schedulers.newElastic("ghnc-io");
    rpcScheduler = reactor.core.scheduler.Schedulers.newParallel("rpc-io",
        Runtime.getRuntime().availableProcessors(), true);
  }

  public static Scheduler io() {
    return Holder.INSTANCE.ioScheduler;
  }

  public static Scheduler rpc() {
    return Holder.INSTANCE.rpcScheduler;
  }

  public static void dispose() {
    Holder.INSTANCE.ioScheduler.dispose();
    Holder.INSTANCE.rpcScheduler.dispose();
  }
}
