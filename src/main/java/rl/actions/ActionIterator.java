package rl.actions;

import java.util.ArrayList;

// Singleton class: sample of all possible rl.actions
public class ActionIterator {
    private static ArrayList<Action> actionIterator;
    private static ActionIterator instance = null;

    protected ActionIterator() {

    }

    public static ActionIterator getInstance() {
        if (instance == null) {
            ArrayList<Action> tmp = new ArrayList<Action>();
            tmp.add(new Action(-1));
            tmp.add(new Action(0));
            tmp.add(new Action(1));
            actionIterator = tmp;
            instance = new ActionIterator();
        }
        return instance;
    }

    public ArrayList<Action> getActionIterator() {
        return actionIterator;
    }



}
