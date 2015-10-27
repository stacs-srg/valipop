# Usage



## Backgorund

TODO explain command line interface
TODO explain program name
TODO explain the command line parameter concept - short and long name
TODO explain the command concept

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


## Commands

### `init`

The `init` command initialises a new workflow that gets persisted on your hard drive. This means when classli program is closed and re-opened, it will remember where things were left off. 

To execute this command, type the following and press enter:

    classli init

The execution of the command above will result in creation of a folder called `.classli` in the current working directory. This is where all the classli files and settings will be stored. The execution of this command in a working directory that already contains a folder named `.classli` will result in failure; to override an existing `.classli` folder, the _force_ parameter must be set:
 
    classli init -f

Alternatively, the _force_ can be set using its log name:

    classli init --force

Short | Long    | Optional | Default Value | Description
------|---------|----------|---------------|-----------------
`-f`  |`--force`| Yes      | `false`       | Weather to replace any existing configuration folder.
[`init` command options.]

### `set`

The `set` command sets the value of configurable variables in the classli configuration. The configurable variables are specified with one of the following options:

* `-ch` or `--charset` -- specifies default [character encoding](https://en.wikipedia.org/wiki/Character_encoding) of input/output files, which should be used if no other encoding is specified. The value of this option can be one of:

    - `SYSTEM_DEFAULT` -- the default character encoding of the current operating system.
    - `ISO_8859_1` -- the [ISO_8859_1](https://en.wikipedia.org/wiki/ISO/IEC_8859-1) ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.
    - `UTF_8` -- the [UTF-8](https://en.wikipedia.org/wiki/UTF-8), eight-bit [UCS](https://en.wikipedia.org/wiki/Universal_Coded_Character_Set) Transformation Format.
    - `UTF_16` -- the [UTF-16](https://en.wikipedia.org/wiki/UTF-16), sixteen-bit [UCS](https://en.wikipedia.org/wiki/Universal_Coded_Character_Set) Transformation Format, byte order identified by an optional byte-order mark.
    - `UTF_16BE` -- the [UTF-16](https://en.wikipedia.org/wiki/UTF-16), sixteen-bit [UCS](https://en.wikipedia.org/wiki/Universal_Coded_Character_Set) Transformation Format, big-endian byte order.
    - `UTF_16LE` -- the [UTF-16](https://en.wikipedia.org/wiki/UTF-16), sixteen-bit [UCS](https://en.wikipedia.org/wiki/Universal_Coded_Character_Set) Transformation Format, little-endian byte order
    - `US_ASCII` -- the seven-bit [ASCII](https://en.wikipedia.org/wiki/ASCII) , a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set

* `-c` or `--classifier` -- specifies the classifier to be used for classification of records. The possible values for this option are:

    - `EXACT_MATCH` -- classifies records, which exactly match the training records.
    - `STRING_SIMILARITY_LEVENSHTEIN` -- classifies records based their [Levenshtein](https://en.wikipedia.org/wiki/Levenshtein_distance) similarity to the training records.
    - `STRING_SIMILARITY_JARO_WINKLER` -- classifies records based their [Jaro Winkler](https://en.wikipedia.org/wiki/Jaro–Winkler_distance) similarity to the training records.
    - `STRING_SIMILARITY_JACCARD` -- classifies records based their [Jaccard](https://en.wikipedia.org/wiki/Jaccard_index) similarity to the training records.
    - `STRING_SIMILARITY_DICE` -- classifies records based their [Dice](http://en.wikipedia.org/wiki/S%C3%B8rensen%E2%80%93Dice_coefficient) similarity to the training records.
    - `OLR` -- classifies records using an [online machine learning](https://en.wikipedia.org/wiki/Online_machine_learning) algorithm based on a [logistic regression](https://en.wikipedia.org/wiki/Logistic_regression) model. It is a heavily modified version of [Mahout's Logistic Regression implementation](https://mahout.apache.org/users/classification/logistic-regression.html).
    - `NAIVE_BAYES` -- classifies records a (naive bayes classifier)[https://en.wikipedia.org/wiki/Naive_Bayes_classifier]. It is heavily based on [Weka](http://www.cs.waikato.ac.nz/ml/weka/) implementation of [naive bayes](http://weka.sourceforge.net/doc.dev/weka/classifiers/bayes/NaiveBayes.html).
    - `VOTING_ENSEMBLE_EXACT_ML_SIMILARITY` -- classifies records using collective voting of `EXACT_MATCH`, `OLR`, `NAIVE_BAYES`, `STRING_SIMILARITY_LEVENSHTEIN`, STRING_SIMILARITY_JARO_WINKLER`, `STRING_SIMILARITY_JACCARD` and `STRING_SIMILARITY_DICE` classifiers. 
    - `VOTING_ENSEMBLE_EXACT_SIMILARITY` -- classifies records using collective voting of `EXACT_MATCH`, `STRING_SIMILARITY_LEVENSHTEIN`, STRING_SIMILARITY_JARO_WINKLER`, `STRING_SIMILARITY_JACCARD` and `STRING_SIMILARITY_DICE` classifiers. 

* `-d`, `--delimiter` -- specifies the default delimiter character of input/output tabular data files. The value of this option may be specified as a single character. If multiple characters are specified, the first character will be considered as the delimiter.

* `-r` or `--randomSeed` -- specifies the seed of the internal random number generator. By default the internal random number generator us non-deterministic. Setting the random seed results in deterministic selection of training and evaluation records. 

* `-s` or `--serializationFormat` -- specifies the format by which to persist the classifier. The possible values for this option are:

    - `JAVA_SERIALIZATION` -- use Java object serialisation. 
    - `JSON` -- use human-readable [JSON](https://en.wikipedia.org/wiki/JSON) format.
    - `JSON_COMPRESSED` -- use compressed human-readable [JSON](https://en.wikipedia.org/wiki/JSON) format.
    
* `-t` or `--trainingRatio` -- specifies the default proportion of _gold standard_ records to use for training the classifier. The remaining gold standard records are used for evaluation of the classifier via the `evaluate` command.
                 
* `-it` or `--internalTrainingRecordRatio` -- specifies the default proportion of _training_ records to use for training the classifier. The remaining training records will be used by the classifier for self evaluation.    

### Load

#### Load Unseen Records

#### Load Gold Standard Records

### Clean

#### Clean User-defined Stop Words

#### Clean User-defined Spelling

### Evaluate

### Train

### Classify




      Commands:

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
    
    

