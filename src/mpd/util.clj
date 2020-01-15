(ns mpd.util)


(defn retry [n f]
  (loop [n n]
    (when (< 0 n)
      (if-let [r (f)]
        r
        (do (println 'retry)
            (recur (dec n)))))))

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

(defn vector-or-nil [item]
  (when item
    (vector item)))
