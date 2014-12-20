package com.zunama;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个状态
 */
public class DawgState
{
    /**
     * 一个简单的ID发生器
     */
    private static int nextId = 0;

    /**
     * 状态ID
     */
    private int id;
    /**
     * 转移函数
     */
    private LinkedHashMap<Character, DawgState> edges = new LinkedHashMap<Character, DawgState>();
    /**
     * 是否是结束状态
     */
    private boolean endWord = false;

    /**
     * 构造一个状态
     */
    public DawgState()
    {
        id = nextId++;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(endWord);

        for (Map.Entry<Character, DawgState> entry : edges.entrySet())
        {
            sb.append(entry.getKey());
            sb.append(entry.getValue().getId());
        }

        return sb.toString();
    }

    /**
     * 获取ID
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * 获取转移函数
     * @return
     */
    public LinkedHashMap<Character, DawgState> getEdges()
    {
        return edges;
    }

    /**
     * 是否是终止状态
     * @return
     */
    public boolean isEndWord()
    {
        return endWord;
    }

    /**
     * 设为终止状态
     * @param endWord
     */
    public void setEndWord(boolean endWord)
    {
        this.endWord = endWord;
    }
}