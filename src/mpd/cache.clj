(ns mpd.cache
  (:require [cheshire.core :as chs]
            [clojure.java.io :refer [file]]
            [clojure.pprint :refer [cl-format]]
            [me.raynes.fs :as fs]
            [mpd.client.command :as client-command]
            [mpd.time-format :as tf]
            [mpd.util :refer [sum-duration]]))


(def ^:dynamic *cache-file* (file (fs/home) ".local/var/cache/clj-mpd/walked.json"))


(defn write-cache [entries]
  (fs/mkdirs (.getParent *cache-file*))
  (chs/generate-stream entries (clojure.java.io/writer *cache-file*)))

(defn read-cache []
  (when (.exists *cache-file*)
    (let [cache (-> *cache-file* slurp (chs/parse-string keyword))
          cache (filter :duration cache)]
      (cl-format *err*
                 "? Library duration: ~A~%"
                 (tf/encode
                   (int (sum-duration cache))))
      cache)))

(defn update-cache []
  (let [entries (client-command/walk "")]
    (write-cache entries)))
