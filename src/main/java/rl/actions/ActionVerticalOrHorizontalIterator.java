package rl.actions;

import java.util.ArrayList;

public class ActionVerticalOrHorizontalIterator extends ActionIterator {

    private static ArrayList<Action> actionIterator;
    private static ActionVerticalOrHorizontalIterator instance = null;

    protected ActionVerticalOrHorizontalIterator() {

    }
    // action = {OUT_NULL, NULL_NULL, IN_NULL, NULL_DOWN, NULL_UP}
    public static ActionVerticalOrHorizontalIterator getInstance() {
        if (instance == null) {
            ArrayList<Action> tmp = new ArrayList<Action>();
            tmp.add(new ActionVerticalOrHorizontal(ActionVerticalOrHorizontal.OUT_NULL));
            tmp.add(new ActionVerticalOrHorizontal(ActionVerticalOrHorizontal.NULL_NULL));
            tmp.add(new ActionVerticalOrHorizontal(ActionVerticalOrHorizontal.IN_NULL));
            tmp.add(new ActionVerticalOrHorizontal(ActionVerticalOrHorizontal.NULL_DOWN));
            tmp.add(new ActionVerticalOrHorizontal(ActionVerticalOrHorizontal.NULL_UP));
            actionIterator = tmp;
            instance = new ActionVerticalOrHorizontalIterator();
        }
        return instance;
    }

    public ArrayList<Action> getActionIterator() {
        return actionIterator;
    }



}
