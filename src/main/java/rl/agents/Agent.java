package rl.agents;

import rl.actions.Action;
import rl.actions.ActionVerticalOrHorizontal;
import rl.actions.ActionVerticalAndHorizontal;
import rl.services.Service;
import rl.simulator.Configuration;
import rl.statistics.AgentStatistics;
import rl.statistics.Statistics;
import rl.utils.Constants;

public abstract class Agent {

    protected Service service;
    protected double lambdaTps;
    protected Action pickedAction;
    protected AgentStatistics stats;

    public Agent(Service service) {
        this.service = service;
        this.lambdaTps = 0;
        if (Configuration.SIMULATION_ELASTICITY == Constants.HORIZONTAL_SCALING) {
            this.pickedAction = new Action();
        } else if (Configuration.SIMULATION_ELASTICITY == Constants.HORIZONTAL_OR_VERTICAL) {
            this.pickedAction = new ActionVerticalOrHorizontal();
        }else {
            this.pickedAction = new ActionVerticalAndHorizontal();
        }
        this.stats = new AgentStatistics();
    }

    public Agent() {};

    public  void initialize() {};

    public abstract double update (long t, double cost, double lambdaTps);

    protected abstract Action _pickAction();

    protected abstract Action _basicAction();

    public void updateStats (Statistics stats) {};

    public AgentStatistics getStats () { return this.stats;}

    public Service getService () {
        return this.service;
    }

    public boolean hasReconfigured () {
        return this.pickedAction.isReconfiguration();
    }

    public Action getAction() {
        return this.pickedAction;
    }

    public Action pickAction () {
        this.pickedAction = _pickAction();
        return this.pickedAction;
    }

    public Action basicAction () {
        this.pickedAction = _basicAction();
        return this.pickedAction;
    }


    public void doAction (Action action) {
        if (action instanceof ActionVerticalOrHorizontal) {

            ActionVerticalOrHorizontal tmp = (ActionVerticalOrHorizontal) action;
            if (tmp.getInstanceDelta() != 0) {
                this.service.adjustParallelism(tmp.getInstanceDelta());

            } else if (tmp.getInstanceCpu() != 0) {
                this.service.updateServiceTimeMean(tmp.getInstanceCpu());
            }

        } else if (action instanceof ActionVerticalAndHorizontal) {
            ActionVerticalAndHorizontal tmp = (ActionVerticalAndHorizontal) action;

            if (tmp.getInstanceDelta() != 0) {
                this.service.adjustParallelism(tmp.getInstanceDelta());
            }
            if (tmp.getInstanceCpu() != 0) {
                this.service.updateServiceTimeMean(tmp.getInstanceCpu());
            }

        }else {
            this.service.adjustParallelism(action.getInstanceDelta());
        }
    }
    public abstract double getLambdaTps();

    public void setLambdaTps (double lambda) {
        this.lambdaTps = lambda;
    }
}
