package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class SearcherImpl implements Searcher {
    public List<Integer> search(String keyPhrase, Map<String, List<List<Integer>>> index) {
        //for my own notes..( e.g. keyPhrase = "hello world"
        //                         index = "hello" -> [ [], [1], [0,2] ]
        //                                 "world" -> [ [], [0], [1] ]   )

        //( e.g. should return doc 2)
        List<Integer> result = new ArrayList<>();

        //Step 0
        //( e.g. words = ["hello", "world"] )
        String[] words = keyPhrase.split("\\s+"); //split keyPhrase into words by space

        //Step 1
        //( e.g. allDocs = [ [1, 2], [1, 2] ]
        List<List<Integer>> allDocs = containingDocs(words, index); //gets the docs that contain all the words in the
        // given phrase

        //get the common number (document id) of both lists
        Set<Integer> commonDocs = commonDocs(allDocs); //list contains all the docs that the words have in common

        //for each common doc, get location index of each word in the search phrase
        List<List<List<Integer>>> allWords= new ArrayList<>(); //stores the indexes that the words appear in
        for (String word: words) {
            List<List<Integer>> docAndInd = new ArrayList<>(); //store indexes of each word
            List<List<Integer>> retrieveInd = index.get(word);
            for (Integer docNumber: inCommon) {
                List<Integer> indexAppeared = retrieveInd.get(docNumber);
                docAndInd.add(indexAppeared);
            }
            allWords.add(docAndInd);
        }

        //determine whether search words are in the correct order right next to each other
        for (int i = 0; i < allWords.get(0).size(); i++) { //get the length of the first word's list, since it should
                                                           //equal the amount of docs
            List<List<Integer>> compareDocs = new ArrayList<>();
            //to arrange a list of lists that has the words from each doc with the indexes they appear in
            for (int j = 0; j < allWords.size(); j++) {
                List<List<Integer>> docAppeared = allWords.get(j); // get word's list of docs appeared in
                List<Integer> indexesAppeared = docAppeared.get(i); // get the indexes appeared in doc
                compareDocs.add(indexesAppeared);
            }

            //subtracts from the indexes of each word so we can now compare and see if the words are next to each other
            for (int k = 0; k < compareDocs.size(); k++) {
                for (int l = 0; l < compareDocs.get(k).size(); l++) {
                    int indexToEdit = compareDocs.get(k).get(l);
                    compareDocs.get(k).remove(l);
                    int newIndex = indexToEdit - k;
                    compareDocs.get(k).add(newIndex);
                }
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
    public List<List<Integer>> containingDocs(String[] wordList, Map<String, List<List<Integer>>> mappedWords) {
        List<List<Integer>> allDocs = new ArrayList<>(); //we will store our results in here

        //assembles all the docs that each word appears in
        for (String word: wordList) { //do this for each word
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

    public Set<Integer> commonDocs(List<List<Integer>> allDocs) {
        Set<Integer> docsInCommon = new HashSet<>(); //so that I can add to here without worrying about duplicates

        for (List<Integer> list: allDocs) { //"list" is the list of docs that each word appears in
            for (Integer doc: list) { //"doc" is the document id that each word appears in
                docsInCommon.add(doc);
            }
        }
        return docsInCommon;
    }

}