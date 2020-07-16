package rl.agents;

import rl.actions.*;
import rl.reward.RewardProvider;
import rl.services.MGNQueue;
import rl.services.Service;
import rl.simulator.Configuration;
import rl.states.State;
import rl.states.StateVerticalScaling;
import rl.utils.Constants;
import rl.utils.ProfileFileParser;
import rl.utils.QTable;
import rl.utils.UnknownCostEstimator;

public class ModelBasedAgent extends RLAgent {

    protected double[][] P = new double[State.UTIL_STATES] [State.UTIL_STATES]; // Probability matrix
    protected boolean known_P_matrix;
    protected boolean known_response_time_model;
    protected MGNQueue operatorModel ;
    protected UnknownCostEstimator unknownCost;
    protected RewardProvider rewardProvider;
    private long[][] transitions_cnt = new long[State.UTIL_STATES] [State.UTIL_STATES]; //to update P
    public static double ALPHA = 0.1;
    boolean FAKE_INPUT_RATE_PROB = false;
    int HORIZON = 0; //if !=0, MDP learns faster
    boolean FINITE_HORIZON_MDP = (HORIZON > 0);

    private final double MAX_Q_VALUE = 1000;

    public ModelBasedAgent (Configuration conf,
                                      Service serv, RewardProvider rp, boolean init_value_function)

    {
        super(conf,serv);
        this.rewardProvider = rp;

        if (conf.known_input_profile_filename.length() > 0) {
            known_P_matrix = true;
            if (!FAKE_INPUT_RATE_PROB)
                parsePmatrix(conf.known_input_profile_filename);
            else
                initFakeProbabilityEstimates();
        } else {
            known_P_matrix = false;
            initProbabilityEstimates();
        }

        if (conf.mb_resptime_model == Configuration.MB_RESPTIME_UNKNOWN) {
            known_response_time_model = false;
            initCostEstimates();
        } else {
            known_response_time_model = true;
            initKnownResptimeModel(conf);
        }

        if (init_value_function) {
            allocateQTable();

            if (known_response_time_model && known_P_matrix) {
                valueIteration(); /* MDP */
            } else {
                valueIteration(1);
            }
        }
    }

    public ModelBasedAgent (Configuration conf,
                                      Service serv, RewardProvider rp)
    { this(conf, serv, rp, true); }



    public double update (long t, double cost, double u)
    {
        super.lambdaTps = u; //update input rate

        // Determine s'
        State ns;
        if (configuration == Constants.HORIZONTAL_SCALING) {
            ns = new State();
            // Service has been updated in the last execution, whereas state has not yet been updated
            ns.k = state.k + pickedAction.getInstanceDelta();
            ns.util = ns.discretizeUtil(super.getService().getUtilization());
        } else {
            StateVerticalScaling tmp = new StateVerticalScaling();
            // Service has been updated in the last execution, whereas state has not yet been updated
            tmp.cpu = super.getService().getCpu();
            tmp.k = super.getService().getInstances();
            tmp.util = tmp.discretizeUtil(super.getService().getUtilization());
            ns = tmp;
        }

        if (!known_P_matrix) {
            updateProbabilityEstimate(state,ns);
        }

        if (!known_response_time_model) {
            updateCostEstimate(state,ns,cost);
        }

        double delta = 0.0;

        if (!known_response_time_model || !known_P_matrix) {
            delta = valueIteration(1);
        }

        state = ns;

        return 0.0;
    }


    private void initProbabilityEstimates()
    {
        for (int i = 0; i<State.UTIL_STATES; ++i) {
            for (int j = 0; j<State.UTIL_STATES; ++j) {
                if (i == j) {
                    transitions_cnt[i][i] = 1;
                    P[i][i] = 1.0;
                } else {
                    transitions_cnt[i][j] = 0;
                    P[i][j] = 0.0;
                }
            }
        }
    }

