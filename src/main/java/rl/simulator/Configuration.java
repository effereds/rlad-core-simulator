package rl.simulator;

import rl.utils.Constants;

public class Configuration {

    // 1. choose the actions space
    public static int SIMULATION_ELASTICITY = Constants.HORIZONTAL_OR_VERTICAL; // vertical or horizontal
    //public static int SIMULATION_ELASTICITY = Constants.HORIZONTAL_SCALING; //horizontal
    //public static int SIMULATION_ELASTICITY = Constants.HORIZONTAL_AND_VERTICAL; //vertical and horizontal

    // 2. choose the cost function weights
    public double w_resources = 0.09;
    public double w_reconfiguration = 0.01;
    public double w_sla = 0.90;

    // 3. choose the Reinforcement Learning policy
    //public int agent_type = Constants.AGENT_RLMB; //Model based: RL
    //public int agent_type = Constants.AGENT_QLEARNING; //Qlearning agent
    //public int agent_type = Constants.AGENT_DYNAQ; //DynaQ agent
    public int agent_type = Constants.AGENT_DYNAQ2;

    // 4. choose action selection policy (for Q-learning and Dyna-Q)
    //public static String PICK_ACTION_POLICY = Constants.ACTION_POLICY_SOFTMAX;
    public static String PICK_ACTION_POLICY = Constants.ACTION_POLICY_EGREEDY;

// --------------------------------------------

    //File profile
    public static String PROFILE = "data/profile.dat";

    public static double THRESHOLD_LAMBDA_SUP = 500;
    public static double COUNT_LIMIT = 100; //Number of element that remain equals
    public static double EPSILON = 0.1;
    public static double THRESHOLD_LAMBDA_INF = 10;

    //DynaQ params
    public static int N_DYNAQ = 25;

    //soft max
    public static double TAU = 0.1;

    //simulation parameters
    public static double MU = 200;
    public static int INITIAL_VALUE = 100; //cpu initial value
    public long time_limit = 4000; //simulation time

    // parameters for states space
    public static double UTIL_MAX_VALUE = 1;
    public static double UTIL_DISCRETIZATION_QUANTUM = 0.1;
    public static double CPU_MAX_VALUE = 100;
    public static double CPU_DICRETIZATION_QUANTUM = 10;
    public int max_replication = 10;


    // model based parameters
    public static int MB_RESPTIME_UNKNOWN = 0;
    public static int MB_RESPTIME_MM1 = 1;
    public static int MB_RESPTIME_MD1 = 2;
    public static int MB_RESPTIME_KNOWN = 9;

    public static int REWARD_UTILIZATION = 0;
    public static int REWARD_LATENCY = 1;


    public double utilization_target = 0.7;

    public int stats_file_writing_period = 500;

    public String input_filename;
    public String output_filename;
    public String known_input_profile_filename = "";


    public int reward_model = REWARD_LATENCY;
    public int mb_resptime_model = MB_RESPTIME_UNKNOWN;
    public int initial_basic_policy_period = 0;

    public long mcts_budget_millis = 100;
    public int mcts_depth = 30;
    public double ucb_expl_coeff = 1.0;
    public int ucb_noadaptive_expl_coeff = 0;
    public int ucb_heuristic_default_policy = 0;

    public double gamma = 0.99;
    //public double gamma = 0.0001;

    public Configuration() {};

    public double getW_resources() {
        return w_resources;
    }

    public double getW_reconfiguration() {
        return w_reconfiguration;
    }

    public double getW_sla() {
        return w_sla;
    }

    public double getUtilization_target() {
        return utilization_target;
    }

    public int getMax_replication() {
        return max_replication;
    }

    public long getTime_limit() {
        return time_limit;
    }

    public int getStats_file_writing_period() {
        return stats_file_writing_period;
    }

    public String getInput_filename() {
        return input_filename;
    }

    public String getOutput_filename() {
        return output_filename;
    }

    public String getKnown_input_profile_filename() {
        return known_input_profile_filename;
    }

    public int getAgent_type() {
        return agent_type;
    }

    public int getReward_model() {
        return reward_model;
    }

    public int getMb_resptime_model() {
        return mb_resptime_model;
    }

    public int getInitial_basic_policy_period() {
        return initial_basic_policy_period;
    }

    public long getMcts_budget_millis() {
        return mcts_budget_millis;
    }

    public int getMcts_depth() {
        return mcts_depth;
    }

    public double getUcb_expl_coeff() {
        return ucb_expl_coeff;
    }

    public int getUcb_noadaptive_expl_coeff() {
        return ucb_noadaptive_expl_coeff;
    }

    public int getUcb_heuristic_default_policy() {
        return ucb_heuristic_default_policy;
    }

    public double getGamma() {
        return gamma;
    }

    public static void setEpsilon(double epsilon) {
        Configuration.EPSILON = epsilon;
    }

    public static void setSimulationElasticity(int simulationElasticity) {
        SIMULATION_ELASTICITY = simulationElasticity;
    }

    public void setW_resources(double w_resources) {
        this.w_resources = w_resources;
    }

    public void setW_reconfiguration(double w_reconfiguration) {
        this.w_reconfiguration = w_reconfiguration;
    }

    public void setW_sla(double w_sla) {
        this.w_sla = w_sla;
    }

    public void setAgent_type(int agent_type) {
        this.agent_type = agent_type;
    }

    public static void setTAU(double TAU) {
        Configuration.TAU = TAU;
    }

    public static void setPickActionPolicy(String pickActionPolicy) {
        PICK_ACTION_POLICY = pickActionPolicy;
    }

}
