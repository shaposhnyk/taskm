package com.sh.finder;

import java.util.*;

/**
 * O(n) - time, O(V+E) - memory
 */
public class TransitionFinderV3 implements TransitionFinder {
  final Set<String> visited = new HashSet<>(); // not clean, but it was as such
  String longest = "";

  @Override
  public String longestTransition(List<String> words) {
    return new TransitionFinderV3().longestTransitionInt(words);
  }

  private String longestTransitionInt(List<String> words) {
    final TrieNode root = buildGraph(words); // O(n * w.length)
    for (String w : words) { // O(n)
      if (w.length() == 1) {
        dfs(root, w); // O(w.length)
      }
    }
    return longest;
  }

  private TrieNode buildGraph(List<String> words) {
    final TrieNode root = new TrieNode();
    for (String w : words) {
      root.add(w, 0);
    }
    return root;
  }

  private void dfs(TrieNode root, String w) {
    if (visited.contains(w)) {
      return;
    } else if (w.length() > longest.length()) {
      longest = w;
    }
    visited.add(w);

    // from 0 to L inclusive, because can insert new character anywhere: before, in-between or after
    for (int insertIdx = 0; insertIdx <= w.length(); insertIdx++) {
      for (String n : root.similarWords(w, insertIdx)) {
        dfs(root, n);
      }
    }
  }

  static class TrieNode {
    final Map<Character, TrieNode> nodes = new HashMap<>();
    String leafWord;

    void add(String w, int i) {
      if (i == w.length()) {
        leafWord = w;
      } else {
        nodes.computeIfAbsent(w.charAt(i), key -> new TrieNode())
            .add(w, i + 1);
      }
    }

    public List<String> similarWords(String w, int insertIdx) {
      final List<String> similarWords = new ArrayList<>(); // working memory - current list of similar words
      similarWords(similarWords, w, 0, insertIdx);
      return similarWords;
    }

    void similarWords(List<String> res, String w, int currIdx, int insertIdx) {
      if (currIdx == insertIdx) {
        for (Map.Entry<Character, TrieNode> e : nodes.entrySet()) { // all possible transitions
          e.getValue().similarWords(res, w, currIdx, -1);
        }
      } else if (currIdx == w.length()) {
        if (leafWord != null) {
          res.add(leafWord);
        }
      } else {
        final TrieNode node = nodes.get(w.charAt(currIdx)); // curr char transition
        if (node != null) {
          node.similarWords(res, w, currIdx + 1, insertIdx);
        }
      }
    }
  }
}
