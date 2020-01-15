(ns mpd.time-format
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :as string]))



(defn- unit-power [u]
  (condp = u
    "d" (* (unit-power "h") 24)
    "h" (* (unit-power "m") 60)
    "m" (* (unit-power "s") 60)
    "s" 1
    nil (unit-power "m")))

(def units [["d" (* 24 60 60)]
            ["h" (* 60 60)]
            ["m" 60]
            ["s" 1]])

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
  (loop [s s [[u p] & units] units result ""]
    (if u
      (let [q (int (/ s p)) r (mod s p)]
        (if (< 0 q)
          (recur r units (cl-format nil "~A~D~A" result q u))
          (recur r units result)))
      result)))
