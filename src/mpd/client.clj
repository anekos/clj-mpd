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

(defmacro with-mpd [host port & body]
  `(binding [*conn* (connect ~host ~port)]
     ~@body))

(defn connect [host port]
  (binding [*conn* (telnet/get-telnet host port)]
    (cl-format *err* "? Connecting~%")
    (read-banner)
    (cl-format *err* "? Connected~%")
    *conn*))

(defn wait-ok []
  (telnet/read-until-or *conn* [#"OK\n"]))

(defn read-response []
  ;(cl-format *err* "? read-response~%")
  (let [r (telnet/read-until-or *conn* [#"\nOK\n"])
        r (str/split r #"\n")]
    ;(cl-format *err* "? response = ~A~%" r)
    (take (- (count r) 1)
          r)))

(defn command [cmd & args]
  ;(cl-format *err* "? command: ~A - ~A~%" cmd args)
  (telnet/write *conn*
                (str
                 cmd
                 " "
                 (str/join " " (map quote args)))))

(defn command-read [& args]
  (apply command args)
  (read-response))

(defn command-wait [& args]
  (apply command args)
  (wait-ok))
