package rl.reward;

import rl.actions.Action;
import rl.agents.Agent;
import rl.services.MGNQueue;
import rl.services.Service;
import rl.simulator.Configuration;
import rl.states.State;

public class UtilizationBasedRewardProvider implements RewardProvider {

    private double w_down, w_res, w_util, target_utilization;

    public UtilizationBasedRewardProvider(Configuration conf)
    {
        this.w_down = conf.w_reconfiguration;
        this.w_util = conf.w_sla;
        this.w_res = conf.w_resources;
        this.target_utilization = conf.utilization_target;
    }

    @Override
    public double reward(Agent a, double inputRateTps) {

        Service serv = a.getService();

        double u = serv.getUtilization();
        double c_res = (double) serv.getInstances() / (double) serv.getMaxParallelism();
        double c_rcf = a.hasReconfigured() ? 1.0 : 0.0;
        double c_sla = (u > target_utilization)? 1.0 : 0.0;

        return w_res*c_res + w_down*c_rcf + w_util*c_sla;
    }

    @Override
    public double knownReward(Agent agent, State s, Action a) {

        Service serv = agent.getService();

        double c_res = (double)(s.k+a.getInstanceDelta()) / (double) serv.getMaxParallelism();
        double c_rcf = a.isReconfiguration()? 1.0 : 0.0;

        return w_res*c_res + w_down*c_rcf;
    }

    public double slaReward(State ns) {
               return slaReward(null, null, -1, ns);
    }

    @Override
    public double slaReward(Agent agent, MGNQueue serviceModel, double inputRateTps, State ns) {

        boolean sla_violated = ns.realUtil() > target_utilization;
        double c_sla = sla_violated? 1.0 : 0.0;

        return w_util*c_sla;

    }
}
