@REM
@REM record_classification - Automatic record attribute classification.
@REM Copyright © 2014-2016 Digitising Scotland project
@REM (http://digitisingscotland.cs.st-andrews.ac.uk/)
@REM
@REM This program is free software: you can redistribute it and/or modify
@REM it under the terms of the GNU General Public License as published by
@REM the Free Software Foundation, either version 3 of the License, or
@REM (at your option) any later version.
@REM
@REM This program is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM GNU General Public License for more details.
@REM
@REM You should have received a copy of the GNU General Public License
@REM along with this program. If not, see <http://www.gnu.org/licenses/>.
@REM

@ECHO OFF

set CLASSPATH=.
set JVM_OPTIONS="-Xms128m -Xmx512m"
set JAR_FILE="record_classification_cli-1.0-SNAPSHOT.jar"

set PROCESS_DIR=hisco_ensemble_with_olr
set UNSEEN_DATA=test_evaluation_ascii_windows.csv
set OUTPUT=output.csv

java %JVM_OPTIONS% -cp %CLASSPATH% -jar %JAR_FILE% load_clean_classify -p %PROCESS_DIR% -d %UNSEEN_DATA% -o %OUTPUT% -f JSON_COMPRESSED -cl COMBINED -dl "|"
