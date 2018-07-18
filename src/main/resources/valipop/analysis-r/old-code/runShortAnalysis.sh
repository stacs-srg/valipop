
summaryInputFile=$1
dirAboveResultsFile=$2

outputFile=$3
largestBirthLabel=$4

first=t
while IFS=, read c1 c2 c3 c4 c5 c6 c7 c8 c9 c10 c11 c12 c13 c14 c15 c16 c17 c18
do
	if [ "$first" == "t" ]
		then
		first="f"
		echo "A"
	elif [ "$c6" == "TRUE" -o "$c6" == "true" ]
		then
		echo "B"
		runDirPath="$dirAboveResultsFile/$c18"
		purposeDirPath="$dirAboveResultsFile/results/$c2"
		runDir="$c1"
		echo "Analysis running for $c1"
		Rscript short-analysis.R "$outputFile" "$purposeDirPath" "$runDir" "$largestBirthLabel"
	fi
done < $summaryInputFile
