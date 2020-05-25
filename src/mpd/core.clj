(ns mpd.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [cl-format]]
            [clojure.spec.alpha :as spec]
            [cli-matic.core :refer [run-cmd]]
            [expound.alpha :as expound]
            [mpd.cache :as cache]
            [mpd.client :as client]
            [mpd.client.command :as cmd]
            [mpd.time-format :as tf]
            [mpd.timer :as timer]
            [mpd.util :as util]))


(defn command-update [{host :host port :port}]
  (client/with-mpd host port
    (cache/update-cache)))

(defn- print-entry [entry print-meta]
  (if print-meta
    (println (json/write-str entry))
    (println (:path entry))))

(defn command-set [{:keys [duration print play meta host port]}]
  (try
    (let [duration (tf/decode duration)]
      (cl-format *err* "! Setup timer playlist for ~A~%" (tf/encode duration))
      (let [pl (timer/generate (timer/make) duration)]
        (cl-format *err*
                   "? ~A (~:D songs)~%~%"
                   (tf/encode (util/sum-duration pl))
                   (count pl))
        (if print
          (doall (map #(print-entry % meta) pl))
          (client/with-mpd host port
            (cmd/clear)
            (doseq [{path :path :as all} pl]
              (print-entry all meta)
              (cmd/add path))
            (when play
              (cmd/play))))
        nil))
    (catch Exception e
      (println (.getMessage e)))))


(expound/def ::TIME-FORMAT #(re-matches #"^(\d{1,2}:\d{1,2}|\d+|(\d+\s*([dhms][a-zA-Z]*)?\s*)+)$" %) "
Duration or Time
e.g.
  123
  2 minutes 3 seconds
  1 hour 2 minutes 3 seconds
  1 day 2 hours 2 minutes 3 seconds
  2d 3h 4m 5s
  3d4h5m6s
  12:30")

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
                  :opts        [{:short 0 :option "duration" :as "Duration or Time(e.g. \"1day2hours3mins4secs\" or \"14:50\")" :type :string :default :present :spec ::TIME-FORMAT}
                                {:option "print" :short "p" :as "Print path only" :type :with-flag :default false}
                                {:option "meta" :as "Print meta" :type :with-flag :default false}
                                {:option "play" :as "Play after set playlist" :type :with-flag :default true}]
                  :runs        command-set}]})

(defn -main
  [& args]
  (run-cmd args cli-options))
