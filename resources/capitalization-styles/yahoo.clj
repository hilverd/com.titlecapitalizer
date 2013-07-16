;; yahoo.clj
;;
;; http://styleguide.yahoo.com/editing/treat-abbreviations-capitalization-and-titles-consistently/capitalization

(capitalization-style
 :version "0.0.1"
 :hy-comp-style separate

 (if ((is token "o'clock")) "o'Clock")

 ;; Parts of contractions
 (if ((is pos-tag VBP) (is token "'m")) no-capitalize)
 (if ((is pos-tag VBP) (is token "'ve")) no-capitalize)
 (if ((is pos-tag VB) (is token "'ve")) no-capitalize)
 (if ((is pos-tag VBP) (is token "'re")) no-capitalize)
 (if ((is pos-tag VBZ) (is token "'s")) no-capitalize)
 (if ((is pos-tag PRP) (is token "'s")) no-capitalize)
 (if ((is pos-tag RB) (is token "n't")) no-capitalize)
 (if ((is pos-tag MD) (is token "'d")) no-capitalize)
 (if ((is pos-tag MD) (is token "'ll")) no-capitalize)
 (if ((is pos-tag PRP) (is token "'em")) no-capitalize)
 (if ((is pos-tag PRP) (is token "'im")) no-capitalize)
 (if ((is pos-tag RP) (is token "'bout")) no-capitalize)

 ;; First and last words
 (if ((is token-position first)) capitalize)
 (if ((is token-position last)) capitalize)

 ;; Alternate or multi-part titles
 (if ((is prev-token ":")) capitalize-if-lowercase)

 ;; Numerals, cardinals
 (if ((is pos-tag CD)) capitalize-if-lowercase)

 ;; Existential "there"
 (if ((is pos-tag EX)) capitalize-if-lowercase)

 ;; Foreign words
 (if ((is pos-tag FW)) no-capitalize)

 ;; List item markers
 (if ((is pos-tag LS)) capitalize-if-lowercase)

 ;; Genitive markers
 (if ((is pos-tag POS)) no-capitalize)

 ;; Particles
 (if ((is pos-tag RP)) no-capitalize)

 ;; Symbols
 (if ((is pos-tag SYM)) no-capitalize)

 ;; "to" as preposition or infinitive marker
 (if ((is pos-tag TO)) no-capitalize)

 ;; Interjections
 (if ((is pos-tag UH)) capitalize-if-lowercase)

 ;; Nouns
 (if ((is pos-tag NN)) capitalize-if-lowercase)
 (if ((is pos-tag NNS)) capitalize-if-lowercase)
 (if ((is pos-tag NNP)) capitalize-if-lowercase)
 (if ((is pos-tag NNPS)) capitalize-if-lowercase)

 ;; Verbs
 (if ((is pos-tag VB)) capitalize-if-lowercase)
 (if ((is pos-tag VBD)) capitalize-if-lowercase)
 (if ((is pos-tag VBG)) capitalize-if-lowercase)
 (if ((is pos-tag VBN)) capitalize-if-lowercase)
 (if ((is pos-tag VBP)) capitalize-if-lowercase)
 (if ((is pos-tag VBZ)) capitalize-if-lowercase)
 (if ((is pos-tag MD)) capitalize-if-lowercase)

 ;; Adjectives
 (if ((is pos-tag JJ)) capitalize-if-lowercase)
 (if ((is pos-tag JJR)) capitalize-if-lowercase)
 (if ((is pos-tag JJS)) capitalize-if-lowercase)

 ;; Adverbs
 (if ((is pos-tag RB)) capitalize-if-lowercase)
 (if ((is pos-tag RBR)) capitalize-if-lowercase)
 (if ((is pos-tag RBS)) capitalize-if-lowercase)
 (if ((is pos-tag WDT)) capitalize-if-lowercase)

 ;; Pronouns
 (if ((is pos-tag PRP)) capitalize-if-lowercase)
 (if ((is pos-tag PRP$)) capitalize-if-lowercase)
 (if ((is pos-tag WP)) capitalize-if-lowercase)
 (if ((is pos-tag WP$)) capitalize-if-lowercase)

 ;; Prepositions of four or more letters (like "over", "from", and "with")
 (if ((is pos-tag IN) (greater-than token-length 3)) capitalize-if-lowercase)

 ;; Conjunctions of four or more letters (like "unless" and "than"), as well as
 ;; "if" and "how" and "why".
 (if ((is pos-tag CC) (greater-than token-length 3)) capitalize-if-lowercase)
 (if ((is token "if")) capitalize-if-lowercase)
 (if ((is token "how")) capitalize-if-lowercase)
 (if ((is token "why")) capitalize-if-lowercase)

 ;; Determiners of four or more letters
 (if ((is pos-tag DT) (greater-than token-length 3)) capitalize-if-lowercase)
 (if ((is pos-tag PDT) (greater-than token-length 3)) capitalize-if-lowercase)
 (if ((is pos-tag WDT) (greater-than token-length 3)) capitalize-if-lowercase))
