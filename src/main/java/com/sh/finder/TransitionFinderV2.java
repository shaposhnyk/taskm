package com.sh.finder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * O(N^2) - time, O(V+E) - memory
 */
public class TransitionFinderV2 implements TransitionFinder {
  final Set<String> visited = new HashSet<>(); // not clean, but it was as such
  String longest = "";

  @Override
  public String longestTransition(List<String> words) {
    return new TransitionFinderV2().longestTransitionInt(words);
  }

  private String longestTransitionInt(List<String> words) {
    for (String w : words) {
      if (w.length() == 1) {
        dfs(words, w); // O(n^2) - worst case
      }
    }
    return longest;
  }

  private boolean canTransition(String w1, String w2) {
    for (int i = 0; i < w2.length(); i++) {
      String w = w2.substring(0, i) + w2.substring(i + 1);
      if (w.equals(w1)) {
        return true;
      }
    }
    return false;
  }

  private void dfs(List<String> words, String w) {
    if (visited.contains(w)) {
      return;
    } else if (w.length() > longest.length()) {
      longest = w;
    }
    visited.add(w);
    for (String n : words) {
      if (w.length() + 1 == n.length() && canTransition(w, n)) {
        dfs(words, n);
      }
    }
  }
}
