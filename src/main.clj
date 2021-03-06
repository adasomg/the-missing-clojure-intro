(ns main ;; checkout https://clojuredocs.org/clojure.core/ns to understand what happened here
  (:gen-class)
  (:require
   [net.cgrand.enlive-html :as enlive]
   [clojure.string :as s]))

(def url "https://dev.to")

;; turn everything into functions so we only fetch stuff when acually calling print-top-words rather than on load 
(defn fetch-document []
  (enlive/html-resource (java.net.URL. url)))

(defn tokenize [s]
  (filter not-empty (s/split s #"( +|\n|:)+")))

(defn get-top-words []
  (->> (enlive/select (fetch-document) [:.single-article :h3])
       (map (comp tokenize first :content))
       flatten
       frequencies
       (sort-by val)))

(defn print-top-words []
  (doseq [w (get-top-words)]
    (println (key w) (val w))))

(defn -main []
  (println "Will print top words in a sec...")
  (print-top-words))
