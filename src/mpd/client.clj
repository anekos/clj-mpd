(ns mpd.client
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :as str]
            [clj-telnet.core :as telnet]))



(def ^:dynamic *conn* nil)


(defn- escape [s]
  (str/replace s
               #"\""
               "\\\""))

(defn- quote [s]
  (str "\""
       (escape s)
       "\""))


(defn read-banner []
  (telnet/read-until-or *conn* [#"OK.*\n"]))

(defmacro with-mpd [& body]
  `(binding [*conn* (connect)]
     ~@body))

(defn connect []
  (binding [*conn* (telnet/get-telnet "localhost" 6600)]
    (cl-format *err* "? Connecting~%")
    (read-banner)
    (cl-format *err* "? Connected~%")
    *conn*))

(defn read-response []
  (let [r (telnet/read-until-or *conn* [#"OK\n" #"ACK .*\n"])
        r (str/split r #"\n")]
    (take (- (count r) 1)
          r)))

(defn command [cmd & args]
  (telnet/write *conn*
                (str
                 cmd
                 " "
                 (str/join " " (map quote args))))
  (read-response))