    private void initFakeProbabilityEstimates()
    {
        for (int i = 0; i< State.UTIL_STATES; ++i) {
            for (int j = 0; j< State.UTIL_STATES; ++j) {
                P[i][j] = 0.0;
            }

            if (i==0) {
                P[i][i] = 0.5;
                P[i][i+1] = 0.5;
            } else if (i== State.UTIL_STATES-1) {
                P[i][i-1] = 0.5;
                P[i][i] = 0.5;
            } else {
                P[i][i-1] = 0.33;
                P[i][i] = 0.33;
                P[i][i+1] = 0.33;
            }
        }
    }

    private void initKnownResptimeModel(Configuration conf)
    {
        double known_servtime_mean = service.getServTimeMean();
        double known_servtime_var = 0.0;

        if (conf.mb_resptime_model == Configuration.MB_RESPTIME_MM1) {
            known_servtime_var = known_servtime_mean*known_servtime_mean;
        } else if (conf.mb_resptime_model == Configuration.MB_RESPTIME_MD1) {
            known_servtime_var = 0.0;
        } else if (conf.mb_resptime_model == Configuration.MB_RESPTIME_KNOWN) {
            known_servtime_var = service.getServTimeVariance();
        } else {
            System.out.println("Undefined response time model");
        }

        operatorModel = new MGNQueue(known_servtime_mean, known_servtime_var);
    }

    private void parsePmatrix(String profileFilename)
    {
        ProfileFileParser parser = new ProfileFileParser(profileFilename);
        parser.parse(P, transitions_cnt);
    }


    protected void updateProbabilityEstimate (State s, State ns)
    {
        int i = s.util;
        int j = ns.util;

        ++transitions_cnt[i][j];

        int total_transitions = 0;
        for (int k=0; k<State.UTIL_STATES; ++k) {
            total_transitions += transitions_cnt[i][k];
        }
        for (int k=0; k< State.UTIL_STATES; ++k) {
            double newval = transitions_cnt[i][k]/(double)total_transitions;
            assert(newval >= 0.0);
            assert(newval <= 1.0);

            P[i][k] = newval;
        }
    }


    protected void initCostEstimates()
    {

        this.unknownCost = new UnknownCostEstimator(n_states());

    }



    protected void updateCostEstimate (State s, State ns, double cost)
    {
        double known = rewardProvider.knownReward(this, s,
                pickedAction);
        double unknown = cost - known;
        assert(unknown >= -0.001);

        if (configuration == Constants.HORIZONTAL_SCALING) {
           // int index = ns.util + State.UTIL_STATES * (ns.k-1);
            double oldval = unknownCost.get(ns);
            double newval = (1.0-ALPHA)*oldval + ALPHA * unknown;
            unknownCost.update(ns, newval);


            int k0,k1;
            int l0,l1;

            if (newval > oldval) {
                k0 = 1;
                k1 = ns.k;
                l0 = ns.util;
                l1 = State.UTIL_STATES-1;

                for (int l = l0; l<=l1; ++l) {
                    for (int k = k0; k<=k1; ++k) {
                        State st = new State(k, l);
                        if (unknownCost.get(st) < newval)
                            unknownCost.update(st, newval);
                    }
                }
            } else if (newval < oldval) {
                k0 = ns.k;
                k1 = service.getMaxParallelism();
                l0 = 0;
                l1 = ns.util;
                for (int l = l0; l <= l1; ++l) {
                    for (int k = k0; k <= k1; ++k) {
                        State st = new State(k, l);
                        if (unknownCost.get(st) > newval)
                            unknownCost.update(st, newval);
                    }
                }
            }

        } else {

            int cpu = ((StateVerticalScaling)ns).cpu;
            int index = ((int)(cpu/Configuration.CPU_DICRETIZATION_QUANTUM) -1) +
                    ns.util * StateVerticalScaling.CPU_STATES +State.UTIL_STATES *(ns.k - 1) *
                    StateVerticalScaling.CPU_STATES;

            StateVerticalScaling ns_vs = (StateVerticalScaling) ns;

            double oldval = unknownCost.get(ns_vs);
            double newval = (1.0-ALPHA)*oldval + ALPHA * unknown;
            unknownCost.update(ns_vs, newval);

            StateVerticalScaling nns = (StateVerticalScaling) ns;
            int k0,k1;
            int l0,l1;
            int c0, c1;

            if (newval > oldval) {
                k0 = 1;
                k1 = nns.k;
                l0 = nns.util;
                l1 = State.UTIL_STATES-1;
                c0 = (int)Configuration.CPU_DICRETIZATION_QUANTUM;
                c1 = nns.cpu;
                for (int c = c0; c<= c1; c += Configuration.CPU_DICRETIZATION_QUANTUM) {
                    for (int l = l0; l <= l1; ++l) {
                        for (int k = k0; k <= k1; ++k) {

                            StateVerticalScaling otherState = new StateVerticalScaling(k, l, c);
                            if (unknownCost.get(otherState) < newval)
                                unknownCost.update(otherState, newval);
                      }
                    }
                }
            } else if (newval < oldval) {
                k0 = nns.k;
                k1 = service.getMaxParallelism();
                l0 = 0;
                l1 = nns.util;
                c0 = nns.cpu;
                c1 = (int)Configuration.CPU_MAX_VALUE;
                for (int c = c0; c <= c1; c += Configuration.CPU_DICRETIZATION_QUANTUM) {
                    for (int l = l0; l <= l1; ++l) {
                        for (int k = k0; k <= k1; ++k) {
                            StateVerticalScaling otherState = new StateVerticalScaling(k, l, c);
                            if (unknownCost.get(otherState) > newval)
                                unknownCost.update(otherState, newval);
                      }
                    }
                }
            }

        }

    }


