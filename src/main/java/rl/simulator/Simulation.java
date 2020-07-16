package rl.simulator;

import rl.actions.Action;
import rl.actions.ActionVerticalOrHorizontal;
import rl.actions.ActionVerticalAndHorizontal;
import rl.agents.*;
import rl.reward.RewardProvider;
import rl.services.Service;
import rl.services.ServiceApplication;
import rl.utils.Constants;
import rl.utils.Lambda;
import rl.utils.QTable;
import rl.utils.ReadProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class Simulation {
    private int initial_basic_policy_period;
    private long time;
    private RewardProvider reward_provider;
    private ServiceApplication application;
    private ArrayList<Agent> agents = new ArrayList<>();
    private double w_sla;
    private double w_res;
    private double w_rcf;
    private HashMap<Service, Lambda> hashMap;
    private int configuration;

    public Simulation(Configuration conf, ServiceApplication app, RewardProvider rp, String input_filename)
    {
        this.initial_basic_policy_period = conf.initial_basic_policy_period;
        this.w_res = conf.w_resources;
        this.w_rcf = conf.w_reconfiguration;
        this.w_sla = conf.w_sla;
        this.application = app;
        this.hashMap = new HashMap<>();
        this.configuration = conf.SIMULATION_ELASTICITY;

        this.reward_provider = rp;

        //input_rate_file.open(input_filename);

        for (Service serv : app.getServices()) {
            this.agents.add(new_agent(conf, serv));
        }

        for (Service serv: app.getServices()) {
            hashMap.put(serv, new Lambda());
        }
    }
    public Simulation(Configuration conf, ServiceApplication app, RewardProvider rp,
                      String input_filename, String output_filename) {
	    this(conf, app,rp,input_filename);

    }

    private Agent new_agent(Configuration conf, Service serv)
    {
        if (conf.agent_type == Constants.AGENT_QLEARNING)
            return new QLearningAgent(conf, serv);

        else if (conf.agent_type == Constants.AGENT_RLMB)
            return new ModelBasedAgent(conf, serv, reward_provider);

        else if (conf.agent_type == Constants.AGENT_DYNAQ)
            return new DynaQAgent(conf, serv);
        else if (conf.agent_type == Constants.AGENT_DYNAQ2) {
            return  new DynaQAgent_V2(conf,serv);
        }
       else
        System.out.println("Error in agent type");
       return null;
    }




    public void update_agents ()
    {
        double new_lambda_tps;
        for (Agent agent : agents) {
            new_lambda_tps = hashMap.get(agent.getService()).getLastValue();

            agent.getStats().updateSlaViolation(agent.getService().isSlaViolated(new_lambda_tps));
            agent.getStats().updateResponseTimeStatistic(agent.getService().responseTime(new_lambda_tps));

            // c(s,a,s')
            double reward = reward_provider.reward(agent, new_lambda_tps);

            agent.update(time, reward, new_lambda_tps);

            //Update rl.statistics
            agent.getStats().updateRewardStatistic(reward);
            agent.getStats().updateKStatistic(agent.getService().getInstances());
            agent.getStats().updateUtilizationPercentStatistic(agent.getService().getUtilization());
        }
    }

    public void monitorSystem () {
        for (Agent agent : agents) {
            agent.getService().updateUtilization(hashMap.get(agent.getService()).getLastValue());

        }

    }

    public void run(long time_limit)
    {

        readNextLambda();
        for (Agent agent : agents)
            agent.initialize();


        while (time <= time_limit) {

            monitorSystem(); //update utilization for each service

            update_agents();

            for (Agent agent : agents) {
                agent.getStats().updateLambdaTpsStatistic(hashMap.get(agent.getService()).getLastValue());
                Action a;
                if (time < initial_basic_policy_period)
                    a = agent.basicAction();
                else
                    a = agent.pickAction();

                agent.doAction(a);

                agent.getStats().updateDeltaStatistic(a.getInstanceDelta()); //Update statistic of horizontal scaling
                if (a instanceof ActionVerticalOrHorizontal) {
                    agent.getStats().updateCpuIncrement(((ActionVerticalOrHorizontal) a).getInstanceCpu());

                } else if (a instanceof ActionVerticalAndHorizontal) {
                    agent.getStats().updateCpuIncrement(((ActionVerticalAndHorizontal) a).getInstanceCpu());

                }

                agent.setLambdaTps(hashMap.get(agent.getService()).getLastValue());

            }

            print_progress();

            time++;
            readNextLambda();
        }

    }

    private void print_progress()
    {
        for (Agent agent: agents) {



            String s = "Statistics," + time + "," + agent.getStats().getLambdaTpsStatistic().get((int)(time)) + ","
                    + agent.getStats().getKStatistic().get((int) (time))+
            "," + agent.getStats().getUtilizationPercentStatistic().get((int) (time)) +
            "," + agent.getStats().getDeltaStatistic().get((int) (time)) +
            "," + agent.getStats().getRewardStatistic().get((int) (time)) +
            ","+ (agent.getStats().getSlaViolation().get((int) (time))?1:0)
            + ", " + agent.getStats().getResponseTimeStatistic().get((int) (time));

            if (this.configuration == Constants.HORIZONTAL_OR_VERTICAL || this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {
                int val = agent.getService().getCpu() - agent.getStats().getCpu().get((int) (time));
                s += ", " + agent.getStats().getCpu().get((int) (time)) +
                 " , " + val;
            } else {
                s+= ", 0, 100";
            }

            if (agent instanceof RLAgent){
                QTable qtab = ((RLAgent) agent).q;
                double min = Double.MAX_VALUE;
                double sum = 0.0;
                double max = Double.MIN_VALUE;
                for (int i = 0; i < qtab.table.length; i++){
                    if (qtab.table[i] < min)
                        min = qtab.table[i];
                    if (qtab.table[i] > max)
                        max = qtab.table[i];
                    sum += qtab.table[i];
                }

                s += ", " + min + ", " + (sum / (double) qtab.table.length) + ", " + max;
            }

            System.out.println(s);
        }

    }

    private boolean is_time_up(long time_limit)
    {
        return (time_limit > 0 && time >= time_limit);
    }

    // DEBS Profile: slow version
    private void readNextLambda() {
        String s;
        for (Agent agent: agents) {

            Lambda lambda = hashMap.get(agent.getService());

            if (lambda.getCount() == 0) {
                //first value
                s = ReadProfile.getIstance().readLine();
                if (s == null) {
                    s = "200";
                } else {
                    s = ReadProfile.getIstance().readLine();
                }
                lambda.setLastValue(Double.parseDouble(s));
                lambda.setCount(1);
                hashMap.replace(agent.getService(), lambda);
            } else if (lambda.getCount() == 2) {
                s = ReadProfile.getIstance().readLine();
                if (s == null) {
                    s = "200";
                } else {
                    s = ReadProfile.getIstance().readLine();
                }
                lambda.setLastValue(Double.parseDouble(s));
                lambda.setCount(1);
                hashMap.replace(agent.getService(), lambda);
            } else {
                lambda.updateCount();
            }

        }
    }


//    // DEBS Profile: normal version
//    private void readNextLambda() {
//        String s;
//        for (Agent agent: agents) {
//
//            Lambda lambda = hashMap.get(agent.getService());
//            //lambda.setLastValue(Math.random() * 10);
//            s = ReadProfile.getIstance().readLine();
//            if (s == null) {
//                s = "200";
//            }
//            lambda.setLastValue(Double.parseDouble(s));
//            lambda.setCount(1);
//            hashMap.replace(agent.getService(), lambda);
//
//        }
//    }


    // Regular pattern
    /*private void readNextLambda()
    {
        for (Agent agent : agents) {
            if (time == 0) {

                Lambda lambda = hashMap.get(agent.getService());
                //lambda.setLastValue(Math.random() * 10);
                lambda.setLastValue(2.3068675878320146);
                lambda.setCount(1);
                hashMap.replace(agent.getService(), lambda);

            } else {
                Lambda lambda = hashMap.get(agent.getService());
                double val = lambda.getLastValue();

                if (lambda.getCount() == Configuration.COUNT_LIMIT) {
                    if (lambda.isUp()) {
                        if (val + Configuration.EPSILON > Configuration.THRESHOLD_LAMBDA_SUP) {
                            val = val - Configuration.EPSILON;
                            lambda.setUp(false);
                        } else {
                            val = val + Configuration.EPSILON;
                        }
                    } else {
                        if (val - Configuration.EPSILON < Configuration.THRESHOLD_LAMBDA_INF) {
                            val = val + Configuration.EPSILON;
                            lambda.setUp(true);
                        } else {
                            val = val - Configuration.EPSILON;
                        }
                    }
                    lambda.setCount(1);
                    lambda.setLastValue(val);

                } else {
                    lambda.updateCount();
                }

                hashMap.replace(agent.getService(),lambda);
            }


        }
    } */

    public void run(double time) {};

    public long getTime() {
        return this.time;
    }
}
