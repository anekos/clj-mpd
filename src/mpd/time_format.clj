(ns mpd.time-format
  (:import java.time.Duration
           java.time.LocalTime
           java.time.format.DateTimeFormatter)
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :as string]
            [tick.alpha.api :as tk]))



(defn- unit-power [u]
  (if u
    (condp = (first (string/upper-case u))
      \D (* (unit-power "hours") 24)
      \H (* (unit-power "minutes") 60)
      \M (* (unit-power "seconds") 60)
      \S 1)
    (unit-power "minutes")))

(def units [[[" days" "d"]  (* 24 60 60)]
            [[" hours" "h"] (* 60 60)]
            [[" minutes" "m"] 60]
            [[" seconds" "s"] 1]])

(defn decode-at [s]
  (when (re-matches #"\d{1,2}:\d{1,2}" s)
    (let [fmt (DateTimeFormatter/ofPattern "H:m")
          at (LocalTime/from (.parse fmt s))
          now (java.time.LocalTime/now)
          duration (Duration/between now at)
          result (.getSeconds duration)]
      (when (<= result 0)
        (throw (Exception. "It's a past")))
      result)))

(defn decode-duration
  "Returns the number of seconds"
  [s]
  (let [m (re-matcher #"(\d+)([dhms])?"
                      (string/replace s #"\s+" ""))]
    (loop [[_ n u] (re-find m) result 0]
      (if n
        (recur
         (re-find m)
         (+ (* (Integer/parseInt n)
               (unit-power u))
            result))
        result))))

(defn decode
  "Returns the number of seconds"
  [s]
  (or (decode-at s)
      (decode-duration s)))

(defn encode [s & [short fix]]
  (loop [s s
         [[u p] & units] (map (fn [[[l s] p]] [(if short s l) p]) units)
         result []]
    (if u
      (let [q (int (/ s p)) r (mod s p)]
        (if (or fix (< 0 q))
          (let [u (if (and (not short) (= q 1))
                    (subs u 0 (dec (count u)))
                    u)]
            (recur r units (conj result
                                 (cl-format nil (if fix "~2,'0D~A" "~D~A") q u))))
          (recur r units result)))
      (let [s (string/join (if short "" " ") result)]
        (if (and short fix)
          (let [found (re-find #"^(00[dhms])+" s)
                zero-len (-> found first count)
                prefix (apply str (repeat zero-len " "))]
            (str prefix (subs s zero-len)))
          s)))))
