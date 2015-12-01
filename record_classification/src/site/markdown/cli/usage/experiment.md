Usage: classli [options] [command] [command options]
  Options:
    -c, --commands
       Specifies the path to a text file containing the commands to be executed
       (one command per line).
    -h, --help
       Shows usage.
       Default: false
    -iv, --internalVerbosity
       Specifies the verbosity of the internal logging mechanism.
       Default: SEVERE
       Possible Values: [ALL, SEVERE, WARNING, INFO, OFF]
    -v, --verbosity
       Specifies the verbosity of the command line interface.
       Default: INFO
       Possible Values: [ALL, SEVERE, WARNING, INFO, OFF]
    -w, --workingDirectory
       Specifies path in which to perform commands.
       Default: /home/secure/workspace/nrs_batch_file/test
  Commands:
    init      Initialises a new classification process, allowing the state to be persisted.
      Usage: init [options]
        Options:
          -f, --force
             Weather to replace configuration if it already exists.
             Default: false

    set      Sets a variable in the configuration of this program.
      Usage: set [options]
        Options:
          -ch, --charset
             The default charset of input/output files.
             Possible Values: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE, UTF_16, US_ASCII]
          -c, --classifier
             The classifier to use for the classification process.
             Possible Values: [EXACT_MATCH, STRING_SIMILARITY_LEVENSHTEIN, STRING_SIMILARITY_JARO_WINKLER, STRING_SIMILARITY_JACCARD, STRING_SIMILARITY_DICE, OLR, NAIVE_BAYES, VOTING_ENSEMBLE_EXACT_ML_SIMILARITY, VOTING_ENSEMBLE_EXACT_SIMILARITY]
          -d, --delimiter
             The default delimiter of input/output files.
          -f, --format
             The default format of the input/output tabular data files.
             Possible Values: [DEFAULT, EXCEL, MYSQL, RFC4180, RFC4180_PIPE_SEPARATED, TDF]
          -it, --internalTrainingRecordRatio
             The default internal training ratio.
          -r, --randomSeed
             The seed of random number generator.
          -s, --serializationFormat
             The format with which the classifier is serialized.
             Possible Values: [JAVA_SERIALIZATION, JSON, JSON_COMPRESSED]
          -t, --trainingRatio
             The default internal training ratio.
          -v, --verbosity
             Specifies the verbosity of the command line interface.
             Possible Values: [ALL, SEVERE, WARNING, INFO, OFF]

    classify      Classifies the loaded unseen records.
      Usage: classify [options]
        Options:
        * -o, --output
             The path in which to output the classified evaluation records.

    evaluate      Evaluates the classifier using the loaded gold standard records.
      Usage: evaluate [options]
        Options:
          -o, --output
             command.evaluate.output.description

    train      Trains the classifier using the loaded gold standard data.
      Usage: train [options]
        Options:
          -it, --internalTrainingRecordRatio
             The ratio of gold standard records to be used for training as
             opposed to internal evaluation. The value must be between 0.0 to 1.0
             (inclusive).
             Default: 0.8

    experiment      Enables experimentation mode.
      Usage: experiment [options]
        Options:
        * -c, --commands
             Specifies the path to a text file containing the commands to be
             executed (one command per line).
          -r, --repeat
             Number of times the experiment should be repeated.
             Default: 5

    clean      Cleans loaded gold standard and unseen records.
      Usage: clean [options]       [command] [command options]
        Options:
          -c, --cleaner
             One or more cleaners with which to clean loaded gold standard
             and/or unseen records.
  Commands:
          stop_words      Cleans user-specified stop words
      Usage: stop_words [options]
        Options:
          -cs, --caseSensitive
             Whether the list of stop words are case sensitive.
             Default: false
          -c, --charset
             The character encoding of the input file.
             Default: SYSTEM_DEFAULT
             Possible Values: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE, UTF_16, US_ASCII]
        * -s, --from
             The path to the file containing the stop words to be cleaned.

          spelling      Cleans spellings using user-specified dictionaries.
      Usage: spelling [options]
        Options:
          -a, --accuracyThreshold
             The threshold of accuracy, above which to replace a word with the
             closest found in the dictionary.
             Default: 0.5
          -cs, --caseSensitive
             Whether the list of stop words are case sensitive.
             Default: false
          -c, --charset
             The character encoding of the input file.
             Default: SYSTEM_DEFAULT
             Possible Values: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE, UTF_16, US_ASCII]
          -d, --distanceFunction
             The function by which to measure distance between a word in the
             dictionary and a word to be spell checked.
             Default: JARO_WINKLER
             Possible Values: [N_GRAMS_2, N_GRAMS_3, N_GRAMS_4, N_GRAMS_5, N_GRAMS_6, N_GRAMS_7, LEVENSTEIN, DAMERAU_LEVENSHTEIN, JARO_WINKLER]
        * -s, --from
             The path to the file containing the stop words to be cleaned.


    load      Loads resources from a file.
      Usage: load [options]       [command] [command options]
        Options:
          -c, --charset
             The charset of the resource file to be loaded.
             Default: SYSTEM_DEFAULT
             Possible Values: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE, UTF_16, US_ASCII]
        * -s, --from
             The path to the resource file to be loaded.
          -o, --overrideExisting
             Whether to override any existing resource with the same name.
             Default: false
  Commands:
          unseen      Load unseen records from a file.
      Usage: unseen [options]
        Options:
          -d, --delimiter
             The character by which the values are delimited.
             Default: ,
          -f, --format
             The format of the csv file containing the records to be loaded.
             Default: DEFAULT
             Possible Values: [DEFAULT, EXCEL, MYSQL, RFC4180, RFC4180_PIPE_SEPARATED, TDF]
          -ii, --id_column_index
             The zero-based index of the column containing the ID.
             Default: 0
          -li, --label_column_index
             The zero-based index of the column containing the label.
             Default: 1
          -h, --skip_header
             Whether to consider the first records in the source file to be
             headers.
             Default: false

          gold_standard      Load gold standard records from a file.
      Usage: gold_standard [options]
        Options:
          -ci, --class_column_index
             The zero-based index of the column containing the class associated
             to each label.
             Default: 2
          -d, --delimiter
             The character by which the values are delimited.
             Default: ,
          -f, --format
             The format of the csv file containing the records to be loaded.
             Default: DEFAULT
             Possible Values: [DEFAULT, EXCEL, MYSQL, RFC4180, RFC4180_PIPE_SEPARATED, TDF]
          -ii, --id_column_index
             The zero-based index of the column containing the ID.
             Default: 0
          -li, --label_column_index
             The zero-based index of the column containing the label.
             Default: 1
          -h, --skip_header
             Whether to consider the first records in the source file to be
             headers.
             Default: false
          -t, --trainingRatio
             The ratio of gold standard records to be used for training. The
             value must be between 0.0 to 1.0 (inclusive).
             Default: 0.8



