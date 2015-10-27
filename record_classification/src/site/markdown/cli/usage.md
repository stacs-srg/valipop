# Usage



## Commands

### Init


### Set

### Load

#### Load Unseen Records

#### Load Gold Standard Records

### Clean

#### Clean User-defined Stop Words

#### Clean User-defined Spelling

### Evaluate

### Train

### Classify



    Usage: classi [options] [command] [command options]
      Options:
        -c, --commands
           Specifies the path to a text file containing the commands to be executed
           (one command per line).
        -h, --help
           Shows usage.
           Default: false
        -v, --verbosity
           Specifies the verbosity of the command line interface.
           Default: INFO
           Possible Values: [ALL, SEVERE, WARNING, INFO, OFF]
      Commands:
        init      Initialises a new classification process, allowing the state to be persisted.
          Usage: init [options]
            Options:
              -f, --force
                 Weather to replace configuration if it already exists.
                 Default: false
    
        set      
          Usage: set [options]
            Options:
              -ch, --charset
                 Options: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE,
                 UTF_16, US_ASCII]
                 Possible Values: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE, UTF_16, US_ASCII]
              -c, --classifier
                 Options: [EXACT_MATCH, STRING_SIMILARITY_LEVENSHTEIN,
                 STRING_SIMILARITY_JARO_WINKLER, STRING_SIMILARITY_JACCARD, STRING_SIMILARITY_DICE, OLR,
                 NAIVE_BAYES, VOTING_ENSEMBLE_EXACT_ML_SIMILARITY,
                 VOTING_ENSEMBLE_EXACT_SIMILARITY]
                 Possible Values: [EXACT_MATCH, STRING_SIMILARITY_LEVENSHTEIN, STRING_SIMILARITY_JARO_WINKLER, STRING_SIMILARITY_JACCARD, STRING_SIMILARITY_DICE, OLR, NAIVE_BAYES, VOTING_ENSEMBLE_EXACT_ML_SIMILARITY, VOTING_ENSEMBLE_EXACT_SIMILARITY]
              -d, --delimiter
                 
              -it, --internalTrainingRecordRatio
                 
              -r, --randomSeed
                 
              -s, --serializationFormat
                 Options: [JAVA_SERIALIZATION, JSON, JSON_COMPRESSED]
                 Possible Values: [JAVA_SERIALIZATION, JSON, JSON_COMPRESSED]
              -t, --trainingRatio
                 
    
        classify      Classifies the loaded unseen records.
          Usage: classify [options]
    
        evaluate      Evaluates the classifier using the loaded gold standard records.
          Usage: evaluate [options]
            Options:
              -o, --outputEvaluationRecordsTo
                 The path to which to export the classified evaluation records.
    
        train      Trains the classifier using the loaded gold standard data.
          Usage: train [options]
            Options:
              -it, --internalTrainingRecordRatio
                 The ratio of gold standard records to be used for training as
                 opposed to internal evaluation. The value must be between 0.0 to 1.0
                 (inclusive).
                 Default: 0.8
    
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
                 The path to the file containing the stop words to be cleaned.
                 Default: UTF_8
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
                 The path to the file containing the stop words to be cleaned.
                 Default: UTF_8
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
                 Default: UTF_8
                 Possible Values: [SYSTEM_DEFAULT, ISO_8859_1, UTF_8, UTF_16BE, UTF_16LE, UTF_16, US_ASCII]
            * -s, --from
                 The path to the resource file to be loaded.
              -n, --named
                 The name to associate to the loaded resource.
              -o, --overrideExisting
                 command.load.force.description
                 Default: false
      Commands:
              unseen      Load unseen records from a file.
          Usage: unseen [options]
            Options:
              -d, --delimiter
                 The character by which the values are delimited.
                 Default: |
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
                 Default: |
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
    
    

