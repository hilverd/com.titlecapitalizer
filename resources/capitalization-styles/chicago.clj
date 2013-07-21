;; chicago.clj
;;
;; http://www.chicagomanualofstyle.org/

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

 (if ((is token-position first)) capitalize-if-lowercase)
 (if ((is token-position last)) capitalize-if-lowercase)

 (if ((is prev-token ":")) capitalize-if-lowercase)

 (if ((is pos-tag IN) (less-than token-length 5)) no-capitalize)
 (if ((is pos-tag IN)) capitalize-if-lowercase)
 (if ((is pos-tag CC)) no-capitalize)
 (if ((is pos-tag MD)) capitalize-if-lowercase)
 (if ((is pos-tag WP)) capitalize-if-lowercase)
 (if ((is pos-tag WP$)) capitalize-if-lowercase)
 (if ((is pos-tag PRP)) capitalize-if-lowercase)
 (if ((is pos-tag PRP$)) capitalize-if-lowercase)
 (if ((is pos-tag WRB)) capitalize-if-lowercase)
 (if ((is pos-tag NN)) capitalize-if-lowercase)
 (if ((is pos-tag NNS)) capitalize-if-lowercase)
 (if ((is pos-tag NNP)) capitalize-if-lowercase)
 (if ((is pos-tag NNPS)) capitalize-if-lowercase)
 (if ((is pos-tag JJ)) capitalize-if-lowercase)
 (if ((is pos-tag JJR)) capitalize-if-lowercase)
 (if ((is pos-tag JJS)) capitalize-if-lowercase)
 (if ((is pos-tag VB)) capitalize-if-lowercase)
 (if ((is pos-tag VBD)) capitalize-if-lowercase)
 (if ((is pos-tag VBG)) capitalize-if-lowercase)
 (if ((is pos-tag VBN)) capitalize-if-lowercase)
 (if ((is pos-tag VBP)) capitalize-if-lowercase)
 (if ((is pos-tag VBZ)) capitalize-if-lowercase)
 (if ((is pos-tag RB)) capitalize-if-lowercase)
 (if ((is pos-tag RBR)) capitalize-if-lowercase)
 (if ((is pos-tag RBS)) capitalize-if-lowercase))
