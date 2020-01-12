(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.repl :refer :all]
            [clojure.string :as str]
            [mpd.client :as client]
            [clj-telnet.core :as telnet]
            [clojure.pprint :refer [pp pprint cl-format]]))


(defn init []
  (alter-var-root (var *print-level*) (fn [_] 2))
  (alter-var-root (var *print-length*) (fn [_] 200)))


(defn reset []
  (init)
  (refresh))


(def ^:dynamic *conn* (telnet/get-telnet "localhost" 6600))

(defn escape [s]
  (str/replace s
               #"\""
               "\\\""))

(defn quote [s]
  (str "\""
       (escape s)
       "\""))

(defn read-response []
  (let [r (telnet/read-until-or *conn* ["\nOK\n" "\nACK "])
        ur (String. (.getBytes r) "UTF-8")]
    (->> ur
         reverse
         (take 30)
         reverse
         (apply str)
         (cl-format true "response: ~A~%"))
    r))

(defn command [cmd & args]
  (telnet/write *conn*
                (str
                 cmd
                 " "
                 (str/join " " (map quote args))))
  (read-response))

(defn parse-lsinfo [s]
  (let [lines (str/split s #"\n")]
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
          (conj result item))))))

(defn read-banner []
  (telnet/read-until *conn* "OK")
  (telnet/read-until *conn* "\n"))

(defn lsinfo [path]
  (let [s (command "lsinfo" path)
        r (parse-lsinfo s)]
    (cl-format true "dirs: ~A~%" (count r))
    r))

(defn path-join [parent child]
  (if (empty? parent)
    child
    (str parent "/" child)))

(defn walk [path]
  (cl-format true "walk in: ~A - ~A~%" path (seq (.getBytes path)))
  (let [entries (lsinfo path)
        files (filter #(= (:type %) "file") entries)
        dirs (->> entries
                  (filter #(= (:type %) "directory"))
                  (map :path))]
    (loop [result files [dir & tail] dirs]
      (if dir
        (recur
         (concat result
                 (walk dir))
         tail)
        result))))

(defn t []
  (read-banner)
  (let [items (lsinfo "")]
    (take 10 items)))

(defn tt []
  (read-banner)
  (let [items (walk "")]
    (take 10 items)))

(init)
