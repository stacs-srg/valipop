 
# Short-Term Tasks
 
## For Zen & Kostas

- [ ] Improve the user-friendliness of the classification process.
- [ ] Spell checking cleaning.
   - [ ] General English dictionary.
   - [ ] Domain-specific dictionary: text from HISCO definition.
   - [ ] Mechanism for specifying additional dictionaries.
- [ ] Tests.
   - [ ] End to end test for separate training and testing.
   - [ ] Test for variation between repetitions.
   - [ ] Test for voting classifier.

- [x] Custom stop word list.
- [x] Implement stop word cleaning that considers data as a whole, i.e. a word is a stop word if it appears solely.
- [x] Add the confidence column in output file.

## For Richard before Edinburgh Meeting

- [ ] Occupation classification runs.
   - [ ] Cambridge data set.
   - [ ] Jeff's UK data set.
   - [ ] Decide what others from various census data sets.

## General

- [ ] Implement second string similarity classifier (GK).
- [x] Add multiple user entry points to the project.
   - [x] Train classifier on specified gold standard data and persist state to specified location
   - [x] Load classifier from persistent state, classify specified unseen data, save results from/to specified locations
- [ ] Merge single and multiple classifier API, and parameterise the number of classifications per record.
- [ ] Spell checking cleaning.
   - [ ] Domain-specific dictionaries: text from ICD10 definition.
   - [ ] Domain-specific dictionaries: text from archaic medical terms.

- [x] Set up record-classification testing on Windows platform.
- [x] Implement string similarity classifier.
- [x] Implement Naive Bayes classifier (GK).
- [x] Implement OLR classifier (GK).
- [x] Implement ensemble classifier.
- [x] Implement classifier serialisation/de-serialisation.
- [x] Implement stemming.
- [x] Implement removal of stop words.
- [x] Convert HISCO definitions to three column gold standard data format.
- [x] Merge duplicate classification process functionality in process package
- [x] Enable reproducible training data selection when comparing multiple classifiers.
- [x] Implement String record level cleaning.
- [x] Train using multiple files e.g. HISCO dictionary.
- [x] Spell checking cleaner.


# Medium/Long-Term Tasks

- [x] Implement ensemble using confidence measures.
- [ ] Implement ensemble using ranked lists of possible classifications.
- [x] Implement ensemble using accuracy metrics from training - feedback per-class quality as proxy for confidence.
- [ ] Control over whether persisted models include sensitive training data.
- [ ] Project tools.
   - [ ] Story board?
   - [ ] Issue tracking?
   - [ ] High-level log of what's been done.


# Queries and Comments

- [x] Why bother checking for null in constructor of StringSimilarityClassifier? We don't check parameters in most situations.
- [x] Not clear that conceptually StringSimilarityClassifier should extend ExactMatchClassifier - it's a different thing, not a particular kind of exact match. Also not sure about the similarity classifier first checking for exact match. Might be cleaner for the individual classifiers just to do one thing, and leave that issue to the ensembles.
- [x] Probably nicer to split into Cleaner and Checker interfaces, where the first changes stuff and the second just checks.
