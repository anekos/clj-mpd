(ns mpd.timer
  (:require [util :refer [percentile retry sample vector-or-nil]]))


(declare random-find split-search any-search)

(defn make
  ([entries]
   (make entries 1.0))
  ([entries tolerance]
   {:entries entries
    :long-time (:duration (percentile 90 entries))
    :tolerance tolerance}))

(defn duration-match
  "`target` is duration, `entry` is duration"
  [t target entry]
  (let [tolerance (:tolerance t)]
    (< (- target tolerance)
       entry
       (+ target tolerance))))

(defn smart-search [t duration]
  ((if (< (:long-time t) duration)
     split-search
     any-search)
   t duration))

(defn any-search [t duration]
  ((sample [(comp vector-or-nil random-find)
            split-search])
   t duration))

(defn random-find [t duration]
  (->> (:entries t)
       (filter #(duration-match t duration (:duration %)))
       sample))

(defn split-search [t duration]
  (when-let [fst (retry
                   10
                   #(let [fst (rand (min (:long-time t) duration))]
                      (random-find t fst)))]
    (let [remain (- duration
                    (:duration fst))]
      (when-let [remain (smart-search t remain)]
        (cons fst remain)))))
