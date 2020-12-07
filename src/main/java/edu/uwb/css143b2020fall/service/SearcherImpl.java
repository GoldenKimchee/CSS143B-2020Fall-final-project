package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class SearcherImpl implements Searcher {
    public List<Integer> search(String keyPhrase, Map<String, List<List<Integer>>> index) {
        //for my own notes..( e.g. keyPhrase = "hello world"
        //                         index = "hello" -> [ [], [1], [0,2] ]
        //                                 "world" -> [ [1], [0], [1] ]   )

        //( e.g. we will store the target result of [2] for doc 2)
        List<Integer> result = new ArrayList<>();

        //Step 0
        //Break the keyPhrase into parts
        //( e.g. words = ["hello", "world"] )
        keyPhrase = keyPhrase.trim();
        String[] words = keyPhrase.split("\\s+"); //split keyPhrase into words by space
        if (words[0].equals("")) { //if there was nothing searched up
            return result;
        }

        //Step 1
        //gets the docs that contain all the words in the given phrase
        //( e.g. allDocs = [ [1, 2], [0, 1, 2] ]
        List<List<Integer>> allDocs = containingDocs(words, index);
        if (allDocs == null) {
            return result;
        }

        //( e.g. [1, 2] )
        //get the common number (document id) of both lists
        Set<Integer> preInCommonDocs = commonDocs(allDocs); //list contains all the docs that the words have in common
        Integer[] inCommonDocs = new Integer[preInCommonDocs.size()];
        preInCommonDocs.toArray(inCommonDocs);

        //Step 2
        //( e.g. [ [ [1], [0, 2] ], [ [0], [1] ] ]  for "hello" and "world" respectively
        //for each common doc, get location index of each word in the search phrase
        List<List<List<Integer>>> allWords = organizeIntoInd(words, index, inCommonDocs); //stores the indexes that the
                                                                                          // words appear in

        //Step 3
        //Uses "findIndexesForDoc" function to compile the words from each doc and put their indexes for that particular
        //doc into a List<List<Integer>>
        //Using the result of previously mentioned function, pass into "foundInDoc" function that is performed for
        // every common document, that function performs some location maths. if returns true, it will check which
        // document that is corresponds to and add that to "result"
        //( e.g. will return "result" of [2] )
        for (int numOfDoc = 0; numOfDoc < inCommonDocs.length; numOfDoc++) {
            List<List<Integer>> indexesForDoc = findIndexesForDoc(numOfDoc, allWords);
            calculateInd(indexesForDoc);
            if (findResult(indexesForDoc)) {
                result.add(inCommonDocs[numOfDoc]);
            }
        }
        return result;
    }


    //e.g. "hello world" turns to this: [[0,1,3,4,5], [0,2,3,4,5]]
    //holds where each word appears in what doc
    //for each of the words in wordList, we will check in the mappedWords map for each word, and see what docs they
    //appeared in, and put those doc numbers into the list of lists in order.
    //Each List<Integer> that is in the List<List<Integer>> in allDocs will be for each word (in order) and will hold
    //the number of the docs that the word appears in
    private List<List<Integer>> containingDocs(String[] wordList, Map<String, List<List<Integer>>> mappedWords) {
        List<List<Integer>> allDocs = new ArrayList<>(); //we will store our results in here

        //assembles all the docs that each word appears in
        for (String word: wordList) { //do this for each word
            if (!mappedWords.containsKey(word)) { //in the case that there is a word in the keyPhrase that
                return null;                      //is not in our system
            }
            List<List<Integer>> listOfDocs = mappedWords.get(word); //get the docs that the word does/does not appear in
            List<Integer> commonDocs = new ArrayList<>(); //new list to store the doc numbers that the word appears in
            for (int i = 0; i < listOfDocs.size(); i++) { //look at each doc
                List<Integer> listOfInd = listOfDocs.get(i);
                if (!listOfInd.isEmpty()) { //if present at the doc,
                    commonDocs.add(i);      //it adds that doc number for that word
                }
            }
            allDocs.add(commonDocs); //add the list of docs that the word appears in for every word
        }
        return allDocs;
    }

    private Set<Integer> commonDocs(List<List<Integer>> allDocs) {
        Set<Integer> docsInCommon = new HashSet<>(); //so that I can add to here without worrying about duplicates

        for (List<Integer> list: allDocs) { //"list" is the list of docs that each word appears in
            for (Integer doc: list) { //"doc" is the document id that each word appears in
                docsInCommon.add(doc);
            }
        }
        return docsInCommon;
    }

    private List<List<List<Integer>>> organizeIntoInd (String[] words, Map<String, List<List<Integer>>> mappedWords,
                                                      Integer[] inCommon) {
        List<List<List<Integer>>> allWords = new ArrayList<>(); //to return

        for (String word: words) {
            List<List<Integer>> docAndInd = new ArrayList<>(); //store indexes of each word
            List<List<Integer>> retrieveInd = mappedWords.get(word); //get the list where it has all the docs with ind
            for (Integer docNumber: inCommon) {
                List<Integer> indexAppeared = retrieveInd.get(docNumber); //get the indexes from the docs that all
                                                                          //words appear in
                docAndInd.add(indexAppeared);                             //for this doc's indexes, add to the list
            }
            allWords.add(docAndInd); //add to the overall list
        }

        //this list has a List<List<List<Integer>>> which holds all the results together,
        //the List<List<Integer>> represents each word
        //List<Integer> is for each doc
        //Integer is the index the word appears at
        return allWords;
    }

    //determine whether search words are in the correct order right next to each other
    private List<List<Integer>> findIndexesForDoc(int numOfDoc, List<List<List<Integer>>> allWords) {
        List<List<Integer>> compiledIndexes = new ArrayList<>();

        for (int keyPhraseLength = 0; keyPhraseLength < allWords.size(); keyPhraseLength++) {
            List<List<Integer>> wordDocs = allWords.get(keyPhraseLength);
            List<Integer> indexesToCheck = wordDocs.get(numOfDoc);
            compiledIndexes.add(indexesToCheck);
        }

        return compiledIndexes;
    }

    //Retrieve results by doing some location math to see if the indexes for the words are in order like
    //the keyphrase
    private void calculateInd(List<List<Integer>> indexesForDoc) {
        for (int eachWord = 0; eachWord < indexesForDoc.size(); eachWord++) {
            List<Integer> forWord = indexesForDoc.get(eachWord);
            List<Integer> calculatedInd = new ArrayList<>();
            for (int eachIndex = 0; eachIndex < forWord.size(); eachIndex++) {
                Integer index = forWord.get(eachIndex);
                Integer newInd = index - eachWord;
                calculatedInd.add(newInd);
            }
            indexesForDoc.remove(eachWord);
            indexesForDoc.add(eachWord, calculatedInd);
        }
    }

    //Checks the list of indexes if the words are next to each other (in same order as keyphrase)
    private boolean findResult(List<List<Integer>> indexesForDoc) {
        //if the value for the key is equal to the length of indexesForDoc then that means that same number appeared
        //for all of the words, means that doc is the answer
        Map<Integer, Integer> checkOrder= new TreeMap<>();
        for (int eachWord = 0; eachWord < indexesForDoc.size(); eachWord++) {
            List<Integer> aWord = indexesForDoc.get(eachWord);
            for (Integer index: aWord) {
                if (checkOrder.containsKey(index)) {
                    Integer indKey = checkOrder.get(index);
                    indKey++;
                    checkOrder.put(index, indKey);
                } else {
                    //if the key hasn't been put in the map yet
                    checkOrder.put(index, 1);
                }
            }
        }
        Integer max = checkOrder.values().stream().max(Integer::compare).get();
        if (max == indexesForDoc.size()) {
            return true;
        }
        return false;
    }

}