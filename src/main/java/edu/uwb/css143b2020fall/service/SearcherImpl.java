package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearcherImpl implements Searcher {
    public List<Integer> search(String keyPhrase, Map<String, List<List<Integer>>> index) {
        List<Integer> result = new ArrayList<>(); // [0, 2] appears in docs 0, 2

        String[] words = keyPhrase.split("\\s+"); //split keyPhrase into words by space

        List<List<Integer>> allDocs = new ArrayList<>(); //holds where each word appears in what doc
        //e.g. "hello world" turns to this: [[0,1,3,4,5], [0,2,3,4,5]]

        //assembles all the docs that each word appears in
        for (String word: words) {
            List<List<Integer>> listOfDocs = index.get(word);
            List<Integer> commonDocs = new ArrayList<>();
            for (int i = 0; i < listOfDocs.size(); i++) { //look at each doc
                List<Integer> listOfInd = listOfDocs.get(i);
                if (!listOfInd.isEmpty()) { //if present at a doc,
                    commonDocs.add(i);      //it adds that doc number for that word
                }
            }
            allDocs.add(commonDocs); //add the list of docs that the word appears in for every word
        }

        //get the common number (document id) of both lists
        List<Integer> inCommon = new ArrayList<>(); //list contains all the docs that the words have in common
        List<Integer> toCompare = allDocs.get(0); //compare this list to the rest
        for (int j = 1; j < allDocs.size(); j++) {
            List<Integer> toCheck = allDocs.get(j);
            for (int i = 0; i < toCompare.size(); i++) {
                for (int k = 0; k < toCheck.size(); k++) {
                    if (toCompare.get(i) == toCheck.get(k)) {
                        inCommon.add(toCompare.get(i));
                    }
                }
            }
        }

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


        //step 1: get the documents that contain all the words in the given phrase
        //[0,1,3,4,5], [0,2,3,4,5]
        //get the common number (document id) of both lists --> [0, 3, 4, 5]

        //step 2: for each common doc, get location index of each word in the search phrase
        //"is here already"
        //document 2: {1}, {2}, {3}  which is indexes gathered from looking at each word and the index
        //the word is at in doc 2

        //determine whether search words are in the correct order right next to each other
        //if yes doc 2 is one of the answers

        //do the location math (check onenote) nd return the document that have common index after the calculation
        return result;
    }
}