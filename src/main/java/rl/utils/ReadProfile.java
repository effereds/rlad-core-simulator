package rl.utils;

import rl.simulator.Configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadProfile {

    String FILENAME;
    BufferedReader br;
    FileReader fr;

    private static ReadProfile istance = null;
    private ReadProfile() {
        try {
            this.FILENAME = Configuration.PROFILE;
            this.fr = new FileReader(FILENAME);
            this.br = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static ReadProfile getIstance() {
        if(istance==null)
            istance = new ReadProfile();
        return istance;
    }

    public String readLine() {
        String sCurrentLine = null;
        try {

            if ((sCurrentLine = br.readLine()) != null) {

            }

        } catch (
                IOException e)

        {
            e.printStackTrace();

        } finally

        {

            try {
                if (sCurrentLine == null) {
                    if (br != null)
                        br.close();

                    if (fr != null)
                        fr.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return sCurrentLine;
    }
}