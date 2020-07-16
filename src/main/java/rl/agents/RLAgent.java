package rl.agents;

import rl.actions.*;
import rl.services.Service;
import rl.simulator.Configuration;
import rl.states.State;
import rl.states.StateVerticalScaling;
import rl.utils.Constants;
import rl.utils.QTable;

public abstract class RLAgent extends Agent {
    public QTable q = null;
    public State state;
    protected double GAMMA;
    protected int configuration;

    public RLAgent (Configuration conf, Service service) {
        super(service);
        if (conf.SIMULATION_ELASTICITY == Constants.HORIZONTAL_SCALING) {
            state = new State();
            state.k = super.getService().getInstances();
            state.util = State.discretizeUtil(super.getService().getUtilization());
        } else {
            StateVerticalScaling tmp = new StateVerticalScaling();
            tmp.k = super.getService().getInstances();
            tmp.util = State.discretizeUtil(super.getService().getUtilization());
            tmp.cpu = super.getService().getCpu();
            state = tmp;
        }
        this.GAMMA = conf.gamma;
        this.configuration = conf.SIMULATION_ELASTICITY;
    }

    protected abstract Action _pickAction();


    protected Action _basicAction() {
        int parallelism = state.k;
        double utilization = state.realUtil();
        double THRESHOLD = 0.7;

        return ThresholdAgent._pick_action(parallelism,
                service.getMaxParallelism(), utilization, THRESHOLD);
    }

    protected long n_states() {
        if (configuration == Constants.HORIZONTAL_SCALING) {
            return super.getService().getMaxParallelism() * State.UTIL_STATES;
        } else {
            return super.getService().getMaxParallelism() * State.UTIL_STATES * StateVerticalScaling.CPU_STATES;
        }
    }

    public void allocateQTable() {
        if (this.configuration == Constants.HORIZONTAL_SCALING) {

            this.q = new QTable(n_states(), Action.N_ACTIONS());

        } else if (this.configuration == Constants.HORIZONTAL_OR_VERTICAL) {
            this.q = new QTable(n_states(), ActionVerticalOrHorizontal.N_ACTIONS());

        } else {
            this.q = new QTable(n_states(), ActionVerticalAndHorizontal.N_ACTIONS());

        }
    }



    public double update (long t, double cost, double lambdaTps) {
        this.state.k = super.getService().getInstances();
        this.state.util = State.discretizeUtil(this.getService().getUtilization());
        this.lambdaTps = lambdaTps;
        return 0.0;
    }

    public double getLambdaTps () {
        return this.lambdaTps;
    }

    public boolean isValidAction (State s, Action a)

    {   if (!(a instanceof ActionVerticalOrHorizontal) && !(a instanceof ActionVerticalAndHorizontal)) {

            int parallelism = s.k + a.getInstanceDelta();

            return (parallelism > 0 && parallelism <= super.getService().getMaxParallelism());

        } else if (a instanceof ActionVerticalOrHorizontal) {

            int parallelism = s.k + a.getInstanceDelta();
            int cpu = ((StateVerticalScaling)s).cpu + ((ActionVerticalOrHorizontal) a).getInstanceCpu();

            return (parallelism > 0 && parallelism <= super.getService().getMaxParallelism()
                     && cpu > 0 && cpu <= 100);
        } else {

            int parallelism = s.k + a.getInstanceDelta();
            int cpu = ((StateVerticalScaling)s).cpu + ((ActionVerticalAndHorizontal) a).getInstanceCpu();

            return (parallelism > 0 && parallelism <= super.getService().getMaxParallelism()
                && cpu > 0 && cpu <= 100);
        }
    }


    public Action greedyAction(State s) {
        return greedyAction(s, -1);
    }


    public Action greedyAction (State s, double valueptr) {
        double min_cost = 0.0;
        Action best_action = new Action(0);
        boolean found = false;

        if (this.configuration == Constants.HORIZONTAL_SCALING) {
            for (Action a : ActionIterator.getInstance().getActionIterator()) {
                if (!this.isValidAction(s, a))
                    continue;

                double cost = q.get(s, a);
                if (!found || cost < min_cost) {
                    best_action = a;
                    min_cost = cost;
                    found = true;
                }

            }
        } else if (this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {

            for (Action a : ActionVerticalAndHorizontalIterator.getInstance().getActionIterator()) {
                ActionVerticalAndHorizontal tmp = (ActionVerticalAndHorizontal) a;

                if (!this.isValidAction(s, tmp))
                    continue;

                double cost = q.get(s, tmp);
                if (!found || cost < min_cost) {
                    best_action = a;
                    min_cost = cost;
                    found = true;
                }

            }

        }else {
            for (Action a : ActionVerticalOrHorizontalIterator.getInstance().getActionIterator()) {
                ActionVerticalOrHorizontal tmp = (ActionVerticalOrHorizontal) a;

                if (!this.isValidAction(s, tmp))
                    continue;

                double cost = q.get(s, tmp);
                if (!found || cost < min_cost) {
                    best_action = a;
                    min_cost = cost;
                    found = true;
                }

            }
        }

        if (valueptr == -1)
		valueptr = min_cost;

        return best_action;
    }


    public Action egreedyAction (State s, double epsilon) {
        if (Math.random() > epsilon)
            return greedyAction(s);

        /* Random action selection */
        Action a;
        if (this.configuration == Constants.HORIZONTAL_SCALING) {

            do {
                a = Action.randomAction();
            } while (!isValidAction(s, a));

        } else if (this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {

            do {
                a = ActionVerticalAndHorizontal.randomAction();
            } while (!isValidAction(s, a));

        }else {
            do {
                a = ActionVerticalOrHorizontal.randomAction();
            } while (!isValidAction(s, a));
        }

        return a;
    }

    public double value (State s, QTable qtable) {
        double min_cost = 0.0;
        boolean found = false;

        if (this.configuration == Constants.HORIZONTAL_SCALING) {

            for (Action a : ActionIterator.getInstance().getActionIterator()) {
                if (!isValidAction(s, a))
                    continue;

                double cost = qtable.get(s, a);
                if (!found || cost < min_cost) {
                    min_cost = cost;
                    found = true;
                }
            }
        } else if (this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {

            for(Action a : ActionVerticalAndHorizontalIterator.getInstance().getActionIterator()) {

                if (!isValidAction(s, a))
                    continue;

                double cost = qtable.get(s, a);
                if (!found || cost < min_cost) {
                    min_cost = cost;
                    found = true;
                }
            }
        }else {

            for (Action a : ActionVerticalOrHorizontalIterator.getInstance().getActionIterator()) {

                if (!isValidAction(s, a))
                    continue;

                double cost = qtable.get(s, a);
                if (!found || cost < min_cost) {
                    min_cost = cost;
                    found = true;
                }
            }
        }

        return min_cost;
    }

    protected double value(State s) {
        return value(s, this.q);
    }

    public State getState() {
        return this.state;
    }



}
