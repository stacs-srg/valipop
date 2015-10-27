# Usage



## Backgorund

TODO explain command line interface

TODO explain program name

TODO explain the command line parameter concept - short and long name

TODO explain the command concept

    Usage: classli [options] [command] [command options]
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

The `init` command initialises a new workflow that gets persisted on your hard drive. This means when classli program is closed and re-opened, it will remember where things were left off. The options of this command are:

* `-f` or `--force` -- enables replacement of any existing configuration folder upon initialisation. This option is not mandatory. By default this option is disabled.

To execute this command, type the following and press enter:

    classli init

The execution of the command above will result in creation of a folder called `.classli` in the current working directory. This is where all the classli files and settings will be stored. The execution of this command in a working directory that already contains a folder named `.classli` will result in failure; to override an existing `.classli` folder, the _force_ parameter must be set:
 
    classli init -f

Alternatively, the _force_ can be set using its log name:

    classli init --force

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

* `-r` or `--randomSeed` -- specifies the seed of the internal random number generator. By default the internal random number generator us non-deterministic. Setting the random seed results in deterministic selection of training and evaluation records. The value of the seed is specified as a 64-bit integer value.

* `-s` or `--serializationFormat` -- specifies the format by which to persist the classifier. The possible values for this option are:

    - `JAVA_SERIALIZATION` -- use Java object serialisation. 
    - `JSON` -- use human-readable [JSON](https://en.wikipedia.org/wiki/JSON) format.
    - `JSON_COMPRESSED` -- use compressed human-readable [JSON](https://en.wikipedia.org/wiki/JSON) format.
    
* `-t` or `--trainingRatio` -- specifies the default proportion of _gold standard_ records to use for training the classifier. The remaining gold standard records are used for evaluation of the classifier via the [`evaluate`](#evaluate) command. The value of this option is specified as a numver between _0.0_ to _1.0_.
                 
* `-it` or `--internalTrainingRecordRatio` -- specifies the default proportion of _training_ records to use for training the classifier. The remaining training records will be used by the classifier for self-evaluation mechanism. The value of this option is specified as a numver between _0.0_ to _1.0_.

* `-f` or `--format` -- specifies the default CSV format of input/output tabular data files. The possible values for this option are:
    - `RFC4180` -- the comma separated format as defined by [RFC 4180](http://tools.ietf.org/html/rfc4180). See [RFC4180](https://commons.apache.org/proper/commons-csv/archives/1.2/apidocs/org/apache/commons/csv/CSVFormat.html#RFC4180)
    - `RFC4180_PIPE_SEPARATED` -- the format as defined by [RFC 4180](http://tools.ietf.org/html/rfc4180), but with `|` (pipe) as delimiter.
    - `DEFAULT` -- based on `RFC4180` allowing empty lines. See [DEFAULT](https://commons.apache.org/proper/commons-csv/archives/1.2/apidocs/org/apache/commons/csv/CSVFormat.html#DEFAULT)
    - `EXCEL` -- based on `RFC4180` allowing missing column names. Please note that the delimiter character used by Excel is locale dependent, it may be necessary to customize the delimiter character via `-d` or `--delimiter` options. See [EXCEL](https://commons.apache.org/proper/commons-csv/archives/1.2/apidocs/org/apache/commons/csv/CSVFormat.html#EXCEL)
    - `MYSQL` -- the default [MySQL](http://dev.mysql.com/doc/refman/5.1/en/load-data.html) format used by the SELECT INTO OUTFILE and LOAD DATA INFILE operations. This is a tab-delimited format with a LF character as the line separator. Values are not quoted and special characters are escaped with `\`. See [MYSQL](https://commons.apache.org/proper/commons-csv/archives/1.2/apidocs/org/apache/commons/csv/CSVFormat.html#MYSQL)
    - `TDF` -- the tab-delimited format. See [TFD](https://commons.apache.org/proper/commons-csv/archives/1.2/apidocs/org/apache/commons/csv/CSVFormat.html#TDF)

At least one option must be specified when calling the `set` command. For example, execution of the following command:

    set --delimiter "|"

sets the default delimiter to `|` (pipe) character. Since the pipe character is a special character in command-line environment, it has been surrounded by double quotes.

In another example, the following command:

    set -t 0.7

sets the default training ratio to 70%. This means unless otherwise specified by default 70% of loaded gold standard data will be used for training the classifier and the remaining 30% will be used for evaluation.

It is also possible to set multiple values at once. For example, the following command:

    set -c OLR -r 42 -s JSON

sets the classifier to the _online logistic regression_ classifier, the random seed to _42_ and the classifier serialization format to _JSON_. 

### `load`

The `load` is used for reading in gold standard and unseen records from a file on the local file system. The options of this command are:

* `-s` or `--from` -- the *mandatory* option that specifies the path to the source file from which to load gold standard or unseen records.
* `-c` or `--charset` -- the character encoding of the source file. If unspecified, the default character encoding is used; see [`set`](#set).
* `-n` or `--named` -- specifies a name for the records to be loaded. If unspecified the file name is used as the name of the loaded records.
* `-o` or `--overrideExisting` -- if specified, the load will overrider any existing gold standard or unseen records with the same name. This option is disable by default. 

In order to specify whether the source file contains unseen or gold standard records, the `load` command must be used in conjunction with one of its sub commands: `unseen` and `gold_standard`.

* `unseen` -- the sub command specifying the type of records in the source file as unseen records. The unseen records are records to be classified by the classifier, where each record consist of a unique numerical ID and a textual label. This sub command has the following options:
    * `-d` or `--delimiter` -- specifies the delimiter character of the tabular data in source file. If unspecified the default delimiter character is used; see [`set`](#set).
    * `-f` or `--format` -- specifies the tabular data format of the source file. If unspecified, the default csv format is used. See [`set`](#set) command for possible values of this option.
    * `-ii` or `--id_column_index` -- specifies the index of the column containing the ID associated to each record. The value is specified as a positive integer including `0`. `0` corresponds to the first column, `1` corresponds to the second column and so on. The default value of this option is `0`.
    * `-li` or `--label_column_index` -- specifies the index of the column containing the label (i.e. text or data) associated to each record. The value is specified as a positive integer including `0`. `0` corresponds to the first column, `1` corresponds to the second column and so on. The default value of this option is `1`.
    * `-h` or `--skip_header` -- whether to consider the first record in the source file as column headers and skip it. this option is disabled by default.

* `gold_standard` -- the sub command specifying the type of records in the source file as gold standard records. The gold standard records are records to be used to train and evaluate the classifier, where each record consist of a unique numerical ID, a textual label and a textual class. This sub command has the following options:
    * `-d` or `--delimiter` -- specifies the delimiter character of the tabular data in source file. If unspecified the default delimiter character is used; see [`set`](#set).
    * `-f` or `--format` -- specifies the tabular data format of the source file. If unspecified, the default csv format is used. See [`set`](#set) command for possible values of this option.
    * `-ii` or `--id_column_index` -- specifies the index of the column containing the ID associated to each record. The value is specified as a positive integer including `0`. `0` corresponds to the first column, `1` corresponds to the second column and so on. The default value of this option is `0`.
    * `-li` or `--label_column_index` -- specifies the index of the column containing the label (i.e. text or data) associated to each record. The value is specified as a positive integer including `0`. `0` corresponds to the first column, `1` corresponds to the second column and so on. The default value of this option is `1`.
    * `-ci` or `--class_column_index` -- specifies the index of the column containing the class (i.e. classification code) associated to each record. The value is specified as a positive integer including `0`. `0` corresponds to the first column, `1` corresponds to the second column and so on. The default value of this option is `2`.
    * `-h` or `--skip_header` -- whether to consider the first record in the source file as column headers and skip it. this option is disabled by default.
    *  `-t` or `--trainingRatio` -- specifies the ratio of gold standard records to be used for training. The value must be within inclusive range of _0.0_ to _1.0_. If not specified, the default training ratio is used; see [`set`](#set).

An execution of `load` command either reads gold standard records or unseen records. For example, the following command:

    load --from my_dataset.csv gold_standard -t 0.8 -h

loads  gold standard records from a file in the current working directory called _my_dataset.csv_ while skipping the first record, where 80% of the records will be used for training the classifier, and the remaining 20% will be used for evaluation of the classifier. Default values will be used for any of the unspecified options.

In another example, the following command:

    load --from my_other_dataset.csv unseen -ii 4 -li 5

loads unseen records from a file in the current working directory called my_other_dataset.csv_, where the ID of each record is specified in the fifth column, and the label of each record is specified in the sixth column. Default values will be used for any of the unspecified options.


### `clean`

#### `clean stop_words`

#### `clean spelling`

### `evaluate`

### `train`

### `classify`




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
    
    
        
    
    

