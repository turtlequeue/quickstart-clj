(defproject quickstart-clj "0.1.0-SNAPSHOT"
  :description "Example app using TurtleQueue in Clojure"
  :url "https://github.com/turtlequeue/quickstart-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.turtlequeue/clj-client "0.0.13"]]
  :main ^:skip-aot turtlequeue-quickstart.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :ci {:dependencies  ^:replace [[org.clojure/clojure "1.10.0"]
                                            [org.clojars.turtlequeue/turtlequeue-clj-sdk #=(eval (System/getenv "CI_TURTLEQUEUE_VERSION"))]]}})
