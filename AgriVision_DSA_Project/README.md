# AgriVision – Smart Agriculture Analytics Platform
## DSA Review-1 Java Project

### Dataset
The project uses the prepared UTF-8 agricultural text corpus supplied with this project:

`data/AgriVision_Corpus/all_agriculture_text.txt`

There are 500 converted agricultural advisory records in `data/AgriVision_Corpus/records/`.

### Run
From the project root:

```text
Windows:
run.bat

Manual:
javac -d out src/agrivision/*.java
java -cp out agrivision.Main
```

Java 17+ is recommended.

### Menu
1. Agricultural Data
2. CO1 – Problem Understanding & Data Management
3. CO2 – String Algorithms
4. CO2 – Suffix Structures
5. CO3 – Advanced Dynamic Programming
6. Complexity & Viva Guide
7. Exit

### CO1
Demonstrates the problem, input corpus, metadata organization, data retrieval and DSA mapping.

### CO2
- Naive pattern matching
- KMP
- Z algorithm
- Rabin-Karp
- Aho-Corasick
- Trie with exact word positions
- Suffix Array (O(n log^2 n) in this comparison-sort implementation)
- LCP / Kasai

All pattern-search options report exact character positions.

### CO3
- Levenshtein / Wagner-Fischer
- Damerau-Levenshtein
- Needleman-Wunsch
- Smith-Waterman
- Bitmask DP
- Matrix Chain Multiplication

### Important viva point
The comments inside every algorithm explain the exact logic implemented. The application
is menu-driven and loads the `.txt` corpus at startup rather than hard-coding the dataset.
