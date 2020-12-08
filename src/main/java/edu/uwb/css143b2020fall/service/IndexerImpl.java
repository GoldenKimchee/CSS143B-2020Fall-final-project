package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexerImpl implements Indexer {
    public Map<String, List<List<Integer>>> index(List<String> docs) {
        Map<String, List<List<Integer>>> indexes = new HashMap<>();
        for (int docNum = 0; docNum < docs.size(); docNum++) {
            String[] words = docs.get(docNum).split("\\s+"); //splits each document (a String) by white spaces
            int wordIndex = 0; //start at first word on a fresh doc
            for (String word: words) {
                if (word.equals("")) {
                    continue; //so it does not get added to the list
                }
                if (!indexes.containsKey(word)) { //if not already in the map, add that word in
                    List<List<Integer>> value = new ArrayList<>();
                    for (int i = 0; i < docs.size(); i++) { //create key for the word, while making a list for every doc
                        List<Integer> atIndex = new ArrayList<>();
                        value.add(atIndex);
                    }
                    indexes.put(word, value); //now the word has a empty key
                }
                List<List<Integer>> listOfDocs = indexes.get(word); //get the list for the word
                List<Integer> listOfInd = listOfDocs.get(docNum); //get the list that is for the doc
                listOfInd.add(wordIndex); //add in the index for the word into that doc list
                listOfDocs.remove(docNum); //remove old list
                listOfDocs.add(docNum, listOfInd); //replace with list updated with the word's index
                indexes.replace(word, listOfDocs); //replace old with new list
                wordIndex++; //moving on to do the same operations for the next word
            }
        }
        return indexes;
    }
}