    private double evaluateQ (State s, Action a)
    {
        double qvalue = 0.0;
        qvalue += rewardProvider.knownReward(this, s, a);

        State ns;
        if (configuration == Constants.HORIZONTAL_SCALING) {
            ns = new State();
            ns.k = s.k + a.getInstanceDelta();
            ns.util = ns.computeUtilization(this.lambdaTps, ns.k, (int)Configuration.CPU_MAX_VALUE);
        } else {
            StateVerticalScaling s_tmp = (StateVerticalScaling)s;

            StateVerticalScaling tmp = new StateVerticalScaling();
            tmp.k = s_tmp.k + a.getInstanceDelta();

            if (a instanceof ActionVerticalOrHorizontal) {

                ActionVerticalOrHorizontal a_tmp = (ActionVerticalOrHorizontal)a;
                tmp.cpu = s_tmp.cpu + a_tmp.getInstanceCpu();

            } else {
                ActionVerticalAndHorizontal a_tmp = (ActionVerticalAndHorizontal)a;
                tmp.cpu = s_tmp.cpu + a_tmp.getInstanceCpu();

            }
            tmp.util = tmp.computeUtilization(this.lambdaTps, tmp.k, tmp.cpu);
            ns = tmp;
        }

        for (ns.util = 0; ns.util < State.UTIL_STATES; ++ns.util) {
            double p = P[s.util][ns.util];
            double value_ns = value(ns);
            double sla_cost;

            if (!known_response_time_model) {
                sla_cost = unknownCost.get(ns);
            } else {
                sla_cost = rewardProvider.slaReward(this, operatorModel, super.getLambdaTps(), ns);
            }
            qvalue += p * (sla_cost + GAMMA*value_ns);
        }

        return qvalue;
    }

    // Markov Decision Process
    private double fhEvaluateQ (State s, Action a, QTable qOld, int t)
    {
        double qvalue = 0.0;
        qvalue += rewardProvider.knownReward(this, s, a);

        if (t==HORIZON)
            return 0.0;

        State ns = new State();
        ns.k = s.k + a.getInstanceDelta();
        ns.util = ns.discretizeUtil(getService().getUtilization());

        int _base_index = State.UTIL_STATES * (ns.k-1);
        for (ns.util = 0; ns.util < State.UTIL_STATES; ++ns.util) {
            double p = P[s.util][ns.util];
            double value_ns = value(ns, qOld);
            double sla_cost;

            if (!known_response_time_model) {
                sla_cost = unknownCost.get(ns);
            } else {
                sla_cost = rewardProvider.slaReward(this, operatorModel, super.getLambdaTps(), ns);
            }
            qvalue += p * (sla_cost + GAMMA*value_ns);
        }

        return qvalue;
    }


