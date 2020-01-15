(ns mpd.time-format
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :as string]))



(defn- unit-power [u]
  (if u
    (condp = (first (string/upper-case u))
      \D (* (unit-power "hours") 24)
      \H (* (unit-power "minutes") 60)
      \M (* (unit-power "seconds") 60)
      \S 1)
    (unit-power "minutes")))

(def units [["days" (* 24 60 60)]
            ["hours" (* 60 60)]
            ["minutes" 60]
            ["seconds" 1]])

(defn decode [s]
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

(defn encode [s]
  (loop [s s [[u p] & units] units result []]
    (if u
      (let [q (int (/ s p)) r (mod s p)]
        (if (< 0 q)
          (let [u (if (= q 1)
                    (subs u 0 (dec (count u)))
                    u)]
            (recur r units (conj result (cl-format nil "~A ~D" q u))))
          (recur r units result)))
      (string/join " " result))))
