(ns turtlequeue-quickstart.core
  (:require [turtlequeue.api :as turtlequeue]
            [turtlequeue.config]
            [turtlequeue.create]
            [turtlequeue.driver :as driver]
            [turtlequeue.protocol]
            [turtlequeue.reader]))

(defn -main
  [& args]
  (println "Hello, TurtleQueue! " {:awesome turtlequeue.api/awesome
                                   :version turtlequeue.api/version
                                   :commitVersion turtlequeue.api/commitVersion
                                   :buildNum turtlequeue.api/buildNum})
  (println "Open the source file and REPL in to follow the tutorial"))


(comment
  ;;
  ;; Evaluate these forms in your REPL
  ;;

  (turtlequeue.api/init {})

  (turtlequeue.api/on "ready" (fn [evt]
                                (println "ready event" evt)))

  (turtlequeue.api/on "error" (fn [evt]
                                (println "error event" evt)))

  (turtlequeue.api/on "disconnect" (fn [evt]
                                     (println "disconnected event")))

  (def connect-res @(turtlequeue.api/connect {:UserToken (java.util.UUID/fromString (System/getenv "TURTLEQUEUE_USER_TOKEN"))
                                              :ApiKey (java.util.UUID/fromString (System/getenv "TURTLEQUEUE_API_KEY"))}))

  (def channel (str "#test-clj-quickstart-repl" (System/currentTimeMillis)))

  (def sub-res @(turtlequeue.api/subscribe {:channel channel}
                                           (fn [err data metadata]
                                             (println "data received on channel #test" err data metadata))))

  (def subscription-id (get-in sub-res [:data :id]))

  (def pub-res @(turtlequeue.api/publish {:channel channel
                                          :payload {:ok "true"}}
                                         (fn publish-callback [err data metadata]
                                           (println "data sent on channel #test" err data metadata))))

  (def r @(turtlequeue.api/reader {:subscriptionId (get-in sub-res [:data :id])
                                   :startMessageId "earliest"}))

  ;; replay messages from the beginning
  (while (:data @(turtlequeue.reader/hasMessageAvailable r))
    (println "message " @(turtlequeue.reader/readNext r)))

  @(turtlequeue.reader/close r)

  )


;; This concludes the tutorial :)
;; You may want to look at the tests too, they include some potentially
;; interesting patterns
