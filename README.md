# Title Capitalizer

Title Capitalizer is a small web application for capitalizing titles according
to various styles. Titles are analyzed using
[Apache OpenNLP](https://opennlp.apache.org/) via
[clojure-opennlp](https://github.com/dakrone/clojure-opennlp), and then
capitalization rules are applied to each token.

This is an early release, much still remains to be done.

## Usage

The application is currently hosted by Heroku at
[titlecapitalizer.com](http://www.titlecapitalizer.com/).

Alternatively, to start a local web server for development you can either eval
`web_app.clj` from your editor or launch from the command line:

    $ lein with-profile production trampoline run -m com.titlecapitalizer.view.web-app

## To Do

* Make styles editable. Allow users to log in using
  [Friend](https://github.com/cemerick/friend). Set up a PostgreSQL database and
  store users' custom styles there.

* Allow capitalization styles to be exported and imported to/from text files.

* In the "Analyzed" area show the rules that were matched to each token. Display
  the rule number after the token's POS tag.

* Add [unit tests](https://github.com/marick/Midje).

* Add informal documentation.

* Add documentation about the application's internals.

## License

Copyright © 2013 Hilverd Reker.
