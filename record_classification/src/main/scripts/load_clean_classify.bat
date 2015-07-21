@ECHO OFF

set CLASSPATH=.
set JAVA_HOME=JREs\jre1.8.0_51_x64

::set PROCESS_DIR=hisco_ensemble_similarity
set PROCESS_DIR=hisco_ensemble_with_olr
set UNSEEN_DATA=test_evaluation_ascii_windows.csv
set OUTPUT=output.csv

%JAVA_HOME%\bin\java -Xms128m -Xmx512m -jar record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar load_clean_classify -p %PROCESS_DIR% -d %UNSEEN_DATA% -o %OUTPUT% -f JSON_COMPRESSED -cl COMBINED -dl "|"