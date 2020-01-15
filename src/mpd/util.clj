(ns mpd.util)


(defn percentile [pct entries]
  (let [l (count entries)]
    (nth entries
         (/ (* l pct)
            100))))

(defn sample [coll]
  (when-not (empty? coll)
    (let [l (count coll)]
      (nth coll (rand-int l)))))

(defn sum-duration [entries]
  (->> entries
       (map :duration)
       (reduce +)
       Math/ceil))
