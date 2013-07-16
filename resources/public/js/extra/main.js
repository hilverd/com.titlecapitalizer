var clip = new ZeroClipboard(document.getElementById('copyTitle'), {
  moviePath: '/swf/ZeroClipboard.swf'
})

var posTagDataSource = [
  "CC (conjunction, coordinating)",
  "PDT (pre-determiner)",
  "JJ (adjective or numeral, ordinal)",
  "NN (noun, common, singular or mass)",
  "CD (numeral, cardinal)",
  "RBR (adverb, comparative)",
  "VBN (verb, past participle)",
  "RBS (adverb, superlative)",
  "NNPS (noun, proper, plural)",
  "VBP (verb, present tense, not 3rd person singular)",
  "IN (preposition or conjunction, subordinating)",
  "WRB (WH-adverb)",
  "LS (list item marker)",
  "SYM (symbol)",
  "WDT (WH-determiner)",
  "WP$ (WH-pronoun, possessive)",
  "VB (verb, base form)",
  "PRP (pronoun, personal)",
  "VBZ (verb, present tense, 3rd person singular)",
  "DT (determiner)",
  "RB (adverb)",
  "NNP (noun, proper, singular)",
  "FW (foreign word)",
  "JJR (adjective, comparative)",
  "EX (existential there)",
  "UH (interjection)",
  "NNS (noun, common, plural)",
  "JJS (adjective, superlative)",
  "POS (genitive marker)",
  "PRP$ (pronoun, possessive)",
  "MD (modal auxiliary)",
  "VBD (verb, past tense)",
  "WP (WH-pronoun)",
  "TO ('to' as preposition or infinitive marker)",
  "VBG (verb, present participle or gerund)",
  "RP (particle)"
]

function enableRuleTypeaheads() {
  jQuery('.rule-body input[data-provide="typeahead"]').typeahead({
    source: posTagDataSource
  });
}

function enableClickovers() {
  jQuery('[rel="clickover"]').clickover({
    html: true,
    onShown: function() {
      jQuery('.popover-content input[data-provide="typeahead"]').typeahead({
        source: posTagDataSource,
        updater: function(item) {
          var posTagId = this.$element[0].id;
          var tokenId = posTagId.split('-').pop();
          var posTag = item.split(' ')[0];
          com.titlecapitalizer.main.presenter.updatePosTag(
            tokenId, posTag
          );
        }
      });
    }
  });
}

jQuery('#details').on('hide', function (e) {
  if (e.target !== this) // target is descendant
    return;

  com.titlecapitalizer.main.presenter.collapseDetails();
});

jQuery('#details').on('show', function (e) {
  if (e.target !== this) // target is descendant
    return;

  com.titlecapitalizer.main.presenter.expandDetails();
});

jQuery('#loadDetails').on('click', function(e) {
    e.preventDefault();
});
