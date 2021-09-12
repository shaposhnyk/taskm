package com.sh.finder;

import java.util.*;

/**
 * O(N^2) - time, O(V+E) - memory
 */
public class TransitionFinderV1 implements TransitionFinder {
  final Set<String> visited = new HashSet<>(); // not clean, but it was as such
  String longest = "";

  @Override
  public String longestTransition(List<String> words) {
    return new TransitionFinderV1().longestTransitionInt(words);
  }

  private String longestTransitionInt(List<String> words) {
    final Map<String, List<String>> graph = buildGraph(words); // O(n2)
    for (String w : words) {
      if (w.length() == 1) {
        dfs(graph, w); // O(v)
      }
    }
    return longest;
  }

  private Map<String, List<String>> buildGraph(List<String> words) {
    final Map<String, List<String>> map = new HashMap<>();
    for (String w1 : words) {
      for (String w2 : words) {
        if (w1.length() + 1 == w2.length() && canTransition(w1, w2)) {
          map.computeIfAbsent(w1, key -> new ArrayList<>())
              .add(w2);
        }
      }
    }
    return map;
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

  private void dfs(Map<String, List<String>> graph, String w) {
    if (visited.contains(w)) {
      return;
    } else if (w.length() > longest.length()) {
      longest = w;
    }
    visited.add(w);
    for (String n : graph.getOrDefault(w, Collections.emptyList())) {
      dfs(graph, n);
    }
  }
}
