package rl.utils;

import rl.states.State;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileFileParser {

    private String filename;

    public ProfileFileParser (String filename) {
        this.filename = filename;
    }

    public void parse (double[][] P,
                       long[][] transition_cnt) {

        for (int i = 0; i<State.UTIL_STATES; ++i) {
            for (int j = 0; j<State.UTIL_STATES; ++j) {
                transition_cnt[i][j] = 0;
                P[i][j]=0.0;
            }
        }



        File file = new File(this.filename);
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);

            int tps, util;
            int current = -1;

            while ((tps = fis.read()) != -1) {
                util = State.discretizeUtil(tps);

                if (current >= 0) {
                    transition_cnt[current][util] += 1;
                }

                current = util;
            }

            for (int i = 0; i< State.UTIL_STATES; ++i) {
                long total_transitions = 0;
                for (int k=0; k< State.UTIL_STATES; ++k) {
                    total_transitions += transition_cnt[i][k];
                    assert(total_transitions >= 0);
                }
                for (int k=0; k< State.UTIL_STATES; ++k) {
                    double newval = transition_cnt[i][k]/(double)total_transitions;
                    assert(newval >= 0.0);

                    P[i][k] = newval;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
