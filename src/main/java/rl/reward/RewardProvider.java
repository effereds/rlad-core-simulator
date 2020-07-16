package rl.reward;

import rl.actions.Action;
import rl.agents.Agent;
import rl.services.MGNQueue;
import rl.states.State;

public interface RewardProvider {

    double reward (Agent a, double inputRateTps);
    double knownReward (Agent agent, State s, Action a);
    double slaReward (Agent agent, MGNQueue serviceModel, double inputRateTps, State ns);
}
