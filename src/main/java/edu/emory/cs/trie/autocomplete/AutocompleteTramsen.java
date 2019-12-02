package edu.emory.cs.trie.autocomplete;

import edu.emory.cs.trie.TrieNode;

import java.util.*;

public class AutocompleteTramsen extends Autocomplete<List<String>>{

    public AutocompleteTramsen(String dict_file, int max) {
        super(dict_file, max);
    }

    public AutocompleteTramsen(String dict_file) {
        this(dict_file, 5);
    }

    @Override
    public List<String> getCandidates(String prefix) { // never return null

        prefix = prefix.trim();

        Queue<TrieNode> queue = new ArrayDeque<>();
        TrieNode node = find(prefix);
        List<String> result;
        if(node==null){ // if prefix does not exist in trie
            result = new ArrayList<>();
            put(prefix,result);
            node = find(prefix);
            node.setEndState(false);
        }
        else if(node.getValue()!=null) {  // prefix has been searched before
            result = (List<String>)node.getValue();
        }
        else{ // prefix has never been searched before
            result = new ArrayList<String>();
        }
        Map children = node.getChildrenMap();

        for (Object key: children.keySet()) {
            queue.add(node.getChild((char)key));
        }

        while(!queue.isEmpty() && result.size()<getMax()){ // finds max number of items for return array
            node = queue.poll();
            if(node.isEndState()) {
                String word = getForm(node);
                if(result.contains(word)) continue;
                result.add(getForm(node));
            }
            children = node.getChildrenMap();

            for (Object key: children.keySet()) {
                queue.add(node.getChild((char)key));
            }
        }
        node.setValue(result); // update value so operation only occurs once
        return result;
    }

    private String getForm(TrieNode node){
        String result = "";
        if(node==null){
            return result;
        }
        while(node.getParent()!=null){
            result = ""+node.getKey()+result;
            node = node.getParent();
        }
        return result;

    }

    @Override
    // if prefix is word, first candidate should be that word
    public void pickCandidate(String prefix, String candidate) {

        prefix = prefix.trim();

        TrieNode node = find(prefix);
        if(node==null){ // prefix does not exist in trie
            List<String> newList = new ArrayList<>();
            newList.add(0,candidate);
            put(prefix, newList);
            node = find(prefix);
            node.setEndState(false);
        }
        else if(node.getValue()==null){ // prefix has never been searched before
            List<String> newList = new ArrayList<>();
            newList.add(0,candidate);
            node.setValue(newList);
        }
        else{ // prefix has been searched before, update value
            List<String> list = (ArrayList<String>)node.getValue();
            for(int i=0; i<list.size();i++){
                if(list.get(i).equals(candidate)){
                    list.remove(i);
                    break;
                }
            }
            list.add(0,candidate);
            if(list.size()>getMax()){
                list.remove(list.size()-1);
            }
        }
    }
}
