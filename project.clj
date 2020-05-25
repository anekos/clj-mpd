(defproject mpd-timer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :plugins [[lein-ancient "0.6.15"]]
  :dependencies [[cli-matic "0.3.11"]
                 [clj-tcp "1.0.1"]
                 [commons-net/commons-net "3.6"]
                 [expound "0.8.4"]
                 [me.raynes/fs "1.4.6"]
                 [net.snca/kunekune "0.1.6"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.xerial/sqlite-jdbc "3.31.1"]
                 [progrock "0.1.2"]
                 [tick "0.4.23-alpha"]
                 ; [clj-telnet "0.6.0"] using `lib`
                 ]
  :source-paths ["src" "clj-telnet/src"]
  :main mpd.core
  :profiles {:dev
             {:dependencies [[org.clojure/tools.namespace "1.0.0"]]
              :source-paths ["src" "test" "clj-telnet/src" "dev"]
              :aot []
              :repl-options {:init-ns user}}
             :uberjar
             {:aot :all}})
