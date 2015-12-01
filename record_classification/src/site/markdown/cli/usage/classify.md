# `classify` Command
The `classify` command classifies the loaded unseen records. In order to use this command, firstly, the classifier must be set via the [`set`](#set) command, and secondly, at least one unseen record must be loaded (see [`load` command](#load)). Although, it is not necessary to train the classifier prior to classification, an untrained classifier can result in high proportion if incorrect classifications. Hence, it is recommended to train the classifier prior to the execution of this command. This command offers the following option:

* `-o` or `--output` -- the _mandatory_ option that specifies the path in which to store the classified unseen records.

For example, the following command:

    classify -o classified_unseen_records.csv

classifies the loaded unseen records and stores the classified records into a file called _classified_unseen_records.csv_ within the current working directory.
