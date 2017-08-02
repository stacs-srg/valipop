/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.verisim.utils;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class ProgramTimer {

    private long startTime;
    private long endTime;
    private boolean stopped = false;

    public ProgramTimer() {
        startTime = System.nanoTime();
    }

    public void stopTime() {
        endTime = System.nanoTime();
    }

    public String getTimeMMSS() {

        long runEndTime;

        if (stopped) {
            runEndTime = endTime;
        } else {
            runEndTime = System.nanoTime();
        }

        double runTime = (runEndTime - startTime) / Math.pow(10, 9);
        int minutes = (int) (runTime / 60);
        int seconds = (int) (runTime % 60);
        String rT = minutes + ":" + seconds;
        return rT;

    }

    public double getRunTimeSeconds() {

        long runEndTime;

        if (stopped) {
            runEndTime = endTime;
        } else {
            runEndTime = System.nanoTime();
        }

        return (runEndTime - startTime) / Math.pow(10, 9);

    }


}
