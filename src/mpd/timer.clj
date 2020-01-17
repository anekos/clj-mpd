(ns mpd.timer
  (:require [clojure.pprint :refer [cl-format]]
            [mpd.cache :as cache]
            [mpd.time-format :as tf]
            [mpd.util :refer [sample]]
            [progrock.core :as pr]))


(declare random-find split-search any-search)

(defn make
  ([]
   (make 1.0))
  ([tolerance]
   {:long-time (cache/duration-pct 90)
    :tolerance tolerance}))

(defn random-find [{:keys [tolerance]} duration]
  (sample
   (cache/fetch-in-range
    (- duration tolerance)
    (+ duration tolerance))))

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
