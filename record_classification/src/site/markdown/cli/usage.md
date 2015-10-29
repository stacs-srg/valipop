# Usage
Once `classli` is [installed](install/index.html), it is possible to start using the program via the command line. Open the command-line interface on your operating system (Terminal on OS X, Command Prompt on Windows, or Bash on Linux/Unix operating systems), type `classli` and press enter. You will see the usage message being printed explaining how to use classli. Do not panic; this document aims to explain what the options are, what they mean and how to use them. The `classli -h` commands and options instruct it to do what it is required it to do.

 The format by which the command and options are specified consists of 6 parts:

    classli [options] [command] [command-options] [sub-command] [sub-command-options]
    '--.--' '---.---' '---.---' '-------.-------' '-----.-----' '---------.---------'
       1        2         3             4               5                 6         

Part 1, `classli`, specifies the name of the program to execute, telling the command-line interface what program to start.

Parts 2 to 6 specify the command-line parameters to be passed to `classli` by the command-line interface. The `classli` command-line parameters are either _options_ or _commands_. Options are specified by their name, always starting with a `-` (hyphen). Each option typically has a short name and a long name, which can be used interchangeably. The short names are useful for quickly specifying commands without having to press lots of keystrokes. The long commands are useful for human readability of the instructions, providing more clues to what an instruction does without having to read the documentations.

Similar to options, commands are also specified by their name, except command names never start with `-`. The command names look more like a verb or a noun. Each command can have its own commands (i.e. sub-command). Further, each command and sub-command can have options. This document explains the possible options and commands of `classli`, starting with possible options for part 2.

The possible options of part 2 are:

* `-c` or `--commands` -- specifies the path to a file containing a batch of commands to be executed, where each line represents a command and its options. Please note, if this option is specified, the program enters the batch execution mode and will only execute the commands specified in the file.
* `-h` or `--help` -- displays the usage information on the console.
* `-v` or `--verbosity` -- specifies the level of information printed into the console by classli. The possible values for this option are:

    - `ALL` -- print all messages, regardless of the level of severity.
    - `SEVERE` -- only print severe error messages.
    - `WARNING` -- print warning and severe error messages.
    - `INFO` -- print informational, warnings and severe error message.
    - `OFF` -- do not print any messages.

The rest of this document explains parts 3 to 6, i.e. commands, command options, sub-commands and sub-command options offered by `classli`.

## Commands

### `init`

The `init` command initialises a new workflow that gets persisted on your hard drive. This means when `classli` program is closed and re-opened, it will remember where things were left off. The options of this command are:

* `-f` or `--force` -- enables replacement of any existing configuration folder upon initialisation. This option is not mandatory. By default this option is disabled.

To execute this command, type the following and press enter:

    classli init

The execution of the command above will result in creation of a folder called `.classli` in the current working directory. This is where all the `classli` files and settings will be stored. The execution of this command in a working directory that already contains a folder named `.classli` will result in failure; to override an existing `.classli` folder, the _force_ parameter must be set:
 
    classli init -f

Alternatively, the _force_ can be set using its long name:

    classli init --force

### `set`

The `set` command sets the value of configurable variables in the `classli` configuration. The configurable variables are specified with one of the following options:

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
The `clean` command performs cleaning of unseen and gold standard records, typically performed prior to classification. There are 3 ways to clean records:

1. cleaning using one or more predefined cleaners,
3. stop words removal using a user-defined collection of stop words, and
2. spelling correction using a user-defined dictionary.

To clean using one or more predefined cleaners the following must be set option

