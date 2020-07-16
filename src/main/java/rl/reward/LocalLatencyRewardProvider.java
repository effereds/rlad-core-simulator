package rl.reward;

import rl.actions.Action;
import rl.actions.ActionVerticalOrHorizontal;
import rl.actions.ActionVerticalAndHorizontal;
import rl.agents.Agent;
import rl.services.MGNQueue;
import rl.services.Service;
import rl.simulator.Configuration;
import rl.states.State;
import rl.states.StateVerticalScaling;
import rl.utils.Constants;

public class LocalLatencyRewardProvider implements RewardProvider {
    private double w_res, w_sla, w_down;

    public LocalLatencyRewardProvider(Configuration conf) {
        this.w_down = conf.w_reconfiguration;
        this.w_sla = conf.w_sla;
        this.w_res = conf.w_resources;
    }


    @Override
    public double reward(Agent a, double inputRateTps) {
        Service serv = a.getService();
        double cpu = ((double)serv.getCpu())/100;

        double c_res;
        if ((a.getAction() instanceof ActionVerticalOrHorizontal) ||
                (a.getAction() instanceof ActionVerticalAndHorizontal)){

            c_res = (double) serv.getInstances() * cpu / (double) serv.getMaxParallelism();
        } else {
            c_res = (double) (serv.getInstances())  / (double) serv.getMaxParallelism();
        }

        double c_rcf = a.hasReconfigured()? 1.0 : 0.0;
        boolean sla_violated = serv.responseTime(inputRateTps) > serv.getTargetResponseTime();
        double c_sla = sla_violated? 1.0 : 0.0;

        return w_res*c_res + w_down*c_rcf + w_sla*c_sla;
    }

    private double computeCres(Agent agent, State s, Action a){

        Service serv = agent.getService();

        double c_res = 0;

        if ((agent.getAction() instanceof ActionVerticalOrHorizontal) ||
                (agent.getAction() instanceof ActionVerticalAndHorizontal)){
                double cpu;
                StateVerticalScaling tmp = (StateVerticalScaling) s;

               if (a instanceof ActionVerticalOrHorizontal) {
                   ActionVerticalOrHorizontal action = (ActionVerticalOrHorizontal) a;
                   cpu = ((double) (tmp.cpu + action.getInstanceCpu())) / 100.0;
               } else {
                   ActionVerticalAndHorizontal action = (ActionVerticalAndHorizontal) a;
                   cpu = ((double) (tmp.cpu + action.getInstanceCpu())) / 100.0;
               }
            c_res =  ((s.k + a.getInstanceDelta()) * cpu) / (double) serv.getMaxParallelism();
        } else {
            c_res = (double) (s.k + a.getInstanceDelta())  / (double) serv.getMaxParallelism();
        }

        return c_res;

    }

    @Override
    public double knownReward(Agent agent, State s, Action a) {

        double c_res = computeCres(agent, s, a);
        double c_rcf = a.isReconfiguration()? 1.0 : 0.0;
        return w_res * c_res + w_down * c_rcf;
    }

    @Override
    public double slaReward(Agent agent, MGNQueue serviceModel,double inputRateTps, State ns) {
        Service serv = agent.getService();
        boolean sla_violated = serviceModel.responseTime(ns.k, inputRateTps, ns.realUtil()) > serv.getTargetResponseTime();
        double c_sla = sla_violated? 1.0 : 0.0;
        return w_sla*c_sla;
    }
}
