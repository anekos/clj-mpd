(ns mpd.cache
  (:require [clojure.java.io :refer [file]]
            [clojure.pprint :refer [cl-format]]
            [cheshire.core :as chs]
            [me.raynes.fs :as fs]
            [mpd.client.command :as client-command]))


(def ^:dynamic *cache-file* (file (fs/home) ".local/var/cache/clj-mpd/walked.json"))


(defn write-cache [entries]
  (fs/mkdirs (.getParent *cache-file*))
  (chs/generate-stream entries (clojure.java.io/writer *cache-file*)))

(defn read-cache []
  (when (.exists *cache-file*)
    (let [cache (-> *cache-file* slurp (chs/parse-string keyword))]
      (cl-format *err*
                 "Total duration: ~:D sec~%"
                 (Math/ceil (reduce +
                                    (map
                                      (comp read-string :duration)
                                      (filter :duration cache)))))
      cache)))

(defn update-cache []
  (let [entries (client-command/walk "")]
    (write-cache entries)))
