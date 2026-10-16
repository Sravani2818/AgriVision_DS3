AgriVision – Agricultural Text Corpus
=====================================

Source file:
bd4332f3-5aff-421c-8cb0-8defb5c2bb1c.jsonl

Records converted:
500

Format:
One UTF-8 TXT file per source record, plus one combined searchable corpus.

Purpose:
This corpus is prepared for the AgriVision Smart Agriculture Analytics Platform
Review-1 DSA demonstrations, especially text/string algorithms and text-based DP.

Primary DSA use:
- Naive pattern search
- KMP
- Z-function
- Rabin-Karp
- Aho-Corasick
- Trie / suffix structures
- LCP / Kasai
- Edit distance
- Needleman-Wunsch
- Smith-Waterman

Source fields detected:
id, idx, crop, state, district, sector, query_type, season, question_en, answer_en, question, answer, text, source_answer_used, target_language, script_form, chrfpp, qc_pass, back_translation, error, language

Distinct states:
23

Distinct crops:
235

Distinct query types:
19

Distinct sectors:
2

Important provenance note:
This conversion does not invent agricultural records. It reformats the uploaded
JSONL records into readable TXT documents while preserving the source values.
