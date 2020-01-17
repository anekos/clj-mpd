(ns mpd.cache
  (:require [clojure.data.json :as json]
            [clojure.java.io :refer [file]]
            [clojure.java.jdbc :as jdbc]
            [clojure.pprint :refer [cl-format]]
            [me.raynes.fs :as fs]
            [mpd.client.command :as client-command]))


(def ^:dynamic *cache-file* (file (fs/home) ".local/var/cache/clj-mpd/walked.sqlite"))
(def ^:dynamic *old-cache-file* (file (fs/home) ".local/var/cache/clj-mpd/walked.json"))

(defn db []
  {:subprotocol "sqlite"
   :subname *cache-file*})


(declare total write-cache)


(defn duration-pct
  "`pct` percentile duration"
  [pct]
  (let [total (total)]
    (-> (jdbc/query
         (db)
         ["SELECT duration FROM songs ORDER BY duration LIMIT 1 OFFSET ? " (int (/ (* total pct) 100))])
        first
        :duration)))

(defn init-db []
  (jdbc/execute!
   (db)
   "CREATE TABLE IF NOT EXISTS songs (path varchar primary key, duration real, json varchar)")
  (jdbc/execute!
   (db)
   "CREATE INDEX IF NOT EXISTS duration_index ON songs (duration)"))

(defn fetch-in-range [from to]
  (map
   (comp #(json/read-str % :key-fn keyword) :json)
   (jdbc/query
    (db)
    ["SELECT json FROM songs WHERE ? <= duration AND duration <= ?" from to])))

(defn total []
  (-> (jdbc/query (db) ["SELECT COUNT(*) FROM songs"])
      first
      vals
      first))

(defn update-cache []
  (let [entries (client-command/walk "")]
    (init-db)
    (write-cache entries)))

(defn write-cache [entries]
  (cl-format *err* "! Writing cache~%")
  (fs/mkdirs (.getParent *cache-file*))
  (jdbc/db-transaction*
   (db)
   (fn [db]
     (jdbc/execute! db ["DELETE FROM songs"])
     (jdbc/insert-multi!
      db
      :songs
      (map (fn [{:keys [path duration] :as all}]
             {:path path :duration duration :json (json/write-str all)})
           entries))
     nil)))
