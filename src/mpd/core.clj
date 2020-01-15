(ns mpd.core
  (:gen-class)
  (:require [clojure.pprint :refer [cl-format]]
            [cli-matic.core :refer [run-cmd]]
            [mpd.cache :as cache]
            [mpd.client :as client]
            [mpd.client.command :as cmd]
            [mpd.time-format :as tf]
            [mpd.timer :as timer]
            [mpd.util :as util]))


(defn command-update [{host :host port :port}]
  (client/with-mpd host port
    (cache/update-cache)))

(defn command-set [{duration :duration print-only :print play :play host :host port :port}]
  (let [duration (tf/decode duration)]
    (cl-format *err* "! Setup timer playlist for ~A~%" (tf/encode duration))
    (let [cache (cache/read-cache)
          t (timer/make cache)
          pl (timer/smart-search t duration)]
      (cl-format *err*
                 "? ~A (~:D songs)~%~%"
                 (tf/encode (util/sum-duration pl))
                 (count pl))
      (if print-only
        (doseq [{path :path} pl]
          (println path))
        (client/with-mpd host port
          (cmd/clear)
          (doseq [{path :path} pl]
            (println path)
            (cmd/add path))
          (when play
            (cmd/play))))
      nil)))



(def cli-options
  {:app {:command     "mpd-timer"
         :description "Setup playlist for timer"
         :version     "0.0.1"}
   :global-opts [{:option  "port"
                  :short   "p"
                  :as      "MPD port"
                  :type    :int
                  :default 6600}
                 {:option  "host"
                  :short   "h"
                  :as      "MPD host"
                  :type    :string
                  :default "localhost"}]
   :commands    [{:command     "update"
                  :short       "u"
                  :description "Update cache"
                  :runs        command-update}
                 {:command     "set"
                  :short       "s"
                  :description "Setup playlist for timer"
                  :opts        [{:short 0 :option "duration" :as "Duration in seconds" :type :string}
                                {:short "p" :option "print" :as "Print path only" :type :with-flag :default false}
                                {:option "play" :as "Play after set playlist" :type :with-flag :default true}]
                  :runs        command-set}]})

(defn -main
  [& args]
  (run-cmd args cli-options))
