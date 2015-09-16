 
# Short-Term Tasks
 
- [x] Set up record-classification testing on Windows platform.
- [x] Implement string similarity classifier.
- [ ] Implement second string similarity classifier (GK).
- [ ] Implement Naive Bayes classifier (GK).
- [x] Implement OLR classifier (GK).
- [x] Implement ensemble classifier.
- [x] Make this todo page.
- [x] Implement classifier serialisation/de-serialisation.
- [ ] Add multiple user entry points to the project.
   - [x] Train classifier on specified gold standard data and persist state to specified location
   - [x] Load classifier from persistent state, classify specified unseen data, save results from/to specified locations
- [ ] Automate Windows executable generation.
- [x] Implement stemming.
- [x] Implement removal of stop words.
- [x] Convert HISCO definitions to three column gold standard data format.
- [x] Merge duplicate classification process functionality in process package
- [x] Enable reproducible training data selection when comparing multiple classifiers.
- [ ] Optimise training of classifiers in repetitions. 
- [x] Implement String record level cleaning.
- [ ] Tests.
   - [ ] End to end test for separate training and testing.
   - [ ] Test on Jeff's data.
   - [ ] Test for variation between repetitions.
   - [ ] Test for voting classifier.
- [x] Train using multiple files e.g. HISCO dictionary.
- [ ] Spell checking cleaner, general and specific e.g. HISCO text.
- [x] Implement the ability to extend/customise the set of stop words in EnglishStopWordCleaner.
- [ ] Implement conditional stop word cleaning, i.e. a word is a stop word if appears solely.
- [ ] Improve the user-friendliness of the classification process.
- [ ] Add the confidence column in output file.
- [ ] Merge single and multiple classifier API, and parameterise the number of classifications per record.

# Queries and Comments

- [x] Why bother checking for null in constructor of StringSimilarityClassifier? We don't check parameters in most situations.
- [x] Not clear that conceptually StringSimilarityClassifier should extend ExactMatchClassifier - it's a different thing, not a particular kind of exact match. Also not sure about the similarity classifier first checking for exact match. Might be cleaner for the individual classifiers just to do one thing, and leave that issue to the ensembles.
- [x] Probably nicer to split into Cleaner and Checker interfaces, where the first changes stuff and the second just checks.

# Medium/Long-Term Tasks

- [ ] Implement ensemble using confidence measures.
- [ ] Implement ensemble using ranked lists of possible classifications.
- [ ] Implement ensemble using accuracy metrics from training - feedback per-class quality as proxy for confidence.
- [ ] Control over whether persisted models include sensitive training data.
