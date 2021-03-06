;; wikibooks.clj
;;
;; https://en.wikibooks.org/wiki/Basic_Book_Design/Capitalizing_Words_in_Titles

(capitalization-style
 :version "0.0.1"
 :hy-comp-style all

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

 ;; The first and last words are always capitalized, even if fewer than five
 ;; letters.
 (if ((is token-position first)) capitalize)
 (if ((is token-position last)) capitalize)

 ;; Alternate or multi-part titles
 (if ((is prev-token ":")) capitalize-if-lowercase)

 ;; Words over five letters are always capitalized
 (if ((greater-than token-length 5)) capitalize-if-lowercase)

 ;; Verbs are always capitalized, even if fewer than five letters
 (if ((is pos-tag VB)) capitalize-if-lowercase)
 (if ((is pos-tag VBD)) capitalize-if-lowercase)
 (if ((is pos-tag VBG)) capitalize-if-lowercase)
 (if ((is pos-tag VBN)) capitalize-if-lowercase)
 (if ((is pos-tag VBP)) capitalize-if-lowercase)
 (if ((is pos-tag VBZ)) capitalize-if-lowercase)
 (if ((is pos-tag MD)) capitalize-if-lowercase)

 ;; Nouns are always capitalized, even if fewer than five letters
 (if ((is pos-tag NN)) capitalize-if-lowercase)
 (if ((is pos-tag NNS)) capitalize-if-lowercase)
 (if ((is pos-tag NNP)) capitalize-if-lowercase)
 (if ((is pos-tag NNPS)) capitalize-if-lowercase)

 ;; These words are capitalized, even though they are fewer than five
 ;; letters: also, be, if, than, that, thus, when.
 (if ((is token "also")) capitalize-if-lowercase)
 (if ((is token "be")) capitalize-if-lowercase)
 (if ((is token "if")) capitalize-if-lowercase)
 (if ((is token "than")) capitalize-if-lowercase)
 (if ((is token "that")) capitalize-if-lowercase)
 (if ((is token "thus")) capitalize-if-lowercase)
 (if ((is token "when")) capitalize-if-lowercase))
