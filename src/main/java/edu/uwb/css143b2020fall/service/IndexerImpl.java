package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexerImpl implements Indexer { //doc num 2 index 2
    public Map<String, List<List<Integer>>> index(List<String> docs) {
        Map<String, List<List<Integer>>> indexes = new HashMap<>();
        int docNum = 0;
        for (String doc: docs) {
            String[] words = doc.split("\\s+"); //splits each document (a String) by white spaces
            int wordIndex = 0;
            for (String word: words) {
                if (!indexes.containsKey(word)) {
                    List<List<Integer>> value = new ArrayList<>();
                    for (int i = 0; i <= docNum; i++) {
                        List<Integer> atIndex = new ArrayList<>();
                        value.add(atIndex);
                    }
                    indexes.put(word, value);
                }
                List<List<Integer>> listOfDocs = indexes.get(word);
                if (listOfDocs.size() <= docNum) { //add another array for the next doc, otherwise out of bounds error
                    for (int j = listOfDocs.size(); j <= docNum; j++) {
                        List<Integer> atIndex = new ArrayList<>();
                        listOfDocs.add(atIndex);
                    }
                    List<Integer> add = new ArrayList<>();
                    listOfDocs.add(docNum, add);
                }
                List<Integer> listOfInd = listOfDocs.get(docNum); //index 1 out of bounds for length 1
                listOfInd.add(wordIndex);
                listOfDocs.remove(docNum); //maybe remove. address is shared
                listOfDocs.add(docNum, listOfInd);
                indexes.replace(word, listOfDocs); //this is a problem,
                wordIndex++;
            }
            docNum++;
        }
        return indexes;
    }
}