package rl.agents;


import rl.actions.*;
import rl.services.Service;
import rl.simulator.Configuration;
import rl.states.State;
import rl.states.StateVerticalScaling;
import rl.utils.Constants;
import rl.utils.DynaQElement;

import java.util.ArrayList;

public class DynaQAgent_V2 extends RLAgent{
    private static double ALPHA = 0.1;
    private static int MAX = 100;
    private static double PERCENT = 10;
    private long t;
    private ArrayList<DynaQElement> dynaq_model;

    public DynaQAgent_V2(Configuration conf, Service service) {
        super(conf,service);
        this.t = 1;
        allocateQTable(); //allocate QTable
        dynaq_model = new ArrayList<>(); //allocate DynaQ model
    }

    public double update (long t, double cost, double u)
    {
        this.t += 1;
        super.lambdaTps = u;

        State ns;

        if (configuration == Constants.HORIZONTAL_SCALING) {
            ns = new State();
            ns.k = state.k + pickedAction.getInstanceDelta();
            ns.util = ns.discretizeUtil(super.getService().getUtilization());
        } else {

            StateVerticalScaling nns = new StateVerticalScaling() ;
            nns.k = state.k + pickedAction.getInstanceDelta();
            nns.util = nns.discretizeUtil(super.getService().getUtilization());
            StateVerticalScaling tmp = (StateVerticalScaling)state;

            if (configuration == Constants.HORIZONTAL_OR_VERTICAL) {
                ActionVerticalOrHorizontal a = (ActionVerticalOrHorizontal) pickedAction;
                nns.cpu = tmp.cpu + a.getInstanceCpu();
            } else {
                ActionVerticalAndHorizontal a = (ActionVerticalAndHorizontal) pickedAction;
                nns.cpu = tmp.cpu + a.getInstanceCpu();
            }
            ns = nns;
        }

        Action na = greedyAction(ns); // use this to find Q(s', a')

        /* Q(s,a) <- (1-alpha)Q(s,a) + alpha*(c_s,a + gamma*min_a'Q(s',a')) */
        double value = q.get(state, pickedAction);
        double estimate = cost + GAMMA * q.get(ns,na);
        double newvalue = (1.0-ALPHA)*value + ALPHA*estimate;
        q.set(state, pickedAction, newvalue);

        state = ns;

        // update dyna q model: Model(S, A) <- R, S'
        //dynaq_model.set(state, pickedAction,ns,cost);
        DynaQElement elem = new DynaQElement(state, pickedAction, ns, cost);

        for (int i = 0; i < dynaq_model.size(); i++) {
            DynaQElement tmp = dynaq_model.get(i);

            if (tmp.getState().isEqual(elem.getState()) && tmp.getAction().isEqual(elem.getAction())){
                dynaq_model.remove(i);
                break;
            }
        }


        dynaq_model.add(elem);

        if (dynaq_model.size() > MAX) {
            dynaq_model.remove(0);
        }

        updateDynaQModel();

        return Math.abs(value - newvalue);
    }



    private void updateDynaQModel() {

        int end = (int) Math.ceil((t* PERCENT) / 100);
        end = Math.min(end, dynaq_model.size());

        if (end == 0) {
            return;
        }

        for (int i = 0; i < end; i++ ) {
            // pick a random state and a random action
            int n = (int) ((Math.random() * this.dynaq_model.size()) % this.dynaq_model.size());
            DynaQElement tmp = dynaq_model.get(n);

            State s = tmp.getState();
            State n_s = tmp.getN_state();
            double r = tmp.getCost();
            Action pickedAction = tmp.getAction();

            Action na = greedyAction(n_s); // use this to find Q(s', a')

            /* Q(s,a) <- (1-alpha)Q(s,a) + alpha*(c_s,a + gamma*min_a'Q(s',a')) */
            double value = q.get(s, pickedAction);
            double estimate = r + GAMMA * q.get(n_s,na);
            double newvalue = (1.0-ALPHA)*value + ALPHA*estimate;
            q.set(s, pickedAction, newvalue);

        }


    }



