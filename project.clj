(defproject mpd "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-tcp "1.0.1"]
                 [me.raynes/fs "1.4.6"]
                 [com.cognitect/transit-clj "0.8.319"]
                 [org.clojure/data.json "0.2.7"]
                 [cheshire "5.9.0"]
                 ; [clj-telnet "0.6.0"] using `lib`
                 [commons-net/commons-net "3.5"]
                 [net.snca/kunekune "0.1.6"]]
  :source-paths ["src" "clj-telnet/src"]
  :profiles {:dev
             {:dependencies [[org.clojure/tools.namespace "0.3.1"]]
              :source-paths ["src" "dev" "clj-telnet/src"]
              :aot []
              :repl-options {:init-ns user}}
             :uberjar
             {:aot :all}})



