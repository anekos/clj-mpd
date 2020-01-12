(ns user
  (:require [clojure.java.io :refer [file]]
            [clojure.pprint :refer [cl-format]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [cheshire.core :as chs]
            [me.raynes.fs :as fs]
            [mpd.client :as client]
            [mpd.cache :as cache]
            [mpd.client.command :as client-command]))


(defn init []
  (alter-var-root (var *print-level*) (fn [_] 2))
  (alter-var-root (var *print-length*) (fn [_] 200)))


(defn reset []
  (init)
  (refresh))


(init)
