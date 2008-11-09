(ns test.compojure.html
  (:use fact
        (compojure html)))

(defn rand-tags
  "Generate a sequence of random tag names." []
  (rand-strs (str ascii-letters "-") 1 5))

(fact "Tag vectors can be empty"
  [tag (rand-tags)]
  (= (xml [tag])
     (str "<" tag " />")))

(fact "Tag vectors can contain strings"
  [tag     (rand-tags)
   content (rand-strs)]
  (= (xml [tag content])
     (str "<" tag ">" content "</" tag ">")))

(fact "Tags can be specified by keywords, symbols or strings"
  [tag [:hr 'hr "hr"]]
  (= (xml [tag])
     (str "<hr />")))

(fact "Tag vectors concatenate their contents"
  [tag      (rand-tags)
   contents (rand-seqs rand-strs 1 10)]
  (= (xml (apply vector tag contents))
     (str "<" tag ">" (apply str contents) "</" tag ">")))

(fact "Sequences in tag vectors are expanded out"
  [tag      (rand-tags)
   contents (rand-seqs rand-strs 1 10)]
  (= (xml (apply vector tag contents))
     (xml [tag contents])))

(fact "Tag vectors can be nested"
  [outer-tag  (rand-tags)
   before     (rand-strs)
   inner-tag  (rand-tags)
   inner-str  (rand-strs)
   after      (rand-strs)]
  (= (xml [outer-tag
            before [inner-tag inner-str] after])
     (xml [outer-tag
            before (xml [inner-tag inner-str]) after])))

(defn rand-attr-pairs
  "Generate a random attribute-value pair." []
  (map vector
    (rand-strs (str ascii-letters "-") 2 10)
    (rand-strs (str ascii-letters) 0 30)))

(fact "Tag vectors can have attribute maps"
  [tag        (rand-tags)
   [attr val] (rand-attr-pairs)
   content    (rand-strs)]
  (= (xml [tag {attr val} content])
     (str "<" tag " " attr "=\"" val "\">"
          content
          "</" tag ">")))

(fact "Attributes maps can have many values"
  [attr-map [{:a "1" :b "2" :c "3" :d "4"}
             {:id "a" :class "b"}]
   attr-str ["a=\"1\" b=\"2\" c=\"3\" d=\"4\""
             "class=\"b\" id=\"a\""]]
  (= (xml [:div attr-map])
     (str "<div " attr-str " />")))

(fact "Special characters are escaped in attributes"
  [char    ["\""     "<"    ">"    "&"]
   escaped ["&quot;" "&lt;" "&gt;" "&amp;"]]
  (= (xml [:div {:id char}])
     (str "<div id=\"" escaped "\" />")))

(fact "HTML tag vectors have syntax sugar for class attributes"
  [tag   (rand-tags)
   class (rand-strs ascii-letters 1 20)]
  (= (html [tag {:class class}])
     (html [(str tag "." class)])))

(fact "HTML tag vectors have syntax sugar for id attributes"
  [tag (rand-tags)
   id  (rand-strs ascii-letters 1 20)]
  (= (html [tag {:id id}])
     (html [(str tag "#" id)])))

(fact "The contents of HTML block tags are indented")

(fact "The HTML pre tag is rendered without indentation"
  [content (rand-strs)]
  (= (html [:body [:div [:pre content]]])
     "<body>\n  <div>\n    <pre>" content "</pre>\n  </div>\n</body>\n"))