* `-c` or `--cleaner` -- the option specifying the predefined cleaners with which to clean all loaded unseen and gold standard records. The possible values for this option are:

    - `PUNCTUATION` -- removes punctuation characters.
    - `LOWER_CASE` -- converts the record labels to lower case.
    - `ENGLISH_STOP_WORDS` -- removes a predefined set of English stop words from record labels. The predefined set of english stop words include: "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will" and "with".
    - `PORTER_STEM` -- performs stemming using [Porter stemming algorithm](https://en.wikipedia.org/wiki/Stemming). See [PorterStemFilter](https://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/en/PorterStemFilter.html).
    - `CONSISTENT_CLASSIFICATION_CLEANER_CORRECT` -- corrects the classification of any inconsistently classified records to the most popular.
    - `CONSISTENT_CLASSIFICATION_CLEANER_REMOVE` -- removes any inconsistently classified records.
    - `TRIM_CLASSIFICATION_CODE` -- removes white-space characters fom the beginning/end of classification codes associated to each record.
    - `COMBINED` -- applies all available text cleaners and corrects inconsistent classifications.

One or more predefined cleaners can be specified in a single clean command. For example, the following command:

    clean -c LOWER_CASE ENGLISH_STOP_WORDS PUNCTUATION

converts all the loaded record labels to lower case, removes predefined list of english stop words from the labels, and finally removes punctuation characters from the labels.

To clean stop words using a custom list of stop words the `stop_words` sub command is used. The `stop_words` sub command offers the following options:

* `-s` or `--from` -- the _mandatory_ option, which specifies the path to the source file that contains the stop words, one stop word per line.
* `-c` or `--charset` -- specifies the character encoding of the source file. If unspecified, the default character encoding is used; see [`set`](#set).
* `-cs` or `--caseSensitive` -- If present, specifies that the stop words in the source file are case sensitive.

To correct spelling of record labels using a custom dictionary the `spelling` sub command is used. The spelling correction replaces the words in the labels with words in the dictionary if their similarity is above a given threshold. The `spelling` sub command offers the following options:

* `-s` or `--from` -- the _mandatory_ option, which specifies the path to the source file that contains the dictionary of words, one word/phrase per line.
* `-c` or `--charset` -- specifies the character encoding of the source file. If unspecified, the default character encoding is used; see [`set`](#set).
* `-a` or `--accuracyThreshold` -- specifies the similarity threshold, above which to replace words in record labels with words in the dictionary. The theshold value is specified as a number between inclusive range of _0.0_ to _1.0_. For instance, threshold of _0.0_ results in the replacement of all the words in the labels with words in the dictionary. A threshold of _1.0_ result in on change in the record labels, since only words that are exactly the same will be replaced. The default threshold is set to _0.5_, meaning 50% or more similarity between words will result in word replacement.
* `-d` or `--distanceFunction` -- specifies the algorithm by which to calculate the similarity between words in the labels and words in the dictionary. If this option unspecified the `JARO_WINKLER` function is used. The possible values for this option are:

    - `N_GRAMS_2` -- the [nGram](https://en.wikipedia.org/wiki/N-gram) distance function of size _2_.
    - `N_GRAMS_3` -- the [nGram](https://en.wikipedia.org/wiki/N-gram) distance function of size _3_.
    - `N_GRAMS_4` -- the [nGram](https://en.wikipedia.org/wiki/N-gram) distance function of size _4_.
    - `N_GRAMS_5` -- the [nGram](https://en.wikipedia.org/wiki/N-gram) distance function of size _5_.
    - `N_GRAMS_6` -- the [nGram](https://en.wikipedia.org/wiki/N-gram) distance function of size _6_.
    - `N_GRAMS_7` -- the [nGram](https://en.wikipedia.org/wiki/N-gram) distance function of size _7_.
    - `LEVENSTEIN` -- the [Levenstein](https://en.wikipedia.org/wiki/Levenshtein_distance) distance function.
    - `DAMERAU_LEVENSHTEIN` -- the [Damerau–Levenshtein](https://en.wikipedia.org/wiki/Damerau–Levenshtein_distance) distance function.
    - `JARO_WINKLER` -- the [Jaro-Winkler](https://en.wikipedia.org/wiki/Jaro–Winkler_distance) distance function.

### `train`
The `train` command trains the classifier with the loaded gold standard records. In order to use this command, firstly, the classifier must be set via the [`set`](#set) command, and secondly, at least one training record must be loaded, i.e. at least one gold standard record collection must be loaded with training ratio of more that _0.0_ (see [`load` command](#load)). The `train` command offers the following option:

* `-it`or `--internalTrainingRecordRatio` -- ratio of gold standard records to be used for training as opposed to internal evaluation. If unspecified, the default internal training ratio is used; see ([`set`](#set)) The value must be within inclusive range of _0.0_ to _1.0_.

For example, the following command:

    train -it 0.9

trains the classifier with 90% of the loaded training records, where the remaining 10% of the training records are used by the classifier to evaluate its own performance.

**Note:** internal evaluation is necessarry in order to calculate confidence measure for a classification; if the internal training ratio is set to _1.0_ (i.e. 100%), all of the loaded training records will be used for training the classifier. The classifier will not perform self evaluation, and therefore, is unable to calcuate confidence for any classified records. 

### `evaluate`
The `evaluate` command evaluates the performance of the classifier in terms of precision, accuracy and recall. In order to use this command, firstly, the classifier must be set via the [`set`](#set) command, and secondly, at least one evaluation record must be loaded, i.e. at least one gold standard record collection must be loaded with training ratio of less that _1.0_ (see [`load` command](#load)). It is not mandatory to train the classifier prior to evaluation. However, evaluating an untrained classifier can result in poor performance measures. Typically, it is recommended to train the classifier prior to evaluation. The `evaluate` command offers the following option:

* `-o` or `--outputEvaluationRecordsTo` -- the path in which to store the classified evaluation records. If unspecified, the classified evaluation records will not be stored.

For instance, the following command:

    evaluate -o evaluation_records_classified.csv

will evaluate the classifier with the evaluation records previously loaded (see [`load` command](#load)), and stores the classified evaluation records into a file within the current working directory called _evaluation_records_classified.csv_.  

### `classify`
The `classify` command classifies the loaded unseen records. In order to use this command, firstly, the classifier must be set via the [`set`](#set) command, and secondly, at least one unseen record must be loaded (see [`load` command](#load)). Although, it is not necessary to train the classifier prior to classification, an untrained classifier can result in high proportion if incorrect classifications. Hence, it is recommended to train the classifier prior to the execution of this command. This command offers the following option:

* `-o` or `--output` -- the _mandatory_ option that specifies the path in which to store the classified unseen records.

For example, the following command:

    classify -o classified_unseen_records.csv

classifies the loaded unseen records and stores the classified records into a file called _classified_unseen_records.csv_ within the current working directory.
