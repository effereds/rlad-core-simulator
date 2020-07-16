package rl.agents;

import rl.actions.Action;
import rl.services.Service;
import rl.states.State;
import rl.states.StateThresholdAgent;

public class ThresholdAgent extends Agent {

    private StateThresholdAgent state;
    private double threshold;

    public ThresholdAgent (Service serv, double threshold) {
        super(serv);
        this.threshold = threshold;
    }

    public static Action _pick_action(int parallelism, int max_parallelism, double utilization,
                                        double scale_out_thr)
    {
        if (utilization > scale_out_thr && parallelism < max_parallelism)
            return Action.scaleOut();

	    double scale_in_thr = (parallelism>1) ? 0.75*scale_out_thr*(parallelism-1)/(double)parallelism : -1.0;
        if (utilization < scale_in_thr)
            return Action.scaleIn();

        return Action.none();
    }

    public double update (long t, double cost, double lambda)
    {
        lambdaTps = lambda;
        state.k = service.getInstances();
        state.util = State.discretizeUtil(service.getUtilization());
        return 0.0;
    }

    @Override
    protected Action _pickAction() {
        double lambda_tps = state.util;
        int parallelism = state.k;
        double utilization = service.getUtilization();
        return _pick_action(parallelism, service.getMaxParallelism(), utilization, threshold);

    }

    @Override
    protected Action _basicAction() {
        return _pickAction();
    }

    @Override
    public double getLambdaTps() {
        return super.lambdaTps;
    }


}
