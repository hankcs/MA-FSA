package com.zunama;

import java.util.*;

/**
 * 有向无环词图
 */
public class Dawg
{

    /**
     * 根节点
     */
    private DawgState root;
    /**
     * 前一个词
     */
    private String previousWord = "";
    /**
     * 论文里类似“登记簿”的概念
     */
    private Map<String, DawgState> register = new HashMap<String, DawgState>();
    /**
     * 全部边数
     */
    private int totalEdges = 0;

    /**
     * 当前前缀
     */
    private String currentPrefix;

    /**
     * 构造
     * @param words
     */
    public Dawg(List<String> words)
    {
        root = new DawgState();
        root.setEndWord(false);
        insertWords(words);
        register = null;
    }

    /**
     * 是否包含单词word
     * @param word
     * @return
     */
    public boolean search(String word)
    {
        DawgState current = root;

        for (Character c : word.toCharArray())
        {
            if (current.getEdges().containsKey(c))
                current = current.getEdges().get(c);
            else
                return false;
        }

        return current.isEndWord();
    }

    /**
     * 前缀查询
     * @param prefix
     * @return
     */
    public List<String> prefixSearch(String prefix)
    {

        if (prefix == null)
            throw new RuntimeException("Prefix is set to null");

        DawgState current = root;
        currentPrefix = prefix.toLowerCase();
        List<String> words = new ArrayList<String>();

        for (char letter : prefix.toLowerCase().toCharArray())
        {
            if (current.getEdges().containsKey(letter))
                current = current.getEdges().get(letter);
            else
                return words;
        }

        prefixSearch(current, words, "");
        return words;
    }

    /**
     * 前缀是否存在
     * @param prefix
     * @return
     */
    public boolean prefixExist(String prefix)
    {

        if (prefix == null)
            throw new RuntimeException("Prefix is set to null");

        DawgState current = root;

        for (char c : prefix.toCharArray())
        {
            if (current.getEdges().containsKey(c))
                current = current.getEdges().get(c);
            else
                return false;
        }

        return true;
    }

    public int getTotalEdges()
    {
        return totalEdges;
    }

    /**
     * 前缀查询
     * @param state
     * @param words
     * @param currentString
     */
    private void prefixSearch(DawgState state, List<String> words, String currentString)
    {
        if (state.isEndWord())
        {
            words.add(currentPrefix + currentString);
        }

        for (Character key : state.getEdges().keySet())
        {
            DawgState nextStateToVist = state.getEdges().get(key);
            String newString = currentString + key;

            prefixSearch(nextStateToVist, words, newString);
        }
    }

    /**
     * 开始构造
     * @param words
     */
    private void insertWords(List<String> words)
    {

        Collections.sort(words);
        for (String word : words)
        {
            insertWord(word);
        }
    }

    /**
     * 插入单词，按照字典序
     * @param word
     */
    private void insertWord(String word)
    {
        if (word.compareTo(previousWord) < 0)
            throw new RuntimeException("Trying to insert a word out of order.");

        word = word.toLowerCase();

        String commonPrefix = getCommonPrefix(word, previousWord);
        String currentSuffix = word.substring(commonPrefix.length());

        DawgState lastState = getLastState(commonPrefix);

        if (lastState.getEdges().size() > 0)    // 有分岔
        {
            replaceOrRegister(lastState);
        }

        addSuffix(lastState, currentSuffix);

        previousWord = word;
    }

    /**
     * 将currentSuffix代表的边加入自动机
     * @param lastState
     * @param currentSuffix
     */
    private void addSuffix(DawgState lastState, String currentSuffix)
    {
        char[] wordCharArray = currentSuffix.toCharArray();

        for (char c : wordCharArray)
        {
            DawgState nextState = new DawgState();
            lastState.getEdges().put(c, nextState);
            totalEdges++;
            lastState = nextState;
        }

        lastState.setEndWord(true);
    }

    /**
     * 论文中的replaceOrRegister，替换或登记
     * @param state
     */
    private void replaceOrRegister(DawgState state)
    {
        Character c = getMostRecentAddedLetter(state);
        DawgState child = state.getEdges().get(c);

        if (child.getEdges().size() > 0)
        {
            replaceOrRegister(child);
        }

        if (register.containsKey(child.toString()))
        {
            state.getEdges().put(c, register.get(child.toString()));    // 替换
            totalEdges--;
        }
        else
        {
            register.put(child.toString(), child);                      // 登记
        }
    }

    /**
     * 获取state出发的最后一条边，也就是论文中的getLastChild
     * @param state
     * @return
     */
    private Character getMostRecentAddedLetter(DawgState state)
    {
        Character out = null;
        for (Character key : state.getEdges().keySet())
        {
            out = key;
        }
        return out;
    }

    /**
     * 获取论文中的last state，即从根节点开始，顺序接受commonPrefix的最后状态
     * @param commonPrefix
     * @return
     */
    private DawgState getLastState(String commonPrefix)
    {
        if (commonPrefix == null || commonPrefix.length() == 0)
            return root;

        DawgState current = root;

        for (char c : commonPrefix.toCharArray())
        {
            current = current.getEdges().get(new Character(c));
        }

        return current;
    }

    /**
     * 获取两个词语的公共前缀
     * @param word
     * @param previousWord
     * @return
     */
    private static String getCommonPrefix(String word, String previousWord)
    {
        int count = 0;
        int minCheck = Math.min(word.length(), previousWord.length());

        for (int i = 0; i < minCheck; i++)
        {
            if (word.charAt(i) == previousWord.charAt(i))
                count++;
            else
                break;
        }

        return word.substring(0, count);
    }
}