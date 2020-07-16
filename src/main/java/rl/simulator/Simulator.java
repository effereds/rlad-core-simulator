package rl.simulator;

import rl.reward.LocalLatencyRewardProvider;
import rl.reward.RewardProvider;
import rl.reward.UtilizationBasedRewardProvider;
import rl.services.Service;
import rl.services.ServiceApplication;

public class Simulator {


    static ServiceApplication buildApplication (Configuration conf)
    {
        int num_services = 1; //define the number of indipendent RL agents
        double SLA_THRESHOLD = 0.050 * num_services;
        double servtime_variance = 0.0; /* m/d/1 */
        double mu = Configuration.MU * Configuration.INITIAL_VALUE/100; /* tps */
        double servtime_mean[] = {1.0/mu, 1.0/(mu*0.8),1.0/(1.1*mu)};
        int max_parallelism = conf.max_replication;

        ServiceApplication app = new ServiceApplication();

        for (int i = 0; i<num_services; ++i) {
            Service serv = new Service(i, servtime_mean[i % servtime_mean.length], servtime_variance,
                    max_parallelism);
            serv.setTargetResponseTime(SLA_THRESHOLD);

            app.addService(serv);
        }

        app.setLatencySla(SLA_THRESHOLD);
        return app;
    }

    static void runSimulation (Simulation simulation, long time_limit)
    {
        simulation.run(time_limit);
        System.out.println("Finish");
    }

    static Configuration getCliConfiguration ()
    {
        Configuration conf = new Configuration();
        return conf;
    }


    static RewardProvider newRewardProvider (Configuration conf)
    {
        RewardProvider rewardProvider;
        if (conf.reward_model == Configuration.REWARD_UTILIZATION)
            rewardProvider = new UtilizationBasedRewardProvider(conf);
        else
            rewardProvider = new LocalLatencyRewardProvider(conf);

        return rewardProvider;
    }

    public static void main (String[] args)
    {
        Configuration conf = getCliConfiguration();

        String input_filename = conf.input_filename;
        String output_filename = conf.output_filename;

        ServiceApplication application = buildApplication(conf);

        System.out.println(String.valueOf(application.getTotalInstances()));

        RewardProvider rewardProvider = newRewardProvider(conf);

        Simulation simulation = new Simulation(conf, application, rewardProvider,
                input_filename, output_filename);
        System.out.println("START");
        runSimulation(simulation, conf.time_limit);

        System.out.println("Main finishes");

    }

}
