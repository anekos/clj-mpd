(ns mpd.client.command
  (:require [clojure.string :as str]
            [clojure.pprint :refer [cl-format]]
            [mpd.client :as client]))



(defn- parse-lsinfo [lines]
  (loop [result [] item {} [head & tail] lines]
    (if head
      (let [[k v] (str/split head #": " 2)]
        (if (some #(= % k) ["file" "directory" "playlist"])
          (if (empty? item)
            (recur result {:type k :path v} tail)
            (recur (conj result item) {:type k :path v} tail))
          (recur result (assoc item (keyword k) v) tail)))
      (if (empty? item)
        result
        (conj result item)))))

(defn lsinfo [path]
  (let [s (client/command "lsinfo" path)
        r (parse-lsinfo s)]
    r))

(defn walk [path]
  (cl-format *err* "walk in: ~A~%" path)
  (let [entries (lsinfo path)
        files (filter #(= (:type %) "file") entries)
        dirs (->> entries
                  (filter #(= (:type %) "directory"))
                  (map :path))
        children (apply concat
                        (map walk dirs))]
    (concat files children)))

