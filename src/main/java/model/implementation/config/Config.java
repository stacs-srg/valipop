package model.implementation.config;

import utils.InputFileReader;

import java.io.File;
import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class Config {

    private File varBirthFiles;
    private File varDeathFiles;
    private File varMultipleBirthFiles;
    private File varPartneringFiles;
    private File varSeparationFiles;

    public File[] getVarBirthFiles() {
        return varBirthFiles.listFiles();
    }


    public File[] getVarDeathFiles() {
        return varDeathFiles.listFiles();
    }


    public File[] getVarMultipleBirthFiles() {
        return varMultipleBirthFiles.listFiles();
    }


    public File[] getVarPartneringFiles() {
        return varPartneringFiles.listFiles();
    }


    public File[] getVarSeperationFiles() {
        return varSeparationFiles.listFiles();
    }

    public Config(String pathToConfigFile) {

        String[] configInput = InputFileReader.getAllLines(pathToConfigFile);

        for(String l : configInput) {

            String[] split = l.split("=");
            for(int i = 0; i < split.length; i++)
                split[i] = split[i].trim();

            switch(split[0]) {
                case "var_birth_files":
                    varBirthFiles = new File(split[1]);
                    break;
                case "var_death_files":
                    varDeathFiles = new File(split[1]);
                    break;
                case "var_multiple_birth_files":
                    varMultipleBirthFiles = new File(split[1]);
                    break;
                case "var_partnering_files":
                    varPartneringFiles = new File(split[1]);
                    break;
                case "var_separation_files":
                    varSeparationFiles = new File(split[1]);
                    break;

            }

        }

    }

}