    private double computeProbabilitySoftmax(Action pickedAction) {
        double value;

        if (this.configuration == Constants.HORIZONTAL_SCALING) {

            double sum = 0;

            for (Action tmpAction : ActionIterator.getInstance().getActionIterator()) {
                if (isValidAction(state, tmpAction)) {
                    sum += Math.exp(-q.get(state, tmpAction) / Configuration.TAU);
                }
            }
            value = Math.exp(-q.get(state, pickedAction) / Configuration.TAU) / sum;


        } else if (this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {

            double sum = 0;

            for (Action tmpAction : ActionVerticalAndHorizontalIterator.getInstance().getActionIterator()) {
                if (isValidAction(state, tmpAction)) {
                    sum += Math.exp(-q.get(state, tmpAction) / Configuration.TAU);
                }
            }
            value = Math.exp(-q.get(state, pickedAction) / Configuration.TAU) / sum;


        } else {
            double sum = 0;

            for (Action tmpAction : ActionVerticalOrHorizontalIterator.getInstance().getActionIterator()) {
                if (isValidAction(state, tmpAction)) {
                    sum += Math.exp(-q.get(state, tmpAction) / Configuration.TAU);
                }
            }
            value = Math.exp(-q.get(state, pickedAction) / Configuration.TAU) / sum;
        }

        return value;

    }



    public ArrayList<Double> listProbabilitySoftmax() {

        ArrayList<Double> probAction = new ArrayList<Double>();

        if (this.configuration == Constants.HORIZONTAL_SCALING) {

            for (Action pickedAction: ActionIterator.getInstance().getActionIterator()){
                if(isValidAction(state, pickedAction)) {
                    double value = this.computeProbabilitySoftmax(pickedAction);

                    probAction.add(value);
                } else {
                    probAction.add(0.0);
                }
            }


        } else if (this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {

            for (Action pickedAction: ActionVerticalAndHorizontalIterator.getInstance().getActionIterator()){

                if (isValidAction(state, pickedAction)) {
                    double value = this.computeProbabilitySoftmax(pickedAction);
                    probAction.add(value);
                } else {
                    probAction.add(0.0);
                }
            }


        }else {
            for (Action pickedAction: ActionVerticalOrHorizontalIterator.getInstance().getActionIterator()){

                if (isValidAction(state, pickedAction)) {
                    double value = this.computeProbabilitySoftmax(pickedAction);
                    probAction.add(value);
                } else {
                    probAction.add(0.0);
                }
            }

        }

        return probAction;
    }


    public Action pickActionSoftmax (int i) {


        if (this.configuration == Constants.HORIZONTAL_SCALING) {

            return ActionIterator.getInstance().getActionIterator().get(i);


        } else if (this.configuration == Constants.HORIZONTAL_AND_VERTICAL) {

            return ActionVerticalAndHorizontalIterator.getInstance().getActionIterator().get(i);

        }else {

            return ActionVerticalOrHorizontalIterator.getInstance().getActionIterator().get(i);
        }


    }

    public Action softmax () {
        double p = Math.random();
        ArrayList<Double> list = listProbabilitySoftmax();
        Action a = new Action(0);

        boolean bool = true;
        int i = 0;
        double sum = 0;

        while (bool) {
            sum += list.get(i);
            if (sum > p) {
                bool = false;
                a = pickActionSoftmax(i);
            }

            i++;
        }
        return a;
    }



    public Action _pickAction()
    {
        if (Configuration.PICK_ACTION_POLICY.equals(Constants.ACTION_POLICY_EGREEDY)) {

            double epsilon = 1.0 / t;

            return egreedyAction(state, epsilon);
        } else {
            return softmax();
        }
    }


}
