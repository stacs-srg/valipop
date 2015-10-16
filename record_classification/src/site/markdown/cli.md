This document specifies the Record Classification Command Line Interface (CLI) operations.

# Configuration
The configuration file is specified in JSON, and located at `<working_directory>/.classi/config.json`.
The configuration file contains the following:

    {
        "classifier": "<classifier_name>",
        "charset": "<charset>",
        "delimiter": "<delimiter>",
        "seed": <numerical_random_seed>,
        "serialization_format": "<serialization_format_name>",
        "proceed_on_error": "<boolean_proceed_on_batch_command_execution_error>",
        "error_log": "<path_to_error_log_file>",
        "info_log": "<path_to_info_log_file>",
        
        "gold_standard": [
            {
                "file": "<path_to_data_file>",
                "training_ratio": "<training_ratio>",
                "label_column": <label_column_index>,
                "code_column": <code_column>,
                "charset": "<charset_optional>",
                "delimiter": "<delimiter_optional>",
                "name": "<naming_optional>",
                "skip_first_row": "boolean_optional"
            }
        ],
        
        "unseen": [
            {
                "file": "<path_to_data_file>",
                "label_column": <label_column_index>,
                "charset": "<charset_optional>",
                "delimiter": "<delimiter_optional>",
                "name": "<naming_optional>",
                "skip_first_row": "boolean_optional"
            }
        ]
    }

# Commands

## Init

Initialises a record classification process.

- **Parameters**
    - *name* -- the name associated to the initialised record classification.

Example usage:

    classi init name
    > mkdir -p name/.classi
    cd name/

## Set

Sets a parameter of

- **Parameters**
    - *key* -- the to the variable to be set; enumeration of fixed terms.
    - *value* -- the value of the variable to be set.

Example usage:

    classi set classifier X
    > touch .classi/config.json
    > put .classi/config.json -> classifier: "X"
    > serialize .classi/classifier
    
    classi set default charset X
    > put .classi/config.json -> default.charset: "X"
    
    classi set default delimiter X
    > put .classi/config.json -> default.delimiter: "X"

## Load

Loads data into the classification process.

### Load Gold Standard

Loads the gold standard records.

- **Parameters**
    - *path_to_data* -- the path to the file containing the gold standard data specified in a tabular format.
    - *training_ratio* -- the ratio of the gold standard data to be used for training.

Example usage:

    classi load gold_standard X.csv t_ratio 0.8
    > put .classi/config.json -> gold_standard[{ file: ".classi/gold_standard/X.csv", training_ration: "0.8" }]
    
    classi load gold_standard Y.csv t_ratio 1.8
    > put .classi/config.json -> gold_standard[{ file: ".classi/gold_standard/Y.csv", training_ration: "1.0" }]

### Load Unseen

Example usage:

    classi load unseen X.csv
    > put .classi/config.json -> unseen[".classi/unseen/X.csv"]

## Clean

### Clean Gold Standard
- classi clean gold_standard cleaner X
    - .classi/gold_standard/X.csv -> .classi/gold_standard/X_cleaned.csv -> .classi/gold_standard/X.csv
    - .classi/gold_standard/Y.csv -> .classi/gold_standard/Y_cleaned.csv -> .classi/gold_standard/Y.csv

### Clean Unseen
- classi clean unseen cleaner X
    - .classi/unseen/X.csv -> .classi/unseen/X_cleaned.csv -> .classi/unseen/X.csv

## Train
- classi train int_t_ratio 0.8
    .classi/classifier -> trainAndEvalaute

## Evaluate
- classi evaluate
    - evaluation_HH_mm_dd_MM_YYY/confusion_matrix.csv, metrics.csv, evaluation_records.csv,meta.json, evaluation.log

## Classify
- classi classify
    - classification_HH_mm_dd_MM_YYY/classified_unseen.csv, details.csv, confidences.csv, meta.json, classification.log

# System Behaviour
- **Before Command Execution (Except init)**
    - load context
- **Before Exit**
    - persist context
