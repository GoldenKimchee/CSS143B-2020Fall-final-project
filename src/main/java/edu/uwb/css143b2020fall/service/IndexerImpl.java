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
        int docNum = 0;
        for (String doc: docs) {
            String[] words = doc.split("\\s+"); //splits each document (a String) by white spaces
            int wordIndex = 0;
            for (String word: words) {
                if (!indexes.containsKey(word)) {
                    List<List<Integer>> value = new ArrayList<>();
                    for (int i = 0; i < docs.size(); i++) {
                        List<Integer> atIndex = new ArrayList<>();
                        value.add(atIndex);
                    }
                    indexes.put(word, value);
                }
                List<List<Integer>> listOfDocs = indexes.get(word);
                List<Integer> listOfInd = listOfDocs.get(docNum);
                listOfInd.add(wordIndex);
                listOfDocs.remove(docNum);
                listOfDocs.add(docNum, listOfInd);
                indexes.replace(word, listOfDocs);
                wordIndex++;
            }
            docNum++;
        }
        return indexes;
    }
}