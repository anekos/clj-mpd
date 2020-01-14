(ns mpd.core
  (:gen-class)
  (:require [clojure.pprint :refer [cl-format]]
            [cli-matic.core :refer [run-cmd]]
            [mpd.client :as client]
            [mpd.client.command :as cmd]
            [mpd.timer :as timer]
            [mpd.cache :as cache]))


(defn command-update [{}]
  (client/with-mpd
    (cache/update-cache)))

(defn command-timer [{duration :duration print-only :print}]
 (cl-format *err* "Setup timer playlist for ~A seconds~%" duration)
 (let [cache (cache/read-cache)
       t (timer/make cache)
       pl (timer/smart-search t duration)]
   ; TODO
   (if print-only
     (doseq [{path :path} pl]
       (println path))
     (client/with-mpd
       (cmd/clear)
       (doseq [{path :path} pl]
         (cmd/add path))))))



(def cli-options
  {:app {:command     "mpd-timer"
         :description "Setup playlist for timer"
         :version     "0.0.1"}
   :global-opts [{:option  "port"
                  :short   "p"
                  :as      "MPD port"
                  :type    :int
                  :default 6600}]
   :commands    [{:command     "update"
                  :description "Update cache"
                  :runs        command-update}
                 {:command     "timer"
                  :description "Setup playlist for timer"
                  :opts        [{:short 0 :option "duration" :as "Duration in seconds" :type :int}
                                {:short "p" :option "print" :as "Print path only" :type :with-flag :default false}]
                  :runs        command-timer}]})

(defn -main
  [& args]
  (run-cmd args cli-options))
