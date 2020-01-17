(ns mpd.timer
  (:require [clojure.pprint :refer [cl-format]]
            [mpd.util :refer [percentile sample]]
            [progrock.core :as pr]
            [mpd.time-format :as tf]))


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

(defn random-find [t duration]
  (->> (:entries t)
       (filter #(duration-match t duration (:duration %)))
       sample))

(defn- print-bar [bar]
  (pr/print bar {:format (cl-format nil
                                    "~A/~A :percent% [:bar] ETA: :remaining"
                                    (tf/encode (:progress bar) true true)
                                    (tf/encode (:total bar) true true))}))

(defn generate [t duration]
  (binding [*out* *err*]
    (let [prg-bar (pr/progress-bar (int duration))]
      (loop [dur duration result [] retried 0]
        (print-bar (pr/tick prg-bar (int (- duration
                                           dur))))
        (if (< dur (:long-time t))
          (do (print-bar (pr/tick prg-bar duration))
              (println)
              (cons (random-find t dur)
                    result))
          (let [next-dur (rand (min (:long-time t)
                                    dur))
                found (random-find t next-dur)]
            (if found
              (recur (- dur
                        (:duration found))
                     (cons found result)
                     0)
              (recur dur result (inc retried)))))))))