    private double __valueIteration(State s, double lambdaTps) {
        double delta = 0.0;

        if (configuration == Constants.HORIZONTAL_SCALING) {

            for (Action a : ActionIterator.getInstance().getActionIterator()) {
                if (!isValidAction(s, a))
                    continue;

                double oldval = q.get(s, a);
                double newval = evaluateQ(s, a);
                double _delta = Math.abs(newval - oldval);

                delta = Math.max(delta, _delta);
                q.set(s, a, newval);
            }
        } else if (configuration == Constants.HORIZONTAL_OR_VERTICAL) {
            for (Action a: ActionVerticalOrHorizontalIterator.getInstance().getActionIterator()) {
                if (!isValidAction(s, a))
                    continue;

                double oldval = q.get(s, a);
                double newval = evaluateQ(s, a);
                double _delta = Math.abs(newval - oldval);

                delta = Math.max(delta, _delta);
                q.set(s, a, newval);
            }
        } else {
            for (Action a: ActionVerticalAndHorizontalIterator.getInstance().getActionIterator()) {
                if (!isValidAction(s, a))
                    continue;

                double oldval = q.get(s, a);
                double newval = evaluateQ(s, a);
                double _delta = Math.abs(newval - oldval);

                delta = Math.max(delta, _delta);
                q.set(s, a, newval);
            }
        }
        return delta;
    }

    //Markov Decision Process
    private void fh__value_iteration(State s, QTable qOld, int t) {
        for (Action a : ActionIterator.getInstance().getActionIterator()) {
            if (!isValidAction(s,a))
                continue;

            double newval = fhEvaluateQ(s,a,qOld,t);
            q.set(s,a,newval);
        }
    }

    private double _valueIteration()
    {
        double delta = 0.0;
        State s;
        if (configuration == Constants.HORIZONTAL_SCALING) {
            for (int k = 1; k <= service.getMaxParallelism(); ++k) {
                for (int l = 0; l < State.UTIL_STATES; ++l) {
                    s = new State(k, l);

                    double new_delta = __valueIteration(s, super.getLambdaTps());
                    delta = Math.max(delta, new_delta);
                }
            }
        } else {
            for (int c= (int) Configuration.CPU_DICRETIZATION_QUANTUM;
                 c <= Configuration.CPU_MAX_VALUE; c += Configuration.CPU_DICRETIZATION_QUANTUM) {

                for (int k = 1; k <= service.getMaxParallelism(); ++k) {
                    for (int l = 0; l < State.UTIL_STATES; ++l) {
                        s = new StateVerticalScaling(k, l,c);

                        double new_delta = __valueIteration(s, super.getLambdaTps());
                        delta = Math.max(delta, new_delta);
                    }
                }
            }
        }


        return delta;
    }
    //Markov Decision Process
    private double _fhValueIteration()
    {
        State s;

        QTable q_old = new QTable(n_states(), Action.N_ACTIONS());

        for (int t = HORIZON; t >= 0; --t) {
            QTable temp = q_old;
            q_old = q;
            q = temp;

            for (int k = 1; k <= service.getMaxParallelism(); ++k) {
                for (int l = 0; l < State.UTIL_STATES; ++l) {
                    s = new State(k, l);

                    fh__value_iteration(s,q_old,t);
                }
            }
        }

        return 0.0;
    }


    private double valueIteration(int max_iter)
    {
        double delta = 0.0;
        int iter = 0;

        do {
            delta = _valueIteration();
        } while (iter++ < max_iter);

        return delta;
    }

    //Markov Decision Process
    double valueIteration()
    {
        // Finite horizon simulation
        if (FINITE_HORIZON_MDP)
            return _fhValueIteration();

        double MAX_ERROR = 0.00001;
        double delta = 0.0;

        do {
            delta = _valueIteration();
        } while (delta > MAX_ERROR); // Q(s,a) -> q*(s,a)


        return delta;
    }


    public Action _pickAction(State s) {
        Action a = greedyAction(s);
        return a;
    }

    public Action _pickAction() { return _pickAction(this.state); }


}
