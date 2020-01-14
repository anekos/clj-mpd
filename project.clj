(defproject mpd "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[cheshire "5.9.0"]
                 [cli-matic "0.3.11"]
                 [clj-tcp "1.0.1"]
                 [com.cognitect/transit-clj "0.8.319"]
                 [commons-net/commons-net "3.5"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.7"]
                 [net.snca/kunekune "0.1.6"]
                 ; [clj-telnet "0.6.0"] using `lib`
                 ]
  :source-paths ["src" "clj-telnet/src"]
  :main mpd.core
  :profiles {:dev
             {:dependencies [[org.clojure/tools.namespace "0.3.1"]]
              :source-paths ["src" "test" "clj-telnet/src" "dev"]
              :aot []
              :repl-options {:init-ns user}}
             :uberjar
             {:aot :all}})
