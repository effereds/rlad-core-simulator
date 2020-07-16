package rl.utils;

public class Constants {

    public static String ACTION_POLICY_EGREEDY = "egreedy";
    public static String ACTION_POLICY_SOFTMAX = "softmax";

    // possible actions space
    public static int HORIZONTAL_SCALING = 0;
    public static int HORIZONTAL_OR_VERTICAL = 1;
    public static int HORIZONTAL_AND_VERTICAL = 2;

    // type of RL agent
    public static int AGENT_RLMB = 0;
    public static int AGENT_THRESHOLD = 1;
    public static int AGENT_QLEARNING = 2;
    public static int AGENT_DYNAQ = 3;
    public static int AGENT_DYNAQ2 = 4;


}